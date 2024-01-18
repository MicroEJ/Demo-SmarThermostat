/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat;

import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;
import static com.microej.demo.smart_thermostat.common.Context.State.*;

import com.microej.demo.smart_thermostat.common.*;
import com.microej.demo.smart_thermostat.page.HomePage;
import com.microej.demo.smart_thermostat.page.InsidePage;
import com.microej.demo.smart_thermostat.page.OutsidePage;
import com.microej.demo.smart_thermostat.style.Images;
import com.microej.demo.smart_thermostat.style.StylesheetBuilder;

import ej.microui.display.Display;
import ej.microui.display.ResourceImage;
import ej.motion.Motion;
import ej.motion.linear.LinearFunction;
import ej.motion.sine.SineEaseInOutFunction;
import ej.widget.motion.MotionAnimation;
import ej.widget.motion.MotionAnimationListener;

/**
 * UI defines the entry point of the UI.
 */
public class UI implements ActionListener {

	private static final double HUNDRED = 100d;
	private static final long TRANSITION_ANIMATION_DURATION = 700;
	private static final int PARALLAX_ANIMATION_DURATION = 10000;
	private static final int MOTION_RANGE = scale(40);

	private static final int OVERLAY_HOME_START = 0;
	private static final int OVERLAY_HOME_TO_INSIDE = 2200;
	private static final int OVERLAY_HOME_TO_OUTSIDE = 4000;
	private static final int OVERLAY_INSIDE_END = 3200;
	private static final int OVERLAY_OUTSIDE_END = 1000;
	private static final int OVERLAY_RETURN = 1000;

	private final NavigationDesktop desktop;

	private final int parallaxX;
	private final HomePage homePage;
	private final InsidePage insidePage;
	private final OutsidePage outsidePage;
	private final MainCanvas mainCanvas;

	private int parallaxAnimationValue;
	private MotionAnimation motionAnimation;

	private int overlayElapsed;
	private final int halfWidth;

	private final Progress transitionProgress;

	/**
	 * Creates the UI.
	 */
	public UI() {
		this.transitionProgress = new Progress();
		this.overlayElapsed = 0;

		Display display = Display.getDisplay();
		int width = display.getWidth();
		this.halfWidth = width / 2;

		ResourceImage parallaxInside = Images.getResourceImage(Images.PARALLAX_INSIDE);
		this.parallaxX = parallaxInside.getWidth() / 2;
		parallaxInside.close();

		this.mainCanvas = new MainCanvas(this);

		this.homePage = new HomePage();
		this.insidePage = new InsidePage();
		this.outsidePage = new OutsidePage();

		this.desktop = new NavigationDesktop(this);

		this.desktop.setWidget(this.mainCanvas);
		this.desktop.setStylesheet(StylesheetBuilder.build());

		buildPage(this.homePage);
		this.desktop.requestShow();
		startParallaxMotion();
	}

	private void buildPage(Page page) {
		page.build(this.mainCanvas);
	}

	/**
	 * Navigates to the Home page.
	 *
	 * @return {@code true}, if we navigate successfully. {@code false}, if we are already at home.
	 */
	@Override
	public boolean navigateHome() {
		Context context = Context.INSTANCE;
		Context.State state = context.getCurrentState();
		if (state.equals(HOME)) {
			// already home
			return false;
		}
		context.setState(TRANSITION_HOME);
		this.mainCanvas.buildTransition();
		animateOverlay(this.overlayElapsed, this.overlayElapsed - OVERLAY_RETURN);
		return true;
	}

	/**
	 * Navigates to the inside page.
	 *
	 * @return {@code true}, if we navigate successfully. {@code false}, if we are not at home.
	 */
	@Override
	public boolean navigateInside() {
		Context context = Context.INSTANCE;
		Context.State state = context.getCurrentState();
		if (!state.equals(HOME)) {
			// only possible from home! Go home first.
			return false;
		}
		context.setState(TRANSITION_INSIDE);
		this.mainCanvas.buildTransition();
		this.transitionProgress.configure(0, this.halfWidth);
		animateOverlay(OVERLAY_HOME_TO_INSIDE, OVERLAY_INSIDE_END);
		return true;
	}

