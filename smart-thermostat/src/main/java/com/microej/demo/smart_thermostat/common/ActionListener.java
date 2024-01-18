/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.common;

import com.microej.demo.smart_thermostat.NavigationDesktop;
import com.microej.demo.smart_thermostat.UI;

/**
 * Communication bridge between
 * {@link NavigationDesktop} and
 * {@link UI}.
 */
public interface ActionListener {

	/**
	 * Checks if we are currently on the Home page.
	 *
	 * @return {@code true}, if we are on the Home page.
	 */
	boolean atHome();

	/**
	 * Navigate to the left which is called inside.
	 * 
	 * @return {@code true}, if action consumed
	 */
	boolean navigateInside();

	/**
	 * Navigate to the right which is called outside.
	 * 
	 * @return {@code true}, if action consumed
	 */
	boolean navigateOutside();

	/**
	 * Navigate back to home.
	 * 
	 * @return {@code true}, if action consumed
	 */
	boolean navigateHome();
}
