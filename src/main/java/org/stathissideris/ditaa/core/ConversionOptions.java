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

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * @author Efstathios Sideris
 */
public class ConversionOptions {

    public ProcessingOptions processingOptions =
            new ProcessingOptions();
    public RenderingOptions renderingOptions =
            new RenderingOptions();

    public ConversionOptions()
    {
    }

    public static ConversionOptions parseCommandLineOptions(String[] args) throws UnsupportedEncodingException
    {
        return parseCommandLineOptions(Arrays.asList(args).listIterator());
    }

    public static ConversionOptions parseCommandLineOptions(ListIterator<String> args) throws UnsupportedEncodingException
    {
        ConversionOptions options = new ConversionOptions();

        while (args.hasNext()) {
            String arg = args.next();
            if (arg.equals("-v") || arg.equals("--verbose")) {
                options.processingOptions.setVerbose(true);
            } else if (arg.equals("-o") || arg.equals("--overwrite")) {
                options.processingOptions.setOverwriteFiles(true);
            } else if (arg.equals("-S") || arg.equals("--no-shadows")) {
                options.renderingOptions.setDropShadows(false);
            } else if (arg.equals("-A") || arg.equals("--no-antialias")) {
                options.renderingOptions.setAntialias(false);
            } else if (arg.equals("-W") || arg.equals("--fixed-slope")) {
                options.renderingOptions.setFixedSlope(true);
            } else if (arg.equals("-d") || arg.equals("--debug")) {
                options.setDebug(true);
            } else if (arg.equals("-r") || arg.equals("--round-corners")) {
                options.processingOptions.setAllCornersAreRound(true);
            } else if (arg.equals("-E") || arg.equals("--no-separation")) {
                options.processingOptions.setPerformSeparationOfCommonEdges(false);
            } else if (arg.equals("-T") || arg.equals("--transparent")) {
                options.renderingOptions.setBackgroundColor(new Color(0, 0, 0, 0));
            } else if (arg.equals("-e") || arg.equals("--encoding")) {
                options.processingOptions.setCharacterEncoding(Charset.forName(args.next()));
            } else if (arg.equals("-s") || arg.equals("--scale")) {
                Float scale = Float.parseFloat(args.next());
                options.renderingOptions.setScale(scale);
            } else if (arg.equals("-t") || arg.equals("--tabs")) {
                int tabSize = Integer.parseInt(args.next());
                if (tabSize < 0) tabSize = 0;
                options.processingOptions.setTabSize(tabSize);
            } else if (arg.equals("-b") || arg.equals("--background")) {
                Color background = parseColor(args.next());
                options.renderingOptions.setBackgroundColor(background);
            } else if (arg.equals("-f") || arg.equals("--font")) {
                options.renderingOptions.setFontName(args.next());
            } else if (arg.equals("-F") || arg.equals("--font-size")) {
                Integer size = Integer.parseInt(args.next());
                options.renderingOptions.setFontSize(size);
            } else if (arg.equals("--svg")) {
                options.renderingOptions.setImageType(RenderingOptions.ImageType.SVG);
            } else if (arg.equals("--svg-font-url")) {
                options.renderingOptions.setFontURL(args.next());
            } else {
                args.previous();
                break;
            }
        }

        return options;
    }

    public void setDebug(boolean value)
    {
        processingOptions.setPrintDebugOutput(value);
        renderingOptions.setRenderDebugLines(value);
    }

    /**
     * Parse a color from a 6- or 8-digit hex string.  For example, FF0000 is red.
     * If eight digits, last two digits are alpha.
     */
    public static Color parseColor(String hexString)
    {
        if (hexString.length() == 6) {
            return new Color(Integer.parseInt(hexString, 16));
        } else if (hexString.length() == 8) {
            return new Color(
                    Integer.parseInt(hexString.substring(0, 2), 16),
                    Integer.parseInt(hexString.substring(2, 4), 16),
                    Integer.parseInt(hexString.substring(4, 6), 16),
                    Integer.parseInt(hexString.substring(6, 8), 16)
            );
        } else {
            throw new IllegalArgumentException("Cannot interpret \"" + hexString + "\" as background colour. It needs to be a 6- or 8-digit hex number, depending on whether you have transparency or not (same as HTML).");
        }
    }
}
