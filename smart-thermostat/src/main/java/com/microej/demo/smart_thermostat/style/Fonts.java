/*
 * Java
 *
 * Copyright 2022-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.style;

import ej.microvg.VectorFont;

/**
 * Fonts used by the application.
 */
public class Fonts {
	private static final VectorFont barlowLightItalic = VectorFont.loadFont("/fonts/Barlow-LightItalic.ttf");
	private static final VectorFont barlowMedium = VectorFont.loadFont("/fonts/Barlow-Medium.ttf");

	private Fonts() {
		// Prevent instantiation.
	}

	/**
	 * Get the <b>Barlow Light</b> vector font.
	 *
	 * @return the vector font.
	 */
	public static VectorFont getBarlowLightItalic() {
		return barlowLightItalic;
	}

	/**
	 * Get the <b>Barlow Medium</b> vector font.
	 *
	 * @return the vector font.
	 */
	public static VectorFont getBarlowMedium() {
		return barlowMedium;
	}

}
