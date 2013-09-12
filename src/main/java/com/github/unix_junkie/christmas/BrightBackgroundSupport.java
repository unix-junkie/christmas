/*-
 * $Id$
 */
package com.github.unix_junkie.christmas;

/**
 * @author Andrew ``Bass'' Shcheglov (andrewbass@gmail.com)
 */
public enum BrightBackgroundSupport {
	NONE(false, false),
	AIXTERM_ONLY(false, true),
	BLINK_AND_AIXTERM(true, true),
	;

	private final boolean useBlink;

	private final boolean useAixTerm;

	private final boolean brightColorSupported;

	/**
	 * @param useBlink
	 * @param useAixTerm
	 */
	private BrightBackgroundSupport(final boolean useBlink,
			final boolean useAixTerm) {
		this.useBlink = useBlink;
		this.useAixTerm = useAixTerm;
		this.brightColorSupported = useBlink || useAixTerm;
	}

	public boolean useBlink() {
		return this.useBlink;
	}

	public boolean useAixTerm() {
		return this.useAixTerm;
	}

	public boolean isBrightColorSupported() {
		return this.brightColorSupported;
	}
}
