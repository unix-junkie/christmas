/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface Application {
	/**
	 * @param term
	 */
	Runnable getPostCreationTask(final Terminal term);

	InputEventHandler getInputEventHandler();

	String getWindowTitle();

	String getIconName();
}
