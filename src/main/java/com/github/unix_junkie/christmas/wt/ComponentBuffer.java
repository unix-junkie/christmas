/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import java.util.Set;

import javax.annotation.Nonnull;

import com.github.unix_junkie.christmas.Color;
import com.github.unix_junkie.christmas.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface ComponentBuffer {
	/**
	 * @param text
	 * @param x
	 * @param y
	 */
	void setTextAt(final char text, final int x, final int y);

	/**
	 * @param text
	 * @param x
	 * @param y
	 * @param alternateCharset
	 */
	void setTextAt(final char text, final int x, final int y, final boolean alternateCharset);

	/**
	 * @param text
	 * @param x
	 * @param y
	 * @param alternateCharset
	 * @param foreground
	 * @param background
	 * @param attributes
	 */
	void setTextAt(final char text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			final Color background,
			@Nonnull final Set<TextAttribute> attributes);

	/**
	 * @param text
	 * @param x
	 * @param y
	 * @param alternateCharset
	 * @param foreground
	 * @param background
	 * @param attributes
	 */
	void setTextAt(final char text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			final Color background,
			@Nonnull final TextAttribute ... attributes);

	/**
	 * @param text
	 * @param x
	 * @param y
	 * @param alternateCharset
	 * @param foreground
	 * @param background
	 * @param attributes
	 */
	void setTextAt(final String text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			final Color background,
			@Nonnull final TextAttribute ... attributes);
}
