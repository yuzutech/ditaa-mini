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
package org.stathissideris.ditaa.graphics;

import org.stathissideris.ditaa.core.ConversionOptions;
import org.stathissideris.ditaa.core.Pair;
import org.stathissideris.ditaa.text.AbstractionGrid;
import org.stathissideris.ditaa.text.CellSet;
import org.stathissideris.ditaa.text.TextGrid;
import org.stathissideris.ditaa.text.TextGrid.Cell;
import org.stathissideris.ditaa.text.TextGrid.CellColorPair;
import org.stathissideris.ditaa.text.TextGrid.CellStringPair;
import org.stathissideris.ditaa.text.TextGrid.CellTagPair;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Efstathios Sideris
 */
public class Diagram {

    private static final boolean DEBUG = false;
    private static final boolean DEBUG_VERBOSE = false;
    private static final boolean DEBUG_MAKE_SHAPES = false;

    private ArrayList<DiagramShape> shapes = new ArrayList<DiagramShape>();
    private ArrayList<CompositeDiagramShape> compositeShapes = new ArrayList<CompositeDiagramShape>();
    private ArrayList<DiagramText> textObjects = new ArrayList<DiagramText>();

    private int width, height;
    private int cellWidth, cellHeight;


    /**
     *
     * <p>An outline of the inner workings of this very important (and monstrous)
     * constructor is presented here. Boundary processing is the first step
     * of the process:</p>
     *
     * <ol>
     *   <li>Copy the grid into a work grid and remove all type-on-line
     *       and point markers from the work grid</li>
     *   <li>Split grid into distinct shapes by plotting the grid
     * 	     onto an AbstractionGrid and its getDistinctShapes() method.</li>
     *   <li>Find all the possible boundary sets of each of the
     *       distinct shapes. This can produce duplicate shapes (if the boundaries
     *       are the same when filling from the inside and the outside).</li>
     *   <li>Remove duplicate boundaries.</li>
     *   <li>Remove obsolete boundaries. Obsolete boundaries are the ones that are
     *       the sum of their parts when plotted as filled shapes. (see method
     *       removeObsoleteShapes())</li>
     *   <li>Seperate the found boundary sets to open, closed or mixed
     *       (See CellSet class on how its done).</li>
     *   <li>Are there any closed boundaries?
     *        <ul>
     *           <li>YES. Subtract all the closed boundaries from each of the
     *           open ones. That should convert the mixed shapes into open.</li>
     *           <li>NO. In this (harder) case, we use the method
     *           breakTrulyMixedBoundaries() of CellSet to break boundaries
     *           into open and closed shapes (would work in any case, but it's
     *           probably slower than the other method). This method is based
     *           on tracing from the lines' ends and splitting when we get to
     *           an intersection.</li>
     *        </ul>
     *   </li>
     *   <li>If we had to eliminate any mixed shapes, we seperate the found
     *   boundary sets again to open, closed or mixed.</li>
     * </ol>
     *
     * <p>At this stage, the boundary processing is all complete and we
     * proceed with using those boundaries to create the shapes:</p>
     *
     * <ol>
     *   <li>Create closed shapes.</li>
     *   <li>Create open shapes. That's when the line end corrections are
     *   also applied, concerning the positioning of the ends of lines
     *   see methods connectEndsToAnchors() and moveEndsToCellEdges() of
     *   DiagramShape.</li>
     *   <li>Assign color codes to closed shapes.</li>
     *   <li>Assing extended markup tags to closed shapes.</p>
     *   <li>Create arrowheads.</p>
     *   <li>Create point markers.</p>
     * </ol>
     *
     * <p>Finally, the text processing occurs: [pending]</p>
     */
    public Diagram(TextGrid grid, ConversionOptions options)
    {

        this.cellWidth = options.renderingOptions.getCellWidth();
        this.cellHeight = options.renderingOptions.getCellHeight();

        FontMeasurer fontMeasurer = new FontMeasurer(
                options.renderingOptions.getFont(),
                options.renderingOptions.isFixedFontSize()
        );

        width = grid.getWidth() * cellWidth;
        height = grid.getHeight() * cellHeight;

        TextGrid workGrid = new TextGrid(grid);
        workGrid.replaceTypeOnLine();
        workGrid.replacePointMarkersOnLine();
        if (DEBUG) workGrid.printDebug(System.out);

        int width = grid.getWidth();
        int height = grid.getHeight();


        //split distinct shapes using AbstractionGrid
        AbstractionGrid temp = new AbstractionGrid(workGrid, workGrid.getAllBoundaries());
        ArrayList<CellSet> boundarySetsStep1 = temp.getDistinctShapes();

        if (DEBUG) {
            System.out.println("******* Distinct shapes found using AbstractionGrid *******");
            for (CellSet set : boundarySetsStep1) {
                set.printAsGrid(System.out);
            }
            System.out.println("******* Same set of shapes after processing them by filling *******");
        }


        //Find all the boundaries by using the special version of the filling method
        //(fills in a different buffer than the buffer it reads from)
        ArrayList<CellSet> boundarySetsStep2 = new ArrayList<CellSet>();
        for (CellSet set : boundarySetsStep1) {
            //the fill buffer keeps track of which cells have been
            //filled already
            TextGrid fillBuffer = new TextGrid(width * 3, height * 3);

            for (int yi = 0; yi < height * 3; yi++) {
                for (int xi = 0; xi < width * 3; xi++) {
                    if (fillBuffer.isBlank(xi, yi)) {

                        TextGrid copyGrid = new AbstractionGrid(workGrid, set).getCopyOfInternalBuffer();

                        CellSet boundaries =
                                copyGrid
                                        .findBoundariesExpandingFrom(new Cell(xi, yi));
                        if (boundaries.size() == 0) continue; //i'm not sure why these occur
                        boundarySetsStep2.add(boundaries.makeScaledOneThirdEquivalent());

                        copyGrid = new AbstractionGrid(workGrid, set).getCopyOfInternalBuffer();
                        CellSet filled =
                                copyGrid
                                        .fillContinuousArea(new Cell(xi, yi), '*');
                        fillBuffer.fillCellsWith(filled, '*');
                        fillBuffer.fillCellsWith(boundaries, '-');

                        if (DEBUG) {
                            //System.out.println("Fill buffer:");
                            //fillBuffer.printDebug();
                            boundaries.makeScaledOneThirdEquivalent().printAsGrid(System.out);
                            System.out.println("-----------------------------------");
                        }

                    }
                }
            }
        }

        if (DEBUG)
            System.out.println("******* Removed duplicates *******");

        boundarySetsStep2 = CellSet.removeDuplicateSets(boundarySetsStep2);

        if (DEBUG) {
            for (CellSet set : boundarySetsStep2) {
                set.printAsGrid(System.out);
            }
        }

        int originalSize = boundarySetsStep2.size();
        boundarySetsStep2 = CellSet.removeDuplicateSets(boundarySetsStep2);
        if (DEBUG) {
            System.out.println(
                    "******* Removed duplicates: there were "
                            + originalSize
                            + " shapes and now there are "
                            + boundarySetsStep2.size());
        }


        //split boundaries to open, closed and mixed

        if (DEBUG)
            System.out.println("******* First evaluation of openess *******");

        ArrayList<CellSet> open = new ArrayList<CellSet>();
        ArrayList<CellSet> closed = new ArrayList<CellSet>();
        ArrayList<CellSet> mixed = new ArrayList<CellSet>();

        for (CellSet set : boundarySetsStep2) {
            int type = set.getType(workGrid);
            if (type == CellSet.TYPE_CLOSED) closed.add(set);
            else if (type == CellSet.TYPE_OPEN) open.add(set);
            else if (type == CellSet.TYPE_MIXED) mixed.add(set);
            if (DEBUG) {
                if (type == CellSet.TYPE_CLOSED) System.out.println("Closed boundaries:");
                else if (type == CellSet.TYPE_OPEN) System.out.println("Open boundaries:");
                else if (type == CellSet.TYPE_MIXED) System.out.println("Mixed boundaries:");
                set.printAsGrid(System.out);
            }
        }

        boolean hadToEliminateMixed = false;

        if (mixed.size() > 0 && closed.size() > 0) {
            // mixed shapes can be eliminated by
            // subtracting all the closed shapes from them
            if (DEBUG)
                System.out.println("******* Eliminating mixed shapes (basic algorithm) *******");

            hadToEliminateMixed = true;

            //subtract from each of the mixed sets all the closed sets
            for (CellSet set : mixed) {
                for (CellSet closedSet : closed) {
                    set.subtractSet(closedSet);
                }
                // this is necessary because some mixed sets produce
                // several distinct open sets after you subtract the
                // closed sets from them
                if (set.getType(workGrid) == CellSet.TYPE_OPEN) {
                    boundarySetsStep2.remove(set);
                    boundarySetsStep2.addAll(set.breakIntoDistinctBoundaries(workGrid));
                }
            }

        } else if (mixed.size() > 0 && closed.size() == 0) {
            // no closed shape exists, will have to
            // handle mixed shape on its own
            // an example of this case is the following:
            // +-----+
            // |  A  |C                 B
            // +  ---+-------------------
            // |     |
            // +-----+

            hadToEliminateMixed = true;

            if (DEBUG)
                System.out.println("******* Eliminating mixed shapes (advanced algorithm for truly mixed shapes) *******");

            for (CellSet set : mixed) {
                boundarySetsStep2.remove(set);
                boundarySetsStep2.addAll(set.breakTrulyMixedBoundaries(workGrid));
            }

        } else {
            if (DEBUG)
                System.out.println("No mixed shapes found. Skipped mixed shape elimination step");
        }


        if (hadToEliminateMixed) {
            if (DEBUG)
                System.out.println("******* Second evaluation of openess *******");

            //split boundaries again to open, closed and mixed
            open = new ArrayList<CellSet>();
            closed = new ArrayList<CellSet>();
            mixed = new ArrayList<CellSet>();

            for (CellSet set : boundarySetsStep2) {
                int type = set.getType(workGrid);
                if (type == CellSet.TYPE_CLOSED) closed.add(set);
                else if (type == CellSet.TYPE_OPEN) open.add(set);
                else if (type == CellSet.TYPE_MIXED) mixed.add(set);
                if (DEBUG) {
                    if (type == CellSet.TYPE_CLOSED) System.out.println("Closed boundaries:");
                    else if (type == CellSet.TYPE_OPEN) System.out.println("Open boundaries:");
                    else if (type == CellSet.TYPE_MIXED) System.out.println("Mixed boundaries:");
                    set.printAsGrid(System.out);
                }
            }
        }

        removeObsoleteShapes(workGrid, closed);

        boolean allCornersRound = false;
        if (options.processingOptions.areAllCornersRound()) allCornersRound = true;

        //make shapes from the boundary sets
        //make closed shapes
        if (DEBUG_MAKE_SHAPES) {
            System.out.println("***** MAKING SHAPES FROM BOUNDARY SETS *****");
            System.out.println("***** CLOSED: *****");
        }

        ArrayList<DiagramShape> closedShapes = new ArrayList<DiagramShape>();
        for (CellSet set : closed) {
            if (DEBUG_MAKE_SHAPES) {
                set.printAsGrid(System.out);
            }

            DiagramShape shape = DiagramComponent.createClosedFromBoundaryCells(workGrid, set, cellWidth, cellHeight, allCornersRound);
            if (shape != null) {
                addToShapes(shape);
                closedShapes.add(shape);
            }
        }

        if (options.processingOptions.performSeparationOfCommonEdges())
            separateCommonEdges(closedShapes);

        //make open shapes
        for (CellSet set : open) {
            if (set.size() == 1) { //single cell "shape"
                TextGrid.Cell cell = set.getFirst();
                if (!grid.cellContainsDashedLineChar(cell)) {
                    DiagramShape shape = DiagramShape.createSmallLine(workGrid, cell, cellWidth, cellHeight);
                    if (shape != null) {
                        addToShapes(shape);
                        shape.connectEndsToAnchors(workGrid, this);
                    }
                }
            } else { //normal shape
                if (DEBUG)
                    System.out.println(set.getCellsAsString());

                DiagramComponent shape =
                        CompositeDiagramShape
                                .createOpenFromBoundaryCells(
                                        workGrid, set, cellWidth, cellHeight, allCornersRound);

                if (shape != null) {
                    if (shape instanceof CompositeDiagramShape) {
                        addToCompositeShapes((CompositeDiagramShape) shape);
                        ((CompositeDiagramShape) shape).connectEndsToAnchors(workGrid, this);
                    } else if (shape instanceof DiagramShape) {
                        addToShapes((DiagramShape) shape);
                        ((DiagramShape) shape).connectEndsToAnchors(workGrid, this);
                        ((DiagramShape) shape).moveEndsToCellEdges(grid, this);
                    }
                }

            }
        }

        //assign color codes to shapes
        //TODO: text on line should not change its color

        for (CellColorPair pair : grid.findColorCodes()) {
            ShapePoint point = new ShapePoint(getCellMidX(pair.cell), getCellMidY(pair.cell));
            DiagramShape containingShape = findSmallestShapeContaining(point);

            if (containingShape != null)
                containingShape.setFillColor(pair.color);
        }

        //assign markup to shapes
        for (CellTagPair pair : grid.findMarkupTags()) {
            ShapePoint point = new ShapePoint(getCellMidX(pair.cell), getCellMidY(pair.cell));

            DiagramShape containingShape = findSmallestShapeContaining(point);

            //this tag is not within a shape, skip
            if (containingShape == null) continue;

            //TODO: the code below could be a lot more concise
            if (pair.tag.equals("d")) {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes("d");
                if (def == null)
                    containingShape.setType(DiagramShape.TYPE_DOCUMENT);
                else {
                    containingShape.setType(DiagramShape.TYPE_CUSTOM);
                    containingShape.setDefinition(def);
                }
            } else if (pair.tag.equals("s")) {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes("s");
                if (def == null)
                    containingShape.setType(DiagramShape.TYPE_STORAGE);
                else {
                    containingShape.setType(DiagramShape.TYPE_CUSTOM);
                    containingShape.setDefinition(def);
                }
            } else if (pair.tag.equals("io")) {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes("io");
                if (def == null)
                    containingShape.setType(DiagramShape.TYPE_IO);
                else {
                    containingShape.setType(DiagramShape.TYPE_CUSTOM);
                    containingShape.setDefinition(def);
                }
            } else if (pair.tag.equals("c")) {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes("c");
                if (def == null)
                    containingShape.setType(DiagramShape.TYPE_DECISION);
                else {
                    containingShape.setType(DiagramShape.TYPE_CUSTOM);
                    containingShape.setDefinition(def);
                }
            } else if (pair.tag.equals("mo")) {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes("mo");
                if (def == null)
                    containingShape.setType(DiagramShape.TYPE_MANUAL_OPERATION);
                else {
                    containingShape.setType(DiagramShape.TYPE_CUSTOM);
                    containingShape.setDefinition(def);
                }
            } else if (pair.tag.equals("tr")) {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes("tr");
                if (def == null)
                    containingShape.setType(DiagramShape.TYPE_TRAPEZOID);
                else {
                    containingShape.setType(DiagramShape.TYPE_CUSTOM);
                    containingShape.setDefinition(def);
                }
            } else if (pair.tag.equals("o")) {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes("o");
                if (def == null)
                    containingShape.setType(DiagramShape.TYPE_ELLIPSE);
                else {
                    containingShape.setType(DiagramShape.TYPE_CUSTOM);
                    containingShape.setDefinition(def);
                }
            } else {
                CustomShapeDefinition def =
                        options.processingOptions.getFromCustomShapes(pair.tag);
                containingShape.setType(DiagramShape.TYPE_CUSTOM);
                containingShape.setDefinition(def);
            }
        }

        //make arrowheads
        for (Cell cell : workGrid.findArrowheads()) {
            DiagramShape arrowhead = DiagramShape.createArrowhead(workGrid, cell, cellWidth, cellHeight);
            if (arrowhead != null) addToShapes(arrowhead);
            else System.err.println("Could not create arrowhead shape. Unexpected error.");
        }

        //make point markers
        for (Cell cell : grid.getPointMarkersOnLine()) {
            DiagramShape mark = new DiagramShape();
            mark.addToPoints(new ShapePoint(
                    getCellMidX(cell),
                    getCellMidY(cell)
            ));
            mark.setType(DiagramShape.TYPE_POINT_MARKER);
            mark.setFillColor(Color.white);
            shapes.add(mark);
        }

        removeDuplicateShapes();

        if (DEBUG) System.out.println("Shape count: " + shapes.size());
        if (DEBUG) System.out.println("Composite shape count: " + compositeShapes.size());

        //copy again
        workGrid = new TextGrid(grid);
        workGrid.removeNonText();


        // ****** handle text *******
        //break up text into groups
        TextGrid textGroupGrid = new TextGrid(workGrid);
        CellSet gaps = textGroupGrid.getAllBlanksBetweenCharacters();
        //kludge
        textGroupGrid.fillCellsWith(gaps, '|');
        CellSet nonBlank = textGroupGrid.getAllNonBlank();
        ArrayList<CellSet> textGroups = nonBlank.breakIntoDistinctBoundaries();
        if (DEBUG) System.out.println(textGroups.size() + " text groups found");

        Font font = fontMeasurer.getFontFor(cellHeight);

        for (CellSet textGroupCellSet : textGroups) {
            TextGrid isolationGrid = new TextGrid(width, height);
            workGrid.copyCellsTo(textGroupCellSet, isolationGrid);

            ArrayList<CellStringPair> strings = isolationGrid.findStrings();
            for (CellStringPair pair : strings) {
                Cell cell = pair.cell;
                String string = pair.string;
                if (DEBUG)
                    System.out.println("Found string " + string);
                Cell lastCell = new Cell(cell.x + string.length() - 1, cell.y);

                int minX = getCellMinX(cell);
                int y = getCellMaxY(cell);
                int maxX = getCellMaxX(lastCell);

                DiagramText textObject;
                if (fontMeasurer.getWidthFor(string, font) > maxX - minX) { //does not fit horizontally
                    Font lessWideFont = fontMeasurer.getFontFor(maxX - minX, string);
                    textObject = new DiagramText(minX, y, string, lessWideFont, fontMeasurer);
                } else textObject = new DiagramText(minX, y, string, font, fontMeasurer);

                textObject.centerVerticallyBetween(getCellMinY(cell), getCellMaxY(cell));

                //TODO: if the strings start with bullets they should be aligned to the left

                //position text correctly
                int otherStart = isolationGrid.otherStringsStartInTheSameColumn(cell);
                int otherEnd = isolationGrid.otherStringsEndInTheSameColumn(lastCell);
                if (0 == otherStart && 0 == otherEnd) {
                    textObject.centerHorizontallyBetween(minX, maxX);
                } else if (otherEnd > 0 && otherStart == 0) {
                    textObject.alignRightEdgeTo(maxX);
                } else if (otherEnd > 0 && otherStart > 0) {
                    if (otherEnd > otherStart) {
                        textObject.alignRightEdgeTo(maxX);
                    } else if (otherEnd == otherStart) {
                        textObject.centerHorizontallyBetween(minX, maxX);
                    }
                }

                addToTextObjects(textObject);
            }
        }

        if (DEBUG)
            System.out.println("Positioned text");

        //correct the color of the text objects according
        //to the underlying color
        for (DiagramText textObject : getTextObjects()) {
            DiagramShape shape = findSmallestShapeIntersecting(textObject.getBounds());
            if (shape != null
                    && shape.getFillColor() != null
                    && BitmapRenderer.isColorDark(shape.getFillColor())) {
                textObject.setColor(Color.white);
            }
        }

        //set outline to true for test within custom shapes
        for (DiagramShape shape : this.getAllDiagramShapes()) {
            if (shape.getType() == DiagramShape.TYPE_CUSTOM) {
                for (DiagramText textObject : getTextObjects()) {
                    textObject.setHasOutline(true);
                    textObject.setColor(DiagramText.DEFAULT_COLOR);
                }
            }
        }

        if (DEBUG)
            System.out.println("Corrected color of text according to underlying color");

    }

