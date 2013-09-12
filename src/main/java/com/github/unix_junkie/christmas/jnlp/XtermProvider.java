/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.jnlp;

import static java.lang.Runtime.getRuntime;

import java.io.IOException;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class XtermProvider implements UnixTerminalProvider {
	private static final String XTERM_PATHS[] = {
		"xterm",
		"/usr/X11/bin/xterm", // Mac OS X
		"/usr/bin/xterm", // Linux & CYGWIN
		"/opt/sfw/bin/xterm", // Solaris 8
		"/usr/openwin/bin/xterm", // Solaris 8
	};

	/**
	 * @see UnixTerminalProvider#newTerminalProcess(String, String, String)
	 */
	@Override
	public Process newTerminalProcess(final String title, final String iconName, final String program) {
		final String xtermCommand[] = {null, "-T", null, "-n", null, "-e", null};
		for (final String xtermPath : XTERM_PATHS) {
			try {
				assert xtermCommand.length == 7 : xtermCommand.length;
				xtermCommand[0] = xtermPath;
				xtermCommand[2] = title;
				xtermCommand[4] = iconName;
				xtermCommand[6] = program;
				final Process terminalProcess = getRuntime().exec(xtermCommand);
				return terminalProcess;
			} catch (final IOException ioe) {
				continue;
			}
		}
		return null;
	}
}
