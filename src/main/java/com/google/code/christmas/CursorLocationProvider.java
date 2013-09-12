/*-
 * $Id$
 */
package com.google.code.christmas;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface CursorLocationProvider {
	/**
	 * @param term
	 * @throws IllegalStateException if called from the event dispatch
	 *         thread {@link SequenceConsumer#isDispatchThread()}
	 */
	Point getCursorLocation(final Terminal term);
}
