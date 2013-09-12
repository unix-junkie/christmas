/*-
 * $Id$
 */
package com.google.code.christmas.jnlp;

import static java.lang.Boolean.getBoolean;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import static java.lang.System.setProperty;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Nonnull;

import com.google.code.christmas.Application;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class UnixJnlpLauncher implements JnlpLauncher {
	private final UnixTerminalProvider terminalProvider;

	public UnixJnlpLauncher() {
		this(new XtermProvider());
	}

	public UnixJnlpLauncher(final UnixTerminalProvider terminalProvider) {
		this.terminalProvider = terminalProvider;
	}

	/**
	 * @see JnlpLauncher#launchTerminalEmulator(Application)
	 */
	@Override
	public Process launchTerminalEmulator(@Nonnull final Application application)
	throws IOException {
		final boolean debugMode = getBoolean("terminal.debug");
		final String javaCommandLine = getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java -classpath \"" + getProperty("java.class.path") + "\" "
				+ (getProperty("os.name").equals("Mac OS X")
						? "-Dfile.encoding=\"`locale charmap`\" " // Apple's Java implementation default is MacRoman
						: "")
				+ "-Dterminal.debug=" + debugMode + " "
				+ application.getClass().getName();

		if (getProperty("os.name").equals("Mac OS X")) {
			setProperty("java.io.tmpdir", "/tmp");  // By default, /var/folders/Fv/FvLjTL7NHa06CiaNGkyzpE+++TI/-Tmp-/ is used
		}
		final File shellScript = File.createTempFile("synctimestamps-" + getProperty("user.name") + '-', ".sh");
		final PrintWriter out = new PrintWriter(shellScript);
		final String shell = getenv("SHELL");
		out.println("#!" + (shell != null && shell.length() != 0 ? shell : "/bin/sh"));
		/*
		 * In Cygwin, tty returns /dev/cons0, /dev/cons1,..
		 * those values being invalid Windows filenames.
		 */
		out.println("tty=`tty 2>/dev/null`");
		out.println("returnValue=$?");
		out.println("if [ ${returnValue} -ne 0 ]");
		out.println("then");
		out.println("\texit ${returnValue}");
		out.println("else");
		/*
		 * man stty
		 */
		final StringBuilder sttyCommand = new StringBuilder();
		sttyCommand.append("\t");
		sttyCommand.append("stty");
		sttyCommand.append(" -icanon min 1 time 0"); // Allow individual characters to be read
		sttyCommand.append(" -echo"); // Don't echo what is being read
		sttyCommand.append(" -icrnl"); // ^M and <Enter> generate ^M, not ^J
		sttyCommand.append(" intr undef"); // Allow ^C to be received
		sttyCommand.append(" flush undef"); // Allow ^O to be received
		sttyCommand.append(" start undef"); // Allow ^Q to be received
		sttyCommand.append(" stop undef"); // Allow ^S to be received
		sttyCommand.append(" lnext undef"); // Allow ^V to be received
		sttyCommand.append(" dsusp undef"); // Allow ^Y to be received
		sttyCommand.append(" susp undef"); // Allow ^Z to be received
		sttyCommand.append(" quit undef"); // Allow ^\ to be received (otherwise JVM prints the full stack trace)
		out.println(sttyCommand);
		out.println("\t#stty -a");
		out.println('\t' + javaCommandLine + " ${tty}");
		out.println("\treturnValue=$?");
		out.println("\tstty sane 2>/dev/null");
		if (debugMode) {
			out.println("\techo \"Exiting with code ${returnValue}...\"");
			out.println("\tread dummy");
		}
		out.println("\texit ${returnValue}");
		out.println("fi");
		out.flush();
		out.close();
		shellScript.setExecutable(true);
		shellScript.deleteOnExit();

		return this.terminalProvider.newTerminalProcess(application.getWindowTitle(), application.getIconName(), shellScript.getPath());
	}

	/**
	 * @see JnlpLauncher#exitAfterChildTerminates()
	 */
	@Override
	public boolean exitAfterChildTerminates() {
		/*
		 * The parent JVM process should exit immediately
		 * once the child process terminates.
		 */
		return true;
	}
}
