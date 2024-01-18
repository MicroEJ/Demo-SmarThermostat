/*
 * Java
 *
 * Copyright 2021-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.*;
import static com.microej.demo.smart_thermostat.page.InsidePage.CIRCULAR_SLIDER_X;
import static com.microej.demo.smart_thermostat.page.InsidePage.CIRCULAR_SLIDER_Y;

import com.microej.demo.smart_thermostat.model.SmartThermostatModel;
import com.microej.demo.smart_thermostat.model.ThermostatObserver;
import com.microej.demo.smart_thermostat.common.FlushVisualizer;
import com.microej.demo.smart_thermostat.style.Fonts;
import com.microej.demo.smart_thermostat.style.Images;
import com.microej.demo.smart_thermostat.common.SliderListener;
import com.microej.demo.smart_thermostat.style.ThermoColors;

import ej.annotation.Nullable;
import ej.basictool.ArrayTools;
import ej.bon.XMath;
import ej.microui.display.BufferedImage;
import ej.microui.display.GraphicsContext;
import ej.microui.display.Painter;
import ej.microui.display.ResourceImage;
import ej.microui.event.Event;
import ej.microui.event.generator.Buttons;
import ej.microui.event.generator.Pointer;
import ej.microvg.*;
import ej.mwt.event.DesktopEventGenerator;
import ej.mwt.event.PointerEventDispatcher;
import ej.mwt.util.Rectangle;
import ej.mwt.util.Size;

/**
 * Slider with a round knob and a circular bar that is filled on the left of the knob.
 */
public class CircularSlider extends FadeInWidget implements ThermostatObserver {

	private static final String TEMP_SIGN = "°F";
	private static final int TEMP_THRESHOLD_FAHRENHEIT_MAX = 90;
	private static final int TEMP_THRESHOLD_FAHRENHEIT_MIN = 60;

	private static final int GRADIENT_MARGIN_LEFT = scale(51);
	private static final int GRADIENT_MARGIN_TOP = scale(16);
	private static final int BUTTON_PLUS_POSITION_Y = scale(90);
	private static final int THRESHOLD_POSITION_Y = scale(190);
	private static final int BUTTON_MINUS_POSITION_Y = scale(293);
	private static final int BUTTONS_POSITION_X = scale(181);

	private static final int TEMP_FONT_SIZE = scale(70);
	private static final int SIGN_FONT_SIZE = scale(32);
	private static final int TEMP_SIGN_X_OFFSET = scale(8);
	private static final int TEMP_SIGN_Y_OFFSET = scale(14);

	/**
	 * Values for the slider and buttons clicking areas
	 */
	private static final int BUTTONS_CLICK_AREA_POSITION_X = scale(508);
	private static final int BUTTON_PLUS_CLICK_AREA_POSITION_Y = scale(358);
	private static final int BUTTON_MINUS_CLICK_AREA_POSITION_Y = scale(561);
	private static final int SLIDER_CLICK_AREA_POSITION_Y = scale(220);
	private static final int SLIDER_CLICK_AREA_SIZE = scale(620);
	private static final int CLICKING_OFFSET_ONE_SIDE = scale(12);

	private static final int BUTTON_SIZE = scale(84);
	private static final int SLIDER_BUTTON_SPACING = scale(20);
	private static final int BUTTON_VERTICAL_OFFSET = scale(5);

	private final VectorImage sliderImage;
	private final Matrix matrix;
	private final int gradientsWidth;
	/** Ratio between slider elapsed time and temperature threshold value */
	private final double constantOfProportionality;
	private final float sliderHeight;
	private final float temperatureHeight;
	private final float temperatureSignWidth;
	private final float temperatureSignHeight;

	private int elapsedTime;
	private boolean pressed;
	private boolean buttonPlusPressed;
	private boolean buttonMinusPressed;

	private @Nullable ResourceImage curveGradientDown;
	private @Nullable ResourceImage curveGradientUp;
	private @Nullable ResourceImage curveGradientUpHighlighted;
	private @Nullable ResourceImage curveGradientDownHighlighted;

	private @Nullable ResourceImage buttonPlusOff;
	private @Nullable ResourceImage buttonPlusOn;
	private @Nullable ResourceImage buttonMinusOff;
	private @Nullable ResourceImage buttonMinusOn;

