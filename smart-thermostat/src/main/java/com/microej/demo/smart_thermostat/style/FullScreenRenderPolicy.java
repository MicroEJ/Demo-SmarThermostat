/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.style;

import ej.bon.Constants;
import ej.microui.MicroUI;
import ej.microui.display.Display;
import ej.microui.display.GraphicsContext;
import ej.mwt.Desktop;
import ej.mwt.Widget;
import ej.mwt.render.RenderPolicy;

/**
 * This render policy, re-renders the full screen.
 * <p>
 * That means, the whole widget tree attached to the current desktop is rendered, whenever a widget is rendered.
 * </p>
 */
public class FullScreenRenderPolicy extends RenderPolicy {

	private boolean pendingRepaint;

	/**
	 * Creates a full screen render policy.
	 *
	 * @param desktop
	 *            the desktop to render.
	 */
	public FullScreenRenderPolicy(Desktop desktop) {
		super(desktop);
	}

	@Override
	public void renderDesktop() {
		Desktop desktop = getDesktop();
		Widget widget = desktop.getWidget();
		if (widget != null) {
			// reset translation and clip
			GraphicsContext g = Display.getDisplay().getGraphicsContext();
			g.resetTranslation();
			g.resetClip();

			// render widget
			if (widget.isShown()) {
				renderWidget(g, widget);
			}
		}
	}

	@Override
	public void requestRender(Widget widget, int x, int y, int width, int height) {
		if (Constants.getBoolean(DEBUG_RENDER_ENABLED_CONSTANT)) {
			assert MONITOR != null;
			MONITOR.onRenderRequested(widget, x, y, width, height);
		}
		if (this.pendingRepaint) {
			return;
		}
		this.pendingRepaint = true;
		asynchronousRender();
	}

	private void asynchronousRender() {
		// Calling the call serially before increasing the pending repaints counter ensures that even if an
		// OutOfEventException occurs, the pendingRepaints remains consistent.
		MicroUI.callSerially(new Runnable() {
			@Override
			public void run() {
				executeRender();
			}
		});
	}

	private void executeRender() {
		this.pendingRepaint = false;

		try {
			renderDesktop();
		} finally {
			Display display = Display.getDisplay();
			display.flush();
		}
	}

}
