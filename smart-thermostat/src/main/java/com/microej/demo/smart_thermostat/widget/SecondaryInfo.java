/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;
import static com.microej.demo.smart_thermostat.NavigationDesktop.SCALE;
import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;

import com.microej.demo.smart_thermostat.common.FlushVisualizer;
import com.microej.demo.smart_thermostat.style.Fonts;
import com.microej.demo.smart_thermostat.common.SliderListener;

import ej.annotation.Nullable;
import ej.bon.Util;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microvg.*;
import ej.mwt.animation.Animation;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * Secondary Info widget which is displayed below the main content and just contains one piece of info.
 */
public abstract class SecondaryInfo extends FadeInWidget implements SliderListener {
	/** Style id for title text color. */
	public static final int STYLE_TITLE_TEXT_COLOR = 0;
	/** Style id for background color. */
	public static final int STYLE_BACKGROUND_COLOR = 1;
	/** Style id for corer radius of the background. */
	public static final int STYLE_CORNER_RADIUS = 2;

	/** Offset subtracted from {@link #CONTENT_LEFT_OFFSET} before drawing the value icon, when in high res mode. */
	public static final int HIGH_RES_OFFSET = 10;

	/** Font size for title. */
	protected static final int TITLE_FONT_SIZE = scale(28);
	/** Font size of the value text. */
	protected static final int VALUE_FONT_SIZE = scale(50);
	/** The padding from the border to the first title line. */
	protected static final int CONTENT_TOP_PADDING = scale(32);
	/** The padding between the value icon and value text. */
	protected static final int VALUE_BETWEEN_PADDING = scale(6);

	private static final int CONTENT_LEFT_OFFSET = scale(130);

	private static final long ANIMATION_ON_START = 0;
	private static final long ANIMATION_LOOP_START = 1000;
	private static final long ANIMATION_LOOP_END = 4000;
	private static final long ANIMATION_OFF_END = 5000;

	/** The start time of the animation. */
	protected long startTime;
	/** The current animation elapsed time. This is used for drawing the animation during render. */
	protected long animationTime;
	/** If the animation state is currently frozen. */
	protected boolean animationIsFrozen;

	private final String[] title;
	private final String valueIconPath;
	private final String animatedIconPath;

	private final Animation iconAnimation;
	private boolean animationOff;
	private @Nullable ResourceVectorImage animatedIcon;

	private @Nullable BufferedVectorImage vBackground;

	/**
	 * Creates the SecondaryInfo widget.
	 *
	 * @param title
	 *            array containing the title with one entry per line.
	 * @param valueIconPath
	 *            the path to the VectorImage which is displayed small next to the value.
	 * @param animatedIconPath
	 *            the path to the animated VectorImage which is displayed in big on the left.
	 */
	protected SecondaryInfo(String[] title, String valueIconPath, String animatedIconPath) {
		this(title, valueIconPath, animatedIconPath, false);
	}

	/**
	 * Creates the SecondaryInfo widget.
	 *
	 * @param title
	 *            array containing the title with one entry per line.
	 * @param valueIconPath
	 *            the path to the VectorImage which is displayed small next to the value.
	 * @param animatedIconPath
	 *            the path to the animated VectorImage which is displayed on the left.
	 * @param enabled
	 *            {@code true} if this widget is to be enabled, {@code false} otherwise.
	 */
	protected SecondaryInfo(String[] title, String valueIconPath, String animatedIconPath, boolean enabled) {
		super(enabled);
		this.title = title.clone();
		this.valueIconPath = valueIconPath;
		this.animatedIconPath = animatedIconPath;

		this.iconAnimation = new Animation() {
			@Override
			public boolean tick(long platformTimeMillis) {
				return doAnimationTick(platformTimeMillis);
			}
		};
	}

	/**
	 * Gets the value string to draw.
	 *
	 * @return the value string to draw.
	 */
	protected abstract String getValueString();

	@Override
	protected void onShown() {
		this.animatedIcon = ResourceVectorImage.loadImage(this.animatedIconPath);
		super.onShown();
	}

	@Override
	protected void onHidden() {
		stopAnimationNow();
		super.onHidden();

		if (this.animatedIcon != null) {
			this.animatedIcon.close();
			this.animatedIcon = null;
		}
		if (this.vBackground != null) {
			this.vBackground.close();
			this.vBackground = null;
		}
	}

	protected void startAnimation() {
		this.animationTime = ANIMATION_ON_START;
		this.animationOff = false;
		this.startTime = Util.platformTimeMillis();
		getDesktop().getAnimator().startAnimation(this.iconAnimation);
	}

	protected void stopAnimation() {
		this.animationOff = true;
	}

	protected void stopAnimationNow() {
		getDesktop().getAnimator().stopAnimation(this.iconAnimation);
	}

	private boolean doAnimationTick(long platformTimeMillis) {
		long elapsed = platformTimeMillis - this.startTime;
		if (elapsed >= ANIMATION_LOOP_END && !this.animationOff) {
			this.startTime = platformTimeMillis - ANIMATION_LOOP_START;
			elapsed = ANIMATION_LOOP_START;
		}
		this.animationTime = elapsed;
		requestRender();
		return elapsed < ANIMATION_OFF_END;
	}

