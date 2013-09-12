/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.BrightBackgroundSupport.BLINK_AND_AIXTERM;
import static com.github.unix_junkie.christmas.BrightForegroundSupport.AIXTERM_ONLY;
import static com.github.unix_junkie.christmas.BrightForegroundSupport.BOLD_AND_AIXTERM;
import static com.github.unix_junkie.christmas.BrightForegroundSupport.BOLD_ONLY;
import static com.github.unix_junkie.christmas.Dimension._80X24;
import static com.github.unix_junkie.christmas.Dimension._80X25;
import static com.github.unix_junkie.christmas.InputEvent.ESC;
import static com.github.unix_junkie.christmas.TitleWriter.NONE;
import static com.github.unix_junkie.christmas.TitleWriter.OLD_STYLE;
import static com.github.unix_junkie.christmas.VtKey.DELETE;
import static com.github.unix_junkie.christmas.VtKey.DOWN;
import static com.github.unix_junkie.christmas.VtKey.END;
import static com.github.unix_junkie.christmas.VtKey.F1;
import static com.github.unix_junkie.christmas.VtKey.F10;
import static com.github.unix_junkie.christmas.VtKey.F11;
import static com.github.unix_junkie.christmas.VtKey.F12;
import static com.github.unix_junkie.christmas.VtKey.F2;
import static com.github.unix_junkie.christmas.VtKey.F3;
import static com.github.unix_junkie.christmas.VtKey.F4;
import static com.github.unix_junkie.christmas.VtKey.F5;
import static com.github.unix_junkie.christmas.VtKey.F6;
import static com.github.unix_junkie.christmas.VtKey.F7;
import static com.github.unix_junkie.christmas.VtKey.F8;
import static com.github.unix_junkie.christmas.VtKey.F9;
import static com.github.unix_junkie.christmas.VtKey.HOME;
import static com.github.unix_junkie.christmas.VtKey.INSERT;
import static com.github.unix_junkie.christmas.VtKey.LEFT;
import static com.github.unix_junkie.christmas.VtKey.PAGE_DOWN;
import static com.github.unix_junkie.christmas.VtKey.PAGE_UP;
import static com.github.unix_junkie.christmas.VtKey.RIGHT;
import static com.github.unix_junkie.christmas.VtKey.UP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Function;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum TerminalType {
	ANSI(			"ansi",			_80X24,	false,	BOLD_ONLY,	BrightBackgroundSupport.NONE,	NONE),
	DTTERM(			"dtterm",		_80X24,	true,	BOLD_AND_AIXTERM,	BrightBackgroundSupport.AIXTERM_ONLY,			OLD_STYLE),
	/**
	 * SunOS rxvt reports TERM=kterm.
	 */
	KTERM(			"kterm",			_80X24,	true,	BOLD_AND_AIXTERM,	BLINK_AND_AIXTERM,			OLD_STYLE),
	LINUX(			"linux",			_80X25,	true,	BOLD_AND_AIXTERM,	BLINK_AND_AIXTERM,			NONE),
	RXVT(			"rxvt",			_80X24,	true,	BOLD_AND_AIXTERM,	BLINK_AND_AIXTERM,			TitleWriter.ANSI),
	RXVT_UNICODE(		"rxvt-unicode",		_80X24,	true,	AIXTERM_ONLY,		BLINK_AND_AIXTERM,			TitleWriter.ANSI),
	RXVT_UNICODE_256COLOR(	"rxvt-unicode-256color",	_80X24,	true,	AIXTERM_ONLY,		BLINK_AND_AIXTERM,			TitleWriter.ANSI),
	RXVT_CYGWIN(		"rxvt-cygwin",		_80X24,	true,	AIXTERM_ONLY,		BLINK_AND_AIXTERM,			TitleWriter.ANSI),
	RXVT_CYGWIN_NATIVE(	"rxvt-cygwin-native",	_80X24,	true,	AIXTERM_ONLY,		BLINK_AND_AIXTERM,			TitleWriter.ANSI),
	SCOANSI(			"scoansi",		_80X24,	true,	AIXTERM_ONLY,		BrightBackgroundSupport.AIXTERM_ONLY,	TitleWriter.ANSI),
	SCREEN(			"screen",		_80X24,	true,	BOLD_AND_AIXTERM,	BrightBackgroundSupport.AIXTERM_ONLY,	OLD_STYLE),
	SCREEN_LINUX(		"screen.linux",		_80X25,	true,	BOLD_AND_AIXTERM,	BLINK_AND_AIXTERM,			TitleWriter.ANSI),
	SUN_CMD(			"sun-cmd",		_80X24,	false,	BOLD_AND_AIXTERM,	BrightBackgroundSupport.AIXTERM_ONLY,	TitleWriter.ANSI),
	SUN_COLOR(		"sun-color",		_80X25,	false,	BOLD_AND_AIXTERM,	BrightBackgroundSupport.AIXTERM_ONLY,	NONE),
	VT52(			"vt52",			_80X24,	false,	BOLD_ONLY,		BrightBackgroundSupport.NONE,		NONE),
	VT100(			"vt100",			_80X24,	false,	BOLD_ONLY,		BrightBackgroundSupport.NONE,		NONE),
	VT320(			"vt320",			_80X24,	true,	BOLD_AND_AIXTERM,	BrightBackgroundSupport.AIXTERM_ONLY,	NONE),
	VTNT(			"vtnt",			_80X25,	false,	BOLD_ONLY,		BrightBackgroundSupport.NONE,		NONE),
	XTERM(			"xterm",			_80X24,	true,	AIXTERM_ONLY,		BrightBackgroundSupport.AIXTERM_ONLY,	TitleWriter.ANSI),
	XTERM_COLOR(		"xterm-color",		_80X24,	true,	AIXTERM_ONLY,		BrightBackgroundSupport.AIXTERM_ONLY,	TitleWriter.ANSI),
	XTERM_16COLOR(		"xterm-16color",		_80X24,	true,	AIXTERM_ONLY,		BrightBackgroundSupport.AIXTERM_ONLY,	TitleWriter.ANSI),
	XTERM_256COLOR(		"xterm-256color",	_80X24,	true,	AIXTERM_ONLY,		BrightBackgroundSupport.AIXTERM_ONLY,	TitleWriter.ANSI),
	CYGWIN(			"cygwin",		_80X25,	false,	BOLD_AND_AIXTERM,	BLINK_AND_AIXTERM,			OLD_STYLE),
	;

	static {
		/*
		 * This can't be done in a constructor,
		 * because enum members are not yet known
		 * during the constructor execution.
		 */
		for (final TerminalType type : values()) {
			type.registerEscapeSequences();
		}
	}

	private final String term;

	private final Dimension defaultSize;

	private final boolean canUpdateLowerRightCell;

	private final BrightForegroundSupport brightForegroundSupport;

	private final BrightBackgroundSupport brightBackgroundSupport;

	private final TitleWriter titleWriter;

	private final Map<InputEvent, VtKey> knownEscapeSequences = new HashMap<InputEvent, VtKey>();

	/**
	 * @todo Rewrite using prefix tree.
	 */
	private final List<ListEntry> knownVtResponses = new ArrayList<ListEntry>();

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 */
	private static final class ListEntry {
		/**
		 * Key.
		 */
		private final Pattern key;

		/**
		 * Value.
		 */
		private final Function<InputEvent, ? extends VtResponse> factory;

		/**
		 * @param key
		 * @param factory
		 */
		ListEntry(final Pattern key, final Function<InputEvent, ? extends VtResponse> factory) {
			this.key = key;
			this.factory = factory;
		}

		Pattern getKey() {
			return this.key;
		}

		public Function<InputEvent, ? extends VtResponse> getFactory() {
			return this.factory;
		}
	}

	/**
	 * @param term
	 * @param defaultSize
	 * @param canUpdateLowerRightCell
	 * @param brightForegroundSupport
	 * @param brightBackgroundSupport
	 * @param titleWriter
	 */
	private TerminalType(final String term,
			final Dimension defaultSize,
			final boolean canUpdateLowerRightCell,
			final BrightForegroundSupport brightForegroundSupport,
			final BrightBackgroundSupport brightBackgroundSupport,
			final TitleWriter titleWriter) {
		this.term = term;
		this.defaultSize = defaultSize;
		this.canUpdateLowerRightCell = canUpdateLowerRightCell;
		this.brightForegroundSupport = brightForegroundSupport;
		this.brightBackgroundSupport = brightBackgroundSupport;
		this.titleWriter = titleWriter;
	}

	private void registerEscapeSequences() {
		/*
		 * All terminals.
		 */
		this.registerVtResponse(new Function<InputEvent, VtTerminalSize>() {
			/**
			 * @param from
			 */
			@Override
			public VtTerminalSize apply(final InputEvent from) {
				return new VtTerminalSize(from);
			}
		}, VtTerminalSize.PATTERN);
		this.registerVtResponse(new Function<InputEvent, VtCursorLocation>() {
			/**
			 * @param from
			 */
			@Override
			public VtCursorLocation apply(final InputEvent from) {
				return new VtCursorLocation(from);
			}
		}, VtCursorLocation.PATTERN);

		/*
		 * On a per-terminal basis.
		 */
		switch (this) {
		case XTERM:
		case XTERM_COLOR:
		case XTERM_16COLOR:
		case XTERM_256COLOR:
			this.registerOldFunctionKeys(); // PuTTY sends old function keys by default
			this.registerLinuxFunctionKeys(); // PuTTY can also send linux function keys
			this.registerVt100FunctionKeys(); // PuTTY can also send VT100 function keys
			this.registerScoFunctionKeys(); // PuTTY can also send SCO function keys
			this.registerSunFunctionKeys(); // XTerm can send Sun function keys
			this.registerAnsiFunctionKeys();

			/*
			 * SunOS xterm
			 */
			this.registerEscapeSequence(F11, ESC, '[', '5', '7', '~');
			this.registerEscapeSequence(F12, ESC, '[', '5', '8', '~');

			this.registerAnsiKeypad();
			this.registerAnsiApplicationKeypad(); // XTerm in application keypad mode.
			this.registerRxvtKeypad(); // PuTTY can be switched to RXVT keypad mode.
			this.registerScoKeypad(); // PuTTY can be switched to SCO keypad mode.

			this.registerCursorKeys();
			this.registerApplicationCursorKeys(); // XTerm and PuTTY can also send application cursor keys
			break;
		case LINUX:
		case CYGWIN:
			this.registerLinuxFunctionKeys();
			//$FALL-THROUGH$
		case ANSI:
		case SCREEN:
		case SCREEN_LINUX:
		case VTNT:
			this.registerAnsiFunctionKeys();

			this.registerAnsiKeypad();

			this.registerCursorKeys();
			break;
		case DTTERM:
			/*
			 * Home/End/PgUp/PgDn don't work in dtterm
			 * F11-F12 don't work in dtterm.
			 */
			this.registerOldFunctionKeys();
			this.registerAnsiFunctionKeys();

			this.registerCursorKeys();
			this.registerApplicationCursorKeys(); // DtTerm can also send application cursor keys
			break;
		case SUN_COLOR:
			this.registerSunFunctionKeys();

			this.registerSunKeypad();

			this.registerCursorKeys();
			break;
		case SUN_CMD:
			/*
			 * Home/End/PgUp/PgDn don't work in sun-cmd (shelltool)
			 */
			this.registerSunFunctionKeys();

			this.registerCursorKeys();
			break;
		case RXVT:
		case RXVT_UNICODE:
		case RXVT_UNICODE_256COLOR:
		case RXVT_CYGWIN:
		case RXVT_CYGWIN_NATIVE:
		case KTERM:
			this.registerOldFunctionKeys();
			this.registerAnsiFunctionKeys();

			this.registerAnsiKeypad();
			this.registerRxvtKeypad();

			this.registerCursorKeys();
			break;
		case VT320:
			this.registerAnsiFunctionKeys();

			this.registerAnsiKeypad();
			//$FALL-THROUGH$
		case VT100:
			this.registerVt100FunctionKeys();

			this.registerCursorKeys();
			break;
		case VT52:
			/**
			 * @todo Implement.
			 */
			break;
		case SCOANSI:
			this.registerScoFunctionKeys();

			this.registerScoKeypad();

			this.registerCursorKeys();
			break;
		default:
			break;
		}
	}

	private void registerApplicationCursorKeys() {
		this.registerEscapeSequence(UP, ESC, 'O', 'A');
		this.registerEscapeSequence(DOWN, ESC, 'O', 'B');
		this.registerEscapeSequence(RIGHT, ESC, 'O', 'C');
		this.registerEscapeSequence(LEFT, ESC, 'O', 'D');
	}

	private void registerCursorKeys() {
		this.registerEscapeSequence(UP, ESC, '[', 'A');
		this.registerEscapeSequence(DOWN, ESC, '[', 'B');
		this.registerEscapeSequence(RIGHT, ESC, '[', 'C');
		this.registerEscapeSequence(LEFT, ESC, '[', 'D');
	}

	private void registerAnsiKeypad() {
		this.registerEscapeSequence(HOME, ESC, '[', '1', '~');
		this.registerEscapeSequence(INSERT, ESC, '[', '2', '~');
		this.registerEscapeSequence(DELETE, ESC, '[', '3', '~');
		this.registerEscapeSequence(END, ESC, '[', '4', '~');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', '5', '~');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', '6', '~');
	}

	private void registerAnsiApplicationKeypad() {
		this.registerEscapeSequence(HOME, ESC, 'O', 'H');
		this.registerEscapeSequence(END, ESC, 'O', 'F');
	}

	private void registerAnsiFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, 'O', 'P');
		this.registerEscapeSequence(F2, ESC, 'O', 'Q');
		this.registerEscapeSequence(F3, ESC, 'O', 'R');
		this.registerEscapeSequence(F4, ESC, 'O', 'S');

		this.registerEscapeSequence(F5, ESC, '[', '1', '5', '~');
		this.registerEscapeSequence(F6, ESC, '[', '1', '7', '~');
		this.registerEscapeSequence(F7, ESC, '[', '1', '8', '~');
		this.registerEscapeSequence(F8, ESC, '[', '1', '9', '~');
		this.registerEscapeSequence(F9, ESC, '[', '2', '0', '~');
		this.registerEscapeSequence(F10, ESC, '[', '2', '1', '~');
		this.registerEscapeSequence(F11, ESC, '[', '2', '3', '~');
		this.registerEscapeSequence(F12, ESC, '[', '2', '4', '~');
	}

	private void registerRxvtKeypad() {
		/*
		 * Consistent with ANSI.
		 */
		this.registerEscapeSequence(DELETE, ESC, '[', '3', '~');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', '5', '~');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', '6', '~');

		/*
		 * RXVT in SunOS and Linux
		 */
		this.registerEscapeSequence(HOME, ESC, '[', '7', '~');
		this.registerEscapeSequence(END, ESC, '[', '8', '~');

		/*
		 * PuTTY in RXVT keypad mode.
		 */
		this.registerEscapeSequence(HOME, ESC, '[', 'H');
		this.registerEscapeSequence(END, ESC, 'O', 'w');
	}

	private void registerVt100FunctionKeys() {
		this.registerEscapeSequence(F5, ESC, 'O', 'T');
		this.registerEscapeSequence(F6, ESC, 'O', 'U');
		this.registerEscapeSequence(F7, ESC, 'O', 'V');
		this.registerEscapeSequence(F8, ESC, 'O', 'W');
		this.registerEscapeSequence(F9, ESC, 'O', 'X');
		this.registerEscapeSequence(F10, ESC, 'O', 'Y');
		this.registerEscapeSequence(F11, ESC, 'O', 'Z');
		this.registerEscapeSequence(F12, ESC, 'O', '[');
	}

	private void registerLinuxFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', '[', 'A');
		this.registerEscapeSequence(F2, ESC, '[', '[', 'B');
		this.registerEscapeSequence(F3, ESC, '[', '[', 'C');
		this.registerEscapeSequence(F4, ESC, '[', '[', 'D');
		this.registerEscapeSequence(F5, ESC, '[', '[', 'E');
	}

	private void registerOldFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', '1', '1', '~');
		this.registerEscapeSequence(F2, ESC, '[', '1', '2', '~');
		this.registerEscapeSequence(F3, ESC, '[', '1', '3', '~');
		this.registerEscapeSequence(F4, ESC, '[', '1', '4', '~');
	}

	private void registerSunKeypad() {
		this.registerEscapeSequence(HOME, ESC, '[', '2', '1', '4', 'z');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', '2', '1', '6', 'z');
		this.registerEscapeSequence(END, ESC, '[', '2', '2', '0', 'z');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', '2', '2', '2', 'z');
	}

	private void registerSunFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', '2', '2', '4', 'z');
		this.registerEscapeSequence(F2, ESC, '[', '2', '2', '5', 'z');
		this.registerEscapeSequence(F3, ESC, '[', '2', '2', '6', 'z');
		this.registerEscapeSequence(F4, ESC, '[', '2', '2', '7', 'z');
		this.registerEscapeSequence(F5, ESC, '[', '2', '2', '8', 'z');
		this.registerEscapeSequence(F6, ESC, '[', '2', '2', '9', 'z');
		this.registerEscapeSequence(F7, ESC, '[', '2', '3', '0', 'z');
		this.registerEscapeSequence(F8, ESC, '[', '2', '3', '1', 'z');
		this.registerEscapeSequence(F9, ESC, '[', '2', '3', '2', 'z');
		this.registerEscapeSequence(F10, ESC, '[', '2', '3', '3', 'z');
		this.registerEscapeSequence(F11, ESC, '[', '2', '3', '4', 'z');
		this.registerEscapeSequence(F12, ESC, '[', '2', '3', '5', 'z');

		/*
		 * XTerm in Sun function key mode sends F11 and F12 slightly differently.
		 */
		this.registerEscapeSequence(F11, ESC, '[', '1', '9', '2', 'z');
		this.registerEscapeSequence(F12, ESC, '[', '1', '9', '3', 'z');
	}

	private void registerScoKeypad() {
		this.registerEscapeSequence(END, ESC, '[', 'F');
		this.registerEscapeSequence(PAGE_DOWN, ESC, '[', 'G');
		this.registerEscapeSequence(HOME, ESC, '[', 'H');
		this.registerEscapeSequence(PAGE_UP, ESC, '[', 'I');
		this.registerEscapeSequence(INSERT, ESC, '[', 'L');
	}

	private void registerScoFunctionKeys() {
		this.registerEscapeSequence(F1, ESC, '[', 'M');
		this.registerEscapeSequence(F2, ESC, '[', 'N');
		this.registerEscapeSequence(F3, ESC, '[', 'O');
		this.registerEscapeSequence(F4, ESC, '[', 'P');
		this.registerEscapeSequence(F5, ESC, '[', 'Q');
		this.registerEscapeSequence(F6, ESC, '[', 'R');
		this.registerEscapeSequence(F7, ESC, '[', 'S');
		this.registerEscapeSequence(F8, ESC, '[', 'T');
		this.registerEscapeSequence(F9, ESC, '[', 'U');
		this.registerEscapeSequence(F10, ESC, '[', 'V');
		this.registerEscapeSequence(F11, ESC, '[', 'W');
		this.registerEscapeSequence(F12, ESC, '[', 'X');
	}

	/**
	 * @param vtKey
	 * @param data
	 */
	private void registerEscapeSequence(final VtKey vtKey, final char ... data) {
		this.registerEscapeSequence(vtKey, new InputEvent(this, data));
	}

	/**
	 * @param vtKey
	 * @param event
	 */
	private void registerEscapeSequence(final VtKey vtKey, final InputEvent event) {
		this.knownEscapeSequences.put(event, vtKey);
	}

	/**
	 * @param vtResponseFactory
	 * @param pattern
	 */
	private void registerVtResponse(final Function<InputEvent, ? extends VtResponse> vtResponseFactory, final Pattern pattern) {
		this.knownVtResponses.add(new ListEntry(pattern, vtResponseFactory));
	}

	/**
	 * @param event
	 */
	public boolean isKnownEscapeSequence(final InputEvent event) {
		return event.isEscapeSequence() && (this.knownEscapeSequences.containsKey(event) || this.isKnownVtResponse(event));
	}

	/**
	 * @param event
	 */
	private boolean isKnownVtResponse(final InputEvent event) {
		for (final ListEntry entry : this.knownVtResponses) {
			if (entry.getKey().matcher(event).matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param event
	 */
	public VtKeyOrResponse getVtKeyOrResponse(final InputEvent event) {
		final VtKey vtKey = this.knownEscapeSequences.get(event);
		return vtKey != null ? vtKey : this.getVtResponse(event);
	}

	/**
	 * @param event
	 */
	private VtResponse getVtResponse(final InputEvent event) {
		for (final ListEntry entry : this.knownVtResponses) {
			if (entry.getKey().matcher(event).matches()) {
				return entry.getFactory().apply(event);
			}
		}
		return null;
	}

	public TitleWriter getTitleWriter() {
		return this.titleWriter;
	}

	/**
	 * @return the default size for this terminal type (useful in case
	 *         an attempt to determine one programmatically failed)
	 */
	public final Dimension getDefaultSize() {
		return this.defaultSize;
	}

	/**
	 * @return whether printing any character at the lower right corner
	 *         is "safe", i. e. doesn't cause the whole screen
	 *         to scroll one line up.
	 */
	public final boolean canUpdateLowerRightCell() {
		return this.canUpdateLowerRightCell;
	}

	public BrightForegroundSupport getBrightForegroundSupport() {
		return this.brightForegroundSupport;
	}

	public BrightBackgroundSupport getBrightBackgroundSupport() {
		return this.brightBackgroundSupport;
	}

	public boolean isBrightBackgroundSupported() {
		return this.brightBackgroundSupport.isBrightColorSupported();
	}

	/**
	 * @param term
	 */
	public static TerminalType safeValueOf(final String term) {
		for (final TerminalType type : values()) {
			if (type.term.equals(term)) {
				return type;
			}
		}
		return ANSI;
	}

	/**
	 * @see Enum#toString()
	 */
	@Override
	public String toString() {
		return this.term;
	}
}
