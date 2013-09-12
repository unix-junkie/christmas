/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import static java.util.EnumSet.noneOf;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.github.unix_junkie.christmas.Color;
import com.github.unix_junkie.christmas.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
final class ScreenCell {
	@Nonnull
	private char text;

	@Nonnull
	private Color foreground;

	@Nonnull
	private Color background;

	@Nonnull
	private final Set<TextAttribute> attributes = noneOf(TextAttribute.class);

	private boolean alternateCharset;

	/**
	 * @param text
	 */
	ScreenCell(final char text) {
		this.setText(text);
	}

	public char getText() {
		return this.text;
	}

	/**
	 * @param text
	 */
	public void setText(final char text) {
		this.text = text;
	}

	public Color getForeground() {
		assert this.foreground != null;
		return this.foreground;
	}

	/**
	 * @param foreground
	 */
	public void setForeground(final Color foreground) {
		if (foreground == null) {
			throw new IllegalArgumentException();
		}

		this.foreground = foreground;
	}

	public Color getBackground() {
		assert this.background != null;
		return this.background;
	}

	/**
	 * @param background
	 */
	public void setBackground(final Color background) {
		if (background == null) {
			throw new IllegalArgumentException();
		}

		this.background = background;
	}

	public Set<TextAttribute> getAttributes() {
		return this.attributes.isEmpty()
				? singleton(NORMAL)
				: unmodifiableSet(this.attributes);
	}

	/**
	 * @param attributes
	 */
	public void setAttributes(final Set<TextAttribute> attributes) {
		this.setAttributes(TextAttribute.toArray(attributes));
	}

	/**
	 * @param attributes
	 */
	public void setAttributes(final TextAttribute ... attributes) {
		final List<TextAttribute> newAttributes = asList(attributes);
		if (newAttributes.size() > 1 && newAttributes.contains(NORMAL)) {
			throw new IllegalArgumentException("NORMAL is acceptable, but it should be the only attribute supplied");
		}

		this.attributes.clear();
		this.attributes.addAll(newAttributes);
	}

	public boolean isAlternateCharset() {
		return this.alternateCharset;
	}

	/**
	 * @param alternateCharset
	 */
	public void setAlternateCharset(final boolean alternateCharset) {
		this.alternateCharset = alternateCharset;
	}
}
