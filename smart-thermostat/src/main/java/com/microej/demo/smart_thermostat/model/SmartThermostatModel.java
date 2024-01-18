/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ej.microui.MicroUI;

/**
 * The Smart Thermostat Model to set / load values.
 */
public class SmartThermostatModel {
	private static final int TEMPERATURE_TYPE = 1;
	private static final int HUMIDITY_TYPE = 2;
	private static final int PRESSURE_TYPE = 3;
	private static final int TEMPERATURE_THRESHOLD_TYPE = 4;
	private static SmartThermostatModel smartThermostatModel;
	private final Map<Integer, List<ThermostatObserver>> observers = new HashMap<>();

	private int temperature;
	private int humidity;
	private int pressure;
	private int temperatureThreshold;

	private final int temperatureType;
	private final int humidityType;
	private final int pressureType;
	private final int temperatureThresholdType;

	private final Object lock = new Object();

	@SuppressWarnings("java:S2293") // We can not remove the Type in <> since this causes an error.
	// private constructor to prevent instantiation
	private SmartThermostatModel() {
		// Get the types for the listeners
		this.temperatureType = TEMPERATURE_TYPE;
		this.humidityType = HUMIDITY_TYPE;
		this.pressureType = PRESSURE_TYPE;
		this.temperatureThresholdType = TEMPERATURE_THRESHOLD_TYPE;

		this.observers.put(this.temperatureType, new ArrayList<ThermostatObserver>());
		this.observers.put(this.humidityType, new ArrayList<ThermostatObserver>());
		this.observers.put(this.pressureType, new ArrayList<ThermostatObserver>());
		this.observers.put(this.temperatureThresholdType, new ArrayList<ThermostatObserver>());
	}

	/**
	 * Method to retrieve the singleton instance
	 *
	 * @return an instance of {@link SmartThermostatModel}
	 */
	public static SmartThermostatModel getInstance() {
		if (smartThermostatModel == null) {
			smartThermostatModel = new SmartThermostatModel();
			setupDefaultValues();
		}

		return smartThermostatModel;
	}

	/**
	 * Adds a new observer for the given types.
	 * 
	 * @param observer
	 *            the {@link ThermostatObserver} to add.
	 * @param types
	 *            one or more types for which to add the observer.
	 */
	public void addObserver(ThermostatObserver observer, int... types) {
		for (int t : types) {
			List<ThermostatObserver> list = this.observers.get(t);
			if (list != null && !list.contains(observer)) {
				list.add(observer);
			}
		}
	}

	/**
	 * Removes observer for the given types.
	 *
	 * @param observer
	 *            the {@link ThermostatObserver} to remove.
	 * @param types
	 *            one or more types to remove the Observer from.
	 */
	public void removeObserver(ThermostatObserver observer, int... types) {
		for (int t : types) {
			List<ThermostatObserver> list = this.observers.get(t);
			if (list != null) {
				list.remove(observer);
			}
		}
	}

	/**
	 * Notifies the observer about a value change.
	 * 
	 * @param valueType
	 *            the type of value that changed.
	 * @param value
	 *            the new value.
	 */
	public void notifyObservers(final int valueType, final int value) {
		MicroUI.callSerially(new Runnable() {
			@Override
			public void run() {
				List<ThermostatObserver> targetList = SmartThermostatModel.this.observers.get(valueType);
				if (targetList != null) {
					for (ThermostatObserver observer : targetList) {
						observer.update(valueType, value);
					}
				}

			}
		});
	}

	/**
	 * Sets up the default values for the Smart Thermostat configuration.
	 */
	public static void setupInitialConfiguration() {
		getInstance();
	}

	private static void setupDefaultValues() {
		smartThermostatModel.setTemperature(SmartThermostatDefaultValues.DEFAULT_TEMPERATURE);
		smartThermostatModel.setHumidity(SmartThermostatDefaultValues.DEFAULT_HUMIDITY);
		smartThermostatModel.setPressure(SmartThermostatDefaultValues.DEFAULT_PRESSURE);
		smartThermostatModel.setTemperatureThreshold(SmartThermostatDefaultValues.DEFAULT_TEMPERATURE_THRESHOLD);
	}

	/**
	 * Gets the temperature type.
	 *
	 * @return the temperature type.
	 */
	public int getTemperatureType() {
		return this.temperatureType;
	}

	/**
	 * Gets the humidity type.
	 *
	 * @return the humidity type.
	 */
	public int getHumidityType() {
		return this.humidityType;
	}

	/**
	 * Gets the pressure type.
	 *
	 * @return the pressure type.
	 */
	public int getPressureType() {
		return this.pressureType;
	}

	/**
	 * Gets the temperature threshold type.
	 *
	 * @return the temperature threshold type.
	 */
	public int getTemperatureThresholdType() {
		return this.temperatureThresholdType;
	}

	/**
	 * Gets the temperature.
	 *
	 * @return the temperature.
	 */
	public int getTemperature() {
		synchronized (this.lock) {
			return this.temperature;
		}
	}

	/**
	 * Sets the temperature.
	 *
	 * @param temperature
	 *            the temperature to set.
	 */
	public void setTemperature(int temperature) {
		synchronized (this.lock) {
			this.temperature = temperature;
		}
		notifyObservers(this.temperatureType, temperature);
	}

	/**
	 * Gets the humidity.
	 *
	 * @return the humidity.
	 */
	public int getHumidity() {
		synchronized (this.lock) {
			return this.humidity;
		}
	}

	/**
	 * Sets the humidity.
	 *
	 * @param humidity
	 *            the humidity to set.
	 */
	public void setHumidity(int humidity) {
		synchronized (this.lock) {
			this.humidity = humidity;
		}
		notifyObservers(this.humidityType, humidity);
	}

	/**
	 * Gets the pressure.
	 *
	 * @return the pressure.
	 */
	public int getPressure() {
		synchronized (this.lock) {
			return this.pressure;
		}
	}

	/**
	 * Sets the pressure.
	 *
	 * @param pressure
	 *            the pressure to set.
	 */
	public void setPressure(int pressure) {
		synchronized (this.lock) {
			this.pressure = pressure;
		}
		notifyObservers(this.pressureType, pressure);
	}

	/**
	 * Gets the temperatureThreshold.
	 *
	 * @return the temperatureThreshold.
	 */
	public int getTemperatureThreshold() {
		synchronized (this.lock) {
			return this.temperatureThreshold;
		}
	}

	/**
	 * Sets the temperatureThreshold. Value is set in Fahrenheit.
	 *
	 * @param temperatureThreshold
	 *            the temperatureThreshold to set.
	 */
	public void setTemperatureThreshold(int temperatureThreshold) {
		synchronized (this.lock) {
			this.temperatureThreshold = temperatureThreshold;
		}
		notifyObservers(this.temperatureThresholdType, temperatureThreshold);
	}

	/**
	 * Updates the temperatureThreshold from user side. Value is set in Fahrenheit.
	 *
	 * @param temperatureThreshold
	 *            the temperatureThreshold to set.
	 */
	public void updateTemperatureThreshold(int temperatureThreshold) {
		synchronized (this.lock) {
			this.temperatureThreshold = temperatureThreshold;
		}
	}
}