	/**
	 * Renders content on top of the screenshot that gets updated e.g.: value that changes.
	 *
	 * @param g
	 *            the {@link GraphicsContext} to draw on.
	 * @param contentWidth
	 *            the width available for the content.
	 * @param contentHeight
	 *            the height available for the content.
	 * @param alpha
	 *            the alpha at which to draw everything. This is used during the start animation to fade in. Since most
	 *            Vector operations support alpha by default everyone has to handle it themselves instead of creating a
	 *            new image.
	 */
	protected void renderForegroundContent(GraphicsContext g, int contentWidth, int contentHeight, int alpha) {
		// Nothing to do, by default.
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Nothing to compute. Fixed size given from outside.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		renderActualContent(g, contentWidth, contentHeight);
	}

	private void renderActualContent(GraphicsContext g, int contentWidth, int contentHeight) {
		if (this.vBackground == null) {
			this.vBackground = new BufferedVectorImage(contentWidth, contentHeight);

			GraphicsContext gcBuffer = this.vBackground.getGraphicsContext();
			renderBase(gcBuffer, contentWidth, contentHeight);
		}

		FlushVisualizer.drawVGArea(g, 0, 0, contentWidth, contentHeight);

		int alpha = getAlpha();
		Matrix mx = new Matrix();
		VectorGraphicsPainter.drawImage(g, this.vBackground, mx, alpha);

		renderValue(g, alpha);
		renderForegroundContent(g, contentWidth, contentHeight, alpha);

		if (alpha == MAX_ALPHA) {
			renderAnimatedImage(g, contentHeight);
		}
	}

	private void renderValue(GraphicsContext g, int alpha) {
		g.setColor(getStyle().getColor());
		VectorFont font = Fonts.getBarlowMedium();

		float valuePosX = CONTENT_LEFT_OFFSET + VectorImage.getImage(this.valueIconPath).getWidth()
				+ VALUE_BETWEEN_PADDING;
		float valuePosY = CONTENT_TOP_PADDING + font.getHeight(TITLE_FONT_SIZE) * this.title.length;

		Matrix matrix = new Matrix();
		matrix.setTranslate(valuePosX, valuePosY);
		VectorGraphicsPainter.drawString(g, getValueString(), font, VALUE_FONT_SIZE, matrix, alpha, BlendMode.SRC_OVER,
				0);
	}

	private void renderAnimatedImage(GraphicsContext g, int contentHeight) {
		// Render big image left
		ResourceVectorImage icon = ResourceVectorImage.loadImage(this.animatedIconPath);

		int iconPosY = Alignment.computeTopY(scale((int) icon.getHeight()), 0, contentHeight, Alignment.VCENTER);
		int iconPosX = Alignment.computeLeftX(scale((int) icon.getWidth()), 0, CONTENT_LEFT_OFFSET, Alignment.HCENTER);

		Matrix matrix = new Matrix();
		matrix.setTranslate(iconPosX, iconPosY);
		if (!LOW_RESOLUTION) {
			matrix.preScale(SCALE, SCALE);
		}

		VectorGraphicsPainter.drawAnimatedImage(g, icon, matrix, this.animationTime);
	}

	private void renderBase(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		int cornerRadius = style.getExtraInt(STYLE_CORNER_RADIUS, 0);
		int backgroundColor = style.getExtraInt(STYLE_BACKGROUND_COLOR, Colors.WHITE);
		int valueColor = style.getColor();
		int titleColor = style.getExtraInt(STYLE_TITLE_TEXT_COLOR, valueColor);
		VectorFont font = Fonts.getBarlowMedium();

		// Render Background
		g.setColor(backgroundColor);

		VectorGraphicsPainter.fillPath(g, getBackgroundPath(contentWidth, contentHeight, cornerRadius), 0, 0);

		// Render Title
		float posY = CONTENT_TOP_PADDING;
		float lineHeight = font.getHeight(TITLE_FONT_SIZE);

		String[] titleArray = this.title;
		g.setColor(titleColor);
		for (String titleLine : titleArray) {
			VectorGraphicsPainter.drawString(g, titleLine, font, TITLE_FONT_SIZE, CONTENT_LEFT_OFFSET, posY);
			posY += lineHeight;
		}

		// Render value image
		Matrix matrix = new Matrix();
		VectorImage vImg = VectorImage.getImage(this.valueIconPath);
		posY += font.getBaselinePosition(VALUE_FONT_SIZE) - scale((int) vImg.getHeight());
		int translateXOffset = CONTENT_LEFT_OFFSET;
		if (!LOW_RESOLUTION) {
			matrix.setScale(SCALE, SCALE);
			translateXOffset -= HIGH_RES_OFFSET;
		}
		matrix.postTranslate(translateXOffset, posY);

		g.setColor(valueColor);
		VectorGraphicsPainter.drawAnimatedImage(g, vImg, matrix, 0);
	}
	private Path getBackgroundPath(int contentWidth, int contentHeight, int cornerRadius) {
		Path path = new Path();

		path.moveTo(0, cornerRadius);
		// left top
		path.quadToRelative(0, -cornerRadius, cornerRadius, -cornerRadius);
		path.lineTo(contentWidth - (float) cornerRadius, 0);
		// top right
		path.quadToRelative(cornerRadius, 0, cornerRadius, cornerRadius);
		path.lineTo(contentWidth, contentHeight - (float) cornerRadius);
		// right bottom
		path.quadToRelative(0, cornerRadius, -cornerRadius, cornerRadius);
		path.lineTo(cornerRadius, contentHeight);
		// bottom left
		path.quadToRelative(-cornerRadius, 0, -cornerRadius, -cornerRadius);
		path.lineTo(0, cornerRadius);

		return path;
	}
}
