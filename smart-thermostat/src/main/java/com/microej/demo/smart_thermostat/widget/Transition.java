/*
 * Java
 *
 * Copyright 2021-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

import com.microej.demo.smart_thermostat.common.Context;
import com.microej.demo.smart_thermostat.common.Progress;
import com.microej.demo.smart_thermostat.style.Images;
import com.microej.demo.smart_thermostat.style.ThermoColors;

import ej.bon.XMath;
import ej.drawing.TransformPainter;
import ej.microui.display.*;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Transition handles the transition from home to in or outside and back to home.
 */
public class Transition extends Widget {

	private static final int PROGRESS_CENTER_MULTIPLIER = 3;
	private static final int BACKGROUND_CORNER_X_OFFSET = LOW_RESOLUTION ? 45 : 70;
	private static final int LEFT_CORNER_X = LOW_RESOLUTION ? 49 : 74;
	private static final int LEFT_CORNER_Y = LOW_RESOLUTION ? 172 : 246;
	private static final int RIGHT_CORNER_X = LOW_RESOLUTION ? 616 : 903;
	private static final int RIGHT_CORNER_Y = LOW_RESOLUTION ? 172 : 246;
	private static final int UPPER_RECT_X = LOW_RESOLUTION ? 106 : 178;
	private static final int UPPER_RECT_HEIGHT = LOW_RESOLUTION ? 56 : 105;
	private static final int UPPER_RECT_Y = LOW_RESOLUTION ? 172 : 246;
	private static final int UPPER_RECT_WIDTH = LOW_RESOLUTION ? 510 : 725;
	private static final int LRES_INSIDE_CEIL_Y = 560;
	private static final int LRES_INSIDE_CEIL_X = 673;
	private static final int LRES_RECT_X = 49;
	private static final int LRES_LINE_LEFT_END_X = LRES_RECT_X + 1 + 622;
	private static final int LRES_LEFT_LINE_X = LRES_RECT_X + 1 + 622;
	private static final int LRES_RIGHT_RECT_X = 360;
	private static final int LRES_BOTTOM_RECT_Y = 660;
	private static final int LRES_2ND_UPPER_RECT_Y = 229;
	private static final int LRES_LEFT_RECT_Y = 320;
	private static final int LRES_RIGHT_RECT_Y = 320;
	private static final int LRES_LINE_LEFT_END_Y = 1280 - 543;
	private static final int LRES_RECT_WIDTH = 622;
	private static final int LRES_RECT_HEIGHT = 91;
	private static final int LRES_LEFT_RECT_WIDTH = 110;
	private static final int LRES_LEFT_RECT_HEIGHT = 340;
	private static final int LRES_RIGHT_RECT_WIDTH = 312;
	private static final int LRES_RIGHT_RECT_HEIGHT = 417;
	private static final int LRES_BOTTOM_RECT_WIDTH = 274;
	private static final int LRES_BOTTOM_RECT_HEIGHT = 77;
	private static final int LRES_LINE_TOP_END_X = 106 + 510;
	private static final int LRES_LINE_TOP_END_Y = 172;
	private static final int LRES_LINE_RIGHT_END_Y = 1280 - 543;
	private static final int LRES_LOWER_PART_IMAGE_Y = 1280 - 543;

	private static final int HRES_CENTRAL_RECT_WIDTH = 934;
	private static final int HRES_CENTRAL_RECT_HEIGHT = 488;
	private static final int HRES_CENTRAL_RECT_X = 74;
	private static final int HRES_CENTRAL_RECT_Y = 352;
	private static final int HRES_LINE_TOP_START_X = 178;
	private static final int HRES_LINE_RIGHT_START_X = 1008;

	private final int displayWidth;
	private final int displayHeight;
	private final int displayHalfWidth;
	private final int maxImageSize;
	private final int minImageSize;
	private final int parallaxHeight;
	private final ResourceImage scaleInsideImage;
	private final ResourceImage scaleInsideImageRoundedRectangle;
	private final ResourceImage scaleOutsideImage;
	private final ResourceImage parallaxInsideImage;
	private final ResourceImage topRightInsideImageCorner;
	private final ResourceImage topLeftInsideImageCorner;
	private final ResourceImage topLeftInsideRectangleCorner;
	private final ResourceImage topRightInsideRectangleCorner;
	private final ResourceImage rightInsideCeiling;
	private final ResourceImage leftInsideCeiling;
	private final ResourceImage parallaxOutsideImage;
	private final Parallax myParallax;
	private final Progress transitionProgress;

