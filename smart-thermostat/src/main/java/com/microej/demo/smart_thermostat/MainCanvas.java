/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat;

import com.microej.demo.smart_thermostat.common.AnimationValue;
import com.microej.demo.smart_thermostat.common.Context;
import com.microej.demo.smart_thermostat.style.ClassSelectors;
import com.microej.demo.smart_thermostat.widget.*;
import ej.microui.display.Display;
import ej.widget.container.Canvas;

import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;
import static com.microej.demo.smart_thermostat.common.Context.State.TRANSITION_OUTSIDE;

/**
 * Main canvas container for all application widgets.
 */
public class MainCanvas extends Canvas {

	/** Secondary bubble left x position from left. */
	public static final int SECONDARY_BUBBLE_LEFT_X = scale(49);
	/** Secondary bubble left x position from right. */
	public static final int SECONDARY_BUBBLE_RIGHT_X = scale(373);
	/** Secondary bubble one y position from top. */
	public static final int SECONDARY_BUBBLE_ONE_Y = scale(802);
	/** Secondary bubble width. */
	public static final int SECONDARY_BUBBLE_WIDTH = scale(300);
	/** Secondary bubble height. */
	public static final int SECONDARY_BUBBLE_HEIGHT = scale(186);
	/** Margin from the top for the temperature threshold. */
	public static final int TEMP_THRESHOLD_MARGIN_TOP = scale(20);
	/** Margin from the left for the temperature threshold. */
	public static final int TEMP_THRESHOLD_MARGIN_LEFT = scale(20);
	/** Height for the temperature threshold. */
	public static final int TEMP_THRESHOLD_HEIGHT = scale(120);

	private static final int BUBBLE_X = scale(48);
	private static final int BUBBLE_Y = scale(178);

	private static final int DATE_MARGIN_TOP = scale(15);
	private static final int DATE_HEIGHT = scale(45);

	private static final int INSIDE_LABELS_Y_OFFSET = scale(320);
	private static final int INSIDE_LABELS_X = scale(160);
	private static final int OUTSIDE_LABELS_X = scale(392);
	private static final int INSIDE_LABELS_WIDTH = scale(200);
	private static final int OUTSIDE_LABELS_WIDTH = scale(187);
	private static final int LABELS_HEIGHT = scale(340);

	private static final int PARALLAX_BOTTOM_OFFSET = scale(460);

	private final BubbleLabels insideBubbleLabels;
	private final BubbleLabels outsideBubbleLabels;

	private final SkyColor skyColor;
	private final DateWidget dateWidget;
	private final Parallax parallax;
	private final Transition transition;
	private final Overlay overlay;

	private final int displayWidth;
	private final int displayHeight;
	private final int displayHalfWidth;
	private final BubbleWidget bubbleWidget;
	private final TempThresholdPopup tempThresholdPopup;

	/**
	 * Creates the main canvas.
	 *
	 * @param ui
	 *            the {@link UI} to add.
	 */
	public MainCanvas(final UI ui) {
		Display display = Display.getDisplay();
		this.displayWidth = display.getWidth();
		this.displayHalfWidth = this.displayWidth / 2;
		this.displayHeight = display.getHeight();

		this.parallax = new Parallax(new AnimationValue() {
			@Override
			public int getValue() {
				return ui.getParallaxAnimationValue();
			}
		});
		this.transition = new Transition(ui.getTransitionProgress(), this.parallax);
		this.overlay = new Overlay(new AnimationValue() {
			@Override
			public int getValue() {
				return ui.getOverlayElapsed();
			}
		});

		this.skyColor = new SkyColor();
		this.bubbleWidget = new BubbleWidget();
		this.dateWidget = new DateWidget();

		this.insideBubbleLabels = new BubbleLabels(ui, true);
		this.insideBubbleLabels.addClassSelector(ClassSelectors.INSIDE_PAGE_LABELS);
		this.outsideBubbleLabels = new BubbleLabels(ui, false);
		this.outsideBubbleLabels.addClassSelector(ClassSelectors.OUTSIDE_PAGE_LABELS);

		this.tempThresholdPopup = new TempThresholdPopup();
	}

