/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.common;

import ej.bon.XMath;

/**
 * Progress makes it easier to work with a value range.<br>
 * Examples:<br>
 * 0 - 100 <br>
 * -15 - 31 <br>
 * 30.55 - 99.50 <br>
 * <br>
 * or backward: <br>
 * 100 - 0 <br>
 * 31 - (-15) <br>
 * 99.50 - 30.55 <br>
 *
 */
public class Progress {
	/**
	 * Direction defines the direction of the progress.
	 */
	public enum Direction {
		/**
		 * FORWARD as in 1..2..3
		 */
		FORWARD,
		/**
		 * BACKWARD as in 3..2..1
		 */
		BACKWARD
	}

	/**
	 * 100 Percent constant.
	 */
	public static final int HUNDRED = 100;
	private double currentPercent = 0;
	private double minProgress = 0;
	private double maxProgress = HUNDRED;
	private Direction direction = Direction.FORWARD;

	/**
	 * Configure the ranged progress value. Define minimal, maximal progress value, the scale of the decimals and the
	 * default direction as forward.
	 *
	 * @param min
	 *            minimum progress value.
	 * @param max
	 *            maximum progress value.
	 *
	 * @throws IllegalArgumentException
	 *             if min is higher than max.
	 */
	public void configure(double min, double max) {
		configure(min, max, Direction.FORWARD);
	}

	/**
	 * Configure the ranged progress value. Define minimal, maximal progress value, the scale of the decimals and the
	 * direction.
	 *
	 * @param min
	 *            minimum progress value.
	 * @param max
	 *            maximum progress value.
	 * @param direction
	 *            to define which direction the progress starts.
	 *
	 * @throws IllegalArgumentException
	 *             if min is higher than max.
	 */
	public void configure(double min, double max, Direction direction) {
		if (min > max) {
			throw new IllegalArgumentException("min cannot be higher than max"); //$NON-NLS-1$
		}
		this.minProgress = min;
		this.maxProgress = max;
		this.direction = direction;
		// Reset to -1 to ensure change is true even for 0 after calling configure.
		this.currentPercent = -1;
	}

	/**
	 * Sets progress percentage.
	 *
	 * @param progress
	 *            the progress percentage.
	 * @return {@code true}, if the provided progress is different from the existing one and {@code false}, if the same
	 *         value was provided.
	 */
	public boolean setPercent(double progress) {
		progress = this.ensureProgressInRange(progress);
		if (this.currentPercent != progress) {
			this.currentPercent = progress;
			return true;
		}
		return false;
	}

	/**
	 * Sets the progress value.
	 *
	 * @param progressValue
	 *            the progress value.
	 */
	public void setValue(double progressValue) {
		progressValue = XMath.limit(progressValue, this.minProgress, this.maxProgress);
		if (this.direction == Direction.BACKWARD) {
			progressValue = this.maxProgress - progressValue;
		} else {
			progressValue = progressValue - this.minProgress;
		}
		setPercent(fromProgressValueToPercent(this.maxProgress - this.minProgress, HUNDRED, progressValue));
	}

	/**
	 * Gets the progress value that has been configured.
	 *
	 * @return the custom progress value.
	 */
	public double getValue() {
		double current = fromPercentToProgressValue(this.maxProgress - this.minProgress, HUNDRED, getPercent());
		if (this.direction == Direction.BACKWARD) {
			return this.maxProgress - current;
		}
		current += this.minProgress;
		return current;
	}

	/**
	 * Gets the progress in percent.
	 *
	 * @return the progress in percent.
	 */
	public double getPercent() {
		return this.ensureProgressInRange(this.currentPercent);
	}

	/**
	 * Gets the progress value by providing the following parameter:
	 *
	 * @param maxProgressValue
	 *            the highest progress value.
	 * @param maxPercent
	 *            the percentage of the highest progress value.
	 * @param currentPercent
	 *            the current percent you want the progress value for.
	 * @return the calculated progress value.
	 */
	public static double fromPercentToProgressValue(double maxProgressValue, double maxPercent, double currentPercent) {
		return maxProgressValue * currentPercent / maxPercent;
	}

	/**
	 * Gets the percentage by providing the following parameter:
	 *
	 * @param maxProgressValue
	 *            the highest progress value.
	 * @param maxPercent
	 *            the percentage of the highest progress value.
	 * @param currentProgressValue
	 *            the progress value you want the percentage for.
	 * @return the calculated percent value.
	 */
	public static double fromProgressValueToPercent(double maxProgressValue, double maxPercent,
			double currentProgressValue) {
		return currentProgressValue / maxProgressValue * maxPercent;
	}

	private double ensureProgressInRange(double progress) {
		return XMath.limit(progress, 0.0, HUNDRED);
	}
}
