package org.stathissideris.ascii2image.core;

import java.awt.*;

/**
 * Minimal shim for PlantUML compatibility
 */
public class RenderingOptions {
    public final org.stathissideris.ditaa.core.RenderingOptions renderingOptions = new org.stathissideris.ditaa.core.RenderingOptions();
    public void setDropShadows(boolean enabled) {
        renderingOptions.setDropShadows(enabled);
    }

    public void setScale(float f) {
        renderingOptions.setScale(f);
    }

    public void setAntialias(boolean b) {
        renderingOptions.setAntialias(b);
    }

    public void setBackgroundColor(Color backgroundColor) {
        renderingOptions.setBackgroundColor(backgroundColor);
    }

    public void setFont(Font font) {
        renderingOptions.setFont(font);
    }

    public void setForceFontSize(boolean value) {
        renderingOptions.setFixedFontSize(value);
    }
}