	private final Rectangle buttonPlusClickArea;
	private final Rectangle buttonMinusClickArea;
	private final Rectangle sliderClickArea;
	private int threshold;
	private @Nullable BufferedImage bufferedThresholdImage;
	private final float temperatureWidth;
	private boolean temperatureDirty = true;
	private SliderListener[] sliderListeners = new SliderListener[0];

	/**
	 * Creates a circular slider.
	 */
	public CircularSlider() {
		super(true);

		this.sliderImage = VectorImage.getImage("/vector-images/cursor-slide/slider.xml"); //$NON-NLS-1$
		this.curveGradientUp = Images.getResourceImage(Images.CURVE_UP, ResourceImage.OutputFormat.ARGB8888);
		this.gradientsWidth = this.curveGradientUp.getWidth();
		this.curveGradientUp.close();

		this.matrix = new Matrix();
		if (!LOW_RESOLUTION) {
			this.matrix.setScale(SCALE, SCALE);
		}
		this.sliderHeight = scale((int) this.sliderImage.getHeight());

		this.constantOfProportionality = getConstantOfProportionality();

		setThreshold(SmartThermostatModel.getInstance().getTemperatureThreshold());

		int xPositionClickButtonsArea = BUTTONS_CLICK_AREA_POSITION_X - CLICKING_OFFSET_ONE_SIDE;
		int yPositionClickButtonPlusArea = BUTTON_PLUS_CLICK_AREA_POSITION_Y - CLICKING_OFFSET_ONE_SIDE;
		int yPositionClickButtonMinusArea = BUTTON_MINUS_CLICK_AREA_POSITION_Y - CLICKING_OFFSET_ONE_SIDE;
		int buttonSizeArea = CLICKING_OFFSET_ONE_SIDE + BUTTON_SIZE + CLICKING_OFFSET_ONE_SIDE;
		this.buttonPlusClickArea = new Rectangle(xPositionClickButtonsArea, yPositionClickButtonPlusArea,
				buttonSizeArea, buttonSizeArea);
		this.buttonMinusClickArea = new Rectangle(xPositionClickButtonsArea, yPositionClickButtonMinusArea,
				buttonSizeArea, buttonSizeArea);
		this.sliderClickArea = new Rectangle(CIRCULAR_SLIDER_X, SLIDER_CLICK_AREA_POSITION_Y,
				(int) this.sliderImage.getWidth() - SLIDER_BUTTON_SPACING, SLIDER_CLICK_AREA_SIZE);

		VectorFont mainFont = Fonts.getBarlowLightItalic();

		this.temperatureWidth = mainFont.measureStringWidth(String.valueOf(TEMP_THRESHOLD_FAHRENHEIT_MAX),
				TEMP_FONT_SIZE);
		this.temperatureHeight = mainFont.measureStringHeight(String.valueOf(TEMP_THRESHOLD_FAHRENHEIT_MAX),
				TEMP_FONT_SIZE);
		this.temperatureSignWidth = mainFont.measureStringWidth(TEMP_SIGN, SIGN_FONT_SIZE);
		this.temperatureSignHeight = mainFont.measureStringHeight(TEMP_SIGN, SIGN_FONT_SIZE);
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		size.setSize(GRADIENT_MARGIN_LEFT + this.gradientsWidth, (int) this.sliderHeight);
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		FlushVisualizer.drawVGArea(g, 0, 0, contentWidth, contentHeight);
		int alpha = getAlpha();

		drawGradients(g, alpha);
		drawThresholdButtons(g, alpha);

		// DRAW SLIDER
		VectorGraphicsPainter.drawAnimatedImage(g, this.sliderImage, this.matrix, this.elapsedTime);
	}

	private void drawGradients(GraphicsContext g, int alpha) {
		Painter.drawImage(g, this.pressed ? this.curveGradientUpHighlighted : this.curveGradientUp,
				GRADIENT_MARGIN_LEFT, GRADIENT_MARGIN_TOP, alpha);
		Painter.drawImage(g, this.pressed ? this.curveGradientDownHighlighted : this.curveGradientDown,
				GRADIENT_MARGIN_LEFT, GRADIENT_MARGIN_TOP + this.curveGradientUp.getHeight(), alpha);
	}

