/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static com.github.unix_junkie.christmas.Color.BRIGHT_BLACK;
import static com.github.unix_junkie.christmas.Color.BRIGHT_RED;
import static com.github.unix_junkie.christmas.Color.WHITE;
import static com.github.unix_junkie.christmas.Point.UNDEFINED;
import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;
import static java.lang.Boolean.getBoolean;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import com.github.unix_junkie.christmas.CursorLocationProvider;
import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.Point;
import com.github.unix_junkie.christmas.SequenceConsumer;
import com.github.unix_junkie.christmas.Terminal;
import com.github.unix_junkie.christmas.TerminalType;
import com.github.unix_junkie.christmas.VtCursorLocation;
import com.github.unix_junkie.christmas.VtKeyOrResponse;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class FilteringCursorLocationHandler extends AbstractInputEventHandler implements CursorLocationProvider {
	/**
	 * Terminal emulator on the same host: ~50 ms.<br>
	 * Local area connection: ~220 ms.
	 */
	private static long DEFAULT_EXPECTING_TIMEOUT_MILLIS = 250;

	final Object expectingCursorLocationLock = new Object();

	/**
	 * The moment (in milliseconds) when {@link
	 * #setExpectingCursorLocation(boolean, Terminal) expectingCursorLocation}
	 * was set to {@code true}. If this value is {@code 0L}, then
	 * {@link #isExpectingCursorLocation() expectingCursorLocation} flag
	 * is {@code false}.
	 */
	private long t0;

	final long expectingTimeoutMillis;

	private final ScheduledExecutorService executor = newScheduledThreadPool(1);

	Point cursorLocation;

	final Object cursorLocationLock = new Object();

	public FilteringCursorLocationHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public FilteringCursorLocationHandler(final InputEventHandler next) {
		this(next, DEFAULT_EXPECTING_TIMEOUT_MILLIS);
	}

	/**
	 * @param next
	 * @param expectingTimeoutMillis
	 */
	public FilteringCursorLocationHandler(final InputEventHandler next, final long expectingTimeoutMillis) {
		super(next);
		this.expectingTimeoutMillis = expectingTimeoutMillis;
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		synchronized (this.expectingCursorLocationLock) {
			if (this.isExpectingCursorLocation()) {
				final Iterator<InputEvent> it = events.iterator();
				while (it.hasNext()) {
					final InputEvent event = it.next();
					final TerminalType type = term.getType();
					if (type.isKnownEscapeSequence(event)) {
						final VtKeyOrResponse vtKeyOrResponse = type.getVtKeyOrResponse(event);
						if (vtKeyOrResponse instanceof VtCursorLocation) {
							final VtCursorLocation cursorLocation0 = (VtCursorLocation) vtKeyOrResponse;
							if (!isDebugMode()) {
								it.remove();
							}

							final Point cursorLocation1 = new Point(cursorLocation0);

							if (isDebugMode()) {
								final long t0Snapshot = this.t0;
								final long t1 = System.currentTimeMillis();

								assert t0Snapshot != 0L;

								term.invokeLater(() -> {
									term.setTextAttributes(BRIGHT_RED, WHITE);
									term.print("DEBUG:");
									term.setTextAttributes(BRIGHT_BLACK, WHITE);
									term.println(" Cursor location of " + cursorLocation1 + " reported " + (t1 - t0Snapshot) + " ms after the request.");
									term.setTextAttributes(NORMAL);
									term.flush();
								});
							}

							synchronized (this.cursorLocationLock) {
								this.cursorLocation = cursorLocation1;
								this.cursorLocationLock.notifyAll();
							}

							/*
							 * Reset the "expectingCursorLocation" status.
							 */
							this.setExpectingCursorLocation(false, term);

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
	 * @see CursorLocationProvider#getCursorLocation(Terminal)
	 */
	@Override
	public Point getCursorLocation(final Terminal term) {
		if (SequenceConsumer.isDispatchThread()) {
			throw new IllegalStateException("Shouldn't be called from SequenceConsumer dispatch thread");
		}

		synchronized (this.cursorLocationLock) {
			/*
			 * Re-set the previously stored value.
			 */
			this.cursorLocation = null;

			this.setExpectingCursorLocation(true, term);

			term.requestCursorLocation().flush();

			while (this.cursorLocation == null) {
				try {
					this.cursorLocationLock.wait();
				} catch (final InterruptedException ie) {
					/*
					 * Never.
					 */
					break;
				}
			}

			assert this.cursorLocation != null;

			return this.cursorLocation;
		}
	}

	/**
	 * @param expectingCursorLocation
	 * @param term
	 */
	void setExpectingCursorLocation(final boolean expectingCursorLocation, final Terminal term) {
		synchronized (this.expectingCursorLocationLock) {
			if (expectingCursorLocation) {
				while (this.isExpectingCursorLocation()) {
					/*
					 * Don't start a new background task
					 * if there's one already running.
					 */
					try {
						this.expectingCursorLocationLock.wait();
					} catch (final InterruptedException ie) {
						// ignore
					}
				}

				this.executor.schedule(() -> {
					synchronized (FilteringCursorLocationHandler.this.expectingCursorLocationLock) {
						if (FilteringCursorLocationHandler.this.isExpectingCursorLocation()) {
							FilteringCursorLocationHandler.this.setExpectingCursorLocation(false, term);

							/*
							 * This background task can easily expire
							 * if multiple events are being collected.
							 */
							if (isDebugMode()) {
								term.invokeLater(() -> {
									term.setTextAttributes(BRIGHT_RED, WHITE);
									term.print("DEBUG:");
									term.setTextAttributes(BRIGHT_BLACK, WHITE);
									term.println(" Timed out waiting for cursor location for " + FilteringCursorLocationHandler.this.expectingTimeoutMillis + " ms.");
									term.setTextAttributes(NORMAL);
									term.flush();
								});
							}

							synchronized (FilteringCursorLocationHandler.this.cursorLocationLock) {
								FilteringCursorLocationHandler.this.cursorLocation = UNDEFINED;
								FilteringCursorLocationHandler.this.cursorLocationLock.notifyAll();
							}
						}
					}
				}, this.expectingTimeoutMillis, MILLISECONDS);
			} else {
				this.expectingCursorLocationLock.notifyAll();
			}

			this.t0 = expectingCursorLocation ? System.currentTimeMillis() : 0L;
		}
	}

	boolean isExpectingCursorLocation() {
		synchronized (this.expectingCursorLocationLock) {
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
