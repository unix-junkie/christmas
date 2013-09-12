/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.jnlp;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.github.unix_junkie.christmas.Application;

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
