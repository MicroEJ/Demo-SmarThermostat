/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.style;

/**
 * A static class where the class selectors for all the widgets used throughout the application. Used primarily to link
 * widgets to their corresponding style.
 */
public class ClassSelectors {

	/** Class selector ID for the outside secondary bubble. */
	public static final int SECONDARY_BUBBLE_OUTSIDE = 3;
	/** Class selector ID for the inside secondary bubble. */
	public static final int SECONDARY_BUBBLE_INSIDE = 4;
	/** Class selector ID for the inside page labels. */
	public static final int INSIDE_PAGE_LABELS = 5;
	/** Class selector ID for the outside page labels. */
	public static final int OUTSIDE_PAGE_LABELS = 6;

	/**
	 * Hides the constructor in order to prevent instantiating a class containing only static methods.
	 */
	private ClassSelectors() {
		// prevent instantiation
	}
}