	private void drawThresholdButtons(GraphicsContext g, int alpha) {
		VectorFont mainFont = Fonts.getBarlowLightItalic();

		// BUTTON PLUS
		Painter.drawImage(g, this.buttonPlusPressed ? this.buttonPlusOn : this.buttonPlusOff, BUTTONS_POSITION_X,
				BUTTON_PLUS_POSITION_Y, alpha);
		g.setColor(ThermoColors.INSIDE_LABELS_PRIMARY);
		if (this.temperatureDirty || this.bufferedThresholdImage == null) {
			if (this.bufferedThresholdImage == null) {
				this.bufferedThresholdImage = new BufferedImage(
						(int) (this.temperatureWidth + this.temperatureSignWidth),
						(int) (this.temperatureHeight + this.temperatureSignHeight));
			}

			renderThresholdValue(g, alpha, mainFont);

			if (alpha == MAX_ALPHA) {
				takeScreenshot(this.bufferedThresholdImage);
				this.temperatureDirty = false;
			}
		} else {
			Painter.drawImage(g, this.bufferedThresholdImage, BUTTONS_POSITION_X + BUTTON_VERTICAL_OFFSET,
					THRESHOLD_POSITION_Y + TEMP_SIGN_Y_OFFSET);
		}

		// BUTTON MINUS
		Painter.drawImage(g, this.buttonMinusPressed ? this.buttonMinusOn : this.buttonMinusOff, BUTTONS_POSITION_X,
				BUTTON_MINUS_POSITION_Y, alpha);
	}

	private void renderThresholdValue(GraphicsContext g, int alpha, VectorFont mainFont) {
		Matrix thresholdmatrix = new Matrix();
		// THRESHOLD VALUE
		thresholdmatrix.setTranslate(BUTTONS_POSITION_X + (float) BUTTON_VERTICAL_OFFSET, THRESHOLD_POSITION_Y);
		g.setColor(ThermoColors.INSIDE_LABELS_SECONDARY);
		VectorGraphicsPainter.drawString(g, String.valueOf(this.threshold), mainFont, TEMP_FONT_SIZE, thresholdmatrix,
				alpha, BlendMode.MULTIPLY, 0);

		// THRESHOLD SIGN
		thresholdmatrix.setTranslate(BUTTONS_POSITION_X + this.temperatureWidth + TEMP_SIGN_X_OFFSET,
				THRESHOLD_POSITION_Y + (float) TEMP_SIGN_Y_OFFSET);
		VectorGraphicsPainter.drawString(g, TEMP_SIGN, mainFont, SIGN_FONT_SIZE, thresholdmatrix, alpha,
				BlendMode.MULTIPLY, 0);
	}

	private void takeScreenshot(BufferedImage buffImage) {
		// Use screenshot context only
		GraphicsContext buffImageGc = buffImage.getGraphicsContext();
		buffImageGc.reset();
		/*
		 * Getting the style and rendering the background is not needed for now.
		 */
		// Fill screenshot context with current state.
		Painter.drawDisplayRegion(buffImageGc, CIRCULAR_SLIDER_X + BUTTONS_POSITION_X + BUTTON_VERTICAL_OFFSET,
				CIRCULAR_SLIDER_Y + THRESHOLD_POSITION_Y + TEMP_SIGN_Y_OFFSET, buffImage.getWidth(),
				buffImage.getHeight(), 0, 0);
	}

	@Override
	public boolean handleEvent(int event) {
		int action = Buttons.getAction(event);
		if (event == DesktopEventGenerator.EVENT_TYPE
				&& DesktopEventGenerator.getAction(event) == PointerEventDispatcher.EXITED) {
			action = Buttons.RELEASED;
		}

		if (action != Buttons.RELEASED && Event.getType(event) == Pointer.EVENT_TYPE) {
			Pointer pointer = (Pointer) Event.getGenerator(event);
			// if pressed it can go outside the slider click area
			// if not pressed it has to start in the slider click area
			if (this.pressed || isInArea(pointer, this.sliderClickArea)) {
				return handleSliderEvent(action, pointer);
			} else {
				return handleThresholdButtonEvent(action, pointer);
			}
		} else {
			// update threshold when release outside widget
			boolean consumed = false;
			if (this.pressed) {
				onSliderRelease();
				notifyOnRelease();
				consumed = true;
			} else if (this.buttonMinusPressed || this.buttonPlusPressed) {
				onButtonRelease();
				consumed = true;
			}
			this.pressed = false;
			this.buttonMinusPressed = false;
			this.buttonPlusPressed = false;
			return consumed;
		}
	}

