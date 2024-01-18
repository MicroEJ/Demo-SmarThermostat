/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;
import static com.microej.demo.smart_thermostat.NavigationDesktop.SCALE;

import com.microej.demo.smart_thermostat.common.FlushVisualizer;
import com.microej.demo.smart_thermostat.style.VectorImages;

import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Static Overlay vector image used only while showing home page.
 */
public class StaticOverlay extends Widget {

	private static final float STATIC_OVERLAY_SCALE_Y = 1.4296f;

	private final Matrix overlayMatrix;
	private final VectorImage staticOverlayImage;

	/**
	 * Creates the StaticOverlay.
	 */
	public StaticOverlay(boolean isHome) {
		this.staticOverlayImage = getStaticOverlay(isHome);

		this.overlayMatrix = new Matrix();
		if (!LOW_RESOLUTION) {
			this.overlayMatrix.setScale(SCALE, STATIC_OVERLAY_SCALE_Y);
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(Display.getDisplay().getWidth(), Display.getDisplay().getHeight());
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		FlushVisualizer.drawVGArea(g, 0, 0, contentWidth, contentHeight);
		VectorGraphicsPainter.drawImage(g, this.staticOverlayImage, this.overlayMatrix);
	}

	private VectorImage getStaticOverlay(boolean isHome) {
		return isHome ? VectorImage.getImage(VectorImages.STATIC_HOME_OVERLAY)
				: VectorImage.getImage(VectorImages.STATIC_INSIDE_OVERLAY);
	}
}
