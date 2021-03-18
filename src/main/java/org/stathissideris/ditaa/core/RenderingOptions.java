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
package org.stathissideris.ditaa.core;

import org.stathissideris.ditaa.graphics.CustomShapeDefinition;

import java.awt.*;
import java.util.HashMap;

/**
 * @author Efstathios Sideris
 */
public class RenderingOptions
{
    public enum ImageType
    {
        PNG("png", "png"),
        SVG("svg", null);

        private String extension;
        private String formatName;

        ImageType(String extension, String formatName)
        {
            this.extension = extension;
            this.formatName = formatName;
        }

        public String getExtension()
        {
            return extension;
        }

        public String getFormatName()
        {
            return formatName;
        }
    }

    private HashMap<String, CustomShapeDefinition> customShapes;

    private boolean dropShadows = true;
    private boolean renderDebugLines = false;
    private boolean antialias = true;
    private boolean fixedSlope = false;

    private int cellWidth = 10;
    private int cellHeight = 14;

    private float scale = 1;

    private Color backgroundColor = Color.white;
    private String fontName = "Dialog";
    private int fontStyle = Font.BOLD;
    private int fontSize = 12;
    private String fontFamily = "sans-serif";
    private String fontURL = null;

    private ImageType imageType = ImageType.PNG;

    public ImageType getImageType()
    {
        return imageType;
    }

    public void setImageType(ImageType type)
    {
        imageType = type;
    }

    public String getFontFamily()
    {
        return fontFamily;
    }

    public String getFontURL()
    {
        return fontURL;
    }

    public void setFontURL(String url)
    {
        fontFamily = "Custom";
        fontURL = url;
    }

    public int getCellHeight()
    {
        return cellHeight;
    }

    public int getCellWidth()
    {
        return cellWidth;
    }

    public boolean dropShadows()
    {
        return dropShadows;
    }

    public boolean renderDebugLines()
    {
        return renderDebugLines;
    }

    public float getScale()
    {
        return scale;
    }

    public void setDropShadows(boolean b)
    {
        dropShadows = b;
    }

    public void setRenderDebugLines(boolean b)
    {
        renderDebugLines = b;
    }

    public void setScale(float f)
    {
        scale = f;
        cellWidth *= scale;
        cellHeight *= scale;
    }

    public boolean performAntialias()
    {
        return antialias;
    }

    public void setAntialias(boolean b)
    {
        antialias = b;
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public boolean needsTransparency()
    {
        return backgroundColor.getAlpha() < 255;
    }

    /**
     * Should the sides of trapezoids and parallelograms have fixed width (false, default)
     * or fixed slope (true)?
     *
     * @return true for fixed slope, false for fixed width
     */
    public boolean isFixedSlope()
    {
        return fixedSlope;
    }

    /**
     * Should the sides of trapezoids and parallelograms have fixed width (false, default)
     * or fixed slope (true)?
     *
     * @param b true for fixed slope, false for fixed width
     */
    public void setFixedSlope(boolean b)
    {
        this.fixedSlope = b;
    }

    public String getFontName()
    {
        return fontName;
    }

    public void setFontName(String fontName)
    {
        this.fontName = fontName;
    }

    public int getFontStyle()
    {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle)
    {
        this.fontStyle = fontStyle;
    }

    public int getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(int fontSize)
    {
        this.fontSize = fontSize;
    }
}
