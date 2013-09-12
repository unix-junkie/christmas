/*-
 * $Id$
 */
package com.google.code.christmas.wt;

import javax.annotation.Nonnull;

import com.google.code.christmas.Insets;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface Border {
	/**
	 * @param component
	 * @param buffer
	 * @param term
	 */
	void paintBorder(@Nonnull final Component component,
			@Nonnull final ComponentBuffer buffer);

	Insets getBorderInsets();
}
