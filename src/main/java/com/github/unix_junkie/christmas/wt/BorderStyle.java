/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import static com.github.unix_junkie.christmas.wt.BorderStyle.Style.FLAT;
import static com.github.unix_junkie.christmas.wt.BorderStyle.Style.LOWERED;
import static com.github.unix_junkie.christmas.wt.BorderStyle.Style.RAISED;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum BorderStyle {
	NONE(		true, 	FLAT),
	SINGLE(		false, 	FLAT),
	SINGLE_RAISED(	false,	RAISED),
	SINGLE_LOWERED(	false,	LOWERED),
	DOUBLE(		false,	FLAT),
	DOUBLE_RAISED(	false,	RAISED),
	DOUBLE_LOWERED(	false,	LOWERED),
	;

	private final boolean empty;

	private final Style style;

	/**
	 * @param empty
	 * @param style
	 */
	private BorderStyle(final boolean empty, final Style style) {
		this.empty = empty;
		this.style = style;
	}

	public boolean isEmpty() {
		return this.empty;
	}

	public boolean isRaised() {
		return this.style == RAISED;
	}

	public boolean isLowered() {
		return this.style == LOWERED;
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 */
	static enum Style {
		FLAT,
		RAISED,
		LOWERED,
		;
	}
}
