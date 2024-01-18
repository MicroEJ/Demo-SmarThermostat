/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.model;

/**
 * Contains the default values used for the Smart Thermostat.
 */
public class SmartThermostatDefaultValues {

	/** Default value for Temperature. */
	public static final int DEFAULT_TEMPERATURE = 70;
	/** Default value for Humidity. */
	public static final int DEFAULT_HUMIDITY = 50;
	/** Default value for Pressure. */
	public static final int DEFAULT_PRESSURE = 1024;
	/** Default value for Temperature Threshold. */
	public static final int DEFAULT_TEMPERATURE_THRESHOLD = 69;

	/**
	 * Hides the constructor in order to prevent instantiating a class containing only static methods.
	 */
	private SmartThermostatDefaultValues() {
		// prevent instantiation
	}
}
