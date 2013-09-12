/*-
 * $Id$
 */
package com.google.code.christmas.jnlp;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.google.code.christmas.Application;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public interface JnlpLauncher {
	/**
	 * @param application
	 * @throws IOException
	 */
	Process launchTerminalEmulator(@Nonnull final Application application)
	throws IOException;

	boolean exitAfterChildTerminates();
}
