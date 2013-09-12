/*-
 * $Id$
 */
package com.google.code.christmas.wt;

import javax.annotation.Nullable;

import com.google.code.christmas.Color;
import com.google.code.christmas.Dimension;
import com.google.code.christmas.Point;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface Component {
	Point getLocation();

	Dimension getSize();

	/**
	 * @param border
	 */
	void setBorder(@Nullable final Border border);

	Color getForeground();

	Color getBackground();

	/**
	 * @param background
	 */
	void setBackground(@Nullable final Color background);

	char getBackgroundPattern();

	/**
	 * @param backgroundPattern
	 */
	void setBackgroundPattern(final char backgroundPattern);

	void paint();
}
