/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.page;

import com.microej.demo.smart_thermostat.MainCanvas;
import com.microej.demo.smart_thermostat.common.Page;
import com.microej.demo.smart_thermostat.style.ClassSelectors;
import com.microej.demo.smart_thermostat.widget.CircularSlider;
import com.microej.demo.smart_thermostat.widget.SecondaryInfoFan;
import com.microej.demo.smart_thermostat.widget.SecondaryInfoHumidity;

import static com.microej.demo.smart_thermostat.MainCanvas.*;
import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

/**
 * Shows the inside page.
 */
public class InsidePage implements Page {

	/** X position for the circular slider. */
	public static final int CIRCULAR_SLIDER_X = LOW_RESOLUTION ? 324 : 487;
	/** Y position for the circular slider. */
	public static final int CIRCULAR_SLIDER_Y = LOW_RESOLUTION ? 267 : 373;

	private final CircularSlider circularSlider;
	private final SecondaryInfoFan fanBubble;
	private final SecondaryInfoHumidity humidityBubble;

	/**
	 * Creates the inside page.
	 */
	public InsidePage() {
		this.circularSlider = new CircularSlider();
		this.fanBubble = new SecondaryInfoFan();
		this.fanBubble.addClassSelector(ClassSelectors.SECONDARY_BUBBLE_INSIDE);
		this.humidityBubble = new SecondaryInfoHumidity();
		this.humidityBubble.addClassSelector(ClassSelectors.SECONDARY_BUBBLE_INSIDE);

		this.circularSlider.addSliderListener(this.fanBubble);
		this.circularSlider.addSliderListener(this.humidityBubble);
	}

	@Override
	public void build(MainCanvas canvas) {
		canvas.removeAllChildren();

		canvas.addTransitionWidget();
		canvas.addDateWidget();
		canvas.addInsideBubbleLabels();

		canvas.addChild(this.circularSlider, CIRCULAR_SLIDER_X, CIRCULAR_SLIDER_Y, this.circularSlider.getWidth(),
				this.circularSlider.getHeight());
		canvas.addChild(this.humidityBubble, SECONDARY_BUBBLE_LEFT_X, SECONDARY_BUBBLE_ONE_Y, SECONDARY_BUBBLE_WIDTH,
				SECONDARY_BUBBLE_HEIGHT);
		canvas.addChild(this.fanBubble, SECONDARY_BUBBLE_RIGHT_X, SECONDARY_BUBBLE_ONE_Y, SECONDARY_BUBBLE_WIDTH,
				SECONDARY_BUBBLE_HEIGHT);
	}
}
