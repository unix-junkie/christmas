/*-
 * $Id$
 */
package com.google.code.christmas;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum Color {
	BLACK,
	RED,
	GREEN,
	YELLOW,
	BLUE,
	MAGENTA,
	CYAN,
	WHITE,
	BRIGHT_BLACK,
	BRIGHT_RED,
	BRIGHT_GREEN,
	BRIGHT_YELLOW,
	BRIGHT_BLUE,
	BRIGHT_MAGENTA,
	BRIGHT_CYAN,
	BRIGHT_WHITE,
	;

	private static Map<Integer, Color> VALUES = new HashMap<Integer, Color>();

	static {
		for (final Color color : values()) {
			VALUES.put(Integer.valueOf(color.ordinal()), color);
		}
	}

	public boolean isBright() {
		return this.ordinal() >= values().length / 2;
	}

	public boolean isDark() {
		return !this.isBright();
	}

	public Color brighter() {
		return this.isDark()
				? valueOf(this.ordinal() + values().length / 2)
				: this;
	}

	public Color darker() {
		return this.isBright()
				? valueOf(this.ordinal() - values().length / 2)
				: this;
	}

	/**
	 * @param ordinal
	 * @throws IllegalArgumentException
	 */
	static Color valueOf(final int ordinal) {
		if (ordinal < 0 || ordinal >= values().length) {
			throw new IllegalArgumentException(String.valueOf(ordinal));
		}

		return VALUES.get(Integer.valueOf(ordinal));
	}
}
