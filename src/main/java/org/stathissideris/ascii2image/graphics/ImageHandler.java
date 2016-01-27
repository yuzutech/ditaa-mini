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
package org.stathissideris.ascii2image.graphics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageHandler {
    private static ImageHandler instance = new ImageHandler();

    public static ImageHandler instance()
    {
        return instance;
    }

    private static final MediaTracker tracker = new MediaTracker(new JLabel());

    public BufferedImage loadBufferedImage(File file) throws IOException
    {
        return ImageIO.read(file);
    }

    public Image loadImage(String filename)
    {
        URL url = ClassLoader.getSystemResource(filename);
        Image result = null;
        if (url != null)
            result = Toolkit.getDefaultToolkit().getImage(url);
        else
            result = Toolkit.getDefaultToolkit().getImage(filename);
//			result = null;

        //wait for the image to load before returning
        tracker.addImage(result, 0);
        try {
            tracker.waitForID(0);
        } catch (InterruptedException e) {
            System.err.println("Failed to load image " + filename);
            e.printStackTrace();
        }
        tracker.removeImage(result, 0);

        return result;
    }
}
