/*-
 * $Id$
 */
package com.google.code.christmas.wt;

import static com.google.code.christmas.LineDrawingConstants.DOWN_AND_LEFT;
import static com.google.code.christmas.LineDrawingConstants.DOWN_AND_RIGHT;
import static com.google.code.christmas.LineDrawingConstants.HORIZONTAL;
import static com.google.code.christmas.LineDrawingConstants.UP_AND_LEFT;
import static com.google.code.christmas.LineDrawingConstants.UP_AND_RIGHT;
import static com.google.code.christmas.LineDrawingConstants.VERTICAL;
import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.code.christmas.Color;
import com.google.code.christmas.Dimension;
import com.google.code.christmas.Insets;
import com.google.code.christmas.LineDrawingMethod;
import com.google.code.christmas.Terminal;
import com.google.code.christmas.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class LineBorder implements Border {
	@Nonnull
	private final LineDrawingMethod lineDrawingMethod;

	@Nonnull
	private final BorderStyle style;

	@Nonnull
	private Color color;

	private final Set<TextAttribute> attributes = noneOf(TextAttribute.class);

	/**
	 * @param term
	 * @param style
	 * @param color
	 * @param attributes
	 */
	public LineBorder(@Nonnull final Terminal term,
			@Nonnull final BorderStyle style,
			@Nonnull final Color color,
			@Nonnull final TextAttribute ... attributes) {
		this.lineDrawingMethod = term.getLineDrawingMethod();
		this.style = style;
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
		if (this.style.isEmpty()) {
			return;
		}

		final Dimension size = component.getSize();
		if (size.isUndefined()) {
			return;
		}
		final int width = size.getWidth();
		final int height = size.getHeight();

		final Insets insets = this.getBorderInsets();
		if (insets.getLeft() + insets.getRight() > width
				|| insets.getTop() + insets.getBottom() > height) {
			/*
			 * Do not attempt to paint the border
			 * if component size is too small.
			 */
			return;
		}

		final Color topLeftForeground = this.style.isRaised()
				? this.color.brighter()
				: this.style.isLowered()
						? this.color.darker()
						: this.color;
		final Color bottomRightForeground = this.style.isRaised()
				? this.color.darker()
				: this.style.isLowered()
						? this.color.brighter()
						: this.color;
		final Color background = component.getBackground();

		final boolean alternateCharset = this.lineDrawingMethod.isAlternateCharset();
		final char horizontal = this.lineDrawingMethod.getChar(HORIZONTAL, this.style);
		for (int i = 2; i <= width - 1; i++) {
			buffer.setTextAt(horizontal, i, 1, alternateCharset, topLeftForeground, background, this.attributes);
			buffer.setTextAt(horizontal, i, height, alternateCharset, bottomRightForeground, background, this.attributes);
		}

		final char vertical = this.lineDrawingMethod.getChar(VERTICAL, this.style);
		for (int i = 2; i <= height - 1; i++) {
			buffer.setTextAt(vertical, 1, i, alternateCharset, topLeftForeground, background, this.attributes);
			buffer.setTextAt(vertical, width, i, alternateCharset, bottomRightForeground, background, this.attributes);
		}

		buffer.setTextAt(this.lineDrawingMethod.getChar(DOWN_AND_RIGHT, this.style), 1, 1, alternateCharset, topLeftForeground, background, this.attributes);
		buffer.setTextAt(this.lineDrawingMethod.getChar(UP_AND_RIGHT, this.style), 1, height, alternateCharset, bottomRightForeground, background, this.attributes);
		buffer.setTextAt(this.lineDrawingMethod.getChar(DOWN_AND_LEFT, this.style), width, 1, alternateCharset, topLeftForeground, background, this.attributes);
		buffer.setTextAt(this.lineDrawingMethod.getChar(UP_AND_LEFT, this.style), width, height, alternateCharset, bottomRightForeground, background, this.attributes);
	}

	/**
	 * @see Border#getBorderInsets()
	 */
	@Override
	public Insets getBorderInsets() {
		return this.style.isEmpty()
				? new Insets(0, 0, 0, 0)
				: new Insets(1, 1, 1, 1);
	}
}
