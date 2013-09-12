/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.VtTerminalSize.PATTERN;

import java.util.regex.Matcher;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class VtTerminalSizeTestCase extends TestCase {
	@Test
	public void test() {
		assertFalse(PATTERN.matcher("foo").matches());
		assertFalse(PATTERN.matcher(InputEvent.ESC + "[8;;80").matches());
		assertFalse(PATTERN.matcher(InputEvent.ESC + "[8;24;80").matches());
		assertFalse(PATTERN.matcher(InputEvent.ESC + "[8;24;80tt").matches());

		final Matcher matcher = PATTERN.matcher(InputEvent.ESC + "[8;24;80t");
		assertTrue(matcher.matches());
		assertEquals(2, matcher.groupCount());
		assertEquals(24, Integer.parseInt(matcher.group(1)));
		assertEquals(80, Integer.parseInt(matcher.group(2)));
	}
}
