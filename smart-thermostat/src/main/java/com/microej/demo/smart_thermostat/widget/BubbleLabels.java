/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.*;

import java.util.logging.Logger;

import com.microej.demo.smart_thermostat.common.ActionListener;
import com.microej.demo.smart_thermostat.common.Context;
import com.microej.demo.smart_thermostat.model.SmartThermostatModel;
import com.microej.demo.smart_thermostat.model.ThermostatObserver;
import com.microej.demo.smart_thermostat.common.FlushVisualizer;
import com.microej.demo.smart_thermostat.style.Fonts;
import com.microej.demo.smart_thermostat.style.VectorImages;

import ej.annotation.Nullable;
import ej.microui.display.BufferedImage;
import ej.microui.display.Colors;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microvg.*;
import ej.mwt.Widget;
import ej.mwt.style.Style;
import ej.mwt.util.Size;

/**
 * Shows the labels inside the animated bubble. Optimizes drawing by using screenshots of the bubble inside plus labels.
 */
public class BubbleLabels extends Widget implements ThermostatObserver {

	/** Selector ID for the title color. */
	public static final int STYLE_TITLE_COLOR = 0;

	private static final Logger LOGGER = Logger.getLogger(BubbleLabels.class.getName());

	private static final String TEMP_SIGN = "°F";
	private static final String HUMIDITY_TITLE = "Humidity";
	private static final String PRESSURE_TITLE = "Pressure";
	private static final String PRESSURE_SIGN = "hPa";
	private static final String TEMP_MAX_MEASURE = "95";

	private static final float SCALE_FOR_ICON_POS = 1.5f;
	private static final int TEMP_FONT_SIZE = scale(160);
	private static final int SIGN_FONT_SIZE = scale(32);
	private static final int TITLE_FONT_SIZE = scale(24);
	private static final int SECONDARY_VALUE_FONT_SIZE = scale(42);
	private static final int PRESSURE_SIGN_FONT_SIZE = scale(18);

	private static final int TEMPERATURE_X_OFFSET = scale(8);
	/** Negative Y Offset value for the big temperature title label. */
	private static final int NEGATIVE_TEMP_Y_OFFSET = scale(-36);
	/** Padding between big temperature title and humidity title. */
	private static final int TEMPERATURE_PADDING = scale(55);
	private static final int TEMP_SIGN_X_OFFSET = scale(8);
	private static final int TEMP_SIGN_Y_OFFSET = scale(8);
	/** Padding between big temperature title and humidity title. */
	private static final int SMALL_PADDING_BETWEEN = scale(2);
	/** Padding between humidity and pressure components. */
	private static final int BIG_PADDING_BETWEEN = scale(20);
	private static final int HUMIDITY_ICON_Y_OFFSET = scale(10);
	private static final int PRESSURE_SIGN_X_OFFSET = scale(86);
	private static final int PRESSURE_SIGN_Y_OFFSET = scale(6);
	/**
	 * Min and Max for random values used when not getting model values.
	 */
	private static final int RANDOM_TEMP_MIN = 60;
	private static final int RANDOM_TEMP_MAX = 90;
	private static final int RANDOM_HUMIDITY_MIN = 75;
	private static final int RANDOM_HUMIDITY_MAX = 88;
	private static final int RANDOM_PRESSURE_MIN = 935;
	private static final int RANDOM_PRESSURE_MAX = 1194;

	private int temperature;
	private int humidity;
	private int pressure;

	private boolean dirty = true;
	private final boolean isInside;
	private final SmartThermostatModel model;
	private final ActionListener actionListener;

	private @Nullable BufferedVectorImage bufferedVectorImage;
	private @Nullable BufferedImage bufferedImage;

	/**
	 * Creates the bubble labels.
	 *
	 * @param actionListener
	 *            listener to check if we are currently on the home page or not.
	 * @param isInside
	 *            {@code true}, if the labels are for inside. This enables updating from native instead of random.
	 */
	public BubbleLabels(ActionListener actionListener, boolean isInside) {
		this.model = SmartThermostatModel.getInstance();
		this.isInside = isInside;
		this.temperature = rndNumber(RANDOM_TEMP_MIN, RANDOM_TEMP_MAX);
		this.humidity = rndNumber(RANDOM_HUMIDITY_MIN, RANDOM_HUMIDITY_MAX);
		this.pressure = rndNumber(RANDOM_PRESSURE_MIN, RANDOM_PRESSURE_MAX);
		this.actionListener = actionListener;
	}

