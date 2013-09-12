/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

import static java.lang.System.exit;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class SequenceTokenizer extends Thread {
	/**
	 * <p>The threshold, in milliseconds, which, once exceeded,
	 * means that a new input event has occurred.</p>
	 *
	 * <p>The maximum delay observed between the read operations
	 * within a single escape sequence is 1 ms. The minimum delay
	 * observed between two separate escape sequences is 89 ms.</p>
	 *
	 * <p>Additionally, even a value of <em>1</em> doesn't prevent
	 * multiple keys pressed simultaneously
	 * from being interpreted as a single sequence.</p>
	 *
	 * <p>So the value of 45 ms is a fair trade.</p>
	 */
	private static final long INPUT_EVENT_THRESHOLD = 45;

	/**
	 * Despite a regular escape sequence length rarely exceeds 7,
	 * we should account for situations when multiple keys
	 * are pressed simultaneously.
	 */
	private static final int MAX_SEQUENCE_LENGTH = 1024;


	final Terminal term;

	private final InputEventHandler handler;

	final char sequence[] = new char[MAX_SEQUENCE_LENGTH];

	final Object sequenceLock = new Object();

	int sequencePositionMarker;

	private final BlockingQueue<char[]> sequences = new LinkedBlockingQueue<char[]>();


	/**
	 * @param term
	 * @param handler
	 */
	public SequenceTokenizer(final Terminal term, final InputEventHandler handler) {
		super("SequenceTokenizer");

		this.term = term;
		this.handler = handler;
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				/*
				 * Clear the interrupted status and sleep.
				 */
				interrupted();
				sleep(INPUT_EVENT_THRESHOLD);

				/*
				 * No new character has been read
				 * within the timeout -- process the sequence.
				 */
				final char sequenceClone[];
				synchronized (this.sequenceLock) {
					/*
					 * The sequence is empty.
					 */
					if (this.sequencePositionMarker == 0) {
						continue;
					}

					sequenceClone = new char[this.sequencePositionMarker];
					for (int j = 0; j < this.sequencePositionMarker; j++) {
						sequenceClone[j] = this.sequence[j];
					}
					this.sequencePositionMarker = 0;
				}

				this.sequences.put(sequenceClone);
			} catch (final InterruptedException ie) {
				/*
				 * New character has been read --
				 * continuing from the beginning.
				 */
				continue;
			}
		}
	}

	/**
	 * @see Thread#start()
	 */
	@Override
	public synchronized void start() {
		super.start();

		final Thread sequenceConsumer = SequenceConsumer.getInstance(
				"SequenceConsumer",
				this.sequences,
				this.term,
				this.handler);
		sequenceConsumer.start();

		/*
		 * This is the second thread which produces char sequences
		 * consumed by the tokenizer.
		 */
		final Runnable sequenceProducer = new Runnable() {
			/**
			 * @see Runnable#run()
			 */
			@Override
			public void run() {
				int i;
				try {
					while (/* term.isOpen() && */ (i = SequenceTokenizer.this.term.read()) != -1) {
						/*
						 * 1. Interrupt the tokenizer,
						 * so that a new cycle is started.
						 */
						SequenceTokenizer.this.interrupt();

						/*
						 * 2. Append the character to the sequence.
						 */
						final char c = (char) i;
						synchronized (SequenceTokenizer.this.sequenceLock) {
							if (SequenceTokenizer.this.sequencePositionMarker < SequenceTokenizer.this.sequence.length) {
								SequenceTokenizer.this.sequence[SequenceTokenizer.this.sequencePositionMarker++] = c;
							}
						}
					}
				} catch (final SocketException se) {
					/*
					 * Socket we're reading from is closed,
					 * so suppress the output and exit the application.
					 */
					/**
					 * @todo write to the log file.
					 */
					exit(0);
				} catch (final IOException ioe) {
					/*
					 * Never.
					 */
					ioe.printStackTrace();
				}
			}
		};
		final Thread thread = new Thread(sequenceProducer, "SequenceProducer");
		thread.start();
	}
}
