/*-
 * $Id$
 */
package com.google.code.christmas;

import static com.google.code.christmas.Color.BLUE;
import static com.google.code.christmas.Color.BRIGHT_RED;
import static com.google.code.christmas.Color.BRIGHT_WHITE;
import static com.google.code.christmas.TextAttribute.BOLD;
import static com.google.code.christmas.TextAttribute.NORMAL;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum VtKey implements VtKeyOrResponse {
	F1,
	F2,
	F3,
	F4,
	F5,
	F6,
	F7,
	F8,
	F9,
	F10,
	F11,
	F12,

	HOME,
	INSERT,
	DELETE,
	END,
	PAGE_UP,
	PAGE_DOWN,

	UP,
	DOWN,
	RIGHT,
	LEFT,
	;

	/**
	 * The return value of this method can be returned by
	 * {@link InputEvent#toString(Terminal)}.
	 *
	 * @see InputEvent#toString(Terminal)
	 * @see VtKeyOrResponse#toString(Terminal)
	 */
	@Override
	public void toString(final Terminal term) {
		term.setTextAttributes(BRIGHT_RED, term.getDefaultBackground(), BOLD);
		term.print('[');

		term.setTextAttributes(BRIGHT_WHITE, BLUE, BOLD);
		term.print(this.name());

		term.setTextAttributes(BRIGHT_RED, term.getDefaultBackground(), BOLD);
		term.print(']');

		term.setTextAttributes(NORMAL);
	}
}
