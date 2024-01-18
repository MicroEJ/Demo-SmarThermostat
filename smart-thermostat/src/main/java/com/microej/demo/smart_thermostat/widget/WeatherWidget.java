/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;

import java.util.Arrays;
import java.util.Calendar;

import com.microej.demo.smart_thermostat.model.WeatherCondition;
import com.microej.demo.smart_thermostat.style.Fonts;
import com.microej.demo.smart_thermostat.style.Images;

import ej.annotation.Nullable;
import ej.microui.display.*;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.motion.Motion;
import ej.motion.sine.SineEaseInFunction;
import ej.mwt.style.Style;
import ej.mwt.util.Alignment;
import ej.mwt.util.Size;

/**
 * Widget displaying the weather forecast for this week.
 */
public class WeatherWidget extends FadeInWidget {

	/** Style id for the text color of the weekdays. */
	public static final int STYLE_WEEKDAY_COLOR = 0;
	/** Style id for the text color to use for the background. */
	public static final int STYLE_BACKGROUND_COLOR = 1;

	/** The distance used during the ease in animation. **/
	public static final int ANIMATION_EASE_IN_DISTANCE = scale(24);

	private static final int TITLE_FONT_SIZE = scale(24);
	private static final int OTHER_FONT_SIZE = scale(18);
	private static final int OTHER_ICON_SIZE = scale(70);

	private static final int ICON_COL_ONE_LEFT_DISTANCE = scale(41);
	private static final int TEXT_COL_TWO_RIGHT_DISTANCE = scale(114);
	private static final int ICON_COL_TWO_RIGHT_DISTANCE = scale(76);

	private static final int FORECAST_BOTTOM_OFFSET = scale(12);
	private static final int WEEKDAY_TOP_OFFSET = scale(8);

	private static final int SMALL_ICONS_IN_COLUMN = 3;

	private static final int DAYS_IN_WEEK = 7;

	private static final String TODAY = "Today";
	private static final String FORECAST = "Forecast";

	private final Motion textPosMotion;

	private int currentDayOfWeek;
	private WeatherCondition[] weekOfWeatherData;
	private String[] daysOfWeek;

	private boolean requestRedraw;
	private @Nullable BufferedImage screenshot;

	/**
	 * Creates the WeatherWidget.
	 */
	public WeatherWidget() {
		this.requestRedraw = true;
		this.currentDayOfWeek = -1;
		this.daysOfWeek = new String[DAYS_IN_WEEK];
		this.weekOfWeatherData = new WeatherCondition[DAYS_IN_WEEK];

		this.textPosMotion = new Motion(SineEaseInFunction.INSTANCE, ANIMATION_EASE_IN_DISTANCE, 0,
				getFadeInDuration());
	}

	/**
	 * Sets the weather data to display.
	 * <p>
	 * The expected weather data is an array of <b>7 {@link WeatherCondition}</b>, one for each day of the week. The
	 * first integer is <b>Today</b> and the rest is for the next 6 weekdays in order.
	 * </p>
	 * 
	 * @param weekOfWeatherData
	 *            the weather data to display.
	 */
	public void setWeather(WeatherCondition[] weekOfWeatherData) {
		if (weekOfWeatherData.length != DAYS_IN_WEEK) {
			throw new IllegalArgumentException();
		}
		if (!Arrays.equals(this.weekOfWeatherData, weekOfWeatherData)) {
			createWeekDayArray();
			this.weekOfWeatherData = weekOfWeatherData.clone();
			this.requestRedraw = true;
		}
	}

	private void createWeekDayArray() {
		Calendar today = Calendar.getInstance();
		int currentDay = today.get(Calendar.DAY_OF_WEEK);
		if (this.currentDayOfWeek != currentDay) {
			String[] daysOfWeek = new String[DAYS_IN_WEEK];
			daysOfWeek[0] = TODAY;

			for (int i = 1; i < DAYS_IN_WEEK; i++) {
				today.set(Calendar.DAY_OF_WEEK, today.get(Calendar.DAY_OF_WEEK) + 1);
				daysOfWeek[i] = weekdayToString(today.get(Calendar.DAY_OF_WEEK));
			}

			this.daysOfWeek = daysOfWeek;
			this.currentDayOfWeek = currentDay;
			this.requestRedraw = true;
		}
	}

