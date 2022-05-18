package org.stathissideris.ascii2image.text;

import org.stathissideris.ascii2image.core.ProcessingOptions;

import java.io.UnsupportedEncodingException;

/**
 * Minimal shim for PlantUML compatibility
 */
public class TextGrid {
    public final org.stathissideris.ditaa.text.TextGrid textGrid = new org.stathissideris.ditaa.text.TextGrid();

    public void initialiseWithText(String text, ProcessingOptions options) throws UnsupportedEncodingException {
        textGrid.initialiseWithText(text, options == null ? null : options.processingOptions);
    }
}