	/**
	 * Adds the overlay widget {@link Overlay} to the main canvas.
	 */
	public void addOverlay() {
		addChild(this.overlay, 0, 0, this.overlay.getWidth(), this.overlay.getHeight());
	}

	/**
	 * Adds the parallax widget {@link Parallax} to the main canvas.
	 */
	public void addParallax() {
		addChild(this.parallax, 0, this.displayHeight - PARALLAX_BOTTOM_OFFSET, this.parallax.getWidth(),
				this.parallax.getHeight());
	}

	/**
	 * Adds the transition widget {@link Transition} to the main canvas.
	 */
	public void addTransitionWidget() {
		addChild(this.transition, 0, 0, this.displayWidth, this.displayHeight);
	}

	/**
	 * Adds the bubble widget {@link BubbleWidget} to the main canvas.
	 */
	public void addBubbleWidget() {
		addChild(this.bubbleWidget, BUBBLE_X, BUBBLE_Y, this.bubbleWidget.getWidth(), this.bubbleWidget.getHeight());
	}

	/**
	 * Adds the sky color widget {@link SkyColor} to the main canvas.
	 */
	public void addSkyColor() {
		addChild(this.skyColor, 0, 0, this.skyColor.getWidth(), this.skyColor.getHeight());
	}

	/**
	 * Adds the inside bubble labels {@link BubbleLabels} to the main canvas.
	 */
	public void addInsideBubbleLabels() {
		addChild(this.insideBubbleLabels, INSIDE_LABELS_X, INSIDE_LABELS_Y_OFFSET, INSIDE_LABELS_WIDTH, LABELS_HEIGHT);
	}

	/**
	 * Adds the outside bubble labels {@link BubbleLabels} to the main canvas.
	 */
	public void addOutsideBubbleLabels() {
		addChild(this.outsideBubbleLabels, OUTSIDE_LABELS_X, INSIDE_LABELS_Y_OFFSET, OUTSIDE_LABELS_WIDTH,
				LABELS_HEIGHT);
	}

	/**
	 * Adds the date widget {@link DateWidget} to the main canvas.
	 */
	public void addDateWidget() {
		addChild(this.dateWidget, this.displayHalfWidth, DATE_MARGIN_TOP, this.displayHalfWidth, DATE_HEIGHT);
	}

	/**
	 * Adds the threshold popup widget {@link TempThresholdPopup} to the main canvas.
	 */
	public void addThresholdPopup() {
		addChild(this.tempThresholdPopup, TEMP_THRESHOLD_MARGIN_LEFT, TEMP_THRESHOLD_MARGIN_TOP, this.displayHalfWidth,
				TEMP_THRESHOLD_HEIGHT);
	}

	/**
	 * Builds the transition between home-inside and home-outside.
	 */
	public void buildTransition() {
		removeAllChildren();

		addTransitionWidget();
		addOverlay();

		if (Context.INSTANCE.getCurrentState().equals(TRANSITION_OUTSIDE)) {
			addOutsideBubbleLabels();
		} else {
			addInsideBubbleLabels();
		}
	}

	/**
	 * Requests a new render of the screen and ticks the {@link BubbleWidget} and {@link TempThresholdPopup}.
	 */
	public void renderMainCanvas() {
		this.bubbleWidget.tick();
		this.tempThresholdPopup.tick();
		super.requestRender();
	}

	/**
	 * Handles callback when parallax animation is finished.
	 */
	public void onParallaxFinished() {
		this.tempThresholdPopup.parallaxRunning(false); // Stop it so the bubble updating goes alone.
		updateBubbleLabels();
	}

	/**
	 * Handles callback when parallax animation is started.
	 */
	public void onParallaxStarted() {
		this.tempThresholdPopup.parallaxRunning(true);
	}

	/**
	 * Updates the {@link BubbleWidget}s.
	 */
	public void updateBubbleLabels() {
		this.outsideBubbleLabels.updateBubbleLabels();
		this.insideBubbleLabels.updateBubbleLabels();
	}

}
