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

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 
 * @author Efstathios Sideris
 */
public class ConversionOptions {
	
	public ProcessingOptions processingOptions =
		new ProcessingOptions();
	public RenderingOptions renderingOptions =
		new RenderingOptions();
		
	public void setDebug(boolean value){
		processingOptions.setPrintDebugOutput(value);
		renderingOptions.setRenderDebugLines(value);
	}
	
	public ConversionOptions(){}

    /** Parse a color from a 6- or 8-digit hex string.  For example, FF0000 is red.
     *  If eight digits, last two digits are alpha. */
    public static Color parseColor(String hexString) {
        if(hexString.length() == 6) {
            return new Color(Integer.parseInt(hexString, 16));
        } else if(hexString.length() == 8) {
            return new Color(
                Integer.parseInt(hexString.substring(0,2), 16),
                Integer.parseInt(hexString.substring(2,4), 16),
                Integer.parseInt(hexString.substring(4,6), 16),
                Integer.parseInt(hexString.substring(6,8), 16)
            );
        } else {
            throw new IllegalArgumentException("Cannot interpret \""+hexString+"\" as background colour. It needs to be a 6- or 8-digit hex number, depending on whether you have transparency or not (same as HTML).");
        }
    }
	
	public ConversionOptions(Map<String, String> cmdLine) throws UnsupportedEncodingException{
		
		processingOptions.setVerbose(cmdLine.containsKey("verbose"));
		renderingOptions.setDropShadows(!cmdLine.containsKey("no-shadows"));
		this.setDebug(cmdLine.containsKey("debug"));
		processingOptions.setOverwriteFiles(cmdLine.containsKey("overwrite"));
		
		if(cmdLine.containsKey("scale")){
			Float scale = Float.parseFloat(cmdLine.get("scale"));
			renderingOptions.setScale(scale.floatValue());
		}
		
		processingOptions.setAllCornersAreRound(cmdLine.containsKey("round-corners"));
		processingOptions.setPerformSeparationOfCommonEdges(!cmdLine.containsKey("no-separation"));
		renderingOptions.setAntialias(!cmdLine.containsKey("no-antialias"));
		renderingOptions.setFixedSlope(cmdLine.containsKey("fixed-slope"));

		if(cmdLine.containsKey("background")) {
			String b = cmdLine.get("background");
            Color background = parseColor(b);
			renderingOptions.setBackgroundColor(background);
		}
		
		if(cmdLine.containsKey("transparent")) {
			renderingOptions.setBackgroundColor(new Color(0,0,0,0));
		}

		if(cmdLine.containsKey("tabs")){
			Integer tabSize = Integer.parseInt(cmdLine.get("tabs"));
			int tabSizeValue = tabSize.intValue();
			if(tabSizeValue < 0) tabSizeValue = 0;
			processingOptions.setTabSize(tabSizeValue);
		}

		String encoding = cmdLine.get("encoding");
		if(encoding != null){
			new String(new byte[2], encoding);
			processingOptions.setCharacterEncoding(encoding);
		}
	}
}


// may be supported at a later date:
//String exportFormat = (String) cmdLine.getOptionValue("format");
//if(exportFormat != null){
//	exportFormat = exportFormat.toLowerCase();
//	if(exportFormat == "jpeg" || exportFormat == "jpg"){
//		processingOptions.setExportFormat(ProcessingOptions.FORMAT_JPEG);
//	} else if(exportFormat == "png"){
//		processingOptions.setExportFormat(ProcessingOptions.FORMAT_PNG);
//	} else if(exportFormat == "gif"){
//		processingOptions.setExportFormat(ProcessingOptions.FORMAT_GIF);
//	}
//}
//
//String colorCodeMode = (String) cmdLine.getOptionValue("color-codes");
//if(colorCodeMode != null){
//	if(colorCodeMode.equals("use"))
//		processingOptions.setColorCodesProcessingMode(ProcessingOptions.USE_COLOR_CODES);
//	else if(colorCodeMode.equals("ignore"))
//		processingOptions.setColorCodesProcessingMode(ProcessingOptions.IGNORE_COLOR_CODES);
//	else if(colorCodeMode.equals("render"))
//		processingOptions.setColorCodesProcessingMode(ProcessingOptions.RENDER_COLOR_CODES);
//}
//
//String tagsMode = (String) cmdLine.getOptionValue("tags");
//if(tagsMode != null){
//	if(tagsMode.equals("use"))
//		processingOptions.setTagProcessingMode(ProcessingOptions.USE_TAGS);
//	else if(tagsMode.equals("ignore"))
//		processingOptions.setTagProcessingMode(ProcessingOptions.IGNORE_TAGS);
//	else if(tagsMode.equals("render"))
//		processingOptions.setTagProcessingMode(ProcessingOptions.RENDER_TAGS);
//}
//
//
//String markupMode = (String) cmdLine.getOptionValue("markup");
//if(markupMode != null){
//	if(markupMode.equals("use")){
//		processingOptions.setColorCodesProcessingMode(ProcessingOptions.USE_COLOR_CODES);
//		processingOptions.setTagProcessingMode(ProcessingOptions.USE_TAGS);
//	} else if(markupMode.equals("ignore")){
//		processingOptions.setColorCodesProcessingMode(ProcessingOptions.IGNORE_COLOR_CODES);
//		processingOptions.setTagProcessingMode(ProcessingOptions.IGNORE_TAGS);
//	} else if(markupMode.equals("render")){
//		processingOptions.setColorCodesProcessingMode(ProcessingOptions.RENDER_COLOR_CODES);
//		processingOptions.setTagProcessingMode(ProcessingOptions.RENDER_TAGS);
//	}
//}