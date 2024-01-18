/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.LOW_RESOLUTION;

import com.microej.demo.smart_thermostat.model.SmartThermostatModel;
import com.microej.demo.smart_thermostat.model.ThermostatObserver;
import com.microej.demo.smart_thermostat.style.VectorImages;

import ej.annotation.Nullable;
import ej.bon.Util;
import ej.bon.XMath;
import ej.motion.Motion;
import ej.motion.linear.LinearFunction;
import ej.mwt.animation.Animation;

/**
 * Creates the SecondaryInfo widget for Humidity.
 */
public class SecondaryInfoHumidity extends SecondaryInfo implements ThermostatObserver {

	private static final int PLACEHOLDER_HUMIDITY = 87;

	private static final int HUMIDITY_ANIM_LOOP_BOUND = 80;
	/** Full duration of the animation. Including the loop time at the beginning and the other states in the end. */
	private static final int HUMIDITY_ANIM_FULL_LENGTH = 10000;
	/** The end time of the loop inside the animation. */
	private static final int HUMIDITY_ANIM_HIGH_HUMIDITY_END = 5000;
	/** The length of the animation <b>excluding</b> the loop time. */
	private static final int HUMIDITY_ANIM_LOW_HUMIDITY_LENGTH = HUMIDITY_ANIM_FULL_LENGTH
			- HUMIDITY_ANIM_HIGH_HUMIDITY_END;

	private static final int RANDOM_HUMIDITY_MIN = 20;
	private static final int RANDOM_HUMIDITY_MAX = 95;
	private static final float HUNDRED_PERCENT = 100f;

	private final Animation humidityAnimation;

	private boolean animationInLoop;

	private @Nullable Motion humidityMotion;

	private int humidityPercent;

	/**
	 * Creates the SecondaryInfoHumidity widget.
	 */
	public SecondaryInfoHumidity() {
		super(new String[] { "Humidity" }, VectorImages.HUMIDITY, VectorImages.PLANT);
		this.humidityPercent = PLACEHOLDER_HUMIDITY;
		this.animationInLoop = true;
		this.humidityAnimation = new Animation() {
			@Override
			public boolean tick(long platformTimeMillis) {
				return onHumidityChange(platformTimeMillis);
			}
		};
	}

	/**
	 * Sets the humidity in percent.
	 *
	 * @param humidityPercent
	 *            the humidity in percent: 0-100.
	 * @param force
	 *            if animation should be forcibly run.
	 */
	private void setHumidityPercent(int humidityPercent, boolean force) {
		if (force || humidityPercent != this.humidityPercent) {
			// Actually set the value.
			this.humidityPercent = humidityPercent;

			// Handle Animation of value change.
			boolean oldIsLoop = this.animationInLoop;
			boolean newIsLoop = humidityPercent >= HUMIDITY_ANIM_LOOP_BOUND;
			if (force || !newIsLoop || !oldIsLoop) {

				// If we want to start the loop we always return to 0 first.
				int animationEnd = 0;
				if (!newIsLoop) {
					animationEnd = calculateNewAnimationValue(humidityPercent);
				}
				int currentAnimationTime = (int) this.animationTime;
				long duration = XMath.abs(currentAnimationTime - animationEnd);
				this.humidityMotion = new Motion(LinearFunction.INSTANCE, currentAnimationTime, animationEnd, duration);

				this.animationInLoop = false;
				this.startTime = Util.platformTimeMillis();

				// If the animation was not already running we need to start it.
				if (LOW_RESOLUTION && (force || !oldIsLoop || !super.animationIsFrozen)) {
					getDesktop().getAnimator().startAnimation(this.humidityAnimation);
				}
			}
		}
	}

	private int calculateNewAnimationValue(int humidityPercent) {
		float percentage = humidityPercent / HUNDRED_PERCENT;
		int humidityTime = (int) (HUMIDITY_ANIM_LOW_HUMIDITY_LENGTH - HUMIDITY_ANIM_LOW_HUMIDITY_LENGTH * percentage);
		return HUMIDITY_ANIM_HIGH_HUMIDITY_END + humidityTime;
	}

	private boolean onHumidityChange(long platformTimeMillis) {
		long elapsedTime = platformTimeMillis - this.startTime;

		if (this.animationInLoop) {
			this.animationTime = elapsedTime % HUMIDITY_ANIM_HIGH_HUMIDITY_END;
			requestRender();
		} else {
			Motion motion = this.humidityMotion;
			this.animationTime = motion.getValue(elapsedTime);
			requestRender();
			if (elapsedTime >= motion.getDuration()) {
				// If we returned to a high humidity value we need to continue to loop after the end.
				this.animationInLoop = this.humidityPercent >= HUMIDITY_ANIM_LOOP_BOUND;
				this.startTime = platformTimeMillis;
				return this.animationInLoop;
			}
		}
		return true;
	}

	@Override
	protected String getValueString() {
		return this.humidityPercent + "%";
	}

	@Override
	protected void onShown() {
		super.onShown();
		// Initially set percentage to have correct string value.
		this.humidityPercent = SmartThermostatModel.getInstance().getHumidity();
	}

	@Override
	protected void onFadeInDone() {
		super.onFadeInDone();
		// Set humidity to start animation and add observer.
		SmartThermostatModel model = SmartThermostatModel.getInstance();
		setHumidityPercent(model.getHumidity(), true);
		model.addObserver(this, model.getHumidityType());
	}

	@Override
	protected void onHidden() {
		SmartThermostatModel model = SmartThermostatModel.getInstance();
		model.removeObserver(this, model.getHumidityType());
		getDesktop().getAnimator().stopAnimation(this.humidityAnimation);
		super.onHidden();
	}

	@Override
	public void update(int valueType, int newValue) {
		// Listen to the notification but use a random humidity value
		int value = RANDOM_HUMIDITY_MIN + (int) (Math.random() * ((RANDOM_HUMIDITY_MAX - RANDOM_HUMIDITY_MIN) + 1));
		setHumidityPercent(value, false);
	}

	@Override
	public void onSliderDragged() {
		if (!super.animationIsFrozen) {
			getDesktop().getAnimator().stopAnimation(this.humidityAnimation);
			super.animationIsFrozen = true;
		}
	}

	@Override
	public void onSliderReleased() {
		if (LOW_RESOLUTION) {
			getDesktop().getAnimator().startAnimation(this.humidityAnimation);
		}
		super.animationIsFrozen = false;
	}
}
