/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static com.github.unix_junkie.christmas.VtTerminalSize.PATTERN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
@RunWith(JUnit4.class)
public final class VtTerminalSizeTestCase {
	@Test
	@SuppressWarnings("static-method")
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
