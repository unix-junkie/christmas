/*-
 * $Id$
 */
package com.google.code.christmas.wt;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.code.christmas.Color;
import com.google.code.christmas.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
abstract class AbstractComponentBuffer implements ComponentBuffer {
	/**
	 * @see ComponentBuffer#setTextAt(char, int, int)
	 */
	@Override
	public final void setTextAt(final char text, final int x, final int y) {
		this.setTextAt(text, x, y, false);
	}

	/**
	 * @see ComponentBuffer#setTextAt(char, int, int, boolean)
	 */
	@Override
	public final void setTextAt(final char text, final int x, final int y, final boolean alternateCharset) {
		this.setTextAt(text, x, y, alternateCharset, null, null);
	}

	/**
	 * @see ComponentBuffer#setTextAt(char, int, int, boolean, Color, Color, Set)
	 */
	@Override
	public final void setTextAt(final char text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			final Color background,
			@Nonnull final Set<TextAttribute> attributes) {
		this.setTextAt(text, x, y, alternateCharset, foreground, background, TextAttribute.toArray(attributes));
	}

	/**
	 * @see ComponentBuffer#setTextAt(String, int, int, boolean, Color, Color, TextAttribute[])
	 */
	@Override
	public final void setTextAt(final String text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			final Color background,
			@Nonnull final TextAttribute ... attributes) {
		for (int i = 0; i < text.length(); i++) {
			this.setTextAt(text.charAt(i), x + i, y, alternateCharset, foreground, background, attributes);
		}
	}
}
