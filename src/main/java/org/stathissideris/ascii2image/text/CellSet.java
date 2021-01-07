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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Efstathios Sideris
 */
public class CellSet implements Iterable<TextGrid.Cell> {

    private static final boolean DEBUG = false;
    private static final boolean VERBOSE_DEBUG = false;

    public static final int TYPE_CLOSED = 0;
    public static final int TYPE_OPEN = 1;
    public static final int TYPE_MIXED = 2;
    public static final int TYPE_HAS_CLOSED_AREA = 3;
    public static final int TYPE_UNDETERMINED = 4;

    Set<TextGrid.Cell> internalSet = new HashSet<TextGrid.Cell>();

    private int type = TYPE_UNDETERMINED;
    private boolean typeIsValid = false;

    public CellSet()
    {

    }

    public CellSet(CellSet other)
    {
        addAll(other);
    }

    public Iterator<TextGrid.Cell> iterator()
    {
        return internalSet.iterator();
    }

    public Object add(TextGrid.Cell cell)
    {
        return internalSet.add(cell);
    }

    public void addAll(CellSet set)
    {
        internalSet.addAll(set.internalSet);
    }

    public int size()
    {
        return internalSet.size();
    }

    public TextGrid.Cell getFirst()
    {
        return internalSet.iterator().next();
    }

    public void printAsGrid(PrintStream out)
    {
        TextGrid grid = new TextGrid(getMaxX() + 2, getMaxY() + 2);
        grid.fillCellsWith(this, '*');
        grid.printDebug(out);
    }

    public String getCellsAsString()
    {
        StringBuilder str = new StringBuilder();
        for (TextGrid.Cell cell : this) {
            if (str.length() > 0) {
                str.append("/");
            }
            str.append(cell);
        }
        return str.toString();
    }

    public String toString()
    {
        TextGrid grid = new TextGrid(getMaxX() + 2, getMaxY() + 2);
        grid.fillCellsWith(this, '*');
        return grid.getDebugString();
    }

    /**
     * Deep copy
     */
    public static CellSet copyCellSet(CellSet set)
    {
        CellSet newSet = new CellSet();

        for (TextGrid.Cell cell : set) {
            TextGrid.Cell newCell = new TextGrid.Cell(cell);
            newSet.add(newCell);
        }
        return newSet;
    }

    public int getType(TextGrid grid)
    {
        if (typeIsValid) return type;
        typeIsValid = true;
        if (size() == 1) {
            type = TYPE_OPEN;
            return TYPE_OPEN;
        }
        int typeTrace = getTypeAccordingToTraceMethod(grid);

        if (DEBUG) {
            System.out.println("trace: " + typeTrace);
        }

        if (typeTrace == TYPE_OPEN) {
            type = TYPE_OPEN;
            return TYPE_OPEN;
        }
        if (typeTrace == TYPE_CLOSED) {
            type = TYPE_CLOSED;
            return TYPE_CLOSED;
        }

        if (typeTrace == TYPE_UNDETERMINED) {
            int typeFill = getTypeAccordingToFillMethod(grid);
            if (typeFill == TYPE_HAS_CLOSED_AREA) {
                type = TYPE_MIXED;
                return TYPE_MIXED;
            } else if (typeFill == TYPE_OPEN) {
                type = TYPE_OPEN;
                return TYPE_OPEN;
            }
        }

        //in the case that both return undetermined:
        type = TYPE_UNDETERMINED;
        return TYPE_UNDETERMINED;
    }

