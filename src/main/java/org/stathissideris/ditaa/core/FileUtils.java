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

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Efstathios Sideris
 */
public class FileUtils {
    public static String readFile(InputStream is, Charset encoding) throws IOException
    {
        return readFile(is, encoding, -1);
    }

    public static String readFile(InputStream is, Charset encoding, long length) throws IOException
    {
        byte[] bytes;
        if (length < 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int numRead;
            while ((numRead = is.read(buffer)) > 0) {
                out.write(buffer, 0, numRead);
            }
            out.close();
            bytes = out.toByteArray();
        } else {
            bytes = new byte[(int) length];

            int offset = 0;
            int numRead;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file");
            }
        }

        if (encoding == null) {
            return new String(bytes);
        } else {
            return new String(bytes, encoding);
        }
    }
}
