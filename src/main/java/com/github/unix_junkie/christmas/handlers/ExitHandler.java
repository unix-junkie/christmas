/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;

import java.util.List;

import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class ExitHandler extends AbstractInputEventHandler {
	public ExitHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public ExitHandler(final InputEventHandler next) {
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

		/*
		 * This handler should be the last one in a row.
		 */
		for (final InputEvent event : events) {
			if (event.isControlWith('Q') || event.isControlWith('C')) {
				term.invokeLater(() -> {
					/*
					 * Restore the title.
					 *
					 * Setting the title to an empty string
					 * doesn't work for some terminal emulators
					 * (particularly, Xterm),
					 * so consider restoring the original title
					 * or setting the title to a single space (' ').
					 */
					term.setTitle(null);

					/*
					 * Restore the default colors and text
					 * attributes before clearing the screen.
					 */
					term.setDefaultForeground(null);
					term.setDefaultBackground(null);
					term.setTextAttributes(NORMAL);

					term.stopAlternateCs();
					term.clear();
					term.setCursorVisible(true);

					term.close();
					System.exit(0);
				});
			}
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^Q or ^C to quit.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}
}