	/**
	 * Navigates to the outside page.
	 *
	 * @return {@code true}, if we navigate successfully. {@code false}, if we are not at home.
	 */
	@Override
	public boolean navigateOutside() {
		Context context = Context.INSTANCE;
		Context.State state = context.getCurrentState();
		if (!state.equals(HOME)) {
			// only possible from home! Go home first.
			return false;
		}
		context.setState(TRANSITION_OUTSIDE);
		this.mainCanvas.buildTransition();
		this.transitionProgress.configure(-this.halfWidth, 0, Progress.Direction.BACKWARD);
		animateOverlay(OVERLAY_HOME_START, OVERLAY_OUTSIDE_END);
		return true;
	}

	private void animateOverlay(int src, int dst) {
		final Progress motionProgress = new Progress();
		if (src > dst) {
			motionProgress.configure(dst, src);
		} else {
			motionProgress.configure(src, dst);
		}
		stopAnimation();
		Motion motion = new Motion(LinearFunction.INSTANCE, src, dst, TRANSITION_ANIMATION_DURATION);
		this.motionAnimation = new MotionAnimation(this.desktop.getAnimator(), motion, new MotionAnimationListener() {
			@Override
			public void tick(int value, boolean finished) {
				UI.this.overlayElapsed = value;
				motionProgress.setValue(value);
				UI.this.transitionProgress.setPercent(motionProgress.getPercent());
				UI.this.mainCanvas.requestLayOut();
				if (finished) {
					Context context = Context.INSTANCE;
					if (atHome()) {
						context.setState(HOME);
						buildPage(UI.this.homePage);
						startParallaxMotion();
					} else {
						if (context.getCurrentState().equals(TRANSITION_INSIDE)) {
							context.setState(INSIDE);
							buildPage(UI.this.insidePage);
						} else {
							context.setState(OUTSIDE);
							buildPage(UI.this.outsidePage);
						}
					}
				}
			}
		});
		this.motionAnimation.start();
	}

	private void startParallaxMotion() {
		int min;
		int max;
		if (this.parallaxAnimationValue >= 0) {
			min = this.parallaxAnimationValue;
			max = -MOTION_RANGE;
		} else {
			min = this.parallaxAnimationValue;
			max = MOTION_RANGE;
		}
		int diff = Math.abs(min - max);
		double currentPercentage = diff / (double) this.parallaxX * HUNDRED;
		long duration = (long) (PARALLAX_ANIMATION_DURATION * currentPercentage / HUNDRED);
		Motion motion = new Motion(SineEaseInOutFunction.INSTANCE, min, max, duration);
		this.motionAnimation = new MotionAnimation(this.desktop.getAnimator(), motion, new MotionAnimationListener() {

			@Override
			public void tick(int value, boolean finished) {
				UI.this.parallaxAnimationValue = value;
				UI.this.mainCanvas.renderMainCanvas();
				if (finished) {
					UI.this.mainCanvas.onParallaxFinished();
					startParallaxMotion();
				}
			}
		});
		this.motionAnimation.start();
		this.mainCanvas.onParallaxStarted();
	}

	private void stopAnimation() {
		if (this.motionAnimation != null) {
			this.motionAnimation.stop();
		}
	}

	/**
	 * Gets if we are currently at home.
	 *
	 * @return {@code true}, if we are currently at home.
	 */
	@Override
	public boolean atHome() {
		int elapsed = this.overlayElapsed;
		return elapsed == OVERLAY_HOME_START || elapsed == OVERLAY_HOME_TO_INSIDE || elapsed == OVERLAY_HOME_TO_OUTSIDE;
	}

	public int getParallaxAnimationValue() {
		return this.parallaxAnimationValue;
	}

	public int getOverlayElapsed() {
		return this.overlayElapsed;
	}

	public Progress getTransitionProgress() {
		return this.transitionProgress;
	}
}
