package org.stathissideris.ascii2image.core;

/**
 * Minimal shim for PlantUML compatibility
 */
public class ConversionOptions {
    public final RenderingOptions renderingOptions = new RenderingOptions();

    public void setDropShadows(boolean enabled) {
        renderingOptions.setDropShadows(enabled);
    }
}
