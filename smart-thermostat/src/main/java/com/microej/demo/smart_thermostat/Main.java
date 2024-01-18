/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat;

import com.microej.demo.smart_thermostat.model.SmartThermostatModel;
import com.microej.demo.smart_thermostat.model.ValueProvider;

import ej.microui.MicroUI;

/**
 * Please keep it in sync with the property 'applicationMainClass' defined in build.gradle.kts
 */
public class Main {

	/**
	 * Simple main.
	 *
	 * @param args
	 *            command line arguments.
	 */
	public static void main(String[] args) {
		MicroUI.start();

		// Initialize model
		SmartThermostatModel.setupInitialConfiguration();

		// Initialize UI
		new UI();

		// Initialize value provider
		ValueProvider.start(SmartThermostatModel.getInstance());
	}
}
