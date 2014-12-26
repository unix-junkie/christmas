/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.Color.BRIGHT_RED;
import static com.github.unix_junkie.christmas.Color.BRIGHT_WHITE;
import static com.github.unix_junkie.christmas.Color.GREEN;
import static com.github.unix_junkie.christmas.TextAttribute.BOLD;
import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;

import java.util.Arrays;

/**
 * An unparsed sequence of chars from terminal response.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class InputEvent implements CharSequence {
	public static final char BELL = '\007';

	public static final char BACKSPACE = '\b';

	public static final char TAB = '\t';

	public static final char ENTER = '\r';

	public static final char ESC = '\u001B';		//  27

	public static final char DELETE = '\u007F';	// 127

	/**
	 * Required for correct {@link #toString()} operation.
	 */
	private final TerminalType terminalType;

	private final char data[];

	/**
	 * @param terminalType
	 * @param data
	 */
	InputEvent(final TerminalType terminalType, final char ... data) {
		if (data.length == 0) {
			throw new IllegalArgumentException();
		}

		this.terminalType = terminalType;
		this.data = data;
	}

	/**
	 * @param term
	 * @param c
	 */
	private static void toHumanReadable(final Terminal term, final char c) {
		/*
		 * Try to exclude most of the control characters.
		 */
		if (0 <= c && c <= 31) {
			term.setTextAttributes(BRIGHT_WHITE, GREEN, BOLD);

			/*
			 * Those definitely are the control characters.
			 */
			term.print("^" + (char) ('@' + c));
			switch (c) {
			case BACKSPACE:
				term.print("/BackSpace");
				break;
			case TAB:
				term.print("/Tab");
				break;
			case ENTER:
				term.print("/Enter");
				break;
			case ESC:
				term.print("/Escape");
				break;
			}
		} else if (c == DELETE) {
			term.setTextAttributes(BRIGHT_WHITE, GREEN, BOLD);

			term.print((int) c);
			term.print("/Delete");
		} else {
			/*
			 * Most probably, alphanumeric or punctuation.
			 */
			term.restoreDefaultForeground().restoreDefaultBackground();

			term.print(c);
		}
	}


	/**
	 * @param c
	 */
	public static boolean isControlCharacter(final char c) {
		return 0 <= c && c <= 31 || c == 127;
	}

	private boolean isControlCharacter() {
		return this.data.length == 1 && isControlCharacter(this.data[0]);
	}

	/**
	 * @param c
	 */
	public boolean isControlWith(final char c) {
		return this.isControlCharacter() && '@' + this.data[0] == c;
	}

	public boolean isEscapeSequence() {
		return this.data.length > 1 && this.data[0] == ESC;
	}

	/**
	 * @param term
	 */
	public void toString(final Terminal term) {
		if (this.terminalType.isKnownEscapeSequence(this)) {
			this.terminalType.getVtKeyOrResponse(this).toString(term);
			return;
		}

		/*
		 * Fallback implementation.
		 */
		term.setTextAttributes(BRIGHT_RED, term.getDefaultBackground(), BOLD);
		term.print('[');
		term.setTextAttributes(NORMAL);

		for (int i = 0, n = this.data.length; i < n; i++) {
			toHumanReadable(term, this.data[i]);
			if (i == n - 1) {
				/*
				 * Don't print the space after the last item.
				 */
				continue;
			}
			term.print(' ');
		}

		term.setTextAttributes(BRIGHT_RED, term.getDefaultBackground(), BOLD);
		term.print(']');
		term.setTextAttributes(NORMAL);
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof InputEvent
				&& Arrays.equals(this.data, ((InputEvent) obj).data);
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.data);
	}

	/**
	 * @see CharSequence#length()
	 */
	@Override
	public int length() {
		return this.data.length;
	}

	/**
	 * @see CharSequence#charAt(int)
	 */
	@Override
	public char charAt(final int index) {
		return this.data[index];
	}

	/**
	 * @see CharSequence#subSequence(int, int)
	 */
	@Override
	public CharSequence subSequence(final int start, final int end) {
		return start == 0 && end == this.data.length
				? this
				: String.valueOf(this.data).substring(start, end);
	}
}
