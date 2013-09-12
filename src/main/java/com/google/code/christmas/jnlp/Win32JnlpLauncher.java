/*-
 * $Id$
 */
package com.google.code.christmas.jnlp;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.getenv;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.annotation.Nonnull;

import com.google.code.christmas.Application;
import com.google.code.christmas.Terminal;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class Win32JnlpLauncher implements JnlpLauncher {
	private final boolean keepCmdRunning;

	/**
	 * @param keepCmdRunning
	 */
	public Win32JnlpLauncher(final boolean keepCmdRunning) {
		this.keepCmdRunning = keepCmdRunning;
	}

	/**
	 * @see JnlpLauncher#launchTerminalEmulator(Application)
	 */
	@Override
	public Process launchTerminalEmulator(@Nonnull final Application application)
	throws IOException {
		final String terminalType = "vtnt";
		final ServerSocket serverSocket = listenOnLowestUnoccupiedPort();
		final Thread serverThread = new Thread("ServerThread") {
			/**
			 * @see Thread#run()
			 */
			@Override
			public void run() {
				try {
					final Socket socket = serverSocket.accept();
					final Terminal term = new Terminal(terminalType, application.getInputEventHandler(), socket);
					term.invokeLater(application.getPostCreationTask(term));
				} catch (final IOException ioe) {
					ioe.printStackTrace();
				}
			}
		};
		serverThread.start();

		final String winDir = getenv("WINDIR");
		final String cmdPath = (winDir == null || winDir.length() == 0 ? getenv("SYSTEMROOT") : winDir) + File.separatorChar + "system32" + File.separatorChar + "cmd.exe";
		final String telnetPath = (winDir == null || winDir.length() == 0 ? getenv("SYSTEMROOT") : winDir) + File.separatorChar + "system32" + File.separatorChar + "telnet.exe";

		/*-
		 * Creating an external windows batch file
		 * in order to have the "title" command set the window title
		 * before "telnet.exe" is run -- is useless:
		 * "telnet.exe" overrides the window title anyway.
		 *
		 * cmd /C: we end up with an orphaned telnet process, and have no idea of what its PID is.
		 * cmd /K: we have a java.exe -> cmd.exe -> telnet.exe chain of running processes
		 * (and we don't know the PID, either).
		 */
		final String telnetCommandLine[] = new String[]{cmdPath, this.keepCmdRunning ? "/K" : "/C", "start", telnetPath, "-t", terminalType, serverSocket.getInetAddress().getCanonicalHostName(), String.valueOf(serverSocket.getLocalPort())};
		return getRuntime().exec(telnetCommandLine);
	}

	/**
	 * @see JnlpLauncher#exitAfterChildTerminates()
	 */
	@Override
	public boolean exitAfterChildTerminates() {
		/*-
		 * If cmd.exe is run as "cmd /C", we shouldn't exit here
		 * (telnet.exe will still be running).
		 * If cmd.exe is run as "cmd /K",
		 * this child process never returns (unless we terminate the JVM).
		 *
		 * On Windows, we shouldn't be actually waiting for the child process
		 * to return: this launcher doesn't spawn any separate child JVM
		 * (which is what we do on UNIX).
		 *
		 * Additionally, we don't yet detect it if a user
		 * just closes the telnet.exe window (JVM continues running).
		 *
		 *
		 * If cmd.exe has been kept running,
		 * we don't reach this point anyway
		 * (unless it is forcibly terminated -- in this case,
		 * it exits with code 1).
		 */
		return this.keepCmdRunning;
	}

	private static ServerSocket listenOnLowestUnoccupiedPort() {
		int port = 1;

		try {

			while (true) {
				try {
					final InetAddress lo0 = InetAddress.getByName("127.0.0.1");
					assert lo0.isLoopbackAddress();
					return new ServerSocket(port, 50, lo0);
				} catch (final BindException be) {
					port++;
					continue;
				}
			}
		} catch (final IOException ioe) {
			/*-
			 * Never.
			 */
			ioe.printStackTrace();
			return null;
		}
	}
}
