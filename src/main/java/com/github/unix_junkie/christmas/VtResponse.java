/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.Color.BRIGHT_RED;
import static com.github.unix_junkie.christmas.Color.BRIGHT_WHITE;
import static com.github.unix_junkie.christmas.Color.CYAN;
import static com.github.unix_junkie.christmas.TextAttribute.BOLD;
import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public abstract class VtResponse implements VtKeyOrResponse {
	/**
	 * @param term
	 */
	protected abstract Terminal appendDescription(final Terminal term);

	/**
	 * The return value of this method can be returned by
	 * {@link InputEvent#toString(Terminal)}.
	 *
	 * @see InputEvent#toString(Terminal)
	 * @see VtKeyOrResponse#toString(Terminal)
	 */
	@Override
	public final void toString(final Terminal term) {
		term.setTextAttributes(BRIGHT_RED, term.getDefaultBackground(), BOLD);
		term.print('[');

		/*
		 * White on cyan.
		 */
		term.setTextAttributes(BRIGHT_WHITE, CYAN, BOLD);
		this.appendDescription(term);

		term.setTextAttributes(BRIGHT_RED, term.getDefaultBackground(), BOLD);
		term.print(']');
		term.setTextAttributes(NORMAL);
	}
}
