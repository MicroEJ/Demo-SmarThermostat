/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.common;

/**
 * Provides information about the current slider widget state.
 */
public interface SliderListener {

	/**
	 * Performs an action when slider cursor is DRAGGED.
	 */
	void onSliderDragged();

	/**
	 * Performs an action when slider cursor is RELEASED.
	 */
	void onSliderReleased();
}
