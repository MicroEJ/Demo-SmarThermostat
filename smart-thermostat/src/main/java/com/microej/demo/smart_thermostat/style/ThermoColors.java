/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.style;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

/**
 * A static class for the color values used throughout the application.
 */
public class ThermoColors {

	/** Primary color for the bubble inside labels. */
	public static final int INSIDE_LABELS_PRIMARY = 0x766A5C;
	/** Secondary color for the bubble inside labels. */
	public static final int INSIDE_LABELS_SECONDARY = 0xBCB7B2;
	/** Background color for filling up missing parts of inside transition. */
	public static final int INSIDE_BACKGROUND = 0xE3E3E0;
	/** Primary color for the bubble outside labels. */
	public static final int OUTSIDE_LABELS_PRIMARY = 0x4E85C5;
	/** Secondary color for the bubble outside labels. */
	public static final int OUTSIDE_LABELS_SECONDARY = 0x81B0DC;
	/** Color for the outside weather widget title. */
	public static final int OUTSIDE_WEATHER_TITLE = 0x293E51;
	/** Background color for the inside image. */
	public static final int BG_INSIDE = 0xB6B1AB;
	/** Background color for the outside image. */
	public static final int BG_OUTSIDE = 0x4D86C4;
	/** Background color for the bubble vector image. */
	public static final int BG_BUBBLE = LOW_RESOLUTION ? 0xf8fcf8 : 0xffffff;
	/** Background color for the Secondary Info Fan toggle OFF. */
	public static final int BG_TOGGLE_OFF = 0xC0BAB4;
	/** Background color for the Secondary Info Fan toggle ON. */
	public static final int BG_TOGGLE_ON = 0x00A66A;
	/** Primary color for the popup labels. */
	public static final int POPUP_LABELS_PRIMARY = 0x766A5C;

	private ThermoColors() {
		// Prevent instantiation.
	}
}
