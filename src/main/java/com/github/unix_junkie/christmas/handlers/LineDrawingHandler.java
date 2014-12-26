/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static com.github.unix_junkie.christmas.Color.BLUE;
import static com.github.unix_junkie.christmas.Color.BRIGHT_GREEN;
import static com.github.unix_junkie.christmas.Color.BRIGHT_RED;
import static com.github.unix_junkie.christmas.Color.BRIGHT_YELLOW;
import static com.github.unix_junkie.christmas.Color.CYAN;
import static com.github.unix_junkie.christmas.Color.WHITE;
import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class LineDrawingHandler extends AbstractInputEventHandler {
	public LineDrawingHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public LineDrawingHandler(final InputEventHandler next) {
		super(next);
	}

	/**
	 * @see InputEventHandler#handle(Terminal, List)
	 */
	@Override
	public void handle(final Terminal term, final List<InputEvent> events) {
		if (this.next != null) {
			this.next.handle(term, events);
		}

		for (final InputEvent event : events) {
			if (event.isControlWith('D')) {
				term.invokeLater(() -> {
					term.clear();
					lineDrawingUnicode(term);
				});
			} else if (event.isControlWith('F')) {
				term.invokeLater(() -> {
					term.clear();
					lineDrawingVt100(term);
				});
			} else if (event.isControlWith('G')) {
				term.invokeLater(() -> {
					term.clear();
					lineDrawingSunColor(term);
				});
			} else if (event.isControlWith('H')) {
				term.invokeLater(() -> {
					term.clear();
					lineDrawingCp437(term);
				});
			} else if (event.isControlWith('J')) {
				term.invokeLater(() -> {
					term.clear();
					lineDrawingCp866(term);
				});
			} else if (event.isControlWith('K')) {
				term.invokeLater(() -> {
					term.clear();
					lineDrawingKoi8r(term);
				});
			}
		}
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^D for Unicode line-drawing characters demo.");
		term.println("Type ^F for VT100 line-drawing characters demo.");
		term.println("Type ^G for sun-color line-drawing characters demo.");
		term.println("Type ^H for CP437 line-drawing characters demo.");
		term.println("Type ^J for CP866 line-drawing characters demo.");
		term.println("Type ^K for KOI8-R line-drawing characters demo.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}

	/**
	 * @param length
	 * @return a string of length <em>n</em> filled with spaces.
	 */
	private static String emptyString(final int length) {
		final char cs[] = new char[length];
		for (int i = 0; i < length; i++) {
			cs[i] = ' ';
		}
		return String.valueOf(cs);
	}

	/**
	 * @param charsetName
	 * @return the full contents of an 8-bit codepage.
	 */
	private static String getEightBitContents(final String charsetName) {
		final int length = 256;
		final byte bytes[] = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) i;
		}

		try {
			return new String(bytes, charsetName);
		} catch (final UnsupportedEncodingException uee) {
			return emptyString(length);
		}
	}

	private static String getSunColorContents() {
		final int firstIndex = 0x90;
		final int lastIndex = 0x9A;

		final int length = lastIndex - firstIndex + 1;
		final byte bytes[] = new byte[length];
		for (int i = 0; i < length; i++) {
			bytes[i] = (byte) (firstIndex + i);
		}

		try {
			return new String(bytes, "ISO8859-1");
		} catch (final UnsupportedEncodingException uee) {
			return emptyString(length);
		}
	}

	/**
	 * @param i
	 * @param padUpToLength
	 * @param addPrefix
	 */
	private static CharSequence toHexString(final int i, final int padUpToLength, final boolean addPrefix) {
		final String s0 = Integer.toHexString(i);
		final StringBuilder s1 = new StringBuilder();
		if (addPrefix) {
			s1.append("0x");
		}
		for (int j = 0; j < padUpToLength - s0.length(); j++) {
			s1.append('0');
		}
		return s1.append(s0);
	}


	/**
	 * <p>
	 * In order to see line-drawing characters when logging in to a UNIX
	 * from Windows using Microsoft Telnet, one need to issue
	 * <pre>
	 * $ <b>export LANG=ru_RU.CP866</b>
	 * $ <b>export LC_ALL=${LANG}</b>
	 * </pre>
	 * </p>
	 *
	 * @param term
	 */
	static void lineDrawingUnicode(final Terminal term) {
		term.setTextAttributes(BRIGHT_YELLOW, BLUE);
		term.println("Unicode line-drawing characters:");

		term.setTextAttributes(CYAN, BLUE, NORMAL);
		for (char i = '\u2500'; i <= '\u2590'; ) {
			for (char j = 0x0; j <= 0xf; j++) {
				term.print((char) (i + j));
			}
			term.println();
			i += 0x10;
		}

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 * @see <a href = "http://www.in-ulm.de/~mascheck/various/alternate_charset/">http://www.in-ulm.de/~mascheck/various/alternate_charset/</a>
	 */
	static void lineDrawingVt100(final Terminal term) {
		term.setTextAttributes(BRIGHT_YELLOW, BLUE);
		term.println("VT100 alternate character set:");

		term.setTextAttributes(CYAN, BLUE);
		term.startAlternateCs();
		for (char i = 0x60; i <= 0x70; ) {
			for (char j = 0x0; j <= 0xf; j++) {
				term.print((char) (i + j));
			}
			term.println();
			i += 0x10;
		}
		term.stopAlternateCs();

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * <p>Solaris console (<tt>sun-color</tt>) doesn't support
	 * VT100 alternate character set, but has 11 single-line
	 * characters with codes 90..9A. The rest of the characters
	 * are distributed according to ISO8859-1</p>
	 *
	 * @param term
	 */
	static void lineDrawingSunColor(final Terminal term) {
		term.setTextAttributes(BRIGHT_YELLOW, BLUE);
		term.println("sun-color line-drawing characters:");

		term.setTextAttributes(CYAN, BLUE);
		term.println(getSunColorContents());

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 */
	static void lineDrawingCp437(final Terminal term) {
		dumpCodepage(term, "IBM437");

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 */
	static void lineDrawingCp866(final Terminal term) {
		dumpCodepage(term, "IBM866");

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 */
	static void lineDrawingKoi8r(final Terminal term) {
		dumpCodepage(term, "KOI8-R");

		term.setTextAttributes(NORMAL);
		term.flush();
	}

	/**
	 * @param term
	 * @param charsetName
	 */
	private static void dumpCodepage(final Terminal term, final String charsetName) {
		term.setTextAttributes(BRIGHT_YELLOW, BLUE);
		term.println(charsetName + " line-drawing characters:");

		final String chars = getEightBitContents(charsetName);
		for (int i = 0x00; i <= 0xf0; ) {
			term.setTextAttributes(BRIGHT_GREEN, BLUE);
			term.print(toHexString(i, 2, true));
			term.setTextAttributes(CYAN, BLUE);
			for (int j = 0x0; j <= 0xf; j++) {
				if (j != 0) {
					term.print("    ");
				}
				term.print(chars.charAt(i + j));
			}
			term.println();

			for (int j = 0x0; j <= 0xf; j++) {
				final boolean emphasize = chars.charAt(i + j) > 0xff;
				term.setTextAttributes(emphasize ? BRIGHT_RED : WHITE, BLUE);
				term.print(' ');
				term.print(toHexString(chars.charAt(i + j), 4, false));
			}
			term.println();
			i += 0x10;
		}
	}
}
