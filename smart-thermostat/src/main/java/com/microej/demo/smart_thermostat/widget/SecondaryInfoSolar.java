/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

import com.microej.demo.smart_thermostat.style.VectorImages;

/**
 * Creates the SecondaryInfo widget for solar power usage.
 */
public class SecondaryInfoSolar extends SecondaryInfo {

	private static final int PLACEHOLDER_ELECTRICITY_PROD_PERCENT = 30;

	private int electricityProdPercent;

	/**
	 * Creates the SecondaryInfoSolar widget.
	 */
	public SecondaryInfoSolar() {
		super(new String[] { "Electricity", "Production" }, VectorImages.BATTERY, VectorImages.SOLAR_PANEL);
		this.electricityProdPercent = PLACEHOLDER_ELECTRICITY_PROD_PERCENT;
	}

	/**
	 * Sets the electricity production in percent.
	 *
	 * @param electricityProdPercent
	 *            the electricity production in percent: 0-100.
	 */
	public void setElectricityProdPercent(int electricityProdPercent) {
		this.electricityProdPercent = electricityProdPercent;
	}

	@Override
	protected String getValueString() {
		return this.electricityProdPercent + "%";
	}

	@Override
	protected void onFadeInDone() {
		super.onFadeInDone();
		if (LOW_RESOLUTION) {
			startAnimation();
		}
	}

	@Override
	public void onSliderDragged() {
		stopAnimationNow();
	}

	@Override
	public void onSliderReleased() {
		startAnimation();
	}
}
