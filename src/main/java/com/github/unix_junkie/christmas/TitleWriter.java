/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.InputEvent.BELL;
import static com.github.unix_junkie.christmas.InputEvent.ESC;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum TitleWriter {
	ANSI("%d;%s" + ESC + '\\'),
	OLD_STYLE("%d;%s" + BELL),
	NONE;

	/**
	 * The ANSI escape sequence to use for title management.
	 */
	private final String sequence;

	private TitleWriter() {
		this(null);
	}

	/**
	 * @param sequence the ANSI escape sequence to use for title management.
	 */
	private TitleWriter(@Nullable final String sequence) {
		this.sequence = sequence;
	}

	/**
	 * Sets both <em>window</em> and <em>icon</em> title.
	 * For <em>X11</em>-based terminal emulators,
	 * this means setting both {@code WM_ICON_NAME} and {@code WM_NAME}.
	 *
	 * @param term
	 * @param title the window title.
	 */
	public void setTitle(@Nonnull final Terminal term, @Nullable final String title) {
		if (this.sequence == null) {
			return;
		}

		term.printOsc().printf(this.sequence, Integer.valueOf(0), title == null ? "" : title).flush();
	}

	/**
	 * Sets <em>icon</em> title. For <em>X11</em>-based terminal emulators,
	 * this means setting {@code WM_ICON_NAME} only.
	 *
	 * @param term
	 * @param title
	 */
	public void setWmIconName(@Nonnull final Terminal term, @Nullable final String title) {
		if (this.sequence == null) {
			return;
		}

		term.printOsc().printf(this.sequence, Integer.valueOf(1), title == null ? "" : title).flush();
	}

	/**
	 * Sets <em>window</em> title. For <em>X11</em>-based terminal emulators,
	 * this means setting {@code WM_NAME} only.
	 *
	 * @param term
	 * @param title
	 */
	public void setWmName(@Nonnull final Terminal term, final @Nullable String title) {
		if (this.sequence == null) {
			return;
		}

		term.printOsc().printf(this.sequence, Integer.valueOf(2), title == null ? "" : title).flush();
	}
}
