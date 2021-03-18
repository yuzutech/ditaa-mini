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
package org.stathissideris.ditaa.graphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Efstathios Sideris
 */
public class DiagramText extends DiagramComponent {

    public static final Color DEFAULT_COLOR = Color.black;

    private String text;
    private Font font;
    private int xPos, yPos;
    private Color color = Color.black;
    private boolean isTextOnLine = false;
    private boolean hasOutline = false;
    private Color outlineColor = Color.white;
    private final FontMeasurer fontMeasurer;

    public DiagramText(int x, int y, String text, Font font, FontMeasurer fontMeasurer)
    {
        if (text == null) throw new IllegalArgumentException("DiagramText cannot be initialised with a null string");
        if (font == null) throw new IllegalArgumentException("DiagramText cannot be initialised with a null font");

        this.xPos = x;
        this.yPos = y;
        this.text = text;
        this.font = font;
        this.fontMeasurer = fontMeasurer;
    }

    public void centerInBounds(Rectangle2D bounds)
    {
        centerHorizontallyBetween((int) bounds.getMinX(), (int) bounds.getMaxX());
        centerVerticallyBetween((int) bounds.getMinY(), (int) bounds.getMaxY());
    }

    public void centerHorizontallyBetween(int minX, int maxX)
    {
        int width = fontMeasurer.getWidthFor(text, font);
        int center = Math.abs(maxX - minX) / 2;
        xPos += Math.abs(center - width / 2);

    }

    public void centerVerticallyBetween(int minY, int maxY)
    {
        int zHeight = fontMeasurer.getZHeight(font);
        int center = Math.abs(maxY - minY) / 2;
        yPos -= Math.abs(center - zHeight / 2);
    }

    public void alignRightEdgeTo(int x)
    {
        int width = fontMeasurer.getWidthFor(text, font);
        xPos = x - width;
    }


    public Color getColor()
    {
        return color;
    }

    public Font getFont()
    {
        return font;
    }

    public String getText()
    {
        return text;
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getYPos()
    {
        return yPos;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setFont(Font font)
    {
        this.font = font;
    }

    public void setText(String string)
    {
        text = string;
    }

    public void setXPos(int i)
    {
        xPos = i;
    }

    public void setYPos(int i)
    {
        yPos = i;
    }

    public Rectangle2D getBounds()
    {
        Rectangle2D bounds = fontMeasurer.getBoundsFor(text, font);
        bounds.setRect(
                bounds.getMinX() + xPos,
                bounds.getMinY() + yPos,
                bounds.getWidth(),
                bounds.getHeight());
        return bounds;
    }

    public String toString()
    {
        return "DiagramText, at (" + xPos + ", " + yPos + "), within " + getBounds() + " '" + text + "', " + color + " " + font;
    }

    public boolean isTextOnLine()
    {
        return isTextOnLine;
    }

    public void setTextOnLine(boolean b)
    {
        isTextOnLine = b;
    }

    public boolean hasOutline()
    {
        return hasOutline;
    }

    public void setHasOutline(boolean hasOutline)
    {
        this.hasOutline = hasOutline;
    }

    public Color getOutlineColor()
    {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor)
    {
        this.outlineColor = outlineColor;
    }


}
