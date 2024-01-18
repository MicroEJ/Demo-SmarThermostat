/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;

import com.microej.demo.smart_thermostat.MainCanvas;
import com.microej.demo.smart_thermostat.model.SmartThermostatModel;
import com.microej.demo.smart_thermostat.model.ThermostatObserver;
import com.microej.demo.smart_thermostat.style.ThermoColors;
import com.microej.demo.smart_thermostat.style.Fonts;
import com.microej.demo.smart_thermostat.style.VectorImages;

import ej.annotation.Nullable;
import ej.bon.Timer;
import ej.bon.TimerTask;
import ej.microui.display.BufferedImage;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microvg.VectorGraphicsPainter;
import ej.microvg.VectorImage;
import ej.mwt.Widget;
import ej.mwt.util.Size;
import ej.service.ServiceFactory;

/**
 * Popup displaying an external change of the Temperature Threshold.
 */
public class TempThresholdPopup extends Widget implements ThermostatObserver {
	private static final String TEMP_SIGN = "°F";
	private static final int ICON_X = scale(246);
	private static final int ICON_Y = scale(36);
	private static final int LABEL_X = scale(140);
	private static final int LABEL_Y = scale(28);
	private static final int LABEL_SIZE = scale(38);
	private static final int SIGN_X = scale(178);
	private static final int SIGN_Y = scale(36);
	private static final int SIGN_SIZE = scale(16);
	private static final int TICK_TIME = 50;
	private static final long DELAY = 5000;
	private static final int POPUP_IN_END = 360;
	private static final int POPUP_OUT_END = 600;
	private static final int DIRTY_TICKS_COUNT = 5;
	private final VectorImage thresholdPopupImage;
	private final VectorImage thresholdUpImage;
	private final VectorImage thresholdDownImage;
	private int elapsedTime;
	private int thresholdTemp;
	private boolean active;
	private boolean scheduledAnimateOut;
	private boolean poppingIn;
	private boolean thresholdUp;
	private final BufferedImage bufferedImage;
	private boolean dirty = true;
	private int dirtyTicks = 0;
	private boolean parallaxRunning = false;

	private @Nullable TimerTask animateOutTask;

	/**
	 * Creates the TempThresholdPopup.
	 */
	public TempThresholdPopup() {
		this.thresholdPopupImage = VectorImage.getImage(VectorImages.THRESHOLD_POPUP); // $NON-NLS-1$
		this.thresholdUpImage = VectorImage.getImage(VectorImages.THRESHOLD_POPUP_UP); // $NON-NLS-1$
		this.thresholdDownImage = VectorImage.getImage(VectorImages.THRESHOLD_POPUP_DOWN); // $NON-NLS-1$
		this.bufferedImage = new BufferedImage((int) this.thresholdPopupImage.getWidth(),
				(int) this.thresholdPopupImage.getHeight());
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Nothing to do yet. Set from canvas container.
	}

	@Override
	public void render(GraphicsContext g) {
		if (!this.active) {
			return;
		}
		super.render(g);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		if (!this.dirty) {
			Painter.drawImage(g, this.bufferedImage, 0, 0);
			return;
		}
		VectorGraphicsPainter.drawAnimatedImage(g, this.thresholdPopupImage, 0, 0, this.elapsedTime);
		if (this.scheduledAnimateOut) {
			g.setColor(ThermoColors.POPUP_LABELS_PRIMARY);
			VectorGraphicsPainter.drawString(g, String.valueOf(this.thresholdTemp), Fonts.getBarlowLightItalic(),
					LABEL_SIZE, LABEL_X, LABEL_Y);
			VectorGraphicsPainter.drawString(g, TEMP_SIGN, Fonts.getBarlowLightItalic(), SIGN_SIZE, SIGN_X, SIGN_Y);
			VectorGraphicsPainter.drawAnimatedImage(g,
					this.thresholdUp ? this.thresholdUpImage : this.thresholdDownImage, ICON_X, ICON_Y, 0);
			if (this.poppingIn) {
				this.dirtyTicks++;
				if (this.dirtyTicks > DIRTY_TICKS_COUNT) {
					takeScreenshot(this.bufferedImage);
					this.dirty = false;
				}
			}
		}
	}

	private void takeScreenshot(BufferedImage buffImage) {
		// Use screenshot context only
		GraphicsContext buffImageGc = buffImage.getGraphicsContext();
		buffImageGc.reset();
		/*
		 * Getting the style and rendering the background is not needed for now.
		 */
		// Fill screenshot context with current state.
		Painter.drawDisplayRegion(buffImageGc, MainCanvas.TEMP_THRESHOLD_MARGIN_LEFT,
				MainCanvas.TEMP_THRESHOLD_MARGIN_TOP, buffImage.getWidth(), buffImage.getHeight(), 0, 0);
	}

	/**
	 * Continues the animation of the popup.
	 * <p>
	 * A tick is {@value TICK_TIME}ms
	 * </p>
	 */
	public void tick() {
		// Only advance pop-up animation, or schedule popping out when parallax has started
		if (!this.active || !this.parallaxRunning) {
			return;
		}
		this.elapsedTime += TICK_TIME;
		if (this.poppingIn) {
			if (this.elapsedTime >= POPUP_IN_END) { // Finished popping up, keeps showing
				if (!this.scheduledAnimateOut) { // Only schedule once
					scheduleAnimateOut();
					this.dirtyTicks = 0;
				}
				this.elapsedTime = POPUP_IN_END;
				this.scheduledAnimateOut = true;
			}
		} else {
			// popup out
			if (this.elapsedTime >= POPUP_OUT_END) {
				this.elapsedTime = 0;
				this.active = false;
				this.parallaxRunning = false;
			}
		}
	}

	@Override
	protected void onShown() {
		this.elapsedTime = 0;
		this.dirty = true;
		this.dirtyTicks = 0;
		SmartThermostatModel model = SmartThermostatModel.getInstance();
		this.thresholdTemp = model.getTemperatureThreshold();
		model.addObserver(this, model.getTemperatureThresholdType());
	}

	@Override
	protected void onHidden() {
		SmartThermostatModel model = SmartThermostatModel.getInstance();
		model.removeObserver(this, model.getTemperatureThresholdType());
		this.active = false;
		stopTimerTask();
	}

	@Override
	public void update(int valueType, int newValue) {
		if (this.thresholdTemp != newValue) {
			this.thresholdUp = this.thresholdTemp < newValue;
			this.thresholdTemp = newValue;
			this.active = true; // start tick and rendering.
			this.poppingIn = true;
			this.scheduledAnimateOut = false;
			this.dirty = true;
		}
	}

	private void scheduleAnimateOut() {
		stopTimerTask();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				TempThresholdPopup.this.poppingIn = false;
				TempThresholdPopup.this.scheduledAnimateOut = false;
				TempThresholdPopup.this.dirty = true;
				TempThresholdPopup.this.dirtyTicks = 0;
			}
		};
		this.animateOutTask = task;
		ServiceFactory.getService(Timer.class, Timer.class).schedule(task, DELAY);
	}

	private void stopTimerTask() {
		TimerTask task = this.animateOutTask;
		if (task != null) {
			task.cancel();
			this.animateOutTask = null;
		}
	}

	/**
	 * Tells the threshold pop-up whether the parallax is running, or not. The pop-up should not start or continue
	 * advancing, if the parallax is not running.
	 *
	 * @param running {@code true}, if the parallax is currently running.
	 */
	public void parallaxRunning(boolean running) {
		this.parallaxRunning = running;
	}
}
