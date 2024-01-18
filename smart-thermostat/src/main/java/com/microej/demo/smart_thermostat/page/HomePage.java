/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.page;

import com.microej.demo.smart_thermostat.MainCanvas;
import com.microej.demo.smart_thermostat.common.Page;
import com.microej.demo.smart_thermostat.widget.StaticOverlay;

/**
 * Shows the home page.
 */
public class HomePage implements Page {

	private final StaticOverlay staticHomeOverlay;

	/**
	 * Creates the home page.
	 */
	public HomePage() {
		this.staticHomeOverlay = new StaticOverlay(true);
	}

	@Override
	public void build(MainCanvas canvas) {
		if (canvas.getChildrenCount() > 0) {
			canvas.removeAllChildren();
		}

		canvas.addSkyColor();
		canvas.addParallax();
		canvas.addBubbleWidget();
		canvas.addThresholdPopup();
		canvas.addDateWidget();
		canvas.addInsideBubbleLabels();
		canvas.addOutsideBubbleLabels();

		canvas.addChild(this.staticHomeOverlay, 0, 0, this.staticHomeOverlay.getWidth(),
				this.staticHomeOverlay.getHeight());
	}
}
