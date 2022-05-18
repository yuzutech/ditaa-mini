package org.stathissideris.ascii2image.core;

import org.stathissideris.ditaa.graphics.CustomShapeDefinition;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Minimal shim for PlantUML compatibility
 */
public class ProcessingOptions {
    public final org.stathissideris.ditaa.core.ProcessingOptions processingOptions = new org.stathissideris.ditaa.core.ProcessingOptions();

    public void setPerformSeparationOfCommonEdges(boolean b) {
        processingOptions.setPerformSeparationOfCommonEdges(b);
    }

    public void setAllCornersAreRound(boolean b) {
        processingOptions.setAllCornersAreRound(b);
    }

    public void setColorCodesProcessingMode(int i) {
        processingOptions.setColorCodesProcessingMode(i);
    }

    public void setExportFormat(int i) {
        processingOptions.setExportFormat(i);
    }

    public void setTagProcessingMode(int i) {
        processingOptions.setTagProcessingMode(i);
    }

    public void setTabSize(int i) {
        processingOptions.setTabSize(i);
    }

    public void setCharacterEncoding(Charset characterEncoding) {
        processingOptions.setCharacterEncoding(characterEncoding);
    }

    public void setCustomShapes(HashMap<String, CustomShapeDefinition> customShapes) {
        processingOptions.setCustomShapes(customShapes);
    }
}
