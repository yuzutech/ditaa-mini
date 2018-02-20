/**
 * ditaa - Diagrams Through Ascii Art
 * <p/>
 * Copyright (C) 2004-2011 Efstathios Sideris
 * <p/>
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * <p/>
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.stathissideris.ascii2image.core;

import org.stathissideris.ascii2image.graphics.CustomShapeDefinition;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @author Efstathios Sideris
 *
 */
public class ProcessingOptions {

    private HashMap<String, CustomShapeDefinition> customShapes = new HashMap<String, CustomShapeDefinition>();

    private boolean beVerbose = false;
    private boolean printDebugOutput = false;
    private boolean overwriteFiles = false;
    private boolean performSeparationOfCommonEdges = true;
    private boolean allCornersAreRound = false;

    public static final int USE_TAGS = 0;
    public static final int RENDER_TAGS = 1;
    public static final int IGNORE_TAGS = 2;
    private int tagProcessingMode = USE_TAGS;

    public static final int USE_COLOR_CODES = 0;
    public static final int RENDER_COLOR_CODES = 1;
    public static final int IGNORE_COLOR_CODES = 2;
    private int colorCodesProcessingMode = USE_COLOR_CODES;

    public static final int FORMAT_JPEG = 0;
    public static final int FORMAT_PNG = 1;
    public static final int FORMAT_GIF = 2;
    private int exportFormat = FORMAT_PNG;

    public static final int DEFAULT_TAB_SIZE = 8;
    private int tabSize = DEFAULT_TAB_SIZE;

    private String inputFilename;
    private String outputFilename;

    private Charset characterEncoding = Charset.defaultCharset();

    public boolean areAllCornersRound()
    {
        return allCornersAreRound;
    }

    public int getColorCodesProcessingMode()
    {
        return colorCodesProcessingMode;
    }

    public int getExportFormat()
    {
        return exportFormat;
    }

    public boolean performSeparationOfCommonEdges()
    {
        return performSeparationOfCommonEdges;
    }

    public int getTagProcessingMode()
    {
        return tagProcessingMode;
    }

    public void setAllCornersAreRound(boolean b)
    {
        allCornersAreRound = b;
    }

    public void setColorCodesProcessingMode(int i)
    {
        colorCodesProcessingMode = i;
    }

    public void setExportFormat(int i)
    {
        exportFormat = i;
    }

    public void setPerformSeparationOfCommonEdges(boolean b)
    {
        performSeparationOfCommonEdges = b;
    }

    public void setTagProcessingMode(int i)
    {
        tagProcessingMode = i;
    }

    public String getInputFilename()
    {
        return inputFilename;
    }

    public String getOutputFilename()
    {
        return outputFilename;
    }

    public void setInputFilename(String string)
    {
        inputFilename = string;
    }

    public void setOutputFilename(String string)
    {
        outputFilename = string;
    }

    public boolean verbose()
    {
        return beVerbose;
    }

    public boolean printDebugOutput()
    {
        return printDebugOutput;
    }

    public void setVerbose(boolean b)
    {
        beVerbose = b;
    }

    public void setPrintDebugOutput(boolean b)
    {
        printDebugOutput = b;
    }

    public boolean overwriteFiles()
    {
        return overwriteFiles;
    }

    public void setOverwriteFiles(boolean b)
    {
        overwriteFiles = b;
    }

    public int getTabSize()
    {
        return tabSize;
    }

    public void setTabSize(int i)
    {
        tabSize = i;
    }

    public Charset getCharacterEncoding()
    {
        return characterEncoding;
    }

    public void setCharacterEncoding(Charset characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }

    public HashMap<String, CustomShapeDefinition> getCustomShapes()
    {
        return customShapes;
    }

    public void setCustomShapes(HashMap<String, CustomShapeDefinition> customShapes)
    {
        this.customShapes = customShapes;
    }

    public void putAllInCustomShapes(HashMap<String, CustomShapeDefinition> customShapes)
    {
        this.customShapes.putAll(customShapes);
    }

    public CustomShapeDefinition getFromCustomShapes(String tagName)
    {
        return customShapes.get(tagName);
    }


}
