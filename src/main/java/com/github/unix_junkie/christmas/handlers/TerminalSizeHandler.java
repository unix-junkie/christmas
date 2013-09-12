/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static java.lang.Boolean.getBoolean;

import java.util.List;

import com.github.unix_junkie.christmas.Dimension;
import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class TerminalSizeHandler extends AbstractInputEventHandler {
	public TerminalSizeHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public TerminalSizeHandler(final InputEventHandler next) {
		super(next);
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		if (this.next != null) {
			this.next.handle(term, events);
		}

		for (final InputEvent event : events) {
			if (event.isControlWith('L')) {
				term.invokeLater(new Runnable() {
					/**
					 * @see Runnable#run()
					 */
					@Override
					public void run() {
						final Dimension terminalSize;
						if (isDebugMode()) {
							/*
							 * In debug mode, clear the screen *before*
							 * the debug output is printed.
							 */
							term.clear();
							terminalSize = term.getSize();
						} else {
							terminalSize = term.getSize();
							/*
							 * Clear the screen *after*
							 * it has potentially been messed with.
							 */
							term.clear();
						}

						term.println("Terminal size of " + terminalSize + " reported; default is " + term.getDefaultSize() + '.');
						term.flush();
					}
				});
			}
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^L for text area size reporting.");
		term.flush();

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
