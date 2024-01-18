/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.page;

import com.microej.demo.smart_thermostat.MainCanvas;
import com.microej.demo.smart_thermostat.common.Page;
import com.microej.demo.smart_thermostat.model.WeatherCondition;
import com.microej.demo.smart_thermostat.style.ClassSelectors;
import com.microej.demo.smart_thermostat.widget.SecondaryInfoSolar;
import com.microej.demo.smart_thermostat.widget.WeatherWidget;

import static com.microej.demo.smart_thermostat.MainCanvas.*;
import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;

/**
 * Shows the outside page.
 */
public class OutsidePage implements Page {
	/** X position for the weather widget. */
	public static final int WEATHER_X = scale(90);
	/** Y position for the weather widget. */
	public static final int WEATHER_Y = scale(280);
	private static final int WEATHER_WIDTH = scale(241);
	private static final int WEATHER_HEIGHT = scale(419);

	private final WeatherWidget weatherWidget;
	private final SecondaryInfoSolar solarBubble;

	/**
	 * Creates the outside page.
	 */
	public OutsidePage() {
		this.weatherWidget = new WeatherWidget();
		this.weatherWidget.setWeather(new WeatherCondition[] { WeatherCondition.FEW_CLOUDS, WeatherCondition.RAIN,
				WeatherCondition.RAIN_SUN, WeatherCondition.CLOUDY, WeatherCondition.THUNDERSTORM,
				WeatherCondition.CLEAR, WeatherCondition.FEW_CLOUDS });
		this.solarBubble = new SecondaryInfoSolar();
		this.solarBubble.addClassSelector(ClassSelectors.SECONDARY_BUBBLE_OUTSIDE);
	}

	@Override
	public void build(MainCanvas canvas) {
		canvas.removeAllChildren();

		canvas.addTransitionWidget();
		canvas.addDateWidget();
		canvas.addOverlay();
		canvas.addOutsideBubbleLabels();

		canvas.addChild(this.weatherWidget, WEATHER_X, WEATHER_Y, WEATHER_WIDTH,
				WEATHER_HEIGHT + WeatherWidget.ANIMATION_EASE_IN_DISTANCE);
		canvas.addChild(this.solarBubble, SECONDARY_BUBBLE_RIGHT_X, SECONDARY_BUBBLE_ONE_Y, SECONDARY_BUBBLE_WIDTH,
				SECONDARY_BUBBLE_HEIGHT);
	}
}
