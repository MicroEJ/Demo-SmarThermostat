/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.model;

import java.util.Random;

import com.microej.demo.smart_thermostat.common.WeatherValueConverter;

import ej.bon.TimerTask;

/**
 * Demo task that handles updating the various model values.
 */
public class DemoTask extends TimerTask {

	/*
	 * Upper bound is roughly 90F => 32.2222 degree Celsius => 3222. Must be converted in Celsius at the client side.
	 */
	private static final int TEMPERATURE_UPPER_BOUND = 3222;

	/*
	 * Lower bound is roughly 60F => 15.5556 degree Celsius => 1555. Must be converted in Celsius at the client side.
	 */
	private static final int TEMPERATURE_LOWER_BOUND = 1555;

	private static final int TEMPERATURE_THRESHOLD_UPDATE_RATE = 2;

	// Goes from 1 -> 10000. 10000 stands for 100%
	private static final int HUMIDITY_UPPER_BOUND = 10000;

	// pressure in hectopascal
	private static final int PRESSURE_LOWER_BOUND = 900;
	private static final int PRESSURE_UPPER_BOUND = 1200;
	public static final int HUNDREDTH_DEG_CELSIUS_STEP = 100;

	private final Random random = new Random();

	private int temperature = this.random.nextInt(TEMPERATURE_UPPER_BOUND - TEMPERATURE_LOWER_BOUND)
			+ TEMPERATURE_LOWER_BOUND;
	private int temperatureThreshold = this.random.nextInt(TEMPERATURE_UPPER_BOUND - TEMPERATURE_LOWER_BOUND)
			+ TEMPERATURE_LOWER_BOUND;

	private int i = TEMPERATURE_THRESHOLD_UPDATE_RATE;

	private final SmartThermostatModel model;

	/**
	 * Creates the demo task to provide values to the model.
	 *
	 * @param model
	 *            the thermostat model to be changed periodically.
	 */
	public DemoTask(SmartThermostatModel model) {
		super();
		this.model = model;
	}

	@Override
	public void run() {
		/*
		 * Change model directly.
		 *
		 * Keeping the conversions for now, even though we could provide Fahrenheit values directly. The reasons: a) We
		 * might need the conversion again later, and b) the behaviour may change slightly when providing Fahrenheit
		 * instead, which we want to avoid for now.
		 *
		 */
		int temperature = WeatherValueConverter.hundredthsCelsiusToFahrenheit(getTemperature());
		int humidity = WeatherValueConverter.hundredthsOfPercentToPercent(this.random.nextInt(HUMIDITY_UPPER_BOUND));
		int pressure = PRESSURE_LOWER_BOUND + this.random.nextInt(PRESSURE_UPPER_BOUND - PRESSURE_LOWER_BOUND);

		this.model.setTemperature(temperature);
		this.model.setHumidity(humidity);
		this.model.setPressure(pressure);

		this.i++;
		if (this.i >= TEMPERATURE_THRESHOLD_UPDATE_RATE) {
			this.temperatureThreshold = this.random.nextInt(TEMPERATURE_UPPER_BOUND - TEMPERATURE_LOWER_BOUND)
					+ TEMPERATURE_LOWER_BOUND;
			int threshold = WeatherValueConverter.hundredthsCelsiusToFahrenheit(this.temperatureThreshold);
			this.model.setTemperatureThreshold(threshold);
			this.i = 0;
		}

	}

	private int getTemperature() {
		if (this.temperature > this.temperatureThreshold) {
			this.temperature -= HUNDREDTH_DEG_CELSIUS_STEP;
			if (this.temperature < this.temperatureThreshold) {
				this.temperature = this.temperatureThreshold;
			}
		} else if (this.temperature < this.temperatureThreshold) {
			this.temperature += HUNDREDTH_DEG_CELSIUS_STEP;
			if (this.temperature > this.temperatureThreshold) {
				this.temperature = this.temperatureThreshold;
			}
		}
		return this.temperature;
	}
}
