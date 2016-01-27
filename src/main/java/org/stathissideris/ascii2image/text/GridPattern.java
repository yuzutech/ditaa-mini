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
package org.stathissideris.ascii2image.text;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * This is a TextGrid (usually 3x3) that contains the equivalent of a
 * 2D reqular expression (which uses custom syntax to make things more
 * visual, but standard syntax is also possible).
 * <p/>
 * The custom syntax is:
 * . means anything
 * b means any boundary (any of  - = / \ + | :)
 * ! means not boundary (none of  - = / \ + | :)
 * - means - or =
 * | means | or :
 * [ means not | nor :
 * ~ means not - nor =
 * ^ means a boundary but not - nor =
 * ( means a boundary but not | nor :
 * s means a straight boundary (one of - = + | :)
 * S means not a straight boundary (none of - = + | :)
 * <p/>
 * 1 means a cell that has entry point 1
 * 2 means a cell that has entry point 2
 * 3 means a cell that has entry point 3 etc. up to number 8
 * <p/>
 * %1 means a cell that does not have entry point 1 etc.
 * <p/>
 * See below for an explanation of entry points
 * <p/>
 * +, \, / and the space are literal (as is any other character)
 * <p/>
 * <p/>
 * Entry points
 * <p/>
 * <pre>
 * 1   2   3
 *  *--*--*
 *  |     |
 * 8*     *4
 *  |     |
 *  *--*--*
 * 7   6   5
 * </pre>
 * <p/>
 * We number the entry points for each cell as in the diagram
 * above. If a cell is occupied by a character, we define as
 * entry points the points of the above diagram that the character
 * can touch with the end of its lines. For example - has
 * entry points 8 and 4, | and : have entry points 2 and 6,
 * / has 3 and 7, \ has 1 and 5, + has 2, 6, 8 and 4 etc.
 *
 * @author Efstathios Sideris
 */
public class GridPattern extends TextGrid {

    private ArrayList<Pattern> regExps = new ArrayList<Pattern>();

    private static final boolean DEBUG = false;

    public GridPattern(String row1, String row2, String row3)
    {
        super(Math.max(Math.max(row1.length(), row2.length()), row3.length()), 3);
        if (getHeight() != 3)
            throw new RuntimeException("This method can only be called for GridPatternS with height 3");
        writeStringTo(0, 0, row1);
        writeStringTo(0, 1, row2);
        writeStringTo(0, 2, row3);
        prepareRegExps();
    }

    public boolean isMatchedBy(TextGrid grid)
    {
        for (int i = 0; i < grid.getHeight(); i++) {
            String row = grid.getRow(i).toString();
            Pattern regexp = regExps.get(i);
            if (!regexp.matcher(row).matches()) {
                if (DEBUG)
                    System.out.println(row + " does not match " + regexp);
                return false;
            }
        }
        return true;
    }

    private void prepareRegExps()
    {
        regExps.clear();
        if (DEBUG)
            System.out.println("Trying to match:");
        for (StringBuilder stringBuilder : getRows()) {
            String row = stringBuilder.toString();
            regExps.add(Pattern.compile(makeRegExp(row)));
            if (DEBUG)
                System.out.println(row + " becomes " + makeRegExp(row));
        }
    }

    private String makeRegExp(String pattern)
    {
        StringBuilder result = new StringBuilder();
        int tokensHandled = 0;
        for (int i = 0; i < pattern.length() && tokensHandled < 3; i++) {
            char c = pattern.charAt(i);
            if (c == '[') {
                result.append("[^|:]");
            } else if (c == '|') {
                result.append("[|:]");
            } else if (c == '-') {
                result.append("[-=]");
            } else if (c == '!') {
                result.append("[^-=\\/\\\\+|:]");
            } else if (c == 'b') {
                result.append("[-=\\/\\\\+|:]");
            } else if (c == '^') {
                result.append("[\\/\\\\+|:]");
            } else if (c == '(') {
                result.append("[-=\\/\\\\+]");
            } else if (c == '~') {
                result.append(".");
            } else if (c == '+') {
                result.append("\\+");
            } else if (c == '\\') {
                result.append("\\\\");
            } else if (c == 's') {
                result.append("[-=+|:]");
            } else if (c == 'S') {
                result.append("[\\/\\\\]");
            } else if (c == '*') {
                result.append("\\*");

                //entry points
            } else if (c == '1') {
                result.append("[\\\\]");

            } else if (c == '2') {
                result.append("[|:+\\/\\\\]");

            } else if (c == '3') {
                result.append("[\\/]");

            } else if (c == '4') {
                result.append("[-=+\\/\\\\]");

            } else if (c == '5') {
                result.append("[\\\\]");

            } else if (c == '6') {
                result.append("[|:+\\/\\\\]");

            } else if (c == '7') {
                result.append("[\\/]");

            } else if (c == '8') {
                result.append("[-=+\\/\\\\]");

                //entry point negations
            } else if (c == '%') {
                if (i + 1 > pattern.length()) {
                    throw new RuntimeException("Invalid pattern, found % at the end");
                }
                c = pattern.charAt(++i);

                if (c == '1') {
                    result.append("[^\\\\]");

                } else if (c == '2') {
                    result.append("[^|:+\\/\\\\]");

                } else if (c == '3') {
                    result.append("[^\\/]");

                } else if (c == '4') {
                    result.append("[^-=+\\/\\\\]");

                } else if (c == '5') {
                    result.append("[^\\\\]");

                } else if (c == '6') {
                    result.append("[^|:+\\/\\\\]");

                } else if (c == '7') {
                    result.append("[^\\/]");

                } else if (c == '8') {
                    result.append("[^-=+\\/\\\\]");
                }
            } else result.append(String.valueOf(c));
            tokensHandled++;
        }
        return result.toString();
    }
}
