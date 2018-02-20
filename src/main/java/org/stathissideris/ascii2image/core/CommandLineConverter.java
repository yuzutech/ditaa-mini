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
import org.stathissideris.ascii2image.graphics.SVGRenderer;
import org.stathissideris.ascii2image.text.TextGrid;

import javax.imageio.ImageIO;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
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
        ConversionOptions options = ConversionOptions.parseCommandLineOptions(argsIt);

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
                outputFile = inputFile + "." + options.renderingOptions.getImageType().getExtension();
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
            doConvert(input, output, ConversionOptions.parseCommandLineOptions(args));
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
        Diagram diagram = convertToImage(input, options);

        RenderingOptions.ImageType imageType = options.renderingOptions.getImageType();

        if (imageType == RenderingOptions.ImageType.SVG) {
            String content = new SVGRenderer().renderToImage(diagram, options.renderingOptions);
            OutputStreamWriter writer = new OutputStreamWriter(output, Charset.forName("UTF-8"));
            try {
                writer.write(content);
            } finally {
                writer.flush();
            }
        } else {
            BufferedImage image = new BitmapRenderer().renderToImage(diagram, options.renderingOptions);

            MemoryCacheImageOutputStream memCache = new MemoryCacheImageOutputStream(output);
            ImageIO.write(image, imageType.getFormatName(), memCache);
            memCache.flush();
        }
    }

    private static Diagram convertToImage(InputStream input, ConversionOptions options) throws IOException
    {
        TextGrid grid = new TextGrid();
        if (options.processingOptions.getCustomShapes() != null) {
            grid.addToMarkupTags(options.processingOptions.getCustomShapes().keySet());
        }

        grid.loadFrom(input, options.processingOptions);

        if (options.processingOptions.printDebugOutput()) {
            grid.printDebug();
        }

        return new Diagram(grid, options);
    }

}
