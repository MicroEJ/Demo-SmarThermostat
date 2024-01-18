/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;

import com.microej.demo.smart_thermostat.common.AnimationValue;
import com.microej.demo.smart_thermostat.style.Images;

import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Parallax handles the in and outside image animation at home.
 */
public class Parallax extends Widget {

	private static final int MOTION_RANGE = scale(40);
	public static final int PARALLAX_OUTSIDE_IMAGE_X_OFFSET = scale(260);

	private final int displayWidth;
	private final int displayHalfWidth;
	private final int parallaxHeight;
	private final ResourceImage parallaxInsideImage;
	private final ResourceImage parallaxOutsideImage;
	int parallaxInsideImageX;
	int parallaxOutsideImageX;
	private final int parallaxYPosition;
	private final AnimationValue animationValue;

	/**
	 * Creates the Parallax animation.
	 *
	 * @param animationValue
	 *            the interface to pass the elapsed time.
	 */
	public Parallax(AnimationValue animationValue) {
		Display display = Display.getDisplay();
		this.displayWidth = display.getWidth();
		int height = display.getHeight();
		this.displayHalfWidth = this.displayWidth / 2;

		this.animationValue = animationValue;
		this.parallaxInsideImage = Images.getResourceImage(Images.PARALLAX_INSIDE);
		this.parallaxOutsideImage = Images.getResourceImage(Images.PARALLAX_OUTSIDE);

		this.parallaxHeight = this.parallaxInsideImage.getHeight();
		this.parallaxYPosition = height - this.parallaxHeight;
	}

	/**
	 * Gets the parallax Y position from the top of screen.
	 *
	 * @return the parallax Y position value.
	 */
	public int getParallaxYPosition() {
		return this.parallaxYPosition;
	}

	/**
	 * Gets the parallax inside image.
	 *
	 * @return the parallax inside image.
	 */
	public ResourceImage getParallaxInsideImage() {
		return this.parallaxInsideImage;
	}

	/**
	 * Gets the parallax outside image.
	 *
	 * @return the parallax outside image.
	 */
	public ResourceImage getParallaxOutsideImage() {
		return this.parallaxOutsideImage;
	}

	/**
	 * Gets the parallax inside image x position.
	 *
	 * @return the parallax inside image x position.
	 */
	public int getInsideImageX() {
		return this.parallaxInsideImageX;
	}

	/**
	 * Gets the parallax outside image x position.
	 *
	 * @return the parallax outside image x position.
	 */
	public int getOutsideImageX() {
		return this.parallaxOutsideImageX;
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(this.displayWidth, this.parallaxHeight);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		this.parallaxInsideImageX = -MOTION_RANGE + this.animationValue.getValue();
		this.parallaxOutsideImageX = MOTION_RANGE + this.animationValue.getValue();
		int halfWidth = this.displayHalfWidth;

		// Set clipping/masking for left image
		g.setClip(0, 0, halfWidth, this.parallaxHeight);
		Painter.drawImage(g, this.parallaxInsideImage, this.parallaxInsideImageX, 0);

		// Set clipping/masking for right image
		g.setClip(halfWidth, 0, halfWidth, this.parallaxHeight);
		Painter.drawImage(g, this.parallaxOutsideImage, this.parallaxOutsideImageX + PARALLAX_OUTSIDE_IMAGE_X_OFFSET,
				0);
		g.resetClip();
	}
}