	/**
	 * Creates the transition.
	 *
	 * @param transitionProgress
	 *            to communicate the transition progress.
	 * @param parallax
	 *            the {@link Parallax} widget to transition.
	 */
	public Transition(Progress transitionProgress, Parallax parallax) {
		Display display = Display.getDisplay();
		this.displayHeight = display.getHeight();
		this.displayWidth = display.getWidth();
		this.displayHalfWidth = this.displayWidth / 2;
		this.myParallax = parallax;
		this.transitionProgress = transitionProgress;

		this.parallaxInsideImage = parallax.getParallaxInsideImage();
		this.parallaxOutsideImage = parallax.getParallaxOutsideImage();
		this.scaleInsideImage = Images.getResourceImage(Images.PARALLAX_INSIDE_SCALED);
		this.scaleOutsideImage = Images.getResourceImage(Images.PARALLAX_OUTSIDE_SCALED);

		this.scaleInsideImageRoundedRectangle = Images.getResourceImage(Images.BG_HOME_SCALED_RECT);

		this.topLeftInsideRectangleCorner = Images.getResourceImage(Images.BG_TOP_LEFT_RECT_CORNER);
		this.topRightInsideRectangleCorner = Images.getResourceImage(Images.BG_TOP_RIGHT_RECT_CORNER);
		this.topLeftInsideImageCorner = Images.getResourceImage(Images.BG_TOP_LEFT_CORNER);
		this.topRightInsideImageCorner = Images.getResourceImage(Images.BG_TOP_RIGHT_CORNER);
		this.leftInsideCeiling = Images.getResourceImage(Images.BG_HOME_SCALED_CEILING_LEFT);
		this.rightInsideCeiling = Images.getResourceImage(Images.BG_HOME_SCALED_CEILING_RIGHT);

		this.parallaxHeight = this.parallaxInsideImage.getHeight();
		this.maxImageSize = this.scaleInsideImage.getWidth();
		this.minImageSize = this.parallaxInsideImage.getWidth();
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(this.displayWidth, this.parallaxHeight);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		int width = this.displayWidth;
		int halfWidth = this.displayHalfWidth;
		int height = this.displayHeight;
		int maxImgSize = this.maxImageSize;
		int minImgSize = this.minImageSize;

		Parallax parallax = this.myParallax;
		int parallaxYPos = parallax.getParallaxYPosition();
		int parallaxOutsideImgX = parallax.getOutsideImageX();
		int offsetParallaxOutsideImgX = parallaxOutsideImgX + Parallax.PARALLAX_OUTSIDE_IMAGE_X_OFFSET;
		int parallaxInsideImgX = parallax.getInsideImageX();

		int transitionWidth = clampTransitionWidth();
		int centerX = halfWidth + transitionWidth;
		Context context = Context.INSTANCE;
		if (context.isComingToOrGoingFromInside()) {
			int negativeWidth = transitionWidth - halfWidth;
			int currentSize = width + negativeWidth;
			int currentY = height - currentSize;
			float scaleFactor = currentSize / (float) maxImgSize;

			g.setColor(ThermoColors.BG_OUTSIDE);
			Painter.fillRectangle(g, halfWidth, 0, centerX, parallaxYPos);
			g.setColor(ThermoColors.BG_INSIDE);
			Painter.fillRectangle(g, negativeWidth, 0, width, currentY);
			g.setClip(centerX, parallaxYPos, minImgSize, minImgSize);
			Painter.drawImage(g, this.parallaxOutsideImage, offsetParallaxOutsideImgX + transitionWidth, parallaxYPos);
			// clip the part that could overlap on top of outside
			if (currentSize < minImgSize) {
				// move the image until the container fits the width
				g.setClip(0, parallaxYPos, centerX, minImgSize);
				Painter.drawImage(g, this.parallaxInsideImage,
						parallaxInsideImgX + calcCurrentParallaxX(parallaxInsideImgX), parallaxYPos);
			} else {
				g.setClip(0, currentY, currentSize, currentSize);
				if (context.inTransition()) {
					// scale the image until the container doesn't fit the min width
					TransformPainter.drawScaledImageBilinear(g, this.scaleInsideImage, 0, currentY, scaleFactor,
							scaleFactor);
				} else {
					g.setColor(Colors.WHITE);
					g.resetClip();
					Painter.drawImage(g, this.topLeftInsideImageCorner, 0, 0);
					// Draw upper white and background corners. Draw left and right background
					Painter.drawImage(g, this.topRightInsideImageCorner, width - BACKGROUND_CORNER_X_OFFSET, 0);
					Painter.drawImage(g, this.topLeftInsideRectangleCorner, LEFT_CORNER_X, LEFT_CORNER_Y);
					Painter.drawImage(g, this.topRightInsideRectangleCorner, RIGHT_CORNER_X, RIGHT_CORNER_Y);

					Painter.fillRectangle(g, UPPER_RECT_X, UPPER_RECT_Y + 1, UPPER_RECT_WIDTH, UPPER_RECT_HEIGHT); // Top
																													// horizontal

					g.setColor(Colors.WHITE);
					if (LOW_RESOLUTION) {
						Painter.drawImage(g, this.leftInsideCeiling, 0, LRES_INSIDE_CEIL_Y);
						Painter.drawImage(g, this.rightInsideCeiling, LRES_INSIDE_CEIL_X, LRES_INSIDE_CEIL_Y);

						// Draw rest of main inside widget
						Painter.fillRectangle(g, LRES_RECT_X + 1, LRES_2ND_UPPER_RECT_Y, LRES_RECT_WIDTH,
								LRES_RECT_HEIGHT); // 2nd top horizontal
						Painter.fillRectangle(g, LRES_RECT_X + 1, LRES_LEFT_RECT_Y, LRES_LEFT_RECT_WIDTH,
								LRES_LEFT_RECT_HEIGHT); // Left vertical
						Painter.fillRectangle(g, LRES_RIGHT_RECT_X, LRES_RIGHT_RECT_Y, LRES_RIGHT_RECT_WIDTH,
								LRES_RIGHT_RECT_HEIGHT); // Right vertical
						Painter.fillRectangle(g, LRES_RECT_X + 1, LRES_BOTTOM_RECT_Y, LRES_BOTTOM_RECT_WIDTH,
								LRES_BOTTOM_RECT_HEIGHT); // Bottom horizontal

						// Fill up the missing thin parts around main inside widget
						g.setColor(ThermoColors.INSIDE_BACKGROUND);
						Painter.drawLine(g, UPPER_RECT_X, UPPER_RECT_Y, LRES_LINE_TOP_END_X, LRES_LINE_TOP_END_Y); // top
						Painter.drawLine(g, LRES_RECT_X, LRES_2ND_UPPER_RECT_Y, LRES_RECT_X, LRES_LINE_RIGHT_END_Y); // right
						Painter.drawLine(g, LRES_LEFT_LINE_X, LRES_2ND_UPPER_RECT_Y, LRES_LINE_LEFT_END_X,
								LRES_LINE_LEFT_END_Y); // left

						// Draw the remaining lower part
						Painter.drawImage(g, this.scaleInsideImageRoundedRectangle, 0, LRES_LOWER_PART_IMAGE_Y);
					} else {

						Painter.fillRectangle(g, HRES_CENTRAL_RECT_X, HRES_CENTRAL_RECT_Y, HRES_CENTRAL_RECT_WIDTH,
								HRES_CENTRAL_RECT_HEIGHT); // Central

						g.setColor(ThermoColors.INSIDE_BACKGROUND);
						Painter.drawLine(g, HRES_LINE_TOP_START_X, UPPER_RECT_Y,
								HRES_LINE_TOP_START_X + UPPER_RECT_WIDTH, UPPER_RECT_Y); // top
						Painter.drawLine(g, HRES_LINE_RIGHT_START_X, HRES_CENTRAL_RECT_Y, HRES_LINE_RIGHT_START_X,
								currentY); // right

						// Draw the remaining lower part
						Painter.drawImage(g, this.scaleInsideImageRoundedRectangle, 0, currentY);
					}
				}
			}
			g.resetClip();
		} else {
			int currentSize = width - centerX;
			int currentY = height - currentSize;
			float scaleFactor = currentSize / (float) maxImgSize;
			g.setColor(ThermoColors.BG_INSIDE);
			Painter.fillRectangle(g, 0, 0, centerX, parallaxYPos);
			g.setColor(ThermoColors.BG_OUTSIDE);
			Painter.fillRectangle(g, centerX, 0, width, currentY);
			g.setClip(0, parallaxYPos, centerX, minImgSize);
			Painter.drawImage(g, this.parallaxInsideImage, parallaxInsideImgX + transitionWidth, parallaxYPos);
			// clip the part that could overlap on top of inside
			if (currentSize < minImgSize) {
				// move the image until the container fits the width
				g.setClip(centerX, parallaxYPos, minImgSize, minImgSize);
				Painter.drawImage(g, this.parallaxOutsideImage,
						offsetParallaxOutsideImgX - calcCurrentParallaxX(offsetParallaxOutsideImgX), parallaxYPos);
			} else {
				int clipSize = LOW_RESOLUTION ? maxImgSize : currentSize;
				g.setClip(centerX, currentY, clipSize, clipSize);
				if (context.inTransition()) {
					// scale the image until the container doesn't fit the min width
					TransformPainter.drawScaledImageBilinear(g, this.scaleOutsideImage, centerX, currentY, scaleFactor,
							scaleFactor);
				} else {
					Painter.drawImage(g, this.scaleOutsideImage, centerX, currentY);

				}
			}
			g.resetClip();
		}
	}

	private int calcCurrentParallaxX(int currentX) {
		return (int) XMath.floor(currentX * this.transitionProgress.getPercent() / Progress.HUNDRED);
	}

	private int clampTransitionWidth() {
		int currentWidth = (int) (this.transitionProgress.getValue() * PROGRESS_CENTER_MULTIPLIER);
		int halfWidth = this.displayHalfWidth;
		if (currentWidth > 0) {
			if (currentWidth > halfWidth) {
				currentWidth = halfWidth;
			}
		} else {
			if (currentWidth < -halfWidth) {
				currentWidth = -halfWidth;
			}
		}
		return currentWidth;
	}
}
