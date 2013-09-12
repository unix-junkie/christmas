/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.handlers;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import com.github.unix_junkie.christmas.InputEventHandler;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
abstract class AbstractInputEventHandler implements InputEventHandler {
	InputEventHandler next;

	/**
	 * @param next
	 */
	AbstractInputEventHandler(@Nullable final InputEventHandler next) {
		this.setNext(next);
	}

	/**
	 * @param next
	 */
	void setNext(@Nullable final InputEventHandler next) {
		if (this.next != null && this.next != next) {
			throw new IllegalStateException("Next handler already set");
		}
		if (next == this) {
			throw new IllegalArgumentException("Circular chaining not allowed");
		}
		if (next != null) {
			for (final InputEventHandler handler : next) {
				final String className0 = this.getClass().getName();
				final String className1 = handler.getClass().getName();
				if (className0.equals(className1)) {
					throw new IllegalArgumentException(className0 + " is already present in the chain");
				}
			}
		}
		this.next = next;
	}

	/**
	 * @see InputEventHandler#append(InputEventHandler)
	 */
	@Override
	public final InputEventHandler append(@Nullable final InputEventHandler next0) {
		if (this.next == null) {
			this.setNext(next0);
		} else {
			this.next.append(next0);
		}

		return this;
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 */
	private static final class HandlerIterator implements Iterator<InputEventHandler> {
		private AbstractInputEventHandler current;

		/**
		 * @param initial
		 */
		HandlerIterator(final AbstractInputEventHandler initial) {
			if (initial == null) {
				throw new IllegalArgumentException();
			}

			this.current = initial;
		}

		/**
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.current != null;
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public InputEventHandler next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			try {
				return this.current;
			} finally {
				this.current = this.current.next instanceof AbstractInputEventHandler
						? (AbstractInputEventHandler) this.current.next
						: null;
			}
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * @see Iterable#iterator()
	 */
	@Override
	public final Iterator<InputEventHandler> iterator() {
		return new HandlerIterator(this);
	}
}
