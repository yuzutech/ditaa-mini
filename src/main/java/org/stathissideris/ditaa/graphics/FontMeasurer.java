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
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Efstathios Sideris
 */
public class FontMeasurer {

    private final Font baseFont;
    private final boolean fixedFontSize;
    private FontRenderContext fakeRenderContext;
    private Graphics2D fakeGraphics;

    public FontMeasurer(Font font, boolean fixedFontSize)
    {
        baseFont = font;
        this.fixedFontSize = fixedFontSize;

        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        fakeGraphics = image.createGraphics();

        fakeRenderContext = fakeGraphics.getFontRenderContext();
    }

    public int getWidthFor(String str, int pixelHeight)
    {
        Font font = getFontFor(pixelHeight);
        Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
        return (int) rectangle.getWidth();
    }

    public int getHeightFor(String str, int pixelHeight)
    {
        Font font = getFontFor(pixelHeight);
        Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
        return (int) rectangle.getHeight();
    }

    public int getWidthFor(String str, Font font)
    {
        Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
        return (int) rectangle.getWidth();
    }

    public int getHeightFor(String str, Font font)
    {
        Rectangle2D rectangle = font.getStringBounds(str, fakeRenderContext);
        return (int) rectangle.getHeight();
    }

    public Rectangle2D getBoundsFor(String str, Font font)
    {
        return font.getStringBounds(str, fakeRenderContext);
    }

    public int getAscent(Font font)
    {
        fakeGraphics.setFont(font);
        FontMetrics metrics = fakeGraphics.getFontMetrics();
        return metrics.getAscent();
    }

    public int getZHeight(Font font)
    {
        return (int) font.createGlyphVector(fakeRenderContext, "Z").getOutline().getBounds().getHeight();
    }

    public Font getFontFor(final int maxWidth, final String string)
    {
        FontPredicate predicate = new FontPredicate() {
            @Override
            public boolean test(Font font)
            {
                int width = getWidthFor(string, font);
                return width > maxWidth;
            }
        };

        return deriveFont(predicate, 1.0f);
    }

    public Font getFontFor(final int pixelHeight)
    {
        if (fixedFontSize) {
            return baseFont;
        }

        FontPredicate predicate = new FontPredicate() {
            @Override
            public boolean test(Font font)
            {
                //ascent is the distance between the baseline and the tallest character
                int ascent = getAscent(font);
                return ascent > pixelHeight;
            }
        };

        return deriveFont(predicate, 0.5f);
    }

    private Font deriveFont(FontPredicate predicate, float sizeDelta)
    {
        if (fixedFontSize) {
            return baseFont;
        }

        Font currentFont = baseFont;
        float size = baseFont.getSize2D();

        int direction; //direction of size change (towards smaller or bigger)
        if (predicate.test(currentFont)) {
            currentFont = currentFont.deriveFont(size - 1f);
            size--;
            direction = -1;
        } else {
            currentFont = currentFont.deriveFont(size + 1f);
            size++;
            direction = 1;
        }

        while (size > 0) {
            currentFont = currentFont.deriveFont(size);
            //rectangle = currentFont.getStringBounds(testString, frc);
            if (direction == 1) {
                if (predicate.test(currentFont)) {
                    size = size - sizeDelta;
                    return currentFont.deriveFont(size);
                } else {
                    size = size + sizeDelta;
                }
            } else {
                if (!predicate.test(currentFont)) {
                    return currentFont;
                } else {
                    size = size - sizeDelta;
                }
            }
        }
        return null;
    }

    private interface FontPredicate {
        boolean test(Font font);
    }
}
