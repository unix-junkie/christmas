/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
@RunWith(JUnit4.class)
public final class ColorTestCase {
	@Test
	@SuppressWarnings("static-method")
	public void testIsBright() {
		for (final Color color : Color.values()) {
			System.out.println(color + "\tbright? " + color.isBright());
		}
	}

	@Test
	@SuppressWarnings("static-method")
	public void testValueOf() {
		for (final Color color : Color.values()) {
			assertSame(color, Color.valueOf(color.ordinal()));
		}
	}

	@Test
	@SuppressWarnings("static-method")
	public void testDarker() {
		for (final Color color : Color.values()) {
			System.out.println(color + "\t-> " + color.darker());
		}
	}
}
