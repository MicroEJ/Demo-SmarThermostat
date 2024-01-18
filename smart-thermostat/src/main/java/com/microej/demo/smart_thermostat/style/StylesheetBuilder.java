/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.style;

import com.microej.demo.smart_thermostat.NavigationDesktop;
import com.microej.demo.smart_thermostat.widget.BubbleLabels;
import com.microej.demo.smart_thermostat.widget.CircularSlider;
import com.microej.demo.smart_thermostat.widget.SecondaryInfo;
import com.microej.demo.smart_thermostat.widget.SecondaryInfoFan;
import com.microej.demo.smart_thermostat.widget.WeatherWidget;

import ej.mwt.style.EditableStyle;
import ej.mwt.style.background.NoBackground;
import ej.mwt.stylesheet.cascading.CascadingStylesheet;
import ej.mwt.stylesheet.selector.ClassSelector;
import ej.mwt.stylesheet.selector.TypeSelector;
import ej.mwt.stylesheet.selector.combinator.AndCombinator;
import ej.mwt.util.Alignment;

/**
 * Loads all the style definitions into a {@link CascadingStylesheet}. This should usually be called only once at
 * start-up, unless the styles change dynamically.
 */
public class StylesheetBuilder {

	private static final int SECONDARY_BUBBLE_CORNER_RADIUS = NavigationDesktop.scale(60);

	/**
	 * Hides the constructor in order to prevent instantiating a class containing only static methods.
	 */
	private StylesheetBuilder() {
		// prevent instantiation
	}

	/**
	 * Creates a {@link CascadingStylesheet} and populates it with all the style definitions.
	 *
	 * @return the created {@link CascadingStylesheet}.
	 */
	public static CascadingStylesheet build() {

		CascadingStylesheet stylesheet = new CascadingStylesheet();

		EditableStyle style = stylesheet.getDefaultStyle();
		style.setBackground(NoBackground.NO_BACKGROUND);

		// BUBBLE LABELS
		TypeSelector bubbleLabel = new TypeSelector(BubbleLabels.class);
		style = stylesheet
				.getSelectorStyle(new AndCombinator(bubbleLabel, new ClassSelector(ClassSelectors.INSIDE_PAGE_LABELS)));
		style.setColor(ThermoColors.INSIDE_LABELS_PRIMARY);
		style.setExtraInt(BubbleLabels.STYLE_TITLE_COLOR, ThermoColors.INSIDE_LABELS_SECONDARY);

		style = stylesheet.getSelectorStyle(
				new AndCombinator(bubbleLabel, new ClassSelector(ClassSelectors.OUTSIDE_PAGE_LABELS)));
		style.setColor(ThermoColors.OUTSIDE_LABELS_PRIMARY);
		style.setExtraInt(BubbleLabels.STYLE_TITLE_COLOR, ThermoColors.OUTSIDE_LABELS_SECONDARY);

		// CIRCULAR SLIDER
		style = stylesheet.getSelectorStyle(new TypeSelector(CircularSlider.class));
		style.setHorizontalAlignment(Alignment.LEFT);
		style.setVerticalAlignment(Alignment.TOP);

		// WEATHER
		style = stylesheet.getSelectorStyle(new TypeSelector(WeatherWidget.class));
		style.setColor(ThermoColors.OUTSIDE_WEATHER_TITLE);
		style.setExtraInt(WeatherWidget.STYLE_WEEKDAY_COLOR, ThermoColors.OUTSIDE_LABELS_SECONDARY);
		style.setExtraInt(WeatherWidget.STYLE_BACKGROUND_COLOR, ThermoColors.BG_BUBBLE);

		// SECONDARY INFO
		TypeSelector secondaryBubble = new TypeSelector(SecondaryInfo.class);
		style = stylesheet.getSelectorStyle(secondaryBubble);
		style.setExtraInt(SecondaryInfo.STYLE_BACKGROUND_COLOR, ThermoColors.BG_BUBBLE);
		style.setExtraInt(SecondaryInfo.STYLE_CORNER_RADIUS, SECONDARY_BUBBLE_CORNER_RADIUS);

		style = stylesheet.getSelectorStyle(
				new AndCombinator(secondaryBubble, new ClassSelector(ClassSelectors.SECONDARY_BUBBLE_OUTSIDE)));
		style.setColor(ThermoColors.OUTSIDE_LABELS_SECONDARY);
		style.setExtraInt(SecondaryInfo.STYLE_TITLE_TEXT_COLOR, ThermoColors.OUTSIDE_LABELS_SECONDARY);

		style = stylesheet.getSelectorStyle(
				new AndCombinator(secondaryBubble, new ClassSelector(ClassSelectors.SECONDARY_BUBBLE_INSIDE)));
		style.setColor(ThermoColors.INSIDE_LABELS_PRIMARY);
		style.setExtraInt(SecondaryInfo.STYLE_TITLE_TEXT_COLOR, ThermoColors.INSIDE_LABELS_SECONDARY);

		style = stylesheet.getSelectorStyle(new TypeSelector(SecondaryInfoFan.class));
		style.setExtraInt(SecondaryInfoFan.STYLE_TOGGLE_BACKGROUND_ON, ThermoColors.BG_TOGGLE_ON);
		style.setExtraInt(SecondaryInfoFan.STYLE_TOGGLE_BACKGROUND_OFF, ThermoColors.BG_TOGGLE_OFF);

		return stylesheet;
	}
}
