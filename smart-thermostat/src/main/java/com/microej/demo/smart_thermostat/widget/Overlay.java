/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;
import static com.microej.demo.smart_thermostat.NavigationDesktop.SCALE;

import com.microej.demo.smart_thermostat.common.AnimationValue;
import com.microej.demo.smart_thermostat.common.FlushVisualizer;
import com.microej.demo.smart_thermostat.style.VectorImages;

import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Overlay widget which displays an animated overlay vector image.
 */
public class Overlay extends Widget {

	private static final float OVERLAY_SCALE_Y = 1.4296f;

	private final VectorImage imageOverlay;
	private final Matrix overlayMatrix;
	private final AnimationValue animationValue;

	/**
	 * Creates the animated overlay.
	 * 
	 * @param animationValue
	 *            the interface to pass the elapsed time.
	 */
	public Overlay(AnimationValue animationValue) {
		this.animationValue = animationValue;
		this.imageOverlay = VectorImage.getImage(VectorImages.OVERLAY);

		this.overlayMatrix = new Matrix();
		if (!LOW_RESOLUTION) {
			this.overlayMatrix.setScale(SCALE, OVERLAY_SCALE_Y);
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Nothing to do yet. Size set from canvas outside.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		FlushVisualizer.drawVGArea(g, 0, 0, contentWidth, contentHeight);
		VectorGraphicsPainter.drawAnimatedImage(g, this.imageOverlay, this.overlayMatrix,
				this.animationValue.getValue());
	}
}
