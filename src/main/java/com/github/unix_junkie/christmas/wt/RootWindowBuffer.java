/*-
 * $Id$
 */
package com.github.unix_junkie.christmas.wt;

import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.annotation.Nonnull;

import com.github.unix_junkie.christmas.Color;
import com.github.unix_junkie.christmas.Dimension;
import com.github.unix_junkie.christmas.Terminal;
import com.github.unix_junkie.christmas.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
final class RootWindowBuffer extends AbstractComponentBuffer implements Iterable<ScreenCell> {
	ScreenCell cells[][];

	@Nonnull
	private final Color foreground;

	@Nonnull
	private Color background;

	private char backgroundPattern = ' ';

	private final Set<TextAttribute> foregroundAttributes = noneOf(TextAttribute.class);

	/**
	 * @param foreground
	 * @param background
	 * @param foregroundAttributes
	 */
	RootWindowBuffer(@Nonnull final Color foreground,
			@Nonnull final Color background,
			@Nonnull final TextAttribute ... foregroundAttributes) {
		this.foreground = foreground;
		this.background = background;
		this.foregroundAttributes.addAll(asList(foregroundAttributes));
	}

	/**
	 * @see ComponentBuffer#setTextAt(char, int, int, boolean, Color, Color, TextAttribute[])
	 */
	@Override
	public void setTextAt(final char text,
			final int x,
			final int y,
			final boolean alternateCharset,
			final Color foreground,
			final Color background,
			@Nonnull final TextAttribute ... attributes) {
		if (y < 1 || x < 1) {
			throw new IllegalArgumentException("(" + x + ", " + y + ")");
		}
		if (y > this.getHeight() || x > this.getWidth(y)) {
			/*
			 * If root window is larger than this buffer,
			 * simply don't do anything.
			 */
			return;
		}

		final ScreenCell currentCell = this.cells[y - 1][x - 1];
		currentCell.setText(text);
		currentCell.setAlternateCharset(alternateCharset);
		currentCell.setAttributes(attributes);

		if (foreground != null) {
			currentCell.setForeground(foreground);
		}
		if (background != null) {
			currentCell.setBackground(background);
		}
	}

	public void paint(final Terminal term) {
		for (int y = 1, m = this.getHeight(); y <= m; y++) {
			term.setCursorLocation(1, y);

			for (int x = 1, n = this.getWidth(y); x <= n; x++) {
				if (x == n && y == m && !term.getType().canUpdateLowerRightCell()) {
					continue;
				}

				final ScreenCell currentCell = this.cells[y - 1][x - 1];
				final ScreenCell previousCell = this.findPrevious(x, y);
				final boolean alternateCharsetNeeded = currentCell.isAlternateCharset();
				if ((previousCell == null || previousCell.isAlternateCharset()) && !alternateCharsetNeeded) {
					term.stopAlternateCs();
				} else if ((previousCell == null || !previousCell.isAlternateCharset()) && alternateCharsetNeeded) {
					term.startAlternateCs();
				}
				/*
				 * Attributes should precede color settings.
				 */
				final Set<TextAttribute> attributes = currentCell.getAttributes();
				final boolean attributesChanged = previousCell == null || !previousCell.getAttributes().equals(attributes);
				if (attributesChanged) {
					term.setTextAttributes(attributes);
				}
				final Color cellForeground = currentCell.getForeground();
				if (attributesChanged || previousCell == null || previousCell.getForeground() != cellForeground) {
					term.setForeground(cellForeground);
				}
				final Color cellBackground = currentCell.getBackground();
				if (attributesChanged || previousCell == null || previousCell.getBackground() != cellBackground) {
					term.setBackground(cellBackground);
				}
				term.print(currentCell.getText());
			}
		}

		/*
		 * A safety net so that the next output is not messed up.
		 */
		term.stopAlternateCs();

		term.flush();
	}

