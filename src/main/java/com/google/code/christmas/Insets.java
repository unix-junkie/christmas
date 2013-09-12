/*-
 * $Id$
 */
package com.google.code.christmas;

import static java.lang.Math.max;

import javax.annotation.Nonnull;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class Insets {
	private final int top;

	private final int left;

	private final int bottom;

	private final int right;

	/**
	 * @param top
	 * @param left
	 * @param bottom
	 * @param right
	 */
	public Insets(final int top, final int left, final int bottom, final int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public int getTop() {
		return this.top;
	}

	public int getLeft() {
		return this.left;
	}

	public int getBottom() {
		return this.bottom;
	}

	public int getRight() {
		return this.right;
	}

	public int getWidth() {
		return this.left + this.right;
	}

	public int getHeight() {
		return this.top + this.bottom;
	}

	/**
	 * @param that
	 */
	public Insets merge(@Nonnull final Insets that) {
		return new Insets(
				max(this.getTop(), that.getTop()),
				max(this.getLeft(), that.getLeft()),
				max(this.getBottom(), that.getBottom()),
				max(this.getRight(), that.getRight()));
	}
}
