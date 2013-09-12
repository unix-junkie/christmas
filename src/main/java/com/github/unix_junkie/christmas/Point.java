/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class Point {
	public static final Point UNDEFINED = new Point(-1, -1);

	private final int x;

	private final int y;

	/**
	 * @param x
	 * @param y
	 */
	public Point(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param cursorLocation
	 */
	public Point(final VtCursorLocation cursorLocation) {
		this.x = cursorLocation.getX();
		this.y = cursorLocation.getY();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return (this.x >= 0 ? "+" : "") + this.x
				+ (this.y >= 0 ? "+" : "") + this.y;
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Point) {
			final Point that = (Point) obj;
			return this.x == that.x && this.y == that.y;
		}
		return false;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.x ^ this.y;
	}

	public boolean isUndefined() {
		return this.equals(UNDEFINED);
	}
}