	/**
	 * @param x the 1-based x coordinate
	 * @param y the 1-based y coordinate
	 * @return the previous cell in this buffer, or {@code null} if none found.
	 */
	private ScreenCell findPrevious(final int x, final int y) {
		if (x == 1 && y == 1) {
			/*
			 * Return null is current cell is the top left corner.
			 */
			return null;
		}

		final int previousX;
		final int previousY;
		if (x == 1) {
			/*
			 * We're at the first column already.
			 */
			previousX = this.getWidth(y);
			previousY = y - 1;
		} else {
			previousX = x - 1;
			previousY = y;
		}

		return this.cells[previousY - 1][previousX - 1];
	}

	/**
	 * Returns the width of this buffer. Buffer height may be less
	 * than the root window width, whereas clipping occurs.
	 *
	 * @return the width of this buffer.
	 * @see #getWidth(int)
	 */
	int getWidth() {
		return this.getWidth(1);
	}

	/**
	 * Returns the width of this buffer, measured at row identified
	 * by <em>y</em>. Buffer height may be less than the root window
	 * width, whereas clipping occurs.
	 *
	 * @param y the 1-based row identifier.
	 * @return the width of this buffer, measured at row identified by <em>y</em>.
	 * @throws IllegalArgumentException if <em>y &lt; 1</em> or <em>y &gt; {@link #getHeight() buffer height}</em>
	 * @see #getHeight()
	 */
	int getWidth(final int y) {
		final int height = this.getHeight();
		if (y < 1 || y > height) {
			throw new IllegalArgumentException(y + " not in range [1; " + height + ']');
		}
		return this.cells[y - 1].length;
	}

	/**
	 * Returns the height of this buffer. Buffer height may be less
	 * than the root window height, whereas clipping occurs.
	 *
	 * @return the height of this buffer.
	 */
	int getHeight() {
		return this.cells.length;
	}

	Dimension getSize() {
		return new Dimension(this.getWidth(), this.getHeight());
	}

	/**
	 * @param size
	 */
	void setSize(final Dimension size) {
		this.cells = new ScreenCell[size.getHeight()][size.getWidth()];
		for (int y = 1, m = this.getHeight(); y <= m; y++) {
			for (int x = 1, n = this.getWidth(y); x <= n; x++) {
				this.cells[y - 1][x - 1] = new ScreenCell(this.backgroundPattern);
			}
		}

		for (final ScreenCell screenCell : this) {
			screenCell.setForeground(this.foreground);
			screenCell.setAttributes(this.foregroundAttributes);
			screenCell.setBackground(this.background);
		}
	}

	Color getForeground() {
		return this.foreground;
	}

	Color getBackground() {
		return this.background;
	}

	/**
	 * @param background
	 */
	void setBackground(@Nonnull final Color background) {
		if (background == null) {
			throw new IllegalArgumentException();
		}

		this.background = background;
	}

	char getBackgroundPattern() {
		return this.backgroundPattern;
	}

	/**
	 * @param backgroundPattern
	 */
	void setBackgroundPattern(final char backgroundPattern) {
		this.backgroundPattern = backgroundPattern;
	}

	/**
	 * @see Iterable#iterator()
	 */
	@Override
	public Iterator<ScreenCell> iterator() {
		return new ScreenCellIterator();
	}

	/**
	 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
	 */
	private final class ScreenCellIterator implements Iterator<ScreenCell> {
		private int x = 1;

		private int y = 1;

		ScreenCellIterator() {
			// empty
		}

		/**
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			final int height = RootWindowBuffer.this.getHeight();
			return height > 0
					&& RootWindowBuffer.this.getWidth(height) > 0
					&& (this.y != height
							|| this.x != RootWindowBuffer.this.getWidth(this.y));
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public ScreenCell next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			final ScreenCell current = RootWindowBuffer.this.cells[this.y - 1][this.x - 1];

			if (this.x == RootWindowBuffer.this.getWidth(this.y)) {
				/*
				 * We're at the last column already.
				 */
				this.x = 1;
				this.y += 1;
			} else {
				this.x += 1;
			}

			return current;
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
