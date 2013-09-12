/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.jnlp;

import static java.lang.System.exit;
import static java.lang.System.getProperty;

import java.io.IOException;

import com.github.unix_junkie.christmas.Application;

/**
 * This code has been designed for JNLP/Java Web Start use,
 * as it launches external terminal emulators.
 *
 * Launching the application from command line should be done
 * in a different way.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class JnlpMain {
	private JnlpMain() {
		assert false;
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(final String args[])
	throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (args.length != 1) {
			System.out.println("Usage: " + JnlpMain.class.getName() + " <CLASSNAME>");
			return;
		}

		final String className = args[0];
		final Class<?> clazz = Class.forName(className);
		final Application application = (Application) clazz.newInstance();
		final JnlpLauncher launcher = getProperty("os.name").startsWith("Windows") ? new Win32JnlpLauncher(false) : new UnixJnlpLauncher();

		final Process terminalProcess = launcher.launchTerminalEmulator(application);
		if (terminalProcess == null) {
			System.out.println("Failed to find a suitable terminal emulator in PATH.");
			return;
		}
		final int returnValue = terminalProcess.waitFor();
		if (returnValue != 0) {
			System.out.println("Child process exited with code " + returnValue);
		}
		if (launcher.exitAfterChildTerminates()) {
			exit(returnValue);
		}
	}
}
