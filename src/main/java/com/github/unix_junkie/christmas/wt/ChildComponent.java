/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import javax.annotation.Nonnull;

import com.github.unix_junkie.christmas.Dimension;
import com.github.unix_junkie.christmas.Point;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface ChildComponent extends Component {
	/**
	 * @param location
	 */
	void setLocation(@Nonnull final Point location);

	/**
	 * @param size
	 */
	void setSize(@Nonnull final Dimension size);

	Container getParent();

	Border getBorder();
}
