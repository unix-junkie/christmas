/*-
 * $Id$
 */
package com.google.code.christmas;

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
