/*-
 * $Id$
 */
package com.google.code.christmas;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum BrightForegroundSupport {
	BOLD_ONLY(true, false),
	AIXTERM_ONLY(false, true),
	BOLD_AND_AIXTERM(true, true),
	;

	private final boolean useBold;

	private final boolean useAixTerm;

	private final boolean brightColorSupported;

	/**
	 * @param useBold
	 * @param useAixTerm
	 */
	private BrightForegroundSupport(final boolean useBold,
			final boolean useAixTerm) {
		this.useBold = useBold;
		this.useAixTerm = useAixTerm;
		this.brightColorSupported = useBold || useAixTerm;
	}

	public boolean useBold() {
		return this.useBold;
	}

	public boolean useAixTerm() {
		return this.useAixTerm;
	}

	public boolean isBrightColorSupported() {
		return this.brightColorSupported;
	}
}
