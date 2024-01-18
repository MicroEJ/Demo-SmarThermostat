/*
 * Java
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.widget;

import static com.microej.demo.smart_thermostat.NavigationDesktop.scale;

import java.util.logging.Logger;

import com.microej.demo.smart_thermostat.common.FlushVisualizer;
import com.microej.demo.smart_thermostat.style.Fonts;

import ej.annotation.Nullable;
import ej.bon.XMath;
import ej.microui.display.*;
import ej.microvg.VectorFont;
import ej.microvg.VectorGraphicsPainter;
import ej.mwt.Widget;
import ej.mwt.util.Size;

/**
 * Widget displaying the current date.
 */
public class DateWidget extends Widget {
	private static final Logger LOGGER = Logger.getLogger(DateWidget.class.getName());
	private static final String POWERED_BY = "Powered by MICROEJ";
	private static final int POWERED_BY_MARGIN_TOP = scale(15);
	private static final int MARGIN_RIGHT = scale(40);
	private static final int POWERED_BY_FONT_SIZE = scale(24);
	private static final int DATE_HEIGHT = scale(45);

	private final int poweredXOffsetFromMiddle;
	private @Nullable BufferedImage dateBufferedImage;

	private final int halfWidth;
	private boolean dirty = true;

	/**
	 * Creates the DateWidget.
	 */
	public DateWidget() {
		super(true);
		Display display = Display.getDisplay();
		int width = display.getWidth();
		this.halfWidth = width / 2;

		VectorFont font = Fonts.getBarlowLightItalic();
		this.poweredXOffsetFromMiddle = this.halfWidth
				- (int) XMath.ceil(font.measureStringWidth(POWERED_BY, POWERED_BY_FONT_SIZE));
	}

	@Override
	protected void computeContentOptimalSize(Size size) {
		// Nothing to do yet. Set from canvas container.
	}

	@Override
	protected void renderContent(GraphicsContext g, int contentWidth, int contentHeight) {
		if (this.dirty || this.dateBufferedImage == null) {
			if (this.dateBufferedImage == null) {
				this.dateBufferedImage = new BufferedImage(this.halfWidth, DATE_HEIGHT);
			}
			LOGGER.finest("drawing unbuffered date image");
			g.setColor(Colors.WHITE);

			FlushVisualizer.drawVGArea(g, 0, 0, contentWidth, contentHeight);
			VectorGraphicsPainter.drawString(g, POWERED_BY, Fonts.getBarlowLightItalic(), POWERED_BY_FONT_SIZE,
					this.poweredXOffsetFromMiddle - (float) MARGIN_RIGHT, POWERED_BY_MARGIN_TOP);

			takeDateScreenshot(this.dateBufferedImage);
			this.dirty = false;
		} else {
			Painter.drawImage(g, this.dateBufferedImage, 0, 0);
		}
	}

	private void takeDateScreenshot(BufferedImage buffImage) {
		// Use screenshot context only
		GraphicsContext buffImageGc = buffImage.getGraphicsContext();
		buffImageGc.reset();
		/*
		 * Getting the style and rendering the background is not needed for now.
		 */
		// Fill screenshot context with current state.
		Painter.drawDisplayRegion(buffImageGc, this.halfWidth, POWERED_BY_MARGIN_TOP, this.halfWidth, DATE_HEIGHT, 0,
				0);
	}

	@Override
	protected void onHidden() {
		super.onHidden();
		if (this.dateBufferedImage != null) {
			this.dateBufferedImage.close();
			this.dateBufferedImage = null;
		}
		this.dirty = true;
	}

}
