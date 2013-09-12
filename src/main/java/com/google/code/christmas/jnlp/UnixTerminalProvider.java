/*-
 * $Id$
 */
package com.google.code.christmas.jnlp;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface UnixTerminalProvider {
	/**
	 * @param title
	 * @param iconName
	 * @param program
	 */
	Process newTerminalProcess(final String title,
			final String iconName,
			final String program);
}
