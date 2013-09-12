/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import javax.annotation.Nonnull;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface Container extends Component {
	boolean isTopLevel();

	/**
	 * @param child
	 */
	ComponentBuffer getComponentBuffer(@Nonnull final ChildComponent child);
}
