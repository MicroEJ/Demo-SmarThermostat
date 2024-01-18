/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.model;

/**
 * Describes the different WeatherConditions that can be displayed in the forecast.
 */
public enum WeatherCondition {
	CLEAR(800), //
	CLOUDY(802), //
	FEW_CLOUDS(801), //
	RAIN(300), //
	RAIN_SUN(500), //
	THUNDERSTORM(200);

	private final int conditionCode;

	WeatherCondition(int conditionCode) {
		this.conditionCode = conditionCode;
	}

	/**
	 * Gets the WeatherCondition from the given condition code.
	 * <p>
	 * The condition code is based on <a
	 * href=https://openweathermap.org/weather-conditions#Weather-Condition-Codes-2>OpenWeather API</a>. Since the
	 * actual weather provider is not known we only implemented one code for each icon.
	 * </p>
	 * 
	 * @param conditionCode
	 *            the condition code as int.
	 * @return the WeatherCondition.
	 */
	public static WeatherCondition getConditionFromCode(int conditionCode) {
		for (WeatherCondition condition : WeatherCondition.values()) {
			if (condition.conditionCode == conditionCode) {
				return condition;
			}
		}
		return CLOUDY;
	}
}