	@Override
	public void onShown() {
		if (!this.actionListener.atHome()) {
			// Outside bubbles also observe, but will use random numbers instead of the 'inside' values.
			int temperatureType = SmartThermostatModel.getInstance().getTemperatureType();
			int humidityType = SmartThermostatModel.getInstance().getHumidityType();
			int pressureType = SmartThermostatModel.getInstance().getPressureType();

			SmartThermostatModel.getInstance().addObserver(this, temperatureType, humidityType, pressureType);
			super.onShown();
		}
	}

	@Override
	public void onHidden() {
		// Outside bubbles also observe, but will use random numbers instead of the 'inside' values.
		int temperatureType = SmartThermostatModel.getInstance().getTemperatureType();
		int humidityType = SmartThermostatModel.getInstance().getHumidityType();
		int pressureType = SmartThermostatModel.getInstance().getPressureType();

		if (this.bufferedImage != null) {
			this.bufferedImage.close();
			this.bufferedImage = null;
		}

		if (this.bufferedVectorImage != null) {
			this.bufferedVectorImage.close();
			this.bufferedVectorImage = null;
		}

		SmartThermostatModel.getInstance().removeObserver(this, temperatureType, humidityType, pressureType);
		super.onHidden();
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Nothing to do yet. Size given by canvas.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		if (Context.INSTANCE.inTransition() || BubbleWidget.isInFade()) {
			renderBufferedVectorImage(g, contentWidth, contentHeight);
		} else {
			renderBufferedImage(g, contentWidth, contentHeight);
		}
	}

	private void renderBufferedImage(GraphicsContext g, int contentWidth, int contentHeight) {
		// Use buffered image
		if (this.dirty || this.bufferedImage == null) {
			// reset the buffered vector image so we get rid of previous background color
			if (this.bufferedVectorImage != null) {
				this.bufferedVectorImage.close();
				this.bufferedVectorImage = null;
			}
			if (this.bufferedImage == null) {
				this.bufferedImage = new BufferedImage(contentWidth, contentHeight);
			}
			if (this.isInside && !this.actionListener.atHome()) {
				g.setColor(Colors.WHITE);
				Painter.fillRectangle(g, 0, 0, contentWidth, contentHeight);
			}
			renderActualContent(g, contentWidth, contentHeight);
			// Prepare image for next time
			takeScreenshot(this.bufferedImage, contentWidth, contentHeight);
			this.dirty = false;
		} else {
			Painter.drawImage(g, this.bufferedImage, 0, 0);
		}
	}

	private void renderBufferedVectorImage(GraphicsContext g, int contentWidth, int contentHeight) {
		if (this.dirty || this.bufferedVectorImage == null) {
			// reset the buffered image for getting rid of previous bg color
			if (this.bufferedImage != null) {
				this.bufferedImage.close();
				this.bufferedImage = null;
			}
			if (this.bufferedVectorImage == null) {
				this.bufferedVectorImage = new BufferedVectorImage(contentWidth, contentHeight);
			}
			this.bufferedVectorImage.clear();
			renderActualContent(this.bufferedVectorImage.getGraphicsContext(), contentWidth, contentHeight);
			this.dirty = false;
		}
		VectorGraphicsPainter.drawImage(g, this.bufferedVectorImage, 0, 0);
	}

