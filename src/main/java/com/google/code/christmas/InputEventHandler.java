/*-
 * $Id$
 */
package com.google.code.christmas;

import java.util.List;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface InputEventHandler extends Iterable<InputEventHandler> {
	/**
	 * @param term
	 * @param events
	 */
	void handle(final Terminal term, final List<InputEvent> events);

	/**
	 * @param term
	 */
	void printUsage(final Terminal term);

	/**
	 * @param next
	 * @return this input event handler
	 */
	InputEventHandler append(final InputEventHandler next);
}
