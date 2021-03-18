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
package org.stathissideris.ditaa.text;

/**
 * @author sideris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class StringUtils {
    public static String repeatString(String string, int repeats)
    {
        if (repeats == 0) return "";
        String buffer = "";
        for (int i = 0; i < repeats; i++) {
            buffer += string;
        }
        return buffer;
    }

    public static boolean isBlank(String s)
    {
        return (s.length() == 0 || s.matches("^\\s*$"));
    }


    public static boolean isOneOf(char c, char[] group)
    {
        for (char aGroup : group) if (c == aGroup) return true;
        return false;
    }
}
