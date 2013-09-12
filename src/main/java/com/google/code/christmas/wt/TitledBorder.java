/*-
 * $Id$
 */
package com.google.code.christmas.wt;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.code.christmas.Color;
import com.google.code.christmas.Dimension;
import com.google.code.christmas.Insets;
import com.google.code.christmas.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class TitledBorder implements Border {
	@Nullable
	private final Border border;

	@Nullable
	private final String title;

	@Nonnull
	private Color color;

	private final Set<TextAttribute> attributes = noneOf(TextAttribute.class);

	/**
	 * @param border
	 * @param title
	 * @param color
	 * @param attributes
	 */
	public TitledBorder(@Nullable final Border border,
			@Nullable final String title,
			@Nonnull final Color color,
			@Nonnull final TextAttribute ... attributes) {
		this.border = border;
		this.title = title;
		this.setColor(color, attributes);
	}

	/**
	 * @param color
	 * @param attributes
	 */
	public void setColor(@Nonnull final Color color,
			@Nonnull final TextAttribute ... attributes) {
		this.color = color;
		this.setAttributes(attributes);
	}

	/**
	 * @param attributes
	 */
	public void setAttributes(@Nonnull final TextAttribute ... attributes) {
		this.attributes.clear();
		this.attributes.addAll(asList(attributes));
	}

	/**
	 * @see Border#paintBorder(Component, ComponentBuffer)
	 */
	@Override
	public void paintBorder(@Nonnull final Component component,
			@Nonnull final ComponentBuffer buffer) {
		if (this.border != null) {
			this.border.paintBorder(component, buffer);
		}

		if (this.title == null) {
			return;
		}
		final int titleLength = this.title.length();
		if (titleLength == 0) {
			return;
		}

		final Dimension size = component.getSize();
		if (size.isUndefined()) {
			return;
		}

		final int width = size.getWidth();

		final int maximumTitleLength = max(0, width - 2 - this.getChildInsets().getWidth());
		final int effectiveTitleLength = min(titleLength, maximumTitleLength);
		final String effectiveTitle;
		switch (effectiveTitleLength) {
		case 0:
			/*
			 * Don't paint a title at all.
			 */
			return;
		case 1:
			/*
			 * Only the first title character can be painted.
			 */
			effectiveTitle = ' ' + this.title.substring(0, effectiveTitleLength) + ' ';
			break;
		default:
			/*-
			 * If the title doesn't fit, trim it and add the '>' at the end.
			 * If window width is odd and title length is even (or vice versa),
			 * add an extra space to the end of title (unless we're trimming it).
			 */
			effectiveTitle = ' ' + (titleLength == effectiveTitleLength
					? titleLength % 2 == width % 2
							? this.title
							: this.title + ' '
					: this.title.substring(0, effectiveTitleLength - 1) + '>') + ' ';
			break;
		}

		for (int i = 0; i < effectiveTitle.length(); i++) {
			buffer.setTextAt(effectiveTitle.charAt(i), i + (width - effectiveTitleLength) / 2, 1, false, this.color, component.getBackground(), this.attributes);
		}
	}

	/**
	 * @see Border#getBorderInsets()
	 */
	@Override
	public Insets getBorderInsets() {
		final Insets thisInsets = new Insets(this.title == null || this.title.length() == 0 ? 0 : 1, 0, 0, 0);
		return thisInsets.merge(this.getChildInsets());
	}

	private Insets getChildInsets() {
		return this.border == null ? new Insets(0, 0, 0, 0) : this.border.getBorderInsets();
	}
}
