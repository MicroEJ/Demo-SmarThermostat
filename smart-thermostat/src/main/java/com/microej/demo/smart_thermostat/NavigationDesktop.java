/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat;

import com.microej.demo.smart_thermostat.common.Context;
import com.microej.demo.smart_thermostat.common.ActionListener;
import com.microej.demo.smart_thermostat.style.FullScreenRenderPolicy;

import ej.bon.Constants;
import ej.microui.display.Display;
import ej.microui.event.Event;
import ej.microui.event.generator.Pointer;
import ej.mwt.Desktop;
import ej.mwt.render.RenderPolicy;
import ej.mwt.util.Rectangle;

/**
 * The desktop for the thermostat application.
 */
public class NavigationDesktop extends Desktop {
	/**
	 * true for 720p, false for 460p. When changing this value, update the images and fonts lists files.
	 * <p>
	 * WARNING: Don't set this field final to prevent the compiler to inline it.
	 */
	@SuppressWarnings({ "java:S3008", "java:S1104", "java:S1444", "CanBeFinal" })
	public static boolean LOW_RESOLUTION = !Constants.getBoolean("HIGH_RESOLUTION_SWITCH"); //$NON-NLS-1$

	public static final float SCALE = 1.5f;

	public static final float TOUCH_HEIGHT_MULTIPLIER = 0.15f;

	private final Rectangle insideClickArea;
	private final Rectangle outsideClickArea;
	private final Rectangle bottomClickArea;
	private final ActionListener actionListener;

	/**
	 * Creates the NavigationDesktop.
	 * 
	 * @param actionListener
	 *            the {@link ActionListener} to interact with the UI.
	 */
	public NavigationDesktop(ActionListener actionListener) {
		Display display = Display.getDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		int touchHeight = (int) (height * TOUCH_HEIGHT_MULTIPLIER);
		int halfWidth = width / 2;

		this.insideClickArea = new Rectangle(0, height - touchHeight, halfWidth, touchHeight);
		this.outsideClickArea = new Rectangle(halfWidth, height - touchHeight, halfWidth, touchHeight);
		this.bottomClickArea = new Rectangle(0, height - touchHeight, width, touchHeight);
		this.actionListener = actionListener;
	}

	@Override
	protected RenderPolicy createRenderPolicy() {
		return new FullScreenRenderPolicy(this);
	}

	@Override
	public boolean handleEvent(int event) {
		ActionListener action = this.actionListener;
		Context context = Context.INSTANCE;
		Context.State state = context.getCurrentState();
		if (!context.inTransition() && isTouch(event)) {
			Pointer p = getTouchPointer(event);
			if (state.equals(Context.State.HOME)) {
				if (isInArea(p, this.insideClickArea)) {
					return action.navigateInside();
				} else if (isInArea(p, this.outsideClickArea)) {
					return action.navigateOutside();
				}
			} else {
				if (isInArea(p, this.bottomClickArea)) {
					return action.navigateHome();
				}
			}
		}
		return super.handleEvent(event);
	}

	private boolean isInArea(Pointer p, Rectangle area) {
		return area.getX() <= p.getX() && p.getX() <= area.getX() + area.getWidth() && area.getY() <= p.getY()
				&& p.getY() <= area.getY() + area.getHeight();
	}

	/**
	 * Tests if the event is a touch event.
	 *
	 * @param event
	 *            the integer event.
	 * @return true if the event is a touch event, false otherwise.
	 */
	public static boolean isTouch(int event) {
		return Event.getType(event) == Pointer.EVENT_TYPE;
	}

	/**
	 * Gets the touch pointer. This method is only supposed to be called if {@link #isTouch} returns true.
	 *
	 * @param event
	 *            the integer event.
	 * @return the touch pointer reference or null.
	 */
	public static Pointer getTouchPointer(int event) {
		return (Pointer) Event.getGenerator(event);
	}

	/**
	 * Scales the given value to fit the HDPI board, if we are on the HDPI board.
	 * 
	 * @param value
	 *            the value to scale.
	 * @return the scaled value.
	 */
	public static int scale(int value) {
		return LOW_RESOLUTION ? value : (int) (value * SCALE);
	}

}
