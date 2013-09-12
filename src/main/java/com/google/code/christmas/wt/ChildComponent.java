/*-
 * $Id$
 */
package com.google.code.christmas.wt;

import javax.annotation.Nonnull;

import com.google.code.christmas.Dimension;
import com.google.code.christmas.Point;

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
