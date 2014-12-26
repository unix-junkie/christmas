/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import javax.annotation.Nonnull;

import com.github.unix_junkie.christmas.Insets;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface Border {
	/**
	 * @param component
	 * @param buffer
	 */
	void paintBorder(@Nonnull final Component component,
			@Nonnull final ComponentBuffer buffer);

	Insets getBorderInsets();
}
