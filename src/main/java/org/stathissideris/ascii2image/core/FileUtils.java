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

import java.io.*;

/**
 * @author Efstathios Sideris
 */
public class FileUtils {

    //private static final

    public static String makeTargetPathname(String sourcePathname, String extension, boolean overwrite) {
        return makeTargetPathname(sourcePathname, extension, "", overwrite);
    }

    public static String makeTargetPathname(String sourcePathname, String extension, String postfix, boolean overwrite) {
        File sourceFile =
                new File(sourcePathname);

        String path = "";
        if (sourceFile.getParentFile() != null) {
            path = sourceFile.getParentFile().getAbsolutePath();
            if (!path.endsWith(File.separator)) path += File.separator;
        }
        String baseName = getBaseName(sourceFile.getName());

        String targetName = path + baseName + postfix + "." + extension;
        if (new File(targetName).exists() && !overwrite)
            targetName = makeAlternativePathname(targetName);
        return targetName;
    }

    public static String makeAlternativePathname(String pathName) {
        int limit = 100;

        for (int i = 2; i <= limit; i++) {
            String alternative = getBaseName(pathName) + "_" + i;
            String extension = getExtension(pathName);
            if (extension != null) alternative += "." + extension;
            if (!(new File(alternative).exists())) return alternative;
        }
        return null;
    }

    public static String getExtension(String pathName) {
        if (pathName.lastIndexOf('.') == -1) return null;
        return pathName.substring(pathName.lastIndexOf('.') + 1);
    }

    public static String getBaseName(String pathName) {
        if (pathName.lastIndexOf('.') == -1) return pathName;
        return pathName.substring(0, pathName.lastIndexOf('.'));
    }

    public static String readFile(File file) throws IOException {
        return readFile(file, null);
    }

    public static String readFile(File file, String encoding) throws IOException {
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
            // TODO: we need some feedback for the case of the file being too large
        }

        return readFile(new FileInputStream(file), encoding, length);
    }

    public static String readFile(InputStream is, String encoding) throws IOException {
        return readFile(is, encoding, -1);
    }

    public static String readFile(InputStream is, String encoding, long length) throws IOException {
        byte[] bytes;
        if (length < 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int numRead;
            while((numRead = is.read(buffer)) > 0) {
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
