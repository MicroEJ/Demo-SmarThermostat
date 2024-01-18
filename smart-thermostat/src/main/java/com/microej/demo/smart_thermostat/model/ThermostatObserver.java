/*
 * Java
 *
 * Copyright 2021-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.model;

/**
 * Observer to handle updates from the {@link SmartThermostatModel}.
 */
public interface ThermostatObserver {

	/**
	 * Updates the observer.
	 *
	 * @param valueType
	 *            the type of value to update. See {@link SmartThermostatModel} for possible types.
	 * @param newValue
	 *            the new value.
	 */
	void update(int valueType, int newValue);
}
