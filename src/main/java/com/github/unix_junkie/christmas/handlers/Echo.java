/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static java.lang.Boolean.getBoolean;

import java.util.List;

import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class Echo extends AbstractInputEventHandler {
	public Echo() {
		this(null);
	}

	/**
	 * @param next
	 */
	public Echo(final InputEventHandler next) {
		super(next);
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		term.invokeLater(() -> {
			if (events.isEmpty()) {
				/*
				 * Do not clear the screen
				 * or otherwise mess with the terminal
				 * if the sequence is empty
				 * (may happen if certain terminal responses
				 * have been removed from an initially non-empty sequence).
				 */
				return;
			}

			if (!isDebugMode()) {
				/*
				 * In debug mode, don't clear the screen
				 * as we may miss interesting terminal responses.
				 */
				term.clear();
			}
			for (final InputEvent event : events) {
				term.print(event);
			}
			term.println();
			term.flush();
		});

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
	 * @return whether debug mode is turned on.
	 */
	static boolean isDebugMode() {
		return getBoolean("terminal.debug");
	}
}
