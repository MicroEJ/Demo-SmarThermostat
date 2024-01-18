/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import ej.bon.Util;
import ej.motion.Motion;
import ej.motion.sine.SineEaseInFunction;
import ej.mwt.Widget;
import ej.mwt.animation.Animation;

/**
 * A widget that starts an animation when shown and providing an alpha value to handle fade in.
 */
public abstract class FadeInWidget extends Widget {
	protected static final int MAX_ALPHA = 255;
	private static final long DEFAULT_ANIMATION_DURATION = 300;
	private final Motion alphaMotion;
	private final long animationDuration;
	private final Animation fadeInAnimation;
	private long animationStartTime;
	private long animationElapsedTime;

	/**
	 * Creates a FadeInWidget.
	 * <p>
	 * The duration will be {@value DEFAULT_ANIMATION_DURATION} and the widget is disabled.
	 * </p>
	 */
	public FadeInWidget() {
		this(DEFAULT_ANIMATION_DURATION, false);
	}

	/**
	 * Creates a FadeInWidget.
	 * <p>
	 * The widget is disabled.
	 * </p>
	 *
	 * @param animationDuration
	 *            the duration for the animation. Default: {@value DEFAULT_ANIMATION_DURATION}.
	 */
	public FadeInWidget(long animationDuration) {
		this(animationDuration, false);
	}

	/**
	 * Creates a FadeInWidget.
	 * <p>
	 * The duration will be {@value DEFAULT_ANIMATION_DURATION}.
	 * </p>
	 *
	 * @param enabled
	 *            {@code true} if this widget is to be enabled, {@code false} otherwise.
	 */
	public FadeInWidget(boolean enabled) {
		this(DEFAULT_ANIMATION_DURATION, enabled);
	}

	/**
	 * Creates a FadeInWidget.
	 *
	 * @param animationDuration
	 *            the duration for the animation. Default: {@value DEFAULT_ANIMATION_DURATION}.
	 * @param enabled
	 *            {@code true} if this widget is to be enabled, {@code false} otherwise.
	 */
	public FadeInWidget(long animationDuration, boolean enabled) {
		super(enabled);
		this.animationDuration = animationDuration;
		this.alphaMotion = new Motion(SineEaseInFunction.INSTANCE, 0, MAX_ALPHA, animationDuration);

		this.fadeInAnimation = new Animation() {
			@Override
			public boolean tick(long platformTimeMillis) {
				long elapsedTime = platformTimeMillis - FadeInWidget.this.animationStartTime;
				FadeInWidget.this.animationElapsedTime = elapsedTime;
				requestRender();
				if (elapsedTime >= FadeInWidget.this.animationDuration) {
					onFadeInDone();
					return false;
				}
				return true;
			}
		};
	}

	protected void onFadeInDone() {
		// Nothing to do here yet.
	}

	/**
	 * Gets the duration of the animation.
	 *
	 * @return the animation duration.
	 */
	protected long getFadeInDuration() {
		return this.animationDuration;
	}

	/**
	 * Gets the elapsed time of the animation.
	 *
	 * @return the elapsed time.
	 */
	protected long getFadeInElapsedTime() {
		return this.animationElapsedTime;
	}

	/**
	 * Gets the current alpha value.
	 *
	 * @return the alpha value.
	 */
	protected int getAlpha() {
		return getAlpha(getFadeInElapsedTime());
	}

	/**
	 * Gets the alpha value for the given elapsed time.
	 *
	 * @param elapsedTime
	 *            the elapsed time to get the alpha value for.
	 * @return the alpha value for the given time.
	 */
	protected int getAlpha(long elapsedTime) {
		return this.alphaMotion.getValue(elapsedTime);
	}

	@Override
	protected void onShown() {
		this.animationElapsedTime = 0;
		this.animationStartTime = Util.platformTimeMillis();
		getDesktop().getAnimator().startAnimation(this.fadeInAnimation);
	}

	@Override
	protected void onHidden() {
		getDesktop().getAnimator().stopAnimation(this.fadeInAnimation);
	}
}
