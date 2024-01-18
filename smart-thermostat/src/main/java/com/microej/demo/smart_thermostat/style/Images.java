/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.style;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

import com.microej.demo.smart_thermostat.model.WeatherCondition;

import ej.microui.display.ResourceImage;

/**
 * Images repository of all images located in the resources.
 */
public class Images {

	/** Path for the parallax inside image. */
	public static final String PARALLAX_INSIDE = "/background/home.png";
	/** Path for the parallax outside image. */
	public static final String PARALLAX_OUTSIDE = "/background/garden.png";
	/** Path for the parallax inside scaled image after transition. */
	public static final String PARALLAX_INSIDE_SCALED = "/background/home_scaled.png";
	/** Path for the parallax inside scaled image after transition. */
	public static final String PARALLAX_OUTSIDE_SCALED = "/background/garden_scaled.png";

	/** Path for the curve up gradient image behind the slider. */
	public static final String CURVE_UP = "/curve_slider/curve-gradient-up.png";
	/** Path for the curve up highlighted gradient image behind the slider on pressed. */
	public static final String CURVE_UP_HIGHLIGHT = "/curve_slider/curve-gradient-up_highlight.png";
	/** Path for the curve down gradient image behind the slider. */
	public static final String CURVE_DOWN = "/curve_slider/curve-gradient-down.png";
	/** Path for the curve down highlighted gradient image behind the slider on pressed. */
	public static final String CURVE_DOWN_HIGHLIGHT = "/curve_slider/curve-gradient-down_highlight.png";

	/** Path for the plus button OFF state image for the threshold controller on inside page. */
	public static final String BUTTON_PLUS_OFF = "/buttons/button_+_off.png";
	/** Path for the plus button ON state image for the threshold controller on inside page. */
	public static final String BUTTON_PLUS_ON = "/buttons/button_+_on.png";
	/** Path for the minus button OFF state image for the threshold controller on inside page. */
	public static final String BUTTON_MINUS_OFF = "/buttons/button_-_off.png";
	/** Path for the minus button ON state image for the threshold controller on inside page. */
	public static final String BUTTON_MINUS_ON = "/buttons/button_-_on.png";

	/** Path for the Secondary Info Fan toggle background image. */
	public static final String TOGGLE_BACKGROUND = "/icons/toggle-bkg.png";
	/** Path for the Secondary Info Fan toggle bullet image. */
	public static final String TOGGLE_BULLET = "/icons/toggle-bullet.png";

	/** Path for the transition home scaled rounded rectangle image. */
	public static final String BG_HOME_SCALED_RECT = "/background/home_scaled_rounded_rect.png";
	/** Path for the transition top left rectangle corner. */
	public static final String BG_TOP_LEFT_RECT_CORNER = "/background/top_left_rectangle_corner.png";
	/** Path for the transition top right rectangle corner. */
	public static final String BG_TOP_RIGHT_RECT_CORNER = "/background/top_right_rectangle_corner.png";
	/** Path for the transition top left corner. */
	public static final String BG_TOP_LEFT_CORNER = "/background/top_left_corner.png";
	/** Path for the transition top right rectangle corner. */
	public static final String BG_TOP_RIGHT_CORNER = "/background/top_right_corner.png";
	/** Path for the transition home scaled ceiling on the left. */
	public static final String BG_HOME_SCALED_CEILING_LEFT = "/background/home_scaled_ceiling_left.png";
	/** Path for the transition home scaled ceiling on the right. */
	public static final String BG_HOME_SCALED_CEILING_RIGHT = "/background/home_scaled_ceiling_right.png";

	private Images() {
		// Prevent instantiation.
	}

	/**
	 * Gets the Image associated with the given {@link WeatherCondition}.
	 * <p>
	 * NOTE: {@link ResourceImage}s need to be closed with {@code image.close()} when not being used anymore, or there
	 * will be a memory leak.
	 * </p>
	 *
	 * @param conditionCode
	 *            the {@link WeatherCondition} to get the image for.
	 * @param big
	 *            {@code true}, if the returned image should be the big one. {@code false} otherwise.
	 * @return the {@link ResourceImage} for the given {@link WeatherCondition}.
	 */
	public static ResourceImage getWeatherImage(WeatherCondition conditionCode, boolean big) {
		return getResourceImage("/weather/" + conditionCode.name().toLowerCase() + (big ? "_big.png" : ".png"));
	}

	/**
	 * Gets the Image for the given path.
	 * <p>
	 * Use constants in {@link Images} for possible paths.
	 * </p>
	 * <p>
	 * NOTE: {@link ResourceImage}s need to be closed with {@code image.close()} when not being used anymore, or there
	 * will be a memory leak.
	 * </p>
	 * 
	 * @param path
	 *            the path to get the image from.
	 * @return the {@link ResourceImage} for the given path.
	 */
	public static ResourceImage getResourceImage(String path) {
		String dpi = LOW_RESOLUTION ? "ldpi" : "hdpi";
		return ResourceImage.loadImage("/images/" + dpi + path);
	}

	/**
	 * Gets the Image for the given path with the given format.
	 * <p>
	 * Use constants in {@link Images} for possible paths.
	 * </p>
	 * <p>
	 * NOTE: {@link ResourceImage}s need to be closed with {@code image.close()} when not being used anymore, or there
	 * will be a memory leak.
	 * </p>
	 *
	 * @param path
	 *            the path to get the image from.
	 * @param format
	 *            the {@link ResourceImage.OutputFormat} to get the image in.
	 * @return the {@link ResourceImage} for the given path.
	 */
	public static ResourceImage getResourceImage(String path, ResourceImage.OutputFormat format) {
		String dpi = LOW_RESOLUTION ? "ldpi" : "hdpi";
		return ResourceImage.loadImage("/images/" + dpi + path, format);
	}
}
