/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.style;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

/**
 * AVD repository of all AVD XML files located in the resources.
 */
public class VectorImages {

	/** Path for the vector images base folder. */
	private static final String BASE = "/vector-images/";
	/** Path for the vector icons folder. */
	private static final String ICONS = BASE + "icons/";
	/** Path for the vector popup images folder. */
	private static final String POPUP = BASE + "popup/";
	/** Path for the vector transition images folder. */
	private static final String TRANSITION = BASE + "transition-inside-outside/";

	/** Path for the threshold popup vector image. */
	public static final String THRESHOLD_POPUP = POPUP + "temp_threshold_popup.xml";
	/** Path for the threshold popup UP state vector image. */
	public static final String THRESHOLD_POPUP_UP = POPUP + "temp_threshold_popup_up.xml";
	/** Path for the threshold popup DOWN state vector image. */
	public static final String THRESHOLD_POPUP_DOWN = POPUP + "temp_threshold_popup_down.xml";
	/** Path for the bubble vector image. */
	public static final String BUBBLE = TRANSITION + "bubble.xml";
	/** Path for the animated overlay vector image. */
	public static final String OVERLAY = TRANSITION + "dynamic_overlay.xml";
	/** Path for the static home overlay vector image. */
	public static final String STATIC_HOME_OVERLAY = TRANSITION + "static_overlay.xml";
	/** Path for the static inside page overlay vector image. */
	public static final String STATIC_INSIDE_OVERLAY = TRANSITION + "static_inside.xml";

	/** Path for the humidity icon vector image. */
	public static final String HUMIDITY = ICONS + "humidity.xml";
	/** Path for the pressure icon vector image. */
	public static final String PRESSURE = ICONS + "pressure.xml";
	/** Path for the battery icon vector image. */
	public static final String BATTERY = ICONS + "battery.xml";
	/** Path for the animated fan icon vector image. */
	public static final String FAN = ICONS + "fan.xml";

	/** Path for the animated solar panel icon vector image. */
	public static final String SOLAR_PANEL = ICONS + (LOW_RESOLUTION ? "solar_panel.xml" : "solar_panel_static.xml");
	/** Path for the animated plant icon vector image. */
	public static final String PLANT = ICONS + (LOW_RESOLUTION ? "plant.xml" : "plant_static.xml");
	/** Path for the animated big fan icon vector image. */
	public static final String FAN_BIG = ICONS + (LOW_RESOLUTION ? "fan_big.xml" : "fan_big_static.xml");

	/**
	 * Hides the constructor in order to prevent instantiating a class containing only static methods.
	 */
	private VectorImages() {
		// prevent instantiation
	}
}
