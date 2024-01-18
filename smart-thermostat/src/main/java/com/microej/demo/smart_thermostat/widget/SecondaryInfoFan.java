/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;
import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;

import com.microej.demo.smart_thermostat.style.Images;
import com.microej.demo.smart_thermostat.style.VectorImages;

import ej.annotation.Nullable;
import ej.bon.Util;
import ej.bon.XMath;
import ej.microui.display.*;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.mwt.animation.Animation;
import ej.mwt.style.Style;
import ej.widget.color.GradientHelper;

/**
 * Creates the SecondaryInfo widget for Fan state.
 */
public class SecondaryInfoFan extends SecondaryInfo implements Animation {

	/** Style id for the background color of the toggle switch in the OFF position. */
	public static final int STYLE_TOGGLE_BACKGROUND_OFF = 3;
	/** Style id for the background color of the toggle switch in the OFF position. */
	public static final int STYLE_TOGGLE_BACKGROUND_ON = 4;

	private static final int CONTENT_RIGHT_PADDING = scale(36);
	private static final int TOGGLE_BULLET_TOP_PADDING = scale(3);

	private static final long TOGGLE_DURATION = 150;

	private boolean fanState;

	private @Nullable ResourceImage toggleBackground;
	private @Nullable ResourceImage toggleBullet;

	private long animationEndTime;

	/**
	 * Creates the SecondaryInfoFan widget.
	 */
	public SecondaryInfoFan() {
		super(new String[] { "Fan" }, VectorImages.FAN, VectorImages.FAN_BIG, true);
		this.fanState = false;
	}

	@Override
	protected void onShown() {
		this.toggleBackground = Images.getResourceImage(Images.TOGGLE_BACKGROUND, ResourceImage.OutputFormat.A8);
		this.toggleBullet = Images.getResourceImage(Images.TOGGLE_BULLET, ResourceImage.OutputFormat.ARGB4444);
		super.onShown();
	}

	@Override
	protected void onFadeInDone() {
		super.onFadeInDone();
		if (LOW_RESOLUTION && this.fanState) {
			startAnimation();
		}
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		getDesktop().getAnimator().stopAnimation(this);
		if (this.toggleBackground != null) {
			this.toggleBackground.close();
			this.toggleBackground = null;
		}
		if (this.toggleBullet != null) {
			this.toggleBullet.close();
			this.toggleBullet = null;
		}
	}

	/**
	 * Sets the fan state.
	 *
	 * @param fanState
	 *            the fan state.
	 */
	public void setFanState(boolean fanState) {
		this.fanState = fanState;
		if (LOW_RESOLUTION && fanState) {
			startAnimation();
		} else {
			stopAnimationNow();
		}
	}

	@Override
	public boolean handleEvent(int event) {
		int eventType = Event.getType(event);
		if (eventType == Pointer.EVENT_TYPE && Buttons.getAction(event) == Buttons.RELEASED) {
			onClick();
			return true;
		}
		return false;
	}

	private void onClick() {
		this.setFanState(!this.fanState);
		this.animationEndTime = Util.platformTimeMillis() + TOGGLE_DURATION;
		getDesktop().getAnimator().startAnimation(this);
	}

	@Override
	public boolean tick(long platformTimeMillis) {
		requestRender();
		return platformTimeMillis < this.animationEndTime;
	}

	@Override
	protected String getValueString() {
		if (this.fanState) {
			return "ON";
		}
		return "OFF";
	}

	@Override
	protected void renderForegroundContent(GraphicsContext g, int contentWidth, int contentHeight, int alpha) {
		Style style = getStyle();
		int toggleBackgroundOff = style.getExtraInt(STYLE_TOGGLE_BACKGROUND_OFF, Colors.BLACK);
		int toggleBackgroundOn = style.getExtraInt(STYLE_TOGGLE_BACKGROUND_ON, Colors.PURPLE);

		boolean fanOn = this.fanState;

		// compute checked ratio (1 = checked, 0 = unchecked, 0.5 = middle)
		float ratio = (float) (this.animationEndTime - Util.platformTimeMillis()) / TOGGLE_DURATION;
		ratio = XMath.limit(ratio, 0.0f, 1.0f);
		if (fanOn) {
			ratio = 1.0f - ratio;
		}

		Image bkgImg = this.toggleBackground;
		Image bulletImg = this.toggleBullet;

		int toggleX = contentWidth - CONTENT_RIGHT_PADDING - bkgImg.getWidth();
		int toggleY = CONTENT_TOP_PADDING;

		int backgroundColor = GradientHelper.blendColors(toggleBackgroundOff, toggleBackgroundOn, ratio);
		g.setColor(backgroundColor);

		Painter.drawImage(g, bkgImg, toggleX, toggleY, alpha);

		int toggleXDistance = bkgImg.getWidth() - bulletImg.getWidth();
		int cursorX = toggleX + (int) (ratio * toggleXDistance);
		toggleY += TOGGLE_BULLET_TOP_PADDING;

		Painter.drawImage(g, bulletImg, cursorX, toggleY, alpha);
	}

	@Override
	public void onSliderDragged() {
		if (!super.animationIsFrozen) {
			stopAnimationNow();
			stopAnimation();
			super.animationIsFrozen = true;
		}
	}

	@Override
	public void onSliderReleased() {
		if (LOW_RESOLUTION && this.fanState) {
			startAnimation();
		}
		super.animationIsFrozen = false;
	}
}