    /**
     * Returns a list of all DiagramShapes in the Diagram, including
     * the ones within CompositeDiagramShapes
     */
    public ArrayList<DiagramShape> getAllDiagramShapes()
    {
        ArrayList<DiagramShape> shapes = new ArrayList<DiagramShape>();
        shapes.addAll(this.getShapes());

        for (CompositeDiagramShape compShape : getCompositeShapes()) {
            shapes.addAll(compShape.getShapes());
        }
        return shapes;
    }

    /**
     * Removes the sets from <code>sets</code>that are the sum of their parts
     * when plotted as filled shapes.
     *
     * @return true if it removed any obsolete.
     *
     */
    private boolean removeObsoleteShapes(TextGrid grid, ArrayList<CellSet> sets)
    {
        if (DEBUG)
            System.out.println("******* Removing obsolete shapes *******");

        boolean removedAny = false;

        ArrayList<CellSet> filledSets = new ArrayList<CellSet>();

        Iterator it;

        if (DEBUG_VERBOSE) {
            System.out.println("******* Sets before *******");
            it = sets.iterator();
            while (it.hasNext()) {
                CellSet set = (CellSet) it.next();
                set.printAsGrid(System.out);
            }
        }

        //make filled versions of all the boundary sets
        it = sets.iterator();
        while (it.hasNext()) {
            CellSet set = (CellSet) it.next();
            set = set.getFilledEquivalent(grid);
            if (set == null) {
                return false;
            } else filledSets.add(set);
        }

        ArrayList<Integer> toBeRemovedIndices = new ArrayList<Integer>();
        it = filledSets.iterator();
        while (it.hasNext()) {
            CellSet set = (CellSet) it.next();

            if (DEBUG_VERBOSE) {
                System.out.println("*** Deciding if the following should be removed:");
                set.printAsGrid(System.out);
            }

            //find the other sets that have common cells with set
            ArrayList<CellSet> common = new ArrayList<CellSet>();
            common.add(set);
            Iterator<CellSet> it2 = filledSets.iterator();
            while (it2.hasNext()) {
                CellSet set2 = it2.next();
                if (set != set2 && set.hasCommonCells(set2)) {
                    common.add(set2);
                }
            }
            //it only makes sense for more than 2 sets
            if (common.size() == 2) continue;

            //find largest set
            CellSet largest = set;
            it2 = common.iterator();
            while (it2.hasNext()) {
                CellSet set2 = it2.next();
                if (set2.size() > largest.size()) {
                    largest = set2;
                }
            }

            if (DEBUG_VERBOSE) {
                System.out.println("Largest:");
                largest.printAsGrid(System.out);
            }

            //see if largest is sum of others
            common.remove(largest);

            //make the sum set of the small sets on a grid
            TextGrid gridOfSmalls = new TextGrid(largest.getMaxX() + 2, largest.getMaxY() + 2);
            it2 = common.iterator();
            while (it2.hasNext()) {
                CellSet set2 = it2.next();
                if (DEBUG_VERBOSE) {
                    System.out.println("One of smalls:");
                    set2.printAsGrid(System.out);
                }
                gridOfSmalls.fillCellsWith(set2, '*');
            }
            if (DEBUG_VERBOSE) {
                System.out.println("Sum of smalls:");
                gridOfSmalls.printDebug(System.out);
            }
            TextGrid gridLargest = new TextGrid(largest.getMaxX() + 2, largest.getMaxY() + 2);
            gridLargest.fillCellsWith(largest, '*');

            int index = filledSets.indexOf(largest);
            if (gridLargest.equals(gridOfSmalls)
                    && !toBeRemovedIndices.contains(index)) {
                toBeRemovedIndices.add(index);
                if (DEBUG) {
                    System.out.println("Decided to remove set:");
                    largest.printAsGrid(System.out);
                }
            } /*else if (DEBUG){
                System.out.println("This set WILL NOT be removed:");
				largest.printAsGrid();
			}*/
            //if(gridLargest.equals(gridOfSmalls)) toBeRemovedIndices.add(new Integer(index));
        }

        ArrayList<CellSet> setsToBeRemoved = new ArrayList<CellSet>();
        for (Integer i : toBeRemovedIndices) {
            setsToBeRemoved.add(sets.get(i));
        }

        it = setsToBeRemoved.iterator();
        while (it.hasNext()) {
            CellSet set = (CellSet) it.next();
            removedAny = true;
            sets.remove(set);
        }

        if (DEBUG_VERBOSE) {
            System.out.println("******* Sets after *******");
            it = sets.iterator();
            while (it.hasNext()) {
                CellSet set = (CellSet) it.next();
                set.printAsGrid(System.out);
            }
        }

        return removedAny;
    }