	private String weekdayToString(int dayOfWeek) {
		switch (dayOfWeek) {
		case Calendar.MONDAY:
			return "Mon";
		case Calendar.TUESDAY:
			return "Tue";
		case Calendar.WEDNESDAY:
			return "Wed";
		case Calendar.THURSDAY:
			return "Thu";
		case Calendar.FRIDAY:
			return "Fri";
		case Calendar.SATURDAY:
			return "Sat";
		default:
		case Calendar.SUNDAY:
			return "Sun";
		}
	}

	@Override
	protected void onShown() {
		this.requestRedraw = true;
		super.onShown();
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		if (this.screenshot != null) {
			this.screenshot.close();
			this.screenshot = null;
		}
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Nothing to compute, given by outside fixed size.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		long elapsed = getFadeInElapsedTime();
		int yPos = this.textPosMotion.getValue(elapsed);
		int alpha = getAlpha(elapsed);

		if (this.requestRedraw || this.screenshot == null) {
			if (this.screenshot == null) {
				this.screenshot = new BufferedImage(contentWidth, contentHeight - ANIMATION_EASE_IN_DISTANCE);
			}
			renderWeatherContent(this.screenshot.getGraphicsContext(), contentWidth,
					contentHeight - ANIMATION_EASE_IN_DISTANCE);
			this.requestRedraw = false;
		}

		Painter.drawImage(g, this.screenshot, 0, yPos, alpha);
	}

	private void renderWeatherContent(GraphicsContext g, int contentWidth, int contentHeight) {
		Style style = getStyle();
		int color = style.getColor();
		int secondaryColor = style.getExtraInt(STYLE_WEEKDAY_COLOR, color);
		int backgroundColor = style.getExtraInt(STYLE_BACKGROUND_COLOR, Colors.WHITE);

		g.setColor(backgroundColor);
		Painter.fillRectangle(g, 0, 0, contentWidth, contentHeight);

		VectorFont font = Fonts.getBarlowMedium();
		String[] days = this.daysOfWeek;
		g.setColor(color);
		VectorGraphicsPainter.drawString(g, days[0], font, TITLE_FONT_SIZE, 0, 0);

		// Draw Icon
		WeatherCondition[] weather = this.weekOfWeatherData;
		ResourceImage mainIcon = Images.getWeatherImage(weather[0], true);
		int xMainIcon = Alignment.computeLeftX(mainIcon.getWidth(), 0, contentWidth, Alignment.RIGHT);
		Painter.drawImage(g, mainIcon, xMainIcon, 0);
		mainIcon.close();

		int yPos = contentHeight - SMALL_ICONS_IN_COLUMN * OTHER_ICON_SIZE;
		int xIconColumnTwo = contentWidth - ICON_COL_TWO_RIGHT_DISTANCE;
		int xTextColumnTwo = contentWidth - TEXT_COL_TWO_RIGHT_DISTANCE;

		VectorGraphicsPainter.drawString(g, FORECAST, font, TITLE_FONT_SIZE, 0,
				yPos - TITLE_FONT_SIZE - (float) FORECAST_BOTTOM_OFFSET);
		g.setColor(secondaryColor);
		for (int i = 1; i < DAYS_IN_WEEK; i++) {
			boolean isSecond = i % 2 == 0;

			// Draw Text
			int xTextPos = isSecond ? xTextColumnTwo : 0;
			VectorGraphicsPainter.drawString(g, days[i], font, OTHER_FONT_SIZE, xTextPos,
					yPos + (float) WEEKDAY_TOP_OFFSET);
			// Draw Icon
			ResourceImage icon = Images.getWeatherImage(weather[i], false);
			int xIconPos = isSecond ? ICON_COL_ONE_LEFT_DISTANCE : xIconColumnTwo;
			Painter.drawImage(g, icon, xIconPos, yPos);
			icon.close();

			if (isSecond) {
				yPos += OTHER_ICON_SIZE;
			}
		}
	}
}
