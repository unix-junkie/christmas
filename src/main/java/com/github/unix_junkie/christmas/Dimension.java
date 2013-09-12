/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static java.lang.Math.max;


/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class Dimension {
	public static final Dimension UNDEFINED = new Dimension(-1, -1);

	public static final Dimension _80X24 = new Dimension(80, 24);

	public static final Dimension _80X25 = new Dimension(80, 25);

	public static final Dimension DEFAULT = _80X24;


	private final int width;

	private final int height;

	/**
	 * @param width
	 * @param height
	 */
	public Dimension(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * @param terminalSize
	 */
	public Dimension(final VtTerminalSize terminalSize) {
		this.width = terminalSize.getWidth();
		this.height = terminalSize.getHeight();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return this.width + "x" + this.height;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Dimension) {
			final Dimension that = (Dimension) obj;
			return this.width == that.width && this.height == that.height;
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.width ^ this.height;
	}

	public boolean isUndefined() {
		return this.equals(UNDEFINED);
	}

	/**
	 * @param insets
	 */
	public Dimension subtract(final Insets insets) {
		return new Dimension(max(0, this.getWidth() - insets.getWidth()),
				max(0, this.getHeight() - insets.getHeight()));
	}
}
