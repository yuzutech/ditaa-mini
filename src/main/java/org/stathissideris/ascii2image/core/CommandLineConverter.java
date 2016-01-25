/**
 * ditaa - Diagrams Through Ascii Art
 *
 * Copyright (C) 2004-2011 Efstathios Sideris
 *
 * ditaa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * ditaa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ditaa.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.stathissideris.ascii2image.core;

import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Efstathios Sideris
 */
public class CommandLineConverter {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String test = "+--------+   +-------+    +-------+\n" +
                      "|        | --+ ditaa +--> |       |\n" +
                      "|  Text  |   +-------+    |diagram|\n" +
                      "|Document|   |!magic!|    |       |\n" +
                      "|     {d}|   |       |    |       |\n" +
                      "+---+----+   +-------+    +-------+\n" +
                      "    :                         ^\n" +
                      "    |       Lots of work      |\n" +
                      "    +-------------------------+";
        convert(new String[] { "-e", "UTF-8" }, new ByteArrayInputStream(test.getBytes("UTF-8")), new ByteArrayOutputStream());
    }

    public static int convert(String[] args, InputStream input, OutputStream output) {
        try {
            return doConvert(args, input, output);
        } catch (RuntimeException e) {
            printException(e, output);
            return 1;
        }
    }

    private static int doConvert(String[] args, InputStream input, OutputStream output) {
        Map<String, String> cmdLine = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-v") || arg.equals("--verbose")) {
                cmdLine.put("verbose", "true");
            } else if (arg.equals("-o") || arg.equals("--overwrite")) {
                cmdLine.put("overwrite", "true");
            } else if (arg.equals("-S") || arg.equals("--no-shadows")) {
                cmdLine.put("no-shadows", "true");
            } else if (arg.equals("-A") || arg.equals("--no-antialias")) {
                cmdLine.put("no-antialias", "true");
            } else if (arg.equals("-W") || arg.equals("--fixed-slope")) {
                cmdLine.put("fixed-slope", "true");
            } else if (arg.equals("-d") || arg.equals("--debug")) {
                cmdLine.put("debug", "true");
            } else if (arg.equals("-r") || arg.equals("--round-corners")) {
                cmdLine.put("round-corners", "true");
            } else if (arg.equals("-E") || arg.equals("--no-separation")) {
                cmdLine.put("no-separation", "true");
            } else if (arg.equals("-T") || arg.equals("--transparent")) {
                cmdLine.put("transparent", "true");
            } else if (arg.equals("-e") || arg.equals("--encoding")) {
                cmdLine.put("encoding", args[++i]);
            } else if (arg.equals("-s") || arg.equals("--scale")) {
                cmdLine.put("scale", args[++i]);
            } else if (arg.equals("-t") || arg.equals("--tabs")) {
                cmdLine.put("tabs", args[++i]);
            } else if (arg.equals("-b") || arg.equals("--background")) {
                cmdLine.put("background", args[++i]);
            } else if (arg.equals("-f") || arg.equals("--font")) {
                cmdLine.put("font", args[++i]);
            } else if (arg.equals("-F") || arg.equals("--font-size")) {
                cmdLine.put("font-size", args[++i]);
            }
        }

        ///// parse command line options
        ConversionOptions options;
        try {
            options = new ConversionOptions(cmdLine);
        } catch (UnsupportedEncodingException e2) {
            printException(e2, output);
            return 2;
        } catch (IllegalArgumentException e2) {
            printException(e2, output);
            return 2;
        }

        TextGrid grid = new TextGrid();
        if (options.processingOptions.getCustomShapes() != null) {
            grid.addToMarkupTags(options.processingOptions.getCustomShapes().keySet());
        }

        try {
            grid.loadFrom(input, options.processingOptions);
        } catch (UnsupportedEncodingException e1) {
            printException(e1, output);
            return 1;
        } catch (IOException e1) {
            printException(e1, output);
            return 1;
        }

        if (options.processingOptions.printDebugOutput()) {
            grid.printDebug();
        }

        Diagram diagram = new Diagram(grid, options);

        BufferedImage image = new BitmapRenderer().renderToImage(diagram, options.renderingOptions);

        try {
            MemoryCacheImageOutputStream memCache = new MemoryCacheImageOutputStream(output);
            ImageIO.write(image, "png", memCache);
            memCache.flush();
        } catch (IOException e) {
            printException(e, output);
            return 1;
        }

        return 0;
    }

    private static void printException(Exception e2, OutputStream output) {
        PrintStream printStream = new PrintStream(output);
        e2.printStackTrace(printStream);
        printStream.flush();
    }
}
