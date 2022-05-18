package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;

import java.awt.image.BufferedImage;

/**
 * Minimal shim for PlantUML compatibility
 */
public class BitmapRenderer {
    public BufferedImage renderToImage(Diagram diagram, RenderingOptions renderingOptions) {
        return new org.stathissideris.ditaa.graphics.BitmapRenderer().renderToImage(
                diagram.diagram,
                renderingOptions.renderingOptions
        );
    }
}