    private int getTypeAccordingToTraceMethod(TextGrid grid)
    {
        if (size() < 2) return TYPE_OPEN;

        TextGrid workGrid = TextGrid.makeSameSizeAs(grid);
        grid.copyCellsTo(this, workGrid);

        //start with a line end if it exists or with a "random" cell if not
        TextGrid.Cell start = null;
        for (TextGrid.Cell cell : this)
            if (workGrid.isLinesEnd(cell))
                start = cell;
        if (start == null) start = getFirst();

        if (DEBUG)
            System.out.println("Tracing:\nStarting at " + start + " (" + grid.getCellTypeAsString(start) + ")");
        TextGrid.Cell previous = start;
        CellSet nextCells = workGrid.followCell(previous);
        if (nextCells.size() == 0) return TYPE_OPEN;
        TextGrid.Cell cell = nextCells.getFirst();
        if (DEBUG)
            System.out.println("\tat cell " + cell + " (" + grid.getCellTypeAsString(cell) + ")");


        while (!cell.equals(start)) {
            nextCells = workGrid.followCell(cell, previous);
            if (nextCells.size() == 0) {
                if (DEBUG)
                    System.out.println("-> Found dead-end, shape is open");
                return TYPE_OPEN;
            }
            if (nextCells.size() == 1) {
                previous = cell;
                cell = nextCells.getFirst();
                if (DEBUG)
                    System.out.println("\tat cell " + cell + " (" + grid.getCellTypeAsString(cell) + ")");
            } else if (nextCells.size() > 1) {
                if (DEBUG)
                    System.out.println("-> Found intersection at cell " + cell);
                return TYPE_UNDETERMINED;
            }
        }
        if (DEBUG)
            System.out.println("-> Arrived back to start, shape is closed");
        return TYPE_CLOSED;

//		boolean hasMoved = false;
//		
//		CellSet workSet;
//		workSet = new CellSet(this);
//
//		TextGrid.Cell start = (TextGrid.Cell) get(0);
//		
//		workSet.remove(start);
//		TextGrid.Cell cell = workSet.findCellNextTo(start);
//		
//		while(true && cell != null){
//			
//			hasMoved = true;
//			workSet.remove(cell);
//			
//			CellSet setOfNeighbours = workSet.findCellsNextTo(cell);
//			
//			if(setOfNeighbours.isEmpty()) break;
//
//			TextGrid.Cell c = null;
//			if(setOfNeighbours.size() == 1) c = (TextGrid.Cell) setOfNeighbours.get(0);
//			if(setOfNeighbours.size() > 1) return TYPE_UNDETERMINED;
//			if(c == null) break;
//			else cell = c;
//		}
//		if(cell != null && start.isNextTo(cell) && hasMoved) return TYPE_CLOSED;
//		else return TYPE_OPEN;
    }

