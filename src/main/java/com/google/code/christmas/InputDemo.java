/*-
 * $Id$
 */
package com.google.code.christmas;

import static com.google.code.christmas.Color.BLACK;
import static com.google.code.christmas.Color.WHITE;
import static java.lang.System.getenv;

import java.io.IOException;

import com.google.code.christmas.handlers.Echo;
import com.google.code.christmas.handlers.ExitHandler;
import com.google.code.christmas.handlers.WtHandler;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class InputDemo implements Application {
	private static final String WINDOW_TITLE = "SyncTimeStamps";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String args[]) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: ");
			return;
		}

		/*
		 * TTY device specified.
		 */
		final Application application = new InputDemo();
		final String ttyName = args[0];
		final Terminal term = new Terminal(getenv("TERM"), application.getInputEventHandler(), ttyName);
		term.invokeLater(application.getPostCreationTask(term));
	}

	/**
	 * @see Application#getPostCreationTask(Terminal)
	 */
	@Override
	public Runnable getPostCreationTask(final Terminal term) {
		return new Runnable() {
			/**
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				term.setTitle(WINDOW_TITLE);
				term.setToolbarVisible(false);
				term.setCursorVisible(false);
				term.setScrollbarVisible(false);

				term.setDefaultForeground(WHITE);
				term.setDefaultBackground(BLACK);
				term.clear();

				term.start();
			}
		};
	}

	/**
	 * @see Application#getInputEventHandler()
	 */
	@Override
	public InputEventHandler getInputEventHandler() {
		return new ExitHandler().append(new WtHandler(WINDOW_TITLE)).append(new Echo());
	}

	/**
	 * @see Application#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
		return WINDOW_TITLE;
	}

	/**
	 * @see Application#getIconName()
	 */
	@Override
	public String getIconName() {
		return WINDOW_TITLE;
	}
}
