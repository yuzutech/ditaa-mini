package org.stathissideris.ascii2image.graphics;

import org.stathissideris.ascii2image.core.RenderingOptions;
import org.stathissideris.ascii2image.core.Shape3DOrderingComparator;
import org.stathissideris.ascii2image.core.ShapeAreaComparator;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jean Lazarou.
 */
public class SVGRenderer
{
    public String renderToImage(Diagram diagram, RenderingOptions options)
    {
        SVGBuilder builder = new SVGBuilder(diagram, options);

        try {
            return builder.build();
        } catch (XMLStreamException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static class SVGBuilder
    {
        private final Diagram diagram;
        private final RenderingOptions options;
        private final float dashInterval;
        private final float strokeWeight;

        public SVGBuilder(Diagram diagram, RenderingOptions options)
        {
            this.diagram = diagram;
            this.options = options;

            dashInterval = Math.min(diagram.getCellWidth(), diagram.getCellHeight()) / 2f;
            strokeWeight = diagram.getMinimumOfCellDimension() / 10;
        }


        public String build() throws XMLStreamException, IOException
        {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory factory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer = factory.createXMLStreamWriter(stringWriter);

            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("svg");
            writer.writeDefaultNamespace("http://www.w3.org/2000/svg");
            writer.writeAttribute("width", Integer.toString(diagram.getWidth()));
            writer.writeAttribute("height", Integer.toString(diagram.getHeight()));
            writer.writeAttribute("version", "1.0");
            writer.writeAttribute("shape-rendering", options.performAntialias() ? "geometricPrecision" : "optimizeSpeed");

            writeDefinitions(writer);

            writer.writeStartElement("g");
            writer.writeAttribute("stroke-width", "1");
            writer.writeAttribute("stroke-linecap", "square");
            writer.writeAttribute("stroke-linejoin", "round");

            writeBackgroundLayer(writer);
            writeShapes(writer);
            writeTexts(writer);

            writer.writeEndElement();

            writer.writeEndElement();
            writer.writeEndDocument();
            writer.close();

            stringWriter.close();

            return stringWriter.toString();
        }

        private void writeDefinitions(XMLStreamWriter writer) throws XMLStreamException
        {
            float offset = diagram.getMinimumOfCellDimension() / 3.333f;

            writer.writeStartElement("defs");

            writer.writeStartElement("style");
            writer.writeAttribute("type", "text/css");
            writer.writeCharacters(
                    "@font-face {\n" +
                            "  font-family: " + options.getFontFamily() + ";\n" +
                            (options.getFontURL() != null ? "  src: url('" + options.getFontURL() + "');\n" : "") +
                            "}"
            );
            writer.writeEndElement();

            writer.writeStartElement("filter");
            writer.writeAttribute("id", "shadowBlur");
            writer.writeAttribute("x", "0");
            writer.writeAttribute("y", "0");
            writer.writeAttribute("width", "200%");
            writer.writeAttribute("height", "200%");

            writer.writeStartElement("feOffset");
            writer.writeAttribute("in", "SourceGraphic");
            writer.writeAttribute("dx", Float.toString(offset));
            writer.writeAttribute("dy", Float.toString(offset));
            writer.writeAttribute("result", "offOut");
            writer.writeEndElement();

            writer.writeStartElement("feGaussianBlur");
            writer.writeAttribute("in", "offOut");
            writer.writeAttribute("stdDeviation", "3");
            writer.writeEndElement();

            writer.writeEndElement();

            writer.writeEndElement();
        }

        private void writeShapes(XMLStreamWriter writer) throws XMLStreamException
        {
            ArrayList<DiagramShape> shapes = diagram.getAllDiagramShapes();
            ArrayList<DiagramShape> storageShapes = new ArrayList<DiagramShape>();
            ArrayList<DiagramShape> otherShapes = new ArrayList<DiagramShape>();
            ArrayList<DiagramShape> pointMarkers = new ArrayList<DiagramShape>();

            for (DiagramShape shape : shapes) {
                if (shape.getType() == DiagramShape.TYPE_STORAGE) {
                    storageShapes.add(shape);
                } else if (shape.getType() == DiagramShape.TYPE_POINT_MARKER) {
                    pointMarkers.add(shape);
                } else if (shape.getType() == DiagramShape.TYPE_CUSTOM) {
                    throw new RuntimeException("Not yet implemented");
                } else if (!shape.getPoints().isEmpty()) {
                    otherShapes.add(shape);
                }
            }

            Collections.sort(storageShapes, new Shape3DOrderingComparator());
            Collections.sort(otherShapes, new ShapeAreaComparator());

            if (options.dropShadows()) {
                for (DiagramShape storageShape : storageShapes) {
                    if (!storageShape.isStrokeDashed()) {
                        SVGPath path = new SVGPath(storageShape.makeIntoRenderPath(diagram, options));
                        if (path.isClosed) {
                            writeShadowPath(writer, path);
                        }
                    }
                }

                for (DiagramShape otherShape : otherShapes) {
                    if (!otherShape.isStrokeDashed() && otherShape.getType() != DiagramShape.TYPE_ARROWHEAD) {
                        SVGPath path = new SVGPath(otherShape.makeIntoRenderPath(diagram, options));
                        if (path.isClosed) {
                            writeShadowPath(writer, path);
                        }
                    }
                }
            }

            for (DiagramShape shape : storageShapes) {
                SVGPath commands = new SVGPath(shape.makeIntoRenderPath(diagram, options));

                String fill = "none";
                String color = "white";

                if (!shape.isStrokeDashed()) {
                    if (shape.getFillColor() != null) {
                        fill = colorToHex(shape.getFillColor());
                    } else {
                        fill = colorToHex(Color.white);
                    }
                }

                writePath(writer, shape, commands, color, fill);
                writePath(writer, shape, commands, colorToHex(shape.getStrokeColor()), "none");
            }

            for (DiagramShape shape : otherShapes) {
                writePath(writer, shape, new SVGPath(shape.makeIntoRenderPath(diagram, options)));
            }

            writePointMarkers(writer, pointMarkers);
        }

        private void writePath(XMLStreamWriter writer, DiagramShape shape, SVGPath commands) throws XMLStreamException
        {
            String fill = "none";

            if (shape.isClosed() && !shape.isStrokeDashed()) {
                if (shape.getFillColor() != null) {
                    fill = colorToHex(shape.getFillColor());
                } else {
                    fill = "white";
                }

                if (shape.getType() == DiagramShape.TYPE_ARROWHEAD) {
                    writePath(writer, shape, commands, "none", fill);
                }
            }

            if (shape.getType() != DiagramShape.TYPE_ARROWHEAD) {
                writePath(writer, shape, commands, colorToHex(shape.getStrokeColor()), fill);
            }
        }

        private void writeShadowPath(XMLStreamWriter writer, SVGPath path) throws XMLStreamException
        {
            writer.writeStartElement("path");
            writer.writeAttribute("stroke", colorToHex(new Color(150, 150, 150)));
            writer.writeAttribute("fill", colorToHex(new Color(150, 150, 150)));
            writer.writeAttribute("filter", "url(#shadowBlur)");
            writer.writeAttribute("d", path.svgPath);
            writer.writeEndElement();
        }

        private void writePointMarkers(XMLStreamWriter writer, ArrayList<DiagramShape> pointMarkers) throws XMLStreamException
        {
            for (DiagramShape shape : pointMarkers) {
                GeneralPath path = shape.makeIntoRenderPath(diagram, options);

                String fill;
                if (shape.getFillColor() != null) {
                    fill = colorToHex(shape.getFillColor());
                } else {
                    fill = "white";
                }

                writePath(writer, shape, new SVGPath(path), colorToHex(shape.getStrokeColor()), fill);
            }
        }

        private void writePath(XMLStreamWriter writer, DiagramShape shape, SVGPath path, String stroke, String fill) throws XMLStreamException
        {
            writer.writeStartElement("path");
            writer.writeAttribute("stroke", stroke);

            if (shape.isStrokeDashed()) {
                writer.writeAttribute("stroke-width", Float.toString(strokeWeight));
                writer.writeAttribute("stroke-dasharray", String.format("%f,%f", dashInterval, dashInterval));
                writer.writeAttribute("stroke-miterlimit", "0");
                writer.writeAttribute("stroke-linecap", "butt");
                writer.writeAttribute("stroke-linejoin", "round");
            } else {
                writer.writeAttribute("stroke-width", Float.toString(strokeWeight));
                writer.writeAttribute("stroke-linecap", "round");
                writer.writeAttribute("stroke-linejoin", "round");
            }

            writer.writeAttribute("fill", fill);
            writer.writeAttribute("d", path.svgPath);

            writer.writeEndElement();
        }

        private void writeBackgroundLayer(XMLStreamWriter writer) throws XMLStreamException
        {
            Color color = options.getBackgroundColor();

            if (color.getAlpha() == 0) return;

            writer.writeStartElement("rect");
            writer.writeAttribute("x", "0");
            writer.writeAttribute("y", "0");
            writer.writeAttribute("width", Integer.toString(diagram.getWidth()));
            writer.writeAttribute("height", Integer.toString(diagram.getHeight()));
            writer.writeAttribute("style", "fill: " + colorToHex(color));
            writer.writeEndElement();
        }

        private void writeTexts(XMLStreamWriter writer) throws XMLStreamException
        {
            for (DiagramText diagramText : diagram.getTextObjects()) {
                Font font = diagramText.getFont();
                String text = diagramText.getText();

                int xPos = diagramText.getXPos();
                int yPos = diagramText.getYPos();

                writeText(writer, text, xPos, yPos, font, diagramText.getColor());

                if (diagramText.hasOutline()) {
                    Color outlineColor = diagramText.getOutlineColor();

                    writeText(writer, text, xPos + 1, yPos, font, outlineColor);
                    writeText(writer, text, xPos - 1, yPos, font, outlineColor);
                    writeText(writer, text, xPos, yPos + 1, font, outlineColor);
                    writeText(writer, text, xPos, yPos - 1, font, outlineColor);
                }
            }
        }

        private void writeText(XMLStreamWriter writer, String text, int xPos, int yPos, Font font, Color color) throws XMLStreamException
        {
            writer.writeStartElement("text");

            writer.writeAttribute("x", Integer.toString(xPos));
            writer.writeAttribute("y", Integer.toString(yPos));
            writer.writeAttribute("font-family", options.getFontFamily());
            writer.writeAttribute("font-size", Integer.toString(font.getSize()));
            writer.writeAttribute("stroke", "none");
            writer.writeAttribute("fill", colorToHex(color));

            writer.writeCData(text);

            writer.writeEndElement();
            /* Prefer normal font weight
            if (font.isBold()) {
                style = " font-weight='bold'";
            }
            */
        }

        private static String colorToHex(Color color)
        {
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }
    }

    private static class SVGPath
    {
        final String svgPath;
        final boolean isClosed;

        SVGPath(GeneralPath path)
        {
            boolean closed = false;

            float[] coords = new float[6];

            StringBuilder builder = new StringBuilder();

            PathIterator pathIterator = path.getPathIterator(null);

            while (!pathIterator.isDone()) {
                switch (pathIterator.currentSegment(coords)) {
                    case PathIterator.SEG_MOVETO:
                        builder.append("M" + coords[0] + " " + coords[1] + " ");
                        break;
                    case PathIterator.SEG_LINETO:
                        builder.append("L" + coords[0] + " " + coords[1] + " ");
                        break;
                    case PathIterator.SEG_QUADTO:
                        builder.append("Q" + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " ");
                        break;
                    case PathIterator.SEG_CUBICTO:
                        builder.append("C" + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " " + coords[5] + " ");
                        break;
                    case PathIterator.SEG_CLOSE:
                        builder.append("z");
                        closed = true;
                        break;
                }

                pathIterator.next();

            }

            isClosed = closed;
            svgPath = builder.toString();
        }
    }
}
