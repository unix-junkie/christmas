/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.InputEvent.ESC;
import static com.github.unix_junkie.christmas.LineDrawingMethod.ASCII;
import static com.github.unix_junkie.christmas.LineDrawingMethod.VT100_LINES;
import static com.github.unix_junkie.christmas.TerminalType.safeValueOf;
import static com.github.unix_junkie.christmas.TextAttribute.BLINK;
import static com.github.unix_junkie.christmas.TextAttribute.BOLD;
import static com.github.unix_junkie.christmas.TextAttribute.NORMAL;
import static java.lang.System.getProperty;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.unix_junkie.christmas.handlers.QuietTerminalSizeHandler;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class Terminal extends PrintWriter {
	private final TerminalType type;

	private final Reader in;

	final Thread sequenceTokenizer;

	@Nullable
	private Color defaultForeground;

	@Nullable
	private Color defaultBackground;

	@Nullable
	private Color lastForeground;

	@Nullable
	private Color lastBackground;

	private final EventQueueFactory eventQueueFactory = new EventQueueFactory();

	/**
	 * The background thread this executor is backed by is used for
	 * all terminal output (i.&nbsp;e. to schedule a terminal size request,
	 * print the terminal response, etc.).
	 */
	private final ExecutorService eventQueue = newSingleThreadExecutor(this.eventQueueFactory);

	{
		/*
		 * Submit an empty task in order to force the event queue
		 * to start.
		 */
		this.eventQueue.submit(new Runnable() {
			/**
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				// empty
			}
		});
	}

	private final QuietTerminalSizeHandler sizeHandler;

	/**
	 * @param term
	 * @param handler
	 * @throws UnsupportedEncodingException
	 */
	protected Terminal(final String term, final InputEventHandler handler)
	throws UnsupportedEncodingException {
		this(term, handler, System.in, System.out);
	}

	/**
	 * @param term
	 * @param handler
	 * @param ttyName
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected Terminal(final String term, final InputEventHandler handler, final String ttyName)
	throws FileNotFoundException, UnsupportedEncodingException {
		super(ttyName, getEncoding(safeValueOf(term)));
		this.type = safeValueOf(term);
		this.in = new FileReader(ttyName);

		final QuietTerminalSizeHandler probablySizeHandler = findSizeHandler(handler);
		final InputEventHandler rootHandler = probablySizeHandler == null
				? new QuietTerminalSizeHandler().append(handler)
				: handler;
		this.sizeHandler = probablySizeHandler == null
				? (QuietTerminalSizeHandler) rootHandler
				: probablySizeHandler;

		this.sequenceTokenizer = new SequenceTokenizer(this, rootHandler);
	}

	/**
	 * @param term
	 * @param handler
	 * @param in
	 * @param out
	 * @throws UnsupportedEncodingException
	 */
	protected Terminal(final String term, final InputEventHandler handler, final InputStream in, final OutputStream out)
	throws UnsupportedEncodingException {
		super(new OutputStreamWriter(out, getEncoding(safeValueOf(term))));
		this.type = safeValueOf(term);
		this.in = new InputStreamReader(in);

		final QuietTerminalSizeHandler probablySizeHandler = findSizeHandler(handler);
		final InputEventHandler rootHandler = probablySizeHandler == null
				? new QuietTerminalSizeHandler().append(handler)
				: handler;
		this.sizeHandler = probablySizeHandler == null
				? (QuietTerminalSizeHandler) rootHandler
				: probablySizeHandler;

		this.sequenceTokenizer = new SequenceTokenizer(this, rootHandler);
	}

	/**
	 * @param term
	 * @param handler
	 * @param socket
	 * @throws IOException
	 */
	public Terminal(final String term, final InputEventHandler handler, final Socket socket)
	throws IOException {
		this(term, handler, socket.getInputStream(), socket.getOutputStream());
	}

	public void start() {
		this.sequenceTokenizer.start();
	}

	/**
	 * @throws IOException
	 * @throws SocketException
	 */
	public int read() throws IOException {
		return this.in.read();
	}

	/**
	 * @param task
	 * @see javax.swing.SwingUtilities#invokeLater(Runnable)
	 */
	public void invokeLater(@Nonnull final Runnable task) {
		if (task == null) {
			throw new IllegalArgumentException();
		}

		this.eventQueue.submit(task);
	}

	/**
	 * @param <T>
	 * @param task
	 * @see javax.swing.SwingUtilities#invokeLater(Runnable)
	 */
	public <T> Future<T> invokeLater(@Nonnull final Callable<T> task) {
		if (task == null) {
			throw new IllegalArgumentException();
		}

		return this.eventQueue.submit(task);
	}

	public boolean isEventQueue() {
		return this.eventQueueFactory.isEventQueue();
	}

	private void checkIfEventQueue() {
		if (!this.isEventQueue()) {
			throw new IllegalStateException("Thread " + currentThread().getName() + " is not an event queue");
		}
	}

	/**
	 * @see PrintWriter#println()
	 */
	@Override
	public void println() {
		this.checkIfEventQueue();
		super.println();
	}

	/**
	 * @see PrintWriter#write(int)
	 */
	@Override
	public void write(final int c) {
		this.checkIfEventQueue();
		super.write(c);
	}

	/**
	 * @see PrintWriter#write(char[], int, int)
	 */
	@Override
	public void write(final char[] buf, final int off, final int len) {
		this.checkIfEventQueue();
		super.write(buf, off, len);
	}

	/**
	 * @see PrintWriter#write(String, int, int)
	 */
	@Override
	public void write(final String s, final int off, final int len) {
		this.checkIfEventQueue();
		super.write(s, off, len);
	}

	/**
	 * @see PrintWriter#close()
	 */
	@Override
	public void close() {
		/*
		 * The reading while-loop should be modified
		 * before enabling this.
		 * Otherwise, an IOE: Stream closed is thrown.
		 */
		if (false) {
			try {
				this.in.close();
			} catch (final IOException ioe) {
				ioe.printStackTrace();
			}
		}

		super.close();
	}

	public TerminalType getType() {
		return this.type;
	}

	private Terminal printEsc() {
		this.print(ESC);
		return this;
	}

	public Terminal requestTerminalSize() {
		this.printCsi().print("18t");
		return this;
	}

	public Terminal requestCursorLocation() {
		this.printCsi().print("6n");
		return this;
	}

	/**
	 * @param x
	 * @param y
	 */
	public Terminal setCursorLocation(final int x, final int y) {
		if (x <= 0 || y <= 0) {
			throw new IllegalArgumentException(String.format("[%d; %d]", Integer.valueOf(x), Integer.valueOf(y)));
		}

		this.printCsi();
		this.print(y);
		this.print(';');
		this.print(x);
		this.print('H');
		return this;
	}

	/**
	 * VT100 alternate character set is not supported by PuTTY.
	 */
	public Terminal startAlternateCs() {
		if (VT100_LINES.supportedFor(this)) {
			this.printEsc().print("(0");
		}

		return this;
	}

	/**
	 * VT100 alternate character set is not supported by PuTTY.
	 */
	public Terminal stopAlternateCs() {
		if (VT100_LINES.supportedFor(this)) {
			this.printEsc().print("(B");
		}

		return this;
	}

	/**
	 * @param foreground if {@code null}, no foreground color is set.
	 */
	public Terminal setForeground(final Color foreground) {
		return this.setTextAttributes(foreground, null);
	}

	/**
	 * @param background if {@code null}, no background color is set.
	 */
	public Terminal setBackground(final Color background) {
		return this.setTextAttributes(null, background);
	}

	/**
	 * @param defaultForeground
	 */
	public Terminal setDefaultForeground(final Color defaultForeground) {
		this.defaultForeground = defaultForeground;
		return this.setForeground(defaultForeground);
	}

	/**
	 * @param defaultBackground
	 */
	public Terminal setDefaultBackground(final Color defaultBackground) {
		this.defaultBackground = defaultBackground;
		return this.setBackground(defaultBackground);
	}

	/**
	 * @param attributes
	 */
	public Terminal setTextAttributes(@Nonnull final Set<TextAttribute> attributes) {
		return this.setTextAttributes(TextAttribute.toArray(attributes));
	}

	/**
	 * @param attributes
	 */
	public Terminal setTextAttributes(@Nonnull final TextAttribute ... attributes) {
		return this.setTextAttributes(null, null, attributes);
	}

	/**
	 * @param foreground
	 * @param background
	 * @param attributes
	 */
	public Terminal setTextAttributes(@Nullable final Color foreground,
			@Nullable final Color background,
			@Nonnull final TextAttribute ... attributes) {
		/*
		 * Remember the last requested foreground and background.
		 */
		if (foreground != null) {
			this.lastForeground = foreground;
		}
		if (background != null) {
			this.lastBackground = background;
		}

		final Color effectiveForeground = foreground != null
				? foreground
				: this.lastForeground != null ? this.lastForeground : this.defaultForeground;
		final Color effectiveBackground = background != null
				? background
				: this.lastBackground != null ? this.lastBackground : this.defaultBackground;

		final Set<TextAttribute> effectiveAttributes = attributes.length == 0
				? EnumSet.noneOf(TextAttribute.class)
				: EnumSet.copyOf(asList(attributes));
		if (effectiveForeground != null && effectiveForeground.isBright()
				&& this.type.getBrightForegroundSupport().useBold()) {
			effectiveAttributes.add(BOLD);
		}
		if (effectiveBackground != null && effectiveBackground.isBright()
				&& this.type.getBrightBackgroundSupport().useBlink()) {
			effectiveAttributes.add(BLINK);
		}

		final StringBuilder s = new StringBuilder();

		/*
		 * If new text attributes have been supplied
		 * (and not just foreground or background changed),
		 * invalidate previous color and attribute settings.
		 *
		 * Also, invalidate previous settings if current
		 * foreground color is a dark one (so we may need
		 * to reset the BOLD flag)
		 *
		 * This should be done in a *separate* escape sequence.
		 */
		if (!effectiveAttributes.isEmpty() || effectiveForeground != null && effectiveForeground.isDark()) {
			this.printCsi();
			this.print(NORMAL.ordinal());
			this.print(";m");
		}

		/*
		 * Apply whatever attributes have been supplied except NORMAL:
		 */
		for (final TextAttribute attribute : effectiveAttributes) {
			if (attribute == NORMAL) {
				continue;
			}
			s.append(attribute.ordinal()).append(';');
		}

		if (effectiveForeground != null) {
			s.append(30 + effectiveForeground.darker().ordinal()).append(';');
			if (effectiveForeground.isBright()
					&& this.type.getBrightForegroundSupport().useAixTerm()) {
				s.append(90 + effectiveForeground.darker().ordinal()).append(';');
			}
		}
		if (effectiveBackground != null) {
			s.append(40 + effectiveBackground.darker().ordinal()).append(';');
			if (effectiveBackground.isBright()
					&& this.type.getBrightBackgroundSupport().useAixTerm()) {
				s.append(100 + effectiveBackground.darker().ordinal()).append(';');
			}
		}

		final int length = s.length();
		if (length != 0) {
			s.deleteCharAt(length - 1);

			this.printCsi();
			this.print(s);
			this.print('m');
		}

		return this;
	}

	/**
	 * @param title
	 */
	public Terminal setTitle(@Nullable final String title) {
		this.type.getTitleWriter().setTitle(this, title);
		return this;
	}

	/**
	 * Prints the <em>Control Sequence Introducer</em> (<em>CSI</em>).
	 *
	 * @return This terminal
	 */
	private Terminal printCsi() {
		this.printEsc();
		this.print('[');
		return this;
	}

	/**
	 * Prints the <em>Operating System Command</em> (<em>OSC</em>).
	 *
	 * @return This terminal
	 */
	Terminal printOsc() {
		this.printEsc();
		this.print(']');
		return this;
	}

	/**
	 * @param vtKeyOrResponse
	 * @return This terminal
	 */
	public Terminal print(final VtKeyOrResponse vtKeyOrResponse) {
		vtKeyOrResponse.toString(this);
		return this;
	}

	/**
	 * @param inputEvent
	 * @return This terminal
	 */
	public Terminal print(final InputEvent inputEvent) {
		inputEvent.toString(this);
		return this;
	}

	public Terminal restoreDefaultForeground() {
		return this.setForeground(this.defaultForeground);
	}

	public Terminal restoreDefaultBackground() {
		return this.setBackground(this.defaultBackground);
	}

	public Color getDefaultForeground() {
		return this.defaultForeground;
	}

	public Color getDefaultBackground() {
		return this.defaultBackground;
	}

	public Terminal clear() {
		this.restoreDefaultForeground().restoreDefaultBackground().setCursorLocation(1, 1).eraseInDisplay(EraseInDisplay.ERASE_BELOW).flush();
		return this;
	}

	/**
	 * @param visible
	 * @return This terminal
	 */
	public Terminal setToolbarVisible(final boolean visible) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(10);
		this.print(visible ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param visible
	 * @return This terminal
	 */
	public Terminal setCursorVisible(final boolean visible) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(25);
		this.print(visible ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param visible
	 * @return This terminal
	 */
	public Terminal setScrollbarVisible(final boolean visible) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(30);
		this.print(visible ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param enabled
	 */
	public Terminal setAutoWraparound(final boolean enabled) {
		/*
		 * DEC Private Mode Set/Reset
		 */
		this.printCsi().print('?');
		this.print(7);
		this.print(enabled ? 'h' : 'l');
		this.flush();

		return this;
	}

	/**
	 * @param enabled
	 */
	public Terminal setAutoLinefeed(final boolean enabled) {
		/*
		 * LNM: Automatic Newline (h)/Normal Linefeed (l)
		 */
		this.printCsi().print(20);
		this.print(enabled ? 'h' : 'l');
		this.flush();

		return this;
	}
	/**
	 * Can be invoked from any thread except for {@linkplain
	 * SequenceConsumer#isDispatchThread() SequenceConsumer Dispatch Thread}.
	 *
	 * @return this terminal's size
	 * @throws IllegalStateException if invoked from {@linkplain
	 *         SequenceConsumer#isDispatchThread() SequenceConsumer Dispatch Thread}
	 * @see SequenceConsumer#isDispatchThread()
	 */
	public Dimension getSize() {
		return this.sizeHandler.getTerminalSize(this);
	}

	public Dimension getDefaultSize() {
		return this.type.getDefaultSize();
	}

	public LineDrawingMethod getLineDrawingMethod() {
		for (final LineDrawingMethod method : LineDrawingMethod.values()) {
			if (method.supportedFor(this)) {
				return method;
			}
		}

		/*
		 * Fall back to ASCII
		 */
		return ASCII;
	}

	/**
	 * @param mode
	 */
	private Terminal eraseInDisplay(@Nonnull final EraseInDisplay mode) {
		this.printCsi().printf("%dJ", Integer.valueOf(mode.ordinal()));
		return this;
	}

	String getEncoding() {
		return getEncoding(this.type);
	}

	/**
	 * @param type
	 */
	private static String getEncoding(final TerminalType type) {
		switch (type) {
		case SUN_COLOR:
			return "ISO8859-1";
		case VTNT:
			return isLocaleCyrilic() ? "IBM866" : "IBM437";
		default:
			return getProperty("file.encoding");
		}

	}

	private static boolean isLocaleCyrilic() {
		return asList(getProperty("user.language"),
				getProperty("user.language.format"),
				getProperty("user.langage.display")).contains("ru");
	}

	/**
	 * @param initial
	 */
	private static QuietTerminalSizeHandler findSizeHandler(final InputEventHandler initial) {
		if (initial == null) {
			return null;
		}

		for (final InputEventHandler handler : initial) {
			if (handler instanceof QuietTerminalSizeHandler) {
				return (QuietTerminalSizeHandler) handler;
			}
		}

		return null;
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 */
	private static final class EventQueueFactory implements ThreadFactory {
		private Thread eventQueue;

		private final Object lock = new Object();

		EventQueueFactory() {
			// empty
		}

		/**
		 * @see ThreadFactory#newThread(Runnable)
		 */
		@Override
		public Thread newThread(final Runnable r) {
			synchronized (this.lock) {
				return this.eventQueue == null
						? this.eventQueue = new Thread(r, "EventQueue")
						: this.eventQueue;
			}
		}

		public boolean isEventQueue() {
			synchronized (this.lock) {
				if (this.eventQueue == null) {
					throw new IllegalStateException("Event queue is not yet runnable");
				}

				return currentThread() == this.eventQueue;
			}
		}
	}
}