    public float getMinimumOfCellDimension()
    {
        return Math.min(getCellWidth(), getCellHeight());
    }

    private void separateCommonEdges(ArrayList<DiagramShape> shapes)
    {

        float offset = getMinimumOfCellDimension() / 5;

        ArrayList<ShapeEdge> edges = new ArrayList<ShapeEdge>();

        //get all adges
        for (DiagramShape shape : shapes) {
            edges.addAll(shape.getEdges());
        }

        //group edges into pairs of touching edges
        ArrayList<Pair<ShapeEdge, ShapeEdge>> listOfPairs = new ArrayList<Pair<ShapeEdge, ShapeEdge>>();

        //all-against-all touching test for the edges
        int startIndex = 1; //skip some to avoid duplicate comparisons and self-to-self comparisons

        for (ShapeEdge edge1 : edges) {
            for (int k = startIndex; k < edges.size(); k++) {
                ShapeEdge edge2 = edges.get(k);

                if (edge1.touchesWith(edge2)) {
                    listOfPairs.add(new Pair<ShapeEdge, ShapeEdge>(edge1, edge2));
                }
            }
            startIndex++;
        }

        ArrayList<ShapeEdge> movedEdges = new ArrayList<ShapeEdge>();

        //move equivalent edges inwards
        for (Pair<ShapeEdge, ShapeEdge> pair : listOfPairs) {
            if (!movedEdges.contains(pair.first)) {
                pair.first.moveInwardsBy(offset);
                movedEdges.add(pair.first);
            }
            if (!movedEdges.contains(pair.second)) {
                pair.second.moveInwardsBy(offset);
                movedEdges.add(pair.second);
            }
        }

    }


