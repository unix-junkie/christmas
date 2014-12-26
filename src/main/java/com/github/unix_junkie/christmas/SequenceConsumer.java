/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static java.lang.System.arraycopy;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * An event queue.
 *
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class SequenceConsumer extends Thread {
	private final BlockingQueue<char[]> queue;

	private final Terminal term;

	private final InputEventHandler handler;

	private static final Object LOCK = new Object();

	private static Thread instance;

	/**
	 * @param name
	 * @param queue
	 * @param term
	 * @param handler
	 */
	private SequenceConsumer(final String name,
			final BlockingQueue<char[]> queue,
			final Terminal term,
			final InputEventHandler handler) {
		super(name);
		this.queue = queue;
		this.term = term;
		this.handler = handler;
	}

	/**
	 * @param name
	 * @param queue
	 * @param term
	 * @param handler
	 */
	static Thread getInstance(final String name,
			final BlockingQueue<char[]> queue,
			final Terminal term,
			final InputEventHandler handler) {
		synchronized (LOCK) {
			return instance == null
					? instance = new SequenceConsumer(name, queue, term, handler)
					: instance;
		}
	}

	public static boolean isDispatchThread() {
		return Thread.currentThread() == instance;
	}

	/**
	 * @see Thread#start()
	 */
	@Override
	public synchronized void start() {
		super.start();

		if (this.handler != null) {
			this.handler.printUsage(this.term);
		}
	}


	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		try {
			while (true) {
				this.consume(this.queue.take());
			}
		} catch (final InterruptedException ie) {
			return;
		}
	}

	private void consume(final char[] sequence) {
		this.handler.handle(this.term, split(sequence, this.term.getType()));
	}

	/**
	 * Splits the sequence of chars read into individual input events.
	 * This method allows individual keys to be identified whenever
	 * multiple keys are pressed simultaneously.
	 *
	 * @param sequence
	 * @param type
	 */
	static List<InputEvent> split(final char sequence[], final TerminalType type) {
		if (sequence.length == 0) {
			return emptyList();
		}

		final List<InputEvent> events = new ArrayList<>();

		final int firstIndexOfEsc = firstIndexOf(sequence, InputEvent.ESC);
		switch (firstIndexOfEsc) {
		case -1:
			/*
			 * The char sequence doesn't contain any escape sequences.
			 */
			for (final char c : sequence) {
				events.add(new InputEvent(type, c));
			}
			break;
		case 0:
			/*
			 * The char sequence starts with an escape sequence.
			 */
			final int secondIndexOfCtrl = firstIndexOfControlChar(sequence, firstIndexOfEsc + 1);
			if (secondIndexOfCtrl == -1) {
				events.add(new InputEvent(type, sequence));
			} else {
				/*
				 * The first escape sequence (up to the next control character, exclusive).
				 */
				final char head[] = new char[secondIndexOfCtrl];
				/*
				 * The rest of the sequence, starting with a control character
				 */
				final char tail[] = new char[sequence.length - secondIndexOfCtrl];

				arraycopy(sequence, 0, head, 0, head.length);
				arraycopy(sequence, secondIndexOfCtrl, tail, 0, tail.length);
				events.add(new InputEvent(type, head));
				events.addAll(split(tail, type));
			}
			break;
		default:
			/*
			 * The char sequence starts with a normal or control character (excluding escape),
			 * and contains one or more escape sequence(s) starting at any index (excluding 0).
			 *
			 * The head doesn't contain any escape sequences.
			 */
			final char head[] = new char[firstIndexOfEsc];
			/*
			 * The tail starts with an escape sequence.
			 */
			final char tail[] = new char[sequence.length - firstIndexOfEsc];

			arraycopy(sequence, 0, head, 0, head.length);
			arraycopy(sequence, firstIndexOfEsc, tail, 0, tail.length);
			events.addAll(split(head, type));
			events.addAll(split(tail, type));
			break;
		}

		return events;
	}

	/**
	 * @param sequence
	 * @param c
	 */
	private static int firstIndexOf(final char sequence[], final char c) {
		return firstIndexOf(sequence, c, 0);
	}

	/**
	 * @param sequence
	 * @param c
	 * @param fromIndex
	 * @return index of the first occurrence of <em>c</em> in
	 *         <em>sequence</em>, or <em>-1</em> if no such character
	 *         occurs in the char array.
	 */
	private static int firstIndexOf(final char sequence[], final char c, final int fromIndex) {
		for (int i = fromIndex; i < sequence.length; i++) {
			if (c == sequence[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param sequence
	 * @param fromIndex
	 */
	private static int firstIndexOfControlChar(final char sequence[], final int fromIndex) {
		for (int i = fromIndex; i < sequence.length; i++) {
			if (InputEvent.isControlCharacter(sequence[i])) {
				return i;
			}
		}
		return -1;
	}
}
