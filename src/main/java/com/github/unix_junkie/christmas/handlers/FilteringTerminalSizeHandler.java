/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static com.github.unix_junkie.christmas.Color.BRIGHT_BLACK;
import static com.github.unix_junkie.christmas.Color.BRIGHT_RED;
import static com.github.unix_junkie.christmas.Color.WHITE;
import static com.github.unix_junkie.christmas.Dimension.UNDEFINED;
import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;
import static com.github.unix_junkie.christmas.handlers.Handlers.asTerminalSizeProvider;
import static java.lang.Boolean.getBoolean;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import com.github.unix_junkie.christmas.CursorLocationProvider;
import com.github.unix_junkie.christmas.Dimension;
import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.SequenceConsumer;
import com.github.unix_junkie.christmas.Terminal;
import com.github.unix_junkie.christmas.TerminalSizeProvider;
import com.github.unix_junkie.christmas.TerminalType;
import com.github.unix_junkie.christmas.VtKeyOrResponse;
import com.github.unix_junkie.christmas.VtTerminalSize;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class FilteringTerminalSizeHandler extends AbstractInputEventHandler implements TerminalSizeProvider {
	boolean nextIsFiltering;

	/**
	 * Terminal emulator on the same host: ~50 ms.<br>
	 * Local area connection: ~220 ms.
	 */
	private static long DEFAULT_EXPECTING_TIMEOUT_MILLIS = 250;

	final Object expectingTerminalSizeLock = new Object();

	/**
	 * The moment (in milliseconds) when {@link
	 * #setExpectingTerminalSize(boolean, Terminal) expectingTerminalSize}
	 * was set to {@code true}. If this value is {@code 0L}, then
	 * {@link #isExpectingTerminalSize() expectingTerminalSize} flag
	 * is {@code false}.
	 */
	private long t0;

	final long expectingTimeoutMillis;

	/**
	 * The background thread this executor is backed by is used to
	 * schedule the "response timeout" event which will fire after the
	 * thread which entered {@link #getTerminalSize(Terminal)} and sent
	 * a terminal size request via {@link Terminal#requestTerminalSize()}
	 * has been waiting for {@link #expectingTimeoutMillis} ms.
	 *
	 * @see #expectingTimeoutMillis
	 * @see #DEFAULT_EXPECTING_TIMEOUT_MILLIS
	 * @see #getTerminalSize(Terminal)
	 * @see Terminal#requestTerminalSize()
	 */
	private final ScheduledExecutorService executor = newScheduledThreadPool(1);

	Dimension terminalSize;

	final Object terminalSizeLock = new Object();

	public FilteringTerminalSizeHandler() {
		this(new FilteringCursorLocationHandler());
	}

	/**
	 * @param next
	 */
	public FilteringTerminalSizeHandler(final InputEventHandler next) {
		this(next, DEFAULT_EXPECTING_TIMEOUT_MILLIS);
	}

	/**
	 * @param next
	 * @param expectingTimeoutMillis
	 */
	public FilteringTerminalSizeHandler(final InputEventHandler next, final long expectingTimeoutMillis) {
		super(next);
		this.expectingTimeoutMillis = expectingTimeoutMillis;
	}

	/**
	 * @see AbstractInputEventHandler#setNext(InputEventHandler)
	 */
	@Override
	void setNext(final InputEventHandler next) {
		super.setNext(next);
		this.nextIsFiltering = next instanceof CursorLocationProvider;
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		synchronized (this.expectingTerminalSizeLock) {
			if (this.isExpectingTerminalSize()) {
				final Iterator<InputEvent> it = events.iterator();
				while (it.hasNext()) {
					final InputEvent event = it.next();
					final TerminalType type = term.getType();
					if (type.isKnownEscapeSequence(event)) {
						final VtKeyOrResponse vtKeyOrResponse = type.getVtKeyOrResponse(event);
						if (vtKeyOrResponse instanceof VtTerminalSize) {
							final VtTerminalSize terminalSize0 = (VtTerminalSize) vtKeyOrResponse;
							if (!isDebugMode()) {
								it.remove();
							}

							final Dimension terminalSize1 = new Dimension(terminalSize0);

							if (isDebugMode()) {
								final long t0Snapshot = FilteringTerminalSizeHandler.this.t0;
								final long t1 = System.currentTimeMillis();
								assert t0Snapshot != 0L;

								term.invokeLater(new Runnable() {
									/**
									 * @see Runnable#run()
									 */
									@Override
									public void run() {
										term.setTextAttributes(BRIGHT_RED, WHITE);
										term.print("DEBUG:");
										term.setTextAttributes(BRIGHT_BLACK, WHITE);
										term.println(" Terminal size of " + terminalSize1 + " reported " + (t1 - t0Snapshot) + " ms after the request.");
										term.setTextAttributes(NORMAL);
										term.flush();
									}
								});
							}

							synchronized (this.terminalSizeLock) {
								this.terminalSize = terminalSize1;
								this.terminalSizeLock.notifyAll();
							}

							/*
							 * Reset the "expectingTerminalSize" status.
							 */
							this.setExpectingTerminalSize(false, term);

							/*
							 * We're expecting only a single terminal size event.
							 */
							break;
						}
					}
				}
			}
		}

		if (this.next != null) {
			this.next.handle(term, events);
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		if (this.next != null) {
			this.next.printUsage(term);
		}
	}

	/**
	 * @see TerminalSizeProvider#getTerminalSize(Terminal)
	 */
	@Override
	public Dimension getTerminalSize(final Terminal term) {
		if (SequenceConsumer.isDispatchThread()) {
			throw new IllegalStateException("Shouldn't be called from SequenceConsumer dispatch thread");
		}

		final Dimension lastTerminalSize;

		synchronized (this.terminalSizeLock) {
			/*
			 * Re-set the previously stored value.
			 */
			this.terminalSize = null;

			this.setExpectingTerminalSize(true, term);

			term.requestTerminalSize().flush();

			while (this.terminalSize == null) {
				try {
					this.terminalSizeLock.wait();
				} catch (final InterruptedException ie) {
					/*
					 * Never.
					 */
					break;
				}
			}

			assert this.terminalSize != null;

			lastTerminalSize = this.terminalSize;
		}

		return lastTerminalSize.isUndefined() && this.nextIsFiltering
				? asTerminalSizeProvider((CursorLocationProvider) this.next).getTerminalSize(term)
				: lastTerminalSize;
	}

	/**
	 * @param expectingTerminalSize
	 * @param term
	 */
	void setExpectingTerminalSize(final boolean expectingTerminalSize, final Terminal term) {
		synchronized (this.expectingTerminalSizeLock) {
			if (expectingTerminalSize) {
				while (this.isExpectingTerminalSize()) {
					/*
					 * Don't start a new background task
					 * if there's one already running.
					 */
					try {
						this.expectingTerminalSizeLock.wait();
					} catch (final InterruptedException ie) {
						// ignore
					}
				}

				this.executor.schedule(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						synchronized (FilteringTerminalSizeHandler.this.expectingTerminalSizeLock) {
							if (FilteringTerminalSizeHandler.this.isExpectingTerminalSize()) {
								FilteringTerminalSizeHandler.this.setExpectingTerminalSize(false, term);

								/*
								 * This background task can easily expire
								 * if multiple events are being collected.
								 */
								if (isDebugMode()) {
									term.invokeLater(new Runnable() {
										/**
										 * @see Runnable#run()
										 */
										@Override
										public void run() {
											term.setTextAttributes(BRIGHT_RED, WHITE);
											term.print("DEBUG:");
											term.setTextAttributes(BRIGHT_BLACK, WHITE);
											term.println(" Timed out waiting for terminal size for " + FilteringTerminalSizeHandler.this.expectingTimeoutMillis + " ms.");
											term.setTextAttributes(NORMAL);
											term.flush();
										}
									});
								}

								synchronized (FilteringTerminalSizeHandler.this.terminalSizeLock) {
									FilteringTerminalSizeHandler.this.terminalSize = UNDEFINED;
									FilteringTerminalSizeHandler.this.terminalSizeLock.notifyAll();
								}
							}
						}
					}
				}, this.expectingTimeoutMillis, MILLISECONDS);
			} else {
				this.expectingTerminalSizeLock.notifyAll();
			}

			this.t0 = expectingTerminalSize ? System.currentTimeMillis() : 0L;
		}
	}

	boolean isExpectingTerminalSize() {
		synchronized (this.expectingTerminalSizeLock) {
			return this.t0 != 0L;
		}
	}

	/**
	 * @return whether debug mode is turned on.
	 */
	static boolean isDebugMode() {
		return getBoolean("terminal.debug");
	}
}