    //TODO: removes more than it should
    private void removeDuplicateShapes()
    {
        ArrayList<DiagramShape> originalShapes = new ArrayList<DiagramShape>();

        for (DiagramShape shape : getShapes()) {
            boolean isOriginal = true;
            for (Object originalShape1 : originalShapes) {
                DiagramShape originalShape = (DiagramShape) originalShape1;
                if (shape.equals(originalShape)) {
                    isOriginal = false;
                }
            }
            if (isOriginal) originalShapes.add(shape);
        }

        shapes.clear();
        shapes.addAll(originalShapes);
    }

    private DiagramShape findSmallestShapeContaining(ShapePoint point)
    {
        DiagramShape containingShape = null;
        for (DiagramShape shape : getShapes()) {
            if (shape.contains(point)) {
                if (containingShape == null) {
                    containingShape = shape;
                } else {
                    if (shape.isSmallerThan(containingShape)) {
                        containingShape = shape;
                    }
                }
            }
        }
        return containingShape;
    }

    private DiagramShape findSmallestShapeIntersecting(Rectangle2D rect)
    {
        DiagramShape intersectingShape = null;
        for (DiagramShape shape : getShapes()) {
            if (shape.intersects(rect)) {
                if (intersectingShape == null) {
                    intersectingShape = shape;
                } else {
                    if (shape.isSmallerThan(intersectingShape)) {
                        intersectingShape = shape;
                    }
                }
            }
        }
        return intersectingShape;
    }

