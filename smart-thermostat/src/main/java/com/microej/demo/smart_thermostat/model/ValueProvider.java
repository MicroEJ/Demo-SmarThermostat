/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.model;

import ej.bon.Timer;

/**
 * Periodically provides values to the thermostat model.
 */
public class ValueProvider {

	private static final long UPDATE_PERIOD = 8000;

	/**
	 * Hides the constructor in order to prevent instantiating a class containing only static methods.
	 */
	private ValueProvider() {
		// prevent instantiation
	}

	/**
	 * Starts the smart thermostat model provisioning.
	 *
	 * @param model
	 *            the model to be provided with values.
	 */
	public static void start(SmartThermostatModel model) {
		DemoTask demoTask = new DemoTask(model);

		Timer timer = new Timer(true);
		timer.scheduleAtFixedRate(demoTask, 0L, UPDATE_PERIOD);
	}
}
