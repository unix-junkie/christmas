/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_DOUBLE_DOWN_AND_LEFT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_DOUBLE_DOWN_AND_RIGHT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_DOUBLE_HORIZONTAL;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_DOUBLE_UP_AND_LEFT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_DOUBLE_UP_AND_RIGHT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_DOUBLE_VERTICAL;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_LIGHT_DOWN_AND_LEFT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_LIGHT_DOWN_AND_RIGHT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_LIGHT_HORIZONTAL;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_LIGHT_UP_AND_LEFT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_LIGHT_UP_AND_RIGHT;
import static com.github.unix_junkie.christmas.LineDrawingCharacters.BOX_DRAWINGS_LIGHT_VERTICAL;
import static com.github.unix_junkie.christmas.TerminalType.SUN_COLOR;
import static java.util.Arrays.asList;

import com.github.unix_junkie.christmas.wt.BorderStyle;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum LineDrawingMethod {
	UNICODE {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			switch (term.getType()) {
			case ANSI:
			case LINUX:
			case RXVT_UNICODE:
			case RXVT_UNICODE_256COLOR:
			case SCOANSI:
			case SCREEN:
			case SCREEN_LINUX:
			case VT52:
			case VT100:
			case VT320:
			case VTNT:
			case XTERM:
			case XTERM_COLOR:
			case XTERM_16COLOR:
			case XTERM_256COLOR:
			case CYGWIN:
				return asList("UTF-8", "IBM437", "CP437", "IBM866", "CP866", "KOI8-R").contains(term.getEncoding());
			case DTTERM:
			case KTERM:
			case RXVT:
			case RXVT_CYGWIN:
			case RXVT_CYGWIN_NATIVE:
			case SUN_CMD:
			case SUN_COLOR:
			default:
				return false;
			}
		}

		/**
		 * @see LineDrawingMethod#getChar(LineDrawingConstants, BorderStyle)
		 */
		@Override
		public char getChar(final LineDrawingConstants character, final BorderStyle style) {
			switch (style) {
			case NONE:
				return ' ';
			case SINGLE:
			case SINGLE_RAISED:
			case SINGLE_LOWERED:
				switch (character) {
				case HORIZONTAL:
					return BOX_DRAWINGS_LIGHT_HORIZONTAL.getCharacter();
				case VERTICAL:
					return BOX_DRAWINGS_LIGHT_VERTICAL.getCharacter();
				case DOWN_AND_RIGHT:
					return BOX_DRAWINGS_LIGHT_DOWN_AND_RIGHT.getCharacter();
				case DOWN_AND_LEFT:
					return BOX_DRAWINGS_LIGHT_DOWN_AND_LEFT.getCharacter();
				case UP_AND_RIGHT:
					return BOX_DRAWINGS_LIGHT_UP_AND_RIGHT.getCharacter();
				case UP_AND_LEFT:
					return BOX_DRAWINGS_LIGHT_UP_AND_LEFT.getCharacter();
				}
				break;
			case DOUBLE:
			case DOUBLE_RAISED:
			case DOUBLE_LOWERED:
				switch (character) {
				case HORIZONTAL:
					return BOX_DRAWINGS_DOUBLE_HORIZONTAL.getCharacter();
				case VERTICAL:
					return BOX_DRAWINGS_DOUBLE_VERTICAL.getCharacter();
				case DOWN_AND_RIGHT:
					return BOX_DRAWINGS_DOUBLE_DOWN_AND_RIGHT.getCharacter();
				case DOWN_AND_LEFT:
					return BOX_DRAWINGS_DOUBLE_DOWN_AND_LEFT.getCharacter();
				case UP_AND_RIGHT:
					return BOX_DRAWINGS_DOUBLE_UP_AND_RIGHT.getCharacter();
				case UP_AND_LEFT:
					return BOX_DRAWINGS_DOUBLE_UP_AND_LEFT.getCharacter();
				}
				break;
			}

			return '?';
		}

		/**
		 * @see LineDrawingMethod#isAlternateCharset()
		 */
		@Override
		public boolean isAlternateCharset() {
			return false;
		}
	},
	VT100_LINES {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			switch (term.getType()) {
			case DTTERM:
			case KTERM:
			case RXVT:
			case RXVT_UNICODE:
			case RXVT_UNICODE_256COLOR:
			case RXVT_CYGWIN:
			case SCREEN:
			case SCREEN_LINUX:
			case VT320:
			case XTERM:
			case XTERM_COLOR:
			case XTERM_16COLOR:
			case XTERM_256COLOR:
			case CYGWIN:
				return true;
			case ANSI: // Neither telnet.exe nor HyperTerminal on Windows support this.
			case LINUX:
			case SCOANSI:
			case SUN_CMD:
			case SUN_COLOR:
			case VT52:
			case VT100:
			case VTNT:
			case RXVT_CYGWIN_NATIVE:
			default:
				return false;
			}
		}

		/**
		 * @see LineDrawingMethod#getChar(LineDrawingConstants, BorderStyle)
		 */
		@Override
		public char getChar(final LineDrawingConstants character, final BorderStyle style) {
			switch (style) {
			case NONE:
				return ' ';
			//$CASES-OMITTED$
			default:
				switch (character) {
				case HORIZONTAL:
					return 'q';
				case VERTICAL:
					return 'x';
				case DOWN_AND_RIGHT:
					return 'l';
				case DOWN_AND_LEFT:
					return 'k';
				case UP_AND_RIGHT:
					return 'm';
				case UP_AND_LEFT:
					return 'j';
				}
				break;
			}

			return '?';
		}

		/**
		 * @see LineDrawingMethod#isAlternateCharset()
		 */
		@Override
		public boolean isAlternateCharset() {
			return true;
		}
	},
	SUN_COLOR_LINES {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			return term.getType() == SUN_COLOR;
		}

		/**
		 * @see LineDrawingMethod#getChar(LineDrawingConstants, BorderStyle)
		 */
		@Override
		public char getChar(final LineDrawingConstants character, final BorderStyle style) {
			switch (style) {
			case NONE:
				return ' ';
			//$CASES-OMITTED$
			default:
				switch (character) {
				case HORIZONTAL:
					return '\u0097';
				case VERTICAL:
					return '\u0090';
				case DOWN_AND_RIGHT:
					return '\u009a';
				case DOWN_AND_LEFT:
					return '\u0092';
				case UP_AND_RIGHT:
					return '\u0093';
				case UP_AND_LEFT:
					return '\u0099';
				}
				break;
			}

			return '?';
		}

		/**
		 * @see LineDrawingMethod#isAlternateCharset()
		 */
		@Override
		public boolean isAlternateCharset() {
			return false;
		}
	},
	ASCII {
		/**
		 * @see LineDrawingMethod#supportedFor(Terminal)
		 */
		@Override
		public boolean supportedFor(final Terminal term) {
			/*
			 * Always supported regardless of terminal capabilities.
			 */
			return true;
		}

		/**
		 * @see LineDrawingMethod#getChar(LineDrawingConstants, BorderStyle)
		 */
		@Override
		public char getChar(final LineDrawingConstants character, final BorderStyle style) {
			switch (style) {
			case NONE:
				return ' ';
			case SINGLE:
			case SINGLE_RAISED:
			case SINGLE_LOWERED:
				switch (character) {
				case HORIZONTAL:
					return '-';
				case VERTICAL:
					return '|';
				case DOWN_AND_RIGHT:
				case DOWN_AND_LEFT:
				case UP_AND_RIGHT:
				case UP_AND_LEFT:
					return '+';
				}
				break;
			case DOUBLE:
			case DOUBLE_RAISED:
			case DOUBLE_LOWERED:
				switch (character) {
				case HORIZONTAL:
					return '=';
				case VERTICAL:
					return '|';
				case DOWN_AND_RIGHT:
				case DOWN_AND_LEFT:
				case UP_AND_RIGHT:
				case UP_AND_LEFT:
					return '+';
				}
				break;
			}

			return '?';
		}

		/**
		 * @see LineDrawingMethod#isAlternateCharset()
		 */
		@Override
		public boolean isAlternateCharset() {
			return false;
		}
	},
	;

	/**
	 * @param term
	 */
	public abstract boolean supportedFor(final Terminal term);

	/**
	 * @param character
	 * @param style
	 */
	public abstract char getChar(final LineDrawingConstants character, final BorderStyle style);

	public abstract boolean isAlternateCharset();
}