    private void addToTextObjects(DiagramText shape)
    {
        textObjects.add(shape);
    }

    private void addToCompositeShapes(CompositeDiagramShape shape)
    {
        compositeShapes.add(shape);
    }


    private void addToShapes(DiagramShape shape)
    {
        shapes.add(shape);
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getCellWidth()
    {
        return cellWidth;
    }

    public int getCellHeight()
    {
        return cellHeight;
    }

    public ArrayList<CompositeDiagramShape> getCompositeShapes()
    {
        return compositeShapes;
    }

    public ArrayList<DiagramShape> getShapes()
    {
        return shapes;
    }

    public int getCellMinX(TextGrid.Cell cell)
    {
        return getCellMinX(cell, cellWidth);
    }

    public static int getCellMinX(TextGrid.Cell cell, int cellXSize)
    {
        return cell.x * cellXSize;
    }

    public int getCellMidX(TextGrid.Cell cell)
    {
        return getCellMidX(cell, cellWidth);
    }

    public static int getCellMidX(TextGrid.Cell cell, int cellXSize)
    {
        return cell.x * cellXSize + cellXSize / 2;
    }

    public int getCellMaxX(TextGrid.Cell cell)
    {
        return getCellMaxX(cell, cellWidth);
    }

    public static int getCellMaxX(TextGrid.Cell cell, int cellXSize)
    {
        return cell.x * cellXSize + cellXSize;
    }

    public int getCellMinY(TextGrid.Cell cell)
    {
        return getCellMinY(cell, cellHeight);
    }

    public static int getCellMinY(TextGrid.Cell cell, int cellYSize)
    {
        return cell.y * cellYSize;
    }

    public int getCellMidY(TextGrid.Cell cell)
    {
        return getCellMidY(cell, cellHeight);
    }

    public static int getCellMidY(TextGrid.Cell cell, int cellYSize)
    {
        return cell.y * cellYSize + cellYSize / 2;
    }

    public int getCellMaxY(TextGrid.Cell cell)
    {
        return getCellMaxY(cell, cellHeight);
    }

    public static int getCellMaxY(TextGrid.Cell cell, int cellYSize)
    {
        return cell.y * cellYSize + cellYSize;
    }

    public TextGrid.Cell getCellFor(ShapePoint point)
    {
        if (point == null) throw new IllegalArgumentException("ShapePoint cannot be null");
        return new Cell((int) point.x / cellWidth, (int) point.y / cellHeight);
    }

    public ArrayList<DiagramText> getTextObjects()
    {
        return textObjects;
    }

}
