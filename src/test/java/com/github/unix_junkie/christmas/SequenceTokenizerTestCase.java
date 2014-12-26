/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.InputEvent.BACKSPACE;
import static com.github.unix_junkie.christmas.InputEvent.DELETE;
import static com.github.unix_junkie.christmas.InputEvent.ENTER;
import static com.github.unix_junkie.christmas.InputEvent.ESC;
import static com.github.unix_junkie.christmas.InputEvent.TAB;
import static com.github.unix_junkie.christmas.SequenceConsumer.split;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
@RunWith(JUnit4.class)
public final class SequenceTokenizerTestCase {
	@Test
	@SuppressWarnings("static-method")
	public void testAlpha() {
		final StringBuilder s = new StringBuilder();
		s.append("abcd");
		final char sequence[] = new char[s.length()];
		s.getChars(0, s.length(), sequence, 0);
		assertEquals(4, split(sequence, TerminalType.ANSI).size());
	}

	@Test
	@SuppressWarnings("static-method")
	public void testSingleEscape() {
		final StringBuilder s = new StringBuilder();
		s.append("abcd").append(ESC).append("OP");
		final char sequence[] = new char[s.length()];
		s.getChars(0, s.length(), sequence, 0);
		final List<InputEvent> events = split(sequence, TerminalType.ANSI);
		final int eventCount = events.size();
		assertEquals(5, eventCount);

		final InputEvent last = events.listIterator(eventCount).previous();
		assertEquals("F1", ((VtKey) TerminalType.ANSI.getVtKeyOrResponse(last)).name());
	}

	@Test
	@SuppressWarnings("static-method")
	public void testMultipleEscapes() {
		final StringBuilder s = new StringBuilder();
		s.append("abcd").append(ESC).append("OP").append(ESC).append("OQ");
		final char sequence[] = new char[s.length()];
		s.getChars(0, s.length(), sequence, 0);
		final List<InputEvent> events = split(sequence, TerminalType.ANSI);
		final int eventCount = events.size();
		assertEquals(6, eventCount);

		final InputEvent last = events.listIterator(eventCount).previous();
		assertEquals("F2", ((VtKey) TerminalType.ANSI.getVtKeyOrResponse(last)).name());
	}

	@Test
	@SuppressWarnings("static-method")
	public void testControl() {
		final StringBuilder s = new StringBuilder();
		s.append("abcd").append(BACKSPACE).append(TAB).append(ENTER).append(DELETE);
		final char sequence[] = new char[s.length()];
		s.getChars(0, s.length(), sequence, 0);
		final List<InputEvent> events = split(sequence, TerminalType.ANSI);
		final int eventCount = events.size();
		assertEquals(8, eventCount);
	}

	@Test
	@SuppressWarnings("static-method")
	public void testMixed() {
		final StringBuilder s = new StringBuilder();
		s.append("abcd").append(BACKSPACE).append(TAB).append(ENTER).append(DELETE).append("abcd")
				.append(ESC).append("OP")
				.append(ESC).append("OQ")
				.append(BACKSPACE).append(TAB).append(ENTER).append(DELETE).append("abcd");
		final char sequence[] = new char[s.length()];
		s.getChars(0, s.length(), sequence, 0);
		final List<InputEvent> events = split(sequence, TerminalType.ANSI);
		final int eventCount = events.size();
		assertEquals(22, eventCount);
	}
}
