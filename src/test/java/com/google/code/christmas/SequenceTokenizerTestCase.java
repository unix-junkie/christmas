/*-
 * $Id$
 */
package com.google.code.christmas;

import static com.google.code.christmas.InputEvent.BACKSPACE;
import static com.google.code.christmas.InputEvent.DELETE;
import static com.google.code.christmas.InputEvent.ENTER;
import static com.google.code.christmas.InputEvent.ESC;
import static com.google.code.christmas.InputEvent.TAB;
import static com.google.code.christmas.SequenceConsumer.split;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class SequenceTokenizerTestCase extends TestCase {
	@Test
	public void testAlpha() {
		final StringBuilder s = new StringBuilder();
		s.append("abcd");
		final char sequence[] = new char[s.length()];
		s.getChars(0, s.length(), sequence, 0);
		assertEquals(4, split(sequence, TerminalType.ANSI).size());
	}

	@Test
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
