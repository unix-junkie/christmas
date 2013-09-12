/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import junit.framework.TestCase;

import org.junit.Test;

import com.github.unix_junkie.christmas.InputEventHandler;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class HandlerChainingTestCase extends TestCase {
	public void testAssertionStatus() {
		try {
			assert false;
			fail("Re-run java with -ea switch");
		} catch (final AssertionError ae) {
			assertTrue(true);
		}
	}

	@Test
	public void testChaining() {
		final InputEventHandler initial = new Echo(new ExitHandler());
		for (final InputEventHandler handler : initial) {
			System.out.println(handler.getClass().getName());
		}
	}

	@Test
	public void testDuplicates() {
		final AbstractInputEventHandler handler0 = new Echo(new ExitHandler());
		final AbstractInputEventHandler handler1 = new ExitHandler();

		try {
			handler1.setNext(handler1);
			fail("Circular chaining");
		} catch (final IllegalArgumentException iae) {
			assertTrue(true);
		}

		try {
			handler1.setNext(handler0);
			fail("Duplicates in the chain");
		} catch (final IllegalArgumentException iae) {
			assertTrue(true);
		}

		try {
			handler1.setNext(new ExitHandler());
			fail("Duplicates in the chain");
		} catch (final IllegalArgumentException iae) {
			assertTrue(true);
		}
	}
}
