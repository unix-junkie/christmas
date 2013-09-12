/*-
 * $Id$
 */
package com.google.code.christmas.handlers;

import com.google.code.christmas.CursorLocationProvider;
import com.google.code.christmas.Dimension;
import com.google.code.christmas.Point;
import com.google.code.christmas.Terminal;
import com.google.code.christmas.TerminalSizeProvider;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public abstract class Handlers {
	private Handlers() {
		assert false;
	}

	public static TerminalSizeProvider asTerminalSizeProvider(final CursorLocationProvider cursorLocationProvider) {
		if (cursorLocationProvider == null) {
			throw new IllegalArgumentException();
		}

		return new TerminalSizeProvider() {
			/**
			 * @see TerminalSizeProvider#getTerminalSize(Terminal)
			 */
			@Override
			public Dimension getTerminalSize(final Terminal term) {
				term.setCursorLocation(999, 999);
				final Point cursorLocation = cursorLocationProvider.getCursorLocation(term);
				return new Dimension(cursorLocation.getX(), cursorLocation.getY());
			}
		};
	}
}
