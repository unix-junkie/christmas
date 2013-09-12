/*-
 * $Id$
 */
package com.google.code.christmas;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface TerminalSizeProvider {
	/**
	 * @param term
	 * @throws IllegalStateException if called from the event dispatch
	 *         thread {@link SequenceConsumer#isDispatchThread()}
	 */
	Dimension getTerminalSize(final Terminal term);
}
