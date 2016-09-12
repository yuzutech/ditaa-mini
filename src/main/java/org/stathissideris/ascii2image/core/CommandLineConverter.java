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

import org.stathissideris.ascii2image.graphics.BitmapRenderer;
import org.stathissideris.ascii2image.graphics.Diagram;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * @author Efstathios Sideris
 */
public class CommandLineConverter {
    public static void main(String[] args)
    {
        try {
            convert(args);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void convert(String[] args) throws IOException
    {
        ListIterator<String> argsIt = Arrays.asList(args).listIterator();
        ConversionOptions options = parseCommandLineOptions(argsIt);

        if (!argsIt.hasNext()) {
            throw new IOException("Input file not specified");
        }

        String inputFile = argsIt.next();
        String outputFile;
        if (argsIt.hasNext()) {
            outputFile = argsIt.next();
        } else {
            if (inputFile.equals("-")) {
                outputFile = "-";
            } else {
                outputFile = inputFile + ".png";
            }
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            in = inputFile.equals("-") ? System.in : new FileInputStream(inputFile);
            out = outputFile.equals("-") ? System.out : new FileOutputStream(outputFile);
            doConvert(in, out, options);
        } finally {
            if (in instanceof FileInputStream) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Ignored
                }
            }

            if (out instanceof FileOutputStream) {
                try {
                    out.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }
    }

    public static int convert(String[] args, InputStream input, OutputStream output)
    {
        try {
            doConvert(input, output, parseCommandLineOptions(Arrays.asList(args).listIterator()));
            return 0;
        } catch (RuntimeException e) {
            e.printStackTrace();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private static void doConvert(InputStream input, OutputStream output, ConversionOptions options) throws IOException
    {
        BufferedImage image = convertToImage(input, options);

        MemoryCacheImageOutputStream memCache = new MemoryCacheImageOutputStream(output);
        ImageIO.write(image, "png", memCache);
        memCache.flush();
    }

    private static BufferedImage convertToImage(InputStream input, ConversionOptions options) throws IOException
    {
        TextGrid grid = new TextGrid();
        if (options.processingOptions.getCustomShapes() != null) {
            grid.addToMarkupTags(options.processingOptions.getCustomShapes().keySet());
        }

        grid.loadFrom(input, options.processingOptions);

        if (options.processingOptions.printDebugOutput()) {
            grid.printDebug();
        }

        Diagram diagram = new Diagram(grid, options);

        return new BitmapRenderer().renderToImage(diagram, options.renderingOptions);
    }

    private static ConversionOptions parseCommandLineOptions(ListIterator<String> args) throws UnsupportedEncodingException
    {
        Map<String, String> cmdLine = new HashMap<String, String>();

        while (args.hasNext()) {
            String arg = args.next();
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
                cmdLine.put("encoding", args.next());
            } else if (arg.equals("-s") || arg.equals("--scale")) {
                cmdLine.put("scale", args.next());
            } else if (arg.equals("-t") || arg.equals("--tabs")) {
                cmdLine.put("tabs", args.next());
            } else if (arg.equals("-b") || arg.equals("--background")) {
                cmdLine.put("background", args.next());
            } else if (arg.equals("-f") || arg.equals("--font")) {
                cmdLine.put("font", args.next());
            } else if (arg.equals("-F") || arg.equals("--font-size")) {
                cmdLine.put("font-size", args.next());
            } else {
                args.previous();
                break;
            }
        }

        return new ConversionOptions(cmdLine);
    }
}
