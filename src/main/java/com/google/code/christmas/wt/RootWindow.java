/*-
 * $Id$
 */
package com.google.code.christmas.wt;

import static com.google.code.christmas.Dimension.UNDEFINED;
import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static java.util.EnumSet.noneOf;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.code.christmas.Color;
import com.google.code.christmas.Dimension;
import com.google.code.christmas.Insets;
import com.google.code.christmas.Point;
import com.google.code.christmas.Terminal;
import com.google.code.christmas.TextAttribute;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public final class RootWindow implements Container {
	private static final int MINIMUM_WIDTH = 2;

	private static final int MINIMUM_HEIGHT = 2;

	private final Terminal term;

	private Dimension size = UNDEFINED;

	private Dimension minimumSize = new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT);

	@Nonnull
	private final Color foreground;

	private final Set<TextAttribute> foregroundAttributes = noneOf(TextAttribute.class);

	@Nullable
	private Border border;

	@Nullable
	private ChildComponent contentPane;

	final RootWindowBuffer buffer;

	/**
	 * @param term
	 */
	public RootWindow(@Nonnull final Terminal term) {
		this(term, term.getDefaultForeground(), term.getDefaultBackground());
	}

	/**
	 * @param term
	 * @param foreground
	 * @param background
	 * @param foregroundAttributes
	 */
	public RootWindow(@Nonnull final Terminal term,
			@Nonnull final Color foreground,
			@Nonnull final Color background,
			@Nonnull final TextAttribute ... foregroundAttributes) {
		this.term = term;
		this.foreground = foreground;
		this.foregroundAttributes.addAll(asList(foregroundAttributes));

		this.buffer = new RootWindowBuffer(foreground, background, foregroundAttributes);
	}

	/**
	 * @param minimumSize
	 */
	public void setMinimumSize(@Nonnull final Dimension minimumSize) {
		if (minimumSize.getWidth() < MINIMUM_WIDTH
				|| minimumSize.getHeight() < MINIMUM_HEIGHT) {
			throw new IllegalArgumentException();
		}

		this.minimumSize = minimumSize;
	}

	/**
	 * @param border
	 */
	@Override
	public void setBorder(@Nullable final Border border) {
		this.border = border;
	}

	/**
	 * @param contentPane
	 */
	void setContentPane(@Nullable final ChildComponent contentPane) {
		this.contentPane = contentPane;
	}

	public void resizeToTerm() {
		if (this.term == null || this.buffer == null) {
			throw new IllegalStateException();
		}

		final Dimension reportedTermSize = this.term.getSize();
		final Dimension effectiveTermSize;
		if (reportedTermSize.isUndefined()) {
			final Dimension defaultTermSize = this.term.getDefaultSize();
			assert !defaultTermSize.isUndefined();
			effectiveTermSize = defaultTermSize;
		} else {
			effectiveTermSize = reportedTermSize;
		}

		this.size = new Dimension(max(effectiveTermSize.getWidth(), this.minimumSize.getWidth()),
				max(effectiveTermSize.getHeight(), this.minimumSize.getHeight()));
		this.buffer.setSize(effectiveTermSize);

		if (this.contentPane != null) {
			final Insets insets = this.border.getBorderInsets();
			final Point location = this.getLocation();
			this.contentPane.setLocation(new Point(location.getX() + insets.getLeft(),
					location.getY() + insets.getTop()));
			this.contentPane.setSize(this.size.subtract(insets));
		}
	}

	private void paintBorder() {
		if (this.border != null) {
			this.border.paintBorder(this, this.buffer);
		}
	}

	private void paintChildren() {
		if (this.contentPane != null) {
			this.contentPane.paint();
		}
	}

	/**
	 * @see com.google.code.christmas.wt.Component#getLocation()
	 */
	@Override
	public Point getLocation() {
		return new Point(1, 1);
	}

	/**
	 * @see com.google.code.christmas.wt.Component#getSize()
	 */
	@Override
	public Dimension getSize() {
		return this.size;
	}

	/**
	 * @see Component#getForeground()
	 */
	@Override
	public Color getForeground() {
		return this.buffer.getForeground();
	}

	/**
	 * @see Component#getBackground()
	 */
	@Override
	public Color getBackground() {
		return this.buffer.getBackground();
	}

	/**
	 * @see Component#setBackground(Color)
	 */
	@Override
	public void setBackground(@Nullable final Color background) {
		this.buffer.setBackground(background == null ? this.term.getDefaultBackground() : background);
	}

	/**
	 * @see Component#getBackgroundPattern()
	 */
	@Override
	public char getBackgroundPattern() {
		return this.buffer.getBackgroundPattern();
	}

	/**
	 * @see Component#setBackgroundPattern(char)
	 */
	@Override
	public void setBackgroundPattern(final char backgroundPattern) {
		this.buffer.setBackgroundPattern(backgroundPattern);
	}

	/**
	 * @see Component#paint()
	 */
	@Override
	public void paint() {
		if (this.size.isUndefined()) {
			return;
		}

		this.paintBorder();
		this.paintChildren();

		this.buffer.paint(this.term);
	}

	/**
	 * @see Container#isTopLevel()
	 */
	@Override
	public boolean isTopLevel() {
		return true;
	}

	/**
	 * @see Container#getComponentBuffer(ChildComponent)
	 */
	@Override
	public ComponentBuffer getComponentBuffer(@Nonnull final ChildComponent child) {
		if (child != this.contentPane) {
			throw new IllegalArgumentException();
		}

		return new AbstractComponentBuffer() {
			/**
			 * @see ComponentBuffer#setTextAt(char, int, int, boolean, Color, Color, TextAttribute[])
			 */
			@Override
			public void setTextAt(final char text, final int x, final int y, final boolean alternateCharset, final Color foreground, final Color background, @Nonnull final TextAttribute... attributes) {
				/*
				 * If the child component exceeds its bounds,
				 * then just clip it.
				 */
				final Dimension childSize = child.getSize();
				if (x > childSize.getWidth() || y > childSize.getHeight()) {
					return;
				}

				final Point childLocation = child.getLocation();
				final int xOffset = childLocation.getX() - 1;
				final int yOffset = childLocation.getY() - 1;
				RootWindow.this.buffer.setTextAt(text, x + xOffset, y + yOffset, alternateCharset, foreground, background, attributes);
			}
		};
	}
}
