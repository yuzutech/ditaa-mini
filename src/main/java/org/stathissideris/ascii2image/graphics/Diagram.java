package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.ConversionOptions;
import org.stathissideris.ascii2image.core.ProcessingOptions;
import org.stathissideris.ascii2image.text.TextGrid;

/**
 * Minimal shim for PlantUML compatibility
 */
public class Diagram {
    final org.stathissideris.ditaa.graphics.Diagram diagram;

    public Diagram(TextGrid textGrid, ConversionOptions conversionOptions, ProcessingOptions processingOptions) {
        diagram = new org.stathissideris.ditaa.graphics.Diagram(
                textGrid.textGrid,
                new org.stathissideris.ditaa.core.ConversionOptions(
                        processingOptions.processingOptions,
                        conversionOptions.renderingOptions.renderingOptions
                )
        );
    }
}
