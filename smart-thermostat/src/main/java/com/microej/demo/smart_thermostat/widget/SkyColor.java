/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

import com.microej.demo.smart_thermostat.style.ThermoColors;

import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Split sky background color on Home page.
 */
public class SkyColor extends Widget {

	private static final int PARALLAX_HEIGHT = LOW_RESOLUTION ? 460 : 690;

	private final int displayWidth;
	private final int displayHalfWidth;
	private final int staticBackgroundsHeight;

	/**
	 * Creates SkyColor widget.
	 */
	public SkyColor() {
		Display display = Display.getDisplay();
		this.displayWidth = display.getWidth();
		int height = display.getHeight();
		this.displayHalfWidth = this.displayWidth / 2;
		this.staticBackgroundsHeight = height - PARALLAX_HEIGHT;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(this.displayWidth, this.staticBackgroundsHeight);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		int halfWidth = this.displayHalfWidth;
		int staticBGHeight = this.staticBackgroundsHeight;
		g.setColor(ThermoColors.BG_INSIDE);
		Painter.fillRectangle(g, 0, 0, halfWidth, staticBGHeight);

		g.setColor(ThermoColors.BG_OUTSIDE);
		Painter.fillRectangle(g, halfWidth, 0, halfWidth, staticBGHeight);
	}
}