	private void renderActualContent(GraphicsContext g, int contentWidth, int contentHeight) {
		FlushVisualizer.drawVGArea(g, 0, 0, contentWidth, contentHeight);
		LOGGER.finest("drawing unbuffered label image");

		Style style = getStyle();
		int primaryColor = style.getColor();
		int titleColor = style.getExtraInt(STYLE_TITLE_COLOR, primaryColor);

		VectorFont mainFont = Fonts.getBarlowLightItalic();
		VectorFont titleFont = Fonts.getBarlowMedium();
		VectorFont secondaryValueFont = Fonts.getBarlowMedium();

		int temperatureWidth = (int) mainFont.measureStringWidth(TEMP_MAX_MEASURE, TEMP_FONT_SIZE);
		int temperatureHeight = (int) mainFont.measureStringHeight(TEMP_MAX_MEASURE, TEMP_FONT_SIZE);
		int titleHeight = (int) titleFont.measureStringHeight(HUMIDITY_TITLE, TITLE_FONT_SIZE);
		int secondaryValueHeight = (int) secondaryValueFont.measureStringHeight(HUMIDITY_TITLE,
				SECONDARY_VALUE_FONT_SIZE);

		int humidityTitleY = temperatureHeight + TEMPERATURE_PADDING;
		int humidityValueY = humidityTitleY + titleHeight + SMALL_PADDING_BETWEEN;
		int pressionTitleY = humidityValueY + secondaryValueHeight + BIG_PADDING_BETWEEN;
		int pressionValueY = pressionTitleY + titleHeight + SMALL_PADDING_BETWEEN;

		// TEMPERATURE
		g.setColor(primaryColor);
		VectorGraphicsPainter.drawString(g, String.valueOf(this.temperature), mainFont, TEMP_FONT_SIZE, 0,
				NEGATIVE_TEMP_Y_OFFSET);

		VectorGraphicsPainter.drawString(g, TEMP_SIGN, mainFont, SIGN_FONT_SIZE,
				temperatureWidth + (float) TEMP_SIGN_X_OFFSET, TEMP_SIGN_Y_OFFSET);

		// HUMIDITY
		g.setColor(titleColor);
		VectorGraphicsPainter.drawString(g, HUMIDITY_TITLE, titleFont, TITLE_FONT_SIZE, TEMPERATURE_X_OFFSET,
				humidityTitleY);

		g.setColor(primaryColor);

		Matrix matrix = new Matrix();
		matrix.setTranslate(0, humidityValueY + (float) HUMIDITY_ICON_Y_OFFSET);
		if (!LOW_RESOLUTION) {
			matrix.preScale(SCALE, SCALE);
		}

		ResourceVectorImage humidityIcon = ResourceVectorImage.loadImage(VectorImages.HUMIDITY);
		int iconX = (int) ((((int) humidityIcon.getWidth()) + SMALL_PADDING_BETWEEN) * SCALE_FOR_ICON_POS);
		VectorGraphicsPainter.drawImage(g, humidityIcon, matrix);
		VectorGraphicsPainter.drawString(g, this.humidity + "%", secondaryValueFont, SECONDARY_VALUE_FONT_SIZE, iconX,
				humidityValueY);

		// PRESSURE
		g.setColor(titleColor);
		VectorGraphicsPainter.drawString(g, PRESSURE_TITLE, titleFont, TITLE_FONT_SIZE, TEMPERATURE_X_OFFSET,
				pressionTitleY);

		g.setColor(primaryColor);

		matrix = new Matrix();
		matrix.setTranslate(0, pressionValueY + (float) HUMIDITY_ICON_Y_OFFSET);
		if (!LOW_RESOLUTION) {
			matrix.preScale(SCALE, SCALE);
		}

		ResourceVectorImage pressureIcon = ResourceVectorImage.loadImage(VectorImages.PRESSURE);
		VectorGraphicsPainter.drawImage(g, pressureIcon, matrix);
		VectorGraphicsPainter.drawString(g, String.valueOf(this.pressure), secondaryValueFont,
				SECONDARY_VALUE_FONT_SIZE, iconX, pressionValueY);
		VectorGraphicsPainter.drawString(g, PRESSURE_SIGN, secondaryValueFont, PRESSURE_SIGN_FONT_SIZE,
				iconX + (float) PRESSURE_SIGN_X_OFFSET, pressionValueY + (float) PRESSURE_SIGN_Y_OFFSET);

		humidityIcon.close();
		pressureIcon.close();
	}

	/**
	 * Takes a screenshot of the bubble.
	 *
	 * @param buffImage
	 *            the buffered image.
	 * @param contentWidth
	 *            the width of the content.
	 * @param contentHeight
	 *            the height of the content.
	 */
	public void takeScreenshot(BufferedImage buffImage, int contentWidth, int contentHeight) {
		// Use screenshot context only
		GraphicsContext buffImageGc = buffImage.getGraphicsContext();
		buffImageGc.reset();
		/*
		 * Getting the style and rendering the background is not needed for now.
		 */
		Painter.drawDisplayRegion(buffImageGc, this.getX(), this.getY(), contentWidth, contentHeight, 0, 0);
	}

	/**
	 * Updates the bubble labels randomly.
	 */
	public void updateBubbleLabels() {
		int newTemp = this.temperature;
		int newHumidity = this.humidity;
		int newPressure = this.pressure;
		if (this.isInside) {
			newTemp = this.model.getTemperature();
			newHumidity = this.model.getHumidity();
			newPressure = this.model.getPressure();
		} else {
			newPressure = this.model.getPressure();
			// Update the random temperature and the humidity only if new data has been pushed in the event queue
			if (newPressure != this.pressure) {
				newTemp = rndNumber(RANDOM_TEMP_MIN, RANDOM_TEMP_MAX);
				newHumidity = rndNumber(RANDOM_HUMIDITY_MIN, RANDOM_HUMIDITY_MAX);
			}
		}

		if (newTemp != this.temperature || newHumidity != this.humidity || newPressure != this.pressure) {
			this.temperature = newTemp;
			this.humidity = newHumidity;
			this.pressure = newPressure;
			this.dirty = true;
		}
	}

	private int rndNumber(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	@Override
	public void update(int valueType, int newValue) {
		updateBubbleLabels();
		requestRender();
	}
}