    private int getTypeAccordingToFillMethod(TextGrid grid)
    {
        if (size() == 0) return TYPE_OPEN;

        CellSet tempSet = copyCellSet(this);
        tempSet.translate(-this.getMinX() + 1, -this.getMinY() + 1);
        TextGrid subGrid = grid.getSubGrid(getMinX() - 1, getMinY() - 1, getWidth() + 3, getHeight() + 3);
        AbstractionGrid abstraction = new AbstractionGrid(subGrid, tempSet);
        TextGrid temp = abstraction.getCopyOfInternalBuffer();

        int width = temp.getWidth();
        int height = temp.getHeight();

        TextGrid.Cell fillCell = null;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                TextGrid.Cell cCell = new TextGrid.Cell(x, y);
                if (temp.isBlank(cCell)) {
                    fillCell = cCell;
                    break;
                }
            }
        }

        if (fillCell == null) {
            System.err.println("Unexpected error: fill method cannot fill anywhere");
            return TYPE_UNDETERMINED;
        }

        temp.fillContinuousArea(fillCell, '*');
        if (VERBOSE_DEBUG) {
            System.out.println("Buffer after filling:");
            temp.printDebug(System.out);
        }

        if (temp.hasBlankCells()) return TYPE_HAS_CLOSED_AREA;
        else return TYPE_OPEN;
    }

    public void translate(int dx, int dy)
    {
        typeIsValid = false;
        for (TextGrid.Cell cCell : this) {
            cCell.x += dx;
            cCell.y += dy;
        }
    }

    public TextGrid.Cell find(TextGrid.Cell cell)
    {
        for (TextGrid.Cell cCell : this) {
            if (cCell.equals(cell)) return cCell;
        }
        return null;
    }

    public boolean contains(TextGrid.Cell cell)
    {
        return cell != null && internalSet.contains(cell);
    }

    public boolean hasCommonCells(CellSet otherSet)
    {
        for (TextGrid.Cell cell : this) {
            if (otherSet.contains(cell)) return true;
        }
        return false;
    }

    public CellSet getFilledEquivalent(TextGrid textGrid)
    {
        if (this.getType(textGrid) == CellSet.TYPE_OPEN) return new CellSet(this);
        TextGrid grid = new TextGrid(getMaxX() + 2, getMaxY() + 2);
        grid.fillCellsWith(this, '*');

        //find a cell that has a blank both on the east and the west
        TextGrid.Cell cell = null;
        boolean finished = false;
        for (int y = 0; y < grid.getHeight() && !finished; y++) {
            for (int x = 0; x < grid.getWidth() && !finished; x++) {
                cell = new TextGrid.Cell(x, y);
                if (!grid.isBlank(cell)
                        && grid.isBlank(cell.getEast())
                        && grid.isBlank(cell.getWest())) {
                    finished = true;
                }
            }
        }
        if (cell != null) {
            cell = cell.getEast();
            if (grid.isOutOfBounds(cell)) return new CellSet(this);
            grid.fillContinuousArea(cell, '*');
            return grid.getAllNonBlank();
        }
        System.err.println("Unexpected error, cannot find the filled equivalent of CellSet");
        return null;
    }

    public void subtractSet(CellSet set)
    {
        typeIsValid = false;
        for (TextGrid.Cell cell : set) {
            TextGrid.Cell thisCell = find(cell);
            if (thisCell != null) remove(thisCell);
        }
    }

    public int getWidth()
    {
        return getMaxX() - getMinX();
    }


    public int getHeight()
    {
        return getMaxY() - getMinY();
    }

    public int getMaxX()
    {
        int result = 0;
        for (TextGrid.Cell cell : this) {
            if (cell.x > result) result = cell.x;
        }
        return result;
    }

    public int getMinX()
    {
        int result = Integer.MAX_VALUE;
        for (TextGrid.Cell cell : this) {
            if (cell.x < result) result = cell.x;
        }
        return result;
    }


    public int getMaxY()
    {
        int result = 0;
        for (TextGrid.Cell cell : this) {
            if (cell.y > result) result = cell.y;
        }
        return result;
    }

    public int getMinY()
    {
        int result = Integer.MAX_VALUE;
        for (TextGrid.Cell cell : this) {
            if (cell.y < result) result = cell.y;
        }
        return result;
    }


    public Object remove(TextGrid.Cell cell)
    {
        typeIsValid = false;
        cell = find(cell);
        if (cell != null) return internalSet.remove(cell);
        else return null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellSet cells = (CellSet) o;

        return internalSet != null ? internalSet.equals(cells.internalSet) : cells.internalSet == null;

    }

    @Override
    public int hashCode()
    {
        return internalSet != null ? internalSet.hashCode() : 0;
    }

    public static ArrayList<CellSet> removeDuplicateSets(ArrayList<CellSet> list)
    {
        ArrayList<CellSet> uniqueSets = new ArrayList<CellSet>();

        for (CellSet set : list) {
            boolean isOriginal = true;
            for (CellSet uniqueSet : uniqueSets) {
                if (set.equals(uniqueSet)) {
                    isOriginal = false;
                }
            }
            if (isOriginal) uniqueSets.add(set);
        }
        return uniqueSets;
    }


    /**
     * Takes into account character info from the grid
     *
     * @return ArrayList of distinct BoundarySetS
     */
    public ArrayList<CellSet> breakIntoDistinctBoundaries(TextGrid grid)
    {
        ArrayList<CellSet> result;

        AbstractionGrid temp = new AbstractionGrid(grid, this);
        result = temp.getDistinctShapes();

        return result;
    }


    /**
     *
     * @return ArrayList of distinct BoundarySetS
     */
    public ArrayList<CellSet> breakIntoDistinctBoundaries()
    {
        ArrayList<CellSet> result = new ArrayList<CellSet>();

        //CellSet tempSet = copyCellSet(this);
        //tempSet.translate( - this.getMinX() + 1, - this.getMinY() + 1);

//		TextGrid boundaryGrid = new TextGrid(tempSet.getMaxX()+2, tempSet.getMaxY()+2);
//		boundaryGrid.fillCellsWith(tempSet, '*');

        TextGrid boundaryGrid = new TextGrid(getMaxX() + 2, getMaxY() + 2);
        boundaryGrid.fillCellsWith(this, '*');


        for (TextGrid.Cell cell : this) {
            if (boundaryGrid.isBlank(cell.x, cell.y)) continue;
            CellSet boundarySet = boundaryGrid.fillContinuousArea(cell.x, cell.y, ' ');
            //boundarySet.translate( this.getMinX() - 1, this.getMinY() - 1);
            result.add(boundarySet);
        }
        return result;
    }


    /**
     *
     * Breaks that:
     * <pre>
     *  +-----+
     *  |     |
     *  +  ---+-------------------
     *  |     |
     *  +-----+
     * </pre>
     *
     * into the following 3:
     *
     * <pre>
     *  +-----+
     *  |     |
     *  +     +
     *  |     |
     *  +-----+
     *
     *     ---
     *         -------------------
     * </pre>
     *
     * @return a list of boundaries that are either open or closed but not mixed
     * and they are equivalent to the <code>this</code>
     */
    public ArrayList<CellSet> breakTrulyMixedBoundaries(TextGrid grid)
    {
        ArrayList<CellSet> result = new ArrayList<CellSet>();
        CellSet visitedEnds = new CellSet();

        TextGrid workGrid = TextGrid.makeSameSizeAs(grid);
        grid.copyCellsTo(this, workGrid);

        if (DEBUG) {
            System.out.println("Breaking truly mixed boundaries below:");
            workGrid.printDebug(System.out);
        }

        for (TextGrid.Cell start : this) {
            if (workGrid.isLinesEnd(start) && !visitedEnds.contains(start)) {

                if (DEBUG)
                    System.out.println("Starting new subshape:");

                CellSet set = new CellSet();
                set.add(start);
                if (DEBUG) System.out.println("Added boundary " + start);

                TextGrid.Cell previous = start;
                CellSet nextCells = workGrid.followCell(previous);
                if (nextCells.size() == 0) {
                    throw new IllegalArgumentException("This shape is either open but multipart or has only one cell, and cannot be processed by this method");
                }
                TextGrid.Cell cell = nextCells.getFirst();
                set.add(cell);
                if (DEBUG) System.out.println("Added boundary " + cell);

                boolean finished = false;
                if (workGrid.isLinesEnd(cell)) {
                    visitedEnds.add(cell);
                    finished = true;
                }

                while (!finished) {
                    nextCells = workGrid.followCell(cell, previous);
                    if (nextCells.size() == 1) {
                        set.add(cell);
                        if (DEBUG) System.out.println("Added boundary " + cell);
                        previous = cell;
                        cell = nextCells.getFirst();
                        //if(!cell.equals(start) && grid.isPointCell(cell))
                        //	s.addToPoints(makePointForCell(cell, workGrid, cellWidth, cellHeight, allRound));
                        if (workGrid.isLinesEnd(cell)) {
                            visitedEnds.add(cell);
                            finished = true;
                        }
                    } else if (nextCells.size() > 1) {
                        finished = true;
                    }
                }
                result.add(set);
            }
        }

        //substract all boundary sets from this CellSet
        CellSet whatsLeft = new CellSet(this);
        for (CellSet set : result) {
            whatsLeft.subtractSet(set);
            if (DEBUG) set.printAsGrid(System.out);
        }
        result.add(whatsLeft);
        if (DEBUG) whatsLeft.printAsGrid(System.out);

        return result;
    }


    public TextGrid makeIntoGrid()
    {
        TextGrid grid = new TextGrid(getMaxX() + 2, getMaxY() + 2);
        grid.fillCellsWith(this, '*');
        return grid;
    }

    public CellSet makeScaledOneThirdEquivalent()
    {
        TextGrid gridBig = this.makeIntoGrid();
        gridBig.fillCellsWith(this, '*');
        if (VERBOSE_DEBUG) {
            System.out.println("---> making ScaledOneThirdEquivalent of:");
            gridBig.printDebug(System.out);
        }


        TextGrid gridSmall = new TextGrid((getMaxX() + 2) / 3, (getMaxY() + 2) / 3);


        for (int y = 0; y < gridBig.getHeight(); y++) {
            for (int x = 0; x < gridBig.getWidth(); x++) {
                TextGrid.Cell cell = new TextGrid.Cell(x, y);
                if (!gridBig.isBlank(cell)) gridSmall.set(x / 3, y / 3, '*');
            }
        }

        if (VERBOSE_DEBUG) {
            System.out.println("---> made into grid:");
            gridSmall.printDebug(System.out);
        }

        return gridSmall.getAllNonBlank();
    }

}