	private boolean handleSliderEvent(int action, Pointer pointer) {
		if (action == Buttons.PRESSED) {
			this.pressed = true;
			onSliderMove(pointer.getY());
			return true;
		} else if (action == Pointer.DRAGGED && this.pressed) {
			notifyOnDragged();
			onSliderMove(pointer.getY());
			return true;
		}
		return false;
	}

	private boolean handleThresholdButtonEvent(int action, Pointer pointer) {
		if (action == Buttons.PRESSED) {
			if (isInArea(pointer, this.buttonPlusClickArea)) {
				this.buttonPlusPressed = true;
				int newThreshold = this.threshold + 1;
				if (newThreshold > TEMP_THRESHOLD_FAHRENHEIT_MAX) {
					newThreshold = TEMP_THRESHOLD_FAHRENHEIT_MAX;
				}
				updateThresholdInModel(newThreshold);
				requestRender();
				return true;
			} else if (isInArea(pointer, this.buttonMinusClickArea)) {
				this.buttonMinusPressed = true;
				int newThreshold = this.threshold - 1;
				if (newThreshold < TEMP_THRESHOLD_FAHRENHEIT_MIN) {
					newThreshold = TEMP_THRESHOLD_FAHRENHEIT_MIN;
				}
				updateThresholdInModel(newThreshold);
				requestRender();
				return true;
			}
		}
		return false;
	}

	private boolean isInArea(Pointer p, Rectangle area) {
		return area.getX() <= p.getX() && p.getX() <= area.getX() + area.getWidth() && area.getY() <= p.getY()
				&& p.getY() <= area.getY() + area.getHeight();
	}

	private void onSliderMove(int pointerY) {
		setElapsedTime(calculateElapsedTime(pointerY));
		requestRender();
	}

	private void onSliderRelease() {
		// Use value previously calculated in onMove
		int proportionalInFahrenheit = calculateThresholdFromElapsedTime(this.elapsedTime);
		updateThresholdInModel(proportionalInFahrenheit);
		requestRender();
	}

	private void onButtonRelease() {
		this.buttonMinusPressed = false;
		this.buttonPlusPressed = false;
		requestRender();
	}

	private void updateThresholdInModel(int threshold) {
		SmartThermostatModel.getInstance().updateTemperatureThreshold(threshold);
		setThreshold(threshold);
	}

	private int calculateElapsedTime(int pointerY) {
		// Calculate the distance from the top of the circular slider
		int distanceFromTop = pointerY - CIRCULAR_SLIDER_Y;
		long duration = this.sliderImage.getDuration();

		// Calculate the proportional value based on the pointer from top with animated image duration
		int calculatedValue = (int) ((distanceFromTop * duration) / (int) this.sliderHeight);
		return XMath.limit(calculatedValue, 0, (int) duration);

	}

	private int calculateElapsedTimeFromThreshold(int threshold) {
		return (int) ((TEMP_THRESHOLD_FAHRENHEIT_MAX - threshold) / this.constantOfProportionality);
	}

	private int calculateThresholdFromElapsedTime(int elapsedTime) {
		return (int) (TEMP_THRESHOLD_FAHRENHEIT_MAX - (elapsedTime * this.constantOfProportionality));
	}

	/**
	 * Sets the current value for the slider cursor position within the Vector Image.
	 *
	 * @param value
	 *            the value to set
	 */
	public void setElapsedTime(int value) {
		this.elapsedTime = value;
	}

	@Override
	public void update(int valueType, int newValue) {
		/*
		 * Updating the threshold value from observer calls is disabled, since it can interfere with Slider movement on
		 * lower end boards.
		 */
		// Uncomment this section to enable the feature back.
		// setThreshold(newValue);
		// requestRender();
	}

	private void setThreshold(int newValue) {
		setElapsedTime(calculateElapsedTimeFromThreshold(newValue));
		this.threshold = newValue;
		this.temperatureDirty = true;
	}

