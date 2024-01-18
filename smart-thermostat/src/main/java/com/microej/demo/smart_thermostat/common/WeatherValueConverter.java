/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.common;

/**
 * Provides weather value conversions.
 */
public class WeatherValueConverter {

	private static final int DECIMAL_OFFSET_MULTIPLIER = 100;
	private static final int FAHRENHEIT_OFFSET = 32;
	private static final int FIVE = 5;
	private static final int NINE = 9;

	/**
	 * Prevents instantiation.
	 */
	private WeatherValueConverter() {
		// Nothing to do.
	}

	/**
	 * Converts from hundredths of Celsius to Fahrenheit.
	 *
	 * @param hundredthCelsius
	 *            the temperature in hundredths of Celsius as communicated by the natives.
	 *
	 * @return the temperature in Fahrenheit without decimals.
	 */
	public static int hundredthsCelsiusToFahrenheit(int hundredthCelsius) {
		float celsius = (float) hundredthCelsius / DECIMAL_OFFSET_MULTIPLIER;
		float fahrenheit = celsiusToFahrenheit(celsius);
		return Math.round(fahrenheit);
	}

	/**
	 * Converts from Fahrenheit to hundredths of Celsius.
	 *
	 * @param fahrenheit
	 *            the temperature in Fahrenheit to convert.
	 *
	 * @return temperature in hundredths Celsius used to communicate to the natives.
	 */
	public static int fahrenheitToHundredthsCelsius(int fahrenheit) {
		float celsius = fahrenheitToCelsius(fahrenheit);
		return Math.round(celsius * DECIMAL_OFFSET_MULTIPLIER);
	}

	/**
	 * Converts from hundredths of percent to percent.
	 *
	 * @param hundredthsOfPercent
	 *            the hundredths percentage as communicated by the natives.
	 *
	 * @return the percentage without decimals.
	 */
	public static int hundredthsOfPercentToPercent(int hundredthsOfPercent) {
		return Math.round((float) hundredthsOfPercent / DECIMAL_OFFSET_MULTIPLIER);
	}

	/**
	 * Converts Celsius to Fahrenheit.
	 * 
	 * @param celsius
	 *            the temperature in Celsius.
	 * @return the temperature in Fahrenheit.
	 */
	public static float celsiusToFahrenheit(float celsius) {
		return ((celsius * NINE) / FIVE) + FAHRENHEIT_OFFSET;
	}

	/**
	 * Converts Fahrenheit to Celsius.
	 *
	 * @param fahrenheit
	 *            the temperature in Fahrenheit.
	 * @return the temperature in Celsius.
	 */
	public static float fahrenheitToCelsius(float fahrenheit) {
		return ((fahrenheit - FAHRENHEIT_OFFSET) * FIVE) / NINE;
	}
}
