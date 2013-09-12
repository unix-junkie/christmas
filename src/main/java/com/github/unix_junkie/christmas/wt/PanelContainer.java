/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.unix_junkie.christmas.Color;
import com.github.unix_junkie.christmas.Dimension;
import com.github.unix_junkie.christmas.Point;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class PanelContainer implements ChildComponent, Container {
	public void add(@Nonnull final ChildComponent component, final double weight) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#paint()
	 */
	@Override
	public void paint() {
		final Dimension size = this.getSize();
		if (size.isUndefined() || size.getWidth() == 0 || size.getHeight() == 0) {
			return;
		}

		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#setLocation(Point)
	 */
	@Override
	public void setLocation(@Nonnull final Point location) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#getSize()
	 */
	@Override
	public Dimension getSize() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#setSize(Dimension)
	 */
	@Override
	public void setSize(@Nonnull final Dimension size) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Container#isTopLevel()
	 */
	@Override
	public boolean isTopLevel() {
		return false;
	}

	/**
	 * @see Container#getComponentBuffer(ChildComponent)
	 */
	@Override
	public ComponentBuffer getComponentBuffer(@Nonnull final ChildComponent child) {
		if (child.getParent() != this) {
			throw new IllegalArgumentException();
		}

		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#getParent()
	 */
	@Override
	public Container getParent() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#getLocation()
	 */
	@Override
	public Point getLocation() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see ChildComponent#getBorder()
	 */
	@Override
	public Border getBorder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#setBorder(Border)
	 */
	@Override
	public void setBorder(@Nullable final Border border) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#getForeground()
	 */
	@Override
	public Color getForeground() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#getBackground()
	 */
	@Override
	public Color getBackground() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#setBackground(Color)
	 */
	@Override
	public void setBackground(@Nullable final Color background) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#getBackgroundPattern()
	 */
	@Override
	public char getBackgroundPattern() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Component#setBackgroundPattern(char)
	 */
	@Override
	public void setBackgroundPattern(final char backgroundPattern) {
		throw new UnsupportedOperationException();
	}
}
