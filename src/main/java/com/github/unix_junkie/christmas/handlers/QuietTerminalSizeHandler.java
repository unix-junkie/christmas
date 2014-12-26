/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static com.github.unix_junkie.christmas.Dimension.UNDEFINED;
import static com.github.unix_junkie.christmas.handlers.Handlers.asTerminalSizeProvider;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.github.unix_junkie.christmas.CursorLocationProvider;
import com.github.unix_junkie.christmas.Dimension;
import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.SequenceConsumer;
import com.github.unix_junkie.christmas.Terminal;
import com.github.unix_junkie.christmas.TerminalSizeProvider;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class QuietTerminalSizeHandler extends AbstractInputEventHandler {
	private boolean nextIsFiltering;

	public QuietTerminalSizeHandler() {
		this(new FilteringTerminalSizeHandler());
	}

	/**
	 * @param next
	 */
	public QuietTerminalSizeHandler(final InputEventHandler next) {
		super(next);
	}

	/**
	 * @see AbstractInputEventHandler#setNext(InputEventHandler)
	 */
	@Override
	void setNext(final InputEventHandler next) {
		super.setNext(next);
		this.nextIsFiltering = next instanceof TerminalSizeProvider || next instanceof CursorLocationProvider;
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
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
	 * @param term
	 */
	public Dimension getTerminalSize(final Terminal term) {
		if (SequenceConsumer.isDispatchThread()) {
			throw new IllegalStateException("Shouldn't be called from SequenceConsumer dispatch thread");
		}

		if (!this.nextIsFiltering) {
			return UNDEFINED;
		}

		final TerminalSizeProvider handler = this.next instanceof TerminalSizeProvider
				? (TerminalSizeProvider) this.next
				: asTerminalSizeProvider((CursorLocationProvider) this.next);

		/*
		 * Don't enqueue the request if already in the event queue.
		 */
		if (term.isEventQueue()) {
			return handler.getTerminalSize(term);
		}

		try {
			return term.invokeLater(() -> handler.getTerminalSize(term)).get();
		} catch (final InterruptedException ie) {
			ie.printStackTrace();
			return UNDEFINED;
		} catch (final ExecutionException ee) {
			ee.printStackTrace();
			return UNDEFINED;
		}
	}
}
