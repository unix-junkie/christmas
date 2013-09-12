/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

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
