/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.common;

import ej.bon.Constants;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;

/**
 * FlushVisualizer tool to visualize drawing in simulation.
 */
public class FlushVisualizer {
	/**
	 * Hides the constructor in order to prevent instantiating a class containing only static methods.
	 */
	private FlushVisualizer() {
		// prevent instantiation
	}

	/**
	 * Draws an invisible area in simulation only, for the FlushVisualizer tool to take into account VG drawings.
	 *
	 * @param g
	 *            the {@link GraphicsContext} to draw on.
	 * @param x
	 *            unused for now.
	 * @param y
	 *            unused for now.
	 * @param contentWidth
	 *            the width of the content.
	 * @param contentHeight
	 *            the height of the content.
	 */
	public static void drawVGArea(GraphicsContext g, int x, int y, int contentWidth, int contentHeight) {
		if (Constants.getBoolean("flushvisualizer.drawvgarea.enable")
				&& Constants.getBoolean("com.microej.library.microui.onS3")) {
			Painter.drawRectangle(g, 0, 0, contentWidth, contentHeight);
		}
	}
}
