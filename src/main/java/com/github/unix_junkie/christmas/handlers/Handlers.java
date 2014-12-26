/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import com.github.unix_junkie.christmas.CursorLocationProvider;
import com.github.unix_junkie.christmas.Dimension;
import com.github.unix_junkie.christmas.Point;
import com.github.unix_junkie.christmas.TerminalSizeProvider;

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

		return term -> {
			term.setCursorLocation(999, 999);
			final Point cursorLocation = cursorLocationProvider.getCursorLocation(term);
			return new Dimension(cursorLocation.getX(), cursorLocation.getY());
		};
	}
}
