/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.*;

import com.microej.demo.smart_thermostat.common.FlushVisualizer;
import com.microej.demo.smart_thermostat.style.VectorImages;

import ej.microui.display.GraphicsContext;
import ej.microvg.Matrix;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Widget displaying the animated vector bubble.
 */
public class BubbleWidget extends Widget {

	private static final int FADE_IN_TICK_TIME = 100;
	private static final int TICK_TIME = 50;
	private static final int FADE_IN_END = 320;

	private static boolean fade = false;

	private final VectorImage bubbleImage;
	private final int bubbleImageWidth;
	private final int bubbleImageHeight;
	private final Matrix bubbleMatrix;

	private int elapsedTime = 0;

	/**
	 * Creates the BubbleWidget.
	 */
	public BubbleWidget() {
		VectorImage img = VectorImage.getImage(VectorImages.BUBBLE); // $NON-NLS-1$
		this.bubbleImageWidth = scale((int) img.getWidth());
		this.bubbleImageHeight = scale((int) img.getHeight());

		this.bubbleImage = img;

		this.bubbleMatrix = new Matrix();
		if (!LOW_RESOLUTION) {
			this.bubbleMatrix.setScale(SCALE, SCALE);
		}
	}

	/**
	 * Gets if the BubbleWidget is currently in fade.
	 *
	 * @return {code true}, if bubble is currently fading.
	 */
	public static boolean isInFade() {
		return fade;
	}

	/**
	 * Continues the animation of the bubble VectorGraphic.
	 * <p>
	 * A tick is {@value TICK_TIME}ms
	 * </p>
	 */
	public void tick() {
		if (this.elapsedTime <= FADE_IN_END) {
			this.elapsedTime += FADE_IN_TICK_TIME;
		} else {
			this.elapsedTime += TICK_TIME;
		}

		if (this.elapsedTime >= this.bubbleImage.getDuration()) {
			this.elapsedTime = FADE_IN_END;
		} else if (this.elapsedTime >= FADE_IN_END) {
			fade = false;
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(this.bubbleImageWidth, this.bubbleImageHeight);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		FlushVisualizer.drawVGArea(g, 0, 0, contentWidth, contentHeight);
		VectorGraphicsPainter.drawAnimatedImage(g, this.bubbleImage, this.bubbleMatrix, this.elapsedTime);
	}

	@Override
	protected void onShown() {
		super.onShown();
		startFade();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		stopFade();
	}

	private void startFade() {
		this.elapsedTime = 0;
		fade = true;
	}

	private void stopFade() {
		fade = false;
	}
}
