/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.common;

import com.microej.demo.smart_thermostat.MainCanvas;

/**
 * Base class for Pages.
 */
public interface Page {

	/**
	 * Builds the page on the main canvas {@link MainCanvas}.
	 *
	 * @param canvas the {@link MainCanvas} used to build.
	 */
	void build(MainCanvas canvas);
}