	@Override
	protected void onFadeInDone() {
		super.onFadeInDone();
		this.temperatureDirty = true;
	}

	/**
	 * Calculates the constant ratio value between elapsed time range and temperature range.
	 */
	private double getConstantOfProportionality() {
		double minSlider = 0;
		double maxSlider = this.sliderImage.getDuration();

		return (TEMP_THRESHOLD_FAHRENHEIT_MAX - TEMP_THRESHOLD_FAHRENHEIT_MIN) / (maxSlider - minSlider);
	}

	/**
	 * Adds a {@link SliderListener} to the CircularSlider.
	 * 
	 * @param sliderListener
	 *            the {@link SliderListener} to add.
	 */
	public void addSliderListener(SliderListener sliderListener) {
		if (!ArrayTools.contains(this.sliderListeners, sliderListener)) {
			this.sliderListeners = ArrayTools.add(this.sliderListeners, sliderListener);
		}
	}

	private void notifyOnDragged() {
		for (SliderListener sliderListener : this.sliderListeners) {
			sliderListener.onSliderDragged();
		}
	}

	private void notifyOnRelease() {
		for (SliderListener sliderListener : this.sliderListeners) {
			sliderListener.onSliderReleased();
		}
	}

	@Override
	protected void onShown() {
		this.curveGradientUp = Images.getResourceImage(Images.CURVE_UP, ResourceImage.OutputFormat.ARGB8888);
		this.curveGradientDown = Images.getResourceImage(Images.CURVE_DOWN, ResourceImage.OutputFormat.ARGB8888);
		this.curveGradientUpHighlighted = Images.getResourceImage(Images.CURVE_UP_HIGHLIGHT,
				ResourceImage.OutputFormat.ARGB8888);
		this.curveGradientDownHighlighted = Images.getResourceImage(Images.CURVE_DOWN_HIGHLIGHT,
				ResourceImage.OutputFormat.ARGB8888);
		this.buttonPlusOff = Images.getResourceImage(Images.BUTTON_PLUS_OFF, ResourceImage.OutputFormat.ARGB8888);
		this.buttonPlusOn = Images.getResourceImage(Images.BUTTON_PLUS_ON, ResourceImage.OutputFormat.ARGB8888);
		this.buttonMinusOff = Images.getResourceImage(Images.BUTTON_MINUS_OFF, ResourceImage.OutputFormat.ARGB8888);
		this.buttonMinusOn = Images.getResourceImage(Images.BUTTON_MINUS_ON, ResourceImage.OutputFormat.ARGB8888);
		super.onShown();

		setThreshold(SmartThermostatModel.getInstance().getTemperatureThreshold());

		int temperatureThresholdType = SmartThermostatModel.getInstance().getTemperatureThresholdType();
		SmartThermostatModel.getInstance().addObserver(this, temperatureThresholdType);
	}

	@Override
	protected void onHidden() {
		int temperatureThresholdType = SmartThermostatModel.getInstance().getTemperatureThresholdType();
		SmartThermostatModel.getInstance().removeObserver(this, temperatureThresholdType);

		if (this.bufferedThresholdImage != null) {
			this.bufferedThresholdImage.close();
			this.bufferedThresholdImage = null;
		}
		if (this.curveGradientUp != null) {
			this.curveGradientUp.close();
			this.curveGradientUp = null;
		}
		if (this.curveGradientDown != null) {
			this.curveGradientDown.close();
			this.curveGradientDown = null;
		}
		if (this.curveGradientUpHighlighted != null) {
			this.curveGradientUpHighlighted.close();
			this.curveGradientUpHighlighted = null;
		}
		if (this.curveGradientDownHighlighted != null) {
			this.curveGradientDownHighlighted.close();
			this.curveGradientDownHighlighted = null;
		}
		if (this.buttonPlusOff != null) {
			this.buttonPlusOff.close();
			this.buttonPlusOff = null;
		}
		if (this.buttonPlusOn != null) {
			this.buttonPlusOn.close();
			this.buttonPlusOn = null;
		}
		if (this.buttonMinusOff != null) {
			this.buttonMinusOff.close();
			this.buttonMinusOff = null;
		}
		if (this.buttonMinusOn != null) {
			this.buttonMinusOn.close();
			this.buttonMinusOn = null;
		}
		super.onHidden();
	}
}
