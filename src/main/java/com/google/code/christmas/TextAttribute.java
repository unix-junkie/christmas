/*-
 * $Id$
 */
package com.google.code.christmas;

import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum TextAttribute {
	NORMAL,		// xterm+	iTerm.app+	Terminal.app+	PuTTY+	cmd.exe-cygwin+
	BOLD,		// xterm+	iTerm.app+	Terminal.app+	PuTTY+	cmd.exe-cygwin+
	FAINT,		// xterm-	iTerm.app-	Terminal.app-	PuTTY-	cmd.exe-cygwin-
	ITALIC,		// xterm-	iTerm.app-	Terminal.app-	PuTTY-	cmd.exe-cygwin-
	UNDERLINE,	// xterm+	iTerm.app+	Terminal.app+	PuTTY-	cmd.exe-cygwin±
	BLINK,		// xterm+	iTerm.app+	Terminal.app+	PuTTY±	cmd.exe-cygwin±
	BLINK_RAPID,	// xterm-	iTerm.app-	Terminal.app-	PuTTY±	cmd.exe-cygwin-
	INVERSE,		// xterm+	iTerm.app+	Terminal.app+	PuTTY+	cmd.exe-cygwin+
	CONCEAL,		// xterm+	iTerm.app-	Terminal.app+	PuTTY-	cmd.exe-cygwin+
	;

	private static final TextAttribute EMPTY[] = new TextAttribute[0];

	/**
	 * @param attributes
	 */
	public static TextAttribute[] toArray(@Nonnull final Collection<TextAttribute> attributes) {
		return attributes.toArray(EMPTY);
	}
}
