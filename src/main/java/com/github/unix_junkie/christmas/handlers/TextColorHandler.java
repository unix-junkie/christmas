/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import static com.github.unix_junkie.christmas.Color.BLACK;
import static com.github.unix_junkie.christmas.Color.BLUE;
import static com.github.unix_junkie.christmas.Color.BRIGHT_BLACK;
import static com.github.unix_junkie.christmas.Color.BRIGHT_BLUE;
import static com.github.unix_junkie.christmas.Color.BRIGHT_CYAN;
import static com.github.unix_junkie.christmas.Color.BRIGHT_GREEN;
import static com.github.unix_junkie.christmas.Color.BRIGHT_MAGENTA;
import static com.github.unix_junkie.christmas.Color.BRIGHT_RED;
import static com.github.unix_junkie.christmas.Color.BRIGHT_WHITE;
import static com.github.unix_junkie.christmas.Color.BRIGHT_YELLOW;
import static com.github.unix_junkie.christmas.Color.CYAN;
import static com.github.unix_junkie.christmas.Color.GREEN;
import static com.github.unix_junkie.christmas.Color.MAGENTA;
import static com.github.unix_junkie.christmas.Color.RED;
import static com.github.unix_junkie.christmas.Color.WHITE;
import static com.github.unix_junkie.christmas.Color.YELLOW;

import java.util.List;

import javax.annotation.Nonnull;

import com.github.unix_junkie.christmas.Color;
import com.github.unix_junkie.christmas.InputEvent;
import com.github.unix_junkie.christmas.InputEventHandler;
import com.github.unix_junkie.christmas.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class TextColorHandler extends AbstractInputEventHandler {
	public TextColorHandler() {
		this(null);
	}

	/**
	 * @param next
	 */
	public TextColorHandler(final InputEventHandler next) {
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
			if (event.isControlWith('K')) {
				term.invokeLater(() -> {
					term.clear();
					testColorPair(term, MAGENTA, WHITE);
					testColorPair(term, BRIGHT_MAGENTA, WHITE);
					testColorPair(term, MAGENTA, BRIGHT_WHITE);
					testColorPair(term, BRIGHT_MAGENTA, BRIGHT_WHITE);
					testColorPair(term, RED, BLACK);
					testColorPair(term, BRIGHT_RED, BLACK);
					testColorPair(term, RED, BRIGHT_BLACK);
					testColorPair(term, BRIGHT_RED, BRIGHT_BLACK);
					testColorPair(term, YELLOW, GREEN);
					testColorPair(term, BRIGHT_YELLOW, GREEN);
					testColorPair(term, YELLOW, BRIGHT_GREEN);
					testColorPair(term, BRIGHT_YELLOW, BRIGHT_GREEN);
					testColorPair(term, BLUE, CYAN);
					testColorPair(term, BRIGHT_BLUE, CYAN);
					testColorPair(term, BLUE, BRIGHT_CYAN);
					testColorPair(term, BRIGHT_BLUE, BRIGHT_CYAN);
					term.flush();
				});
			}
		}
	}

	/**
	 * @param term
	 * @param foreground
	 * @param background
	 */
	static void testColorPair(@Nonnull final Terminal term,
			@Nonnull final Color foreground,
			@Nonnull final Color background) {
		if (background.isBright() && !term.getType().isBrightBackgroundSupported()) {
			return;
		}

		term.setTextAttributes(foreground, background);
		term.println(foreground + " on " + background);
	}

	/**
	 * @see InputEventHandler#printUsage(Terminal)
	 */
	@Override
	public void printUsage(final Terminal term) {
		term.println("Type ^K for text color demo.");
		term.flush();

		if (this.next != null) {
			this.next.printUsage(term);
		}
	}
}
