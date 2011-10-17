/*
 * Copyright 2010 MSPMeter
 *
 * Licensed under the EUPL, Version 1.1 or Ð as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl.html
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */


package io.project;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import ui.panels.file.FileRow;
import dataflow.Grid;
import dataflow.datastructures.CategoryFileCell;
import dataflow.datastructures.FilesCell;
import dataflow.datastructures.FirstSeparatorCell;
import dataflow.datastructures.LemmaFileCell;
import dataflow.datastructures.SecondSeparatorCell;
import dataflow.datastructures.UseInMSPCell;
import dataflow.datastructures.VersionCell;

/**
 * Class for writing out project files.<br />
 * A word on the file specifications: see xsd!
 * @author Joris Gillis
 */
public class ProjectWriter {
	
	/** The place we write to */
	protected String projectFile = null;
	
	
	// new line character(s)
	protected final static String nl = System.getProperty("line.separator");
	
	
	/**
	 * Creates a new writer, writing to the specified file
	 * @param projectFile	to where do we write?
	 */
	public ProjectWriter( String projectFile ) {
		this.projectFile = projectFile;
	}
	
	
	/**
	 * Writes out the information stored in the Controller.
	 */
	public void writeProject() throws FileNotFoundException, IOException {
		if( projectFile != null ) {
			//- Tapping into the Grid!
			Grid grid = Grid.instance();
			
			//- opening the writer
			BufferedWriter w = new BufferedWriter(new FileWriter(projectFile));
			
			//- create a xml string
			w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			w.newLine();
			w.newLine();
			
			//- opening the project
			w.write("<project>"); w.newLine();
			
			
			// 1. Files: saving the list of all files
			Vector<FileRow> files = ((FilesCell)grid.getCell("files")).getValue();
			w.write("\t<files>"); w.newLine();
			if( files != null ) {
				for( int i = 0; i < files.size(); i++ ) {
					w.write("\t\t<file>"); w.newLine();
						w.write("\t\t\t<filename>"+ files.get(i).getFile().getAbsolutePath() +"</filename>"); w.newLine();
						w.write("\t\t\t<order>"+ files.get(i).getOrder() +"</order>"); w.newLine();
						w.write("\t\t\t<annotation>"+ files.get(i).getDataSet() +"</annotation>"); w.newLine();
					w.write("\t\t</file>"); w.newLine();
				}
			}
			w.write("\t</files>"); w.newLine();
			
			
			// 2. Line structures
			// get the strings
			String firstSep = ((FirstSeparatorCell)grid.getCell("firstSeparator")).getValue();
			String secondSep = ((SecondSeparatorCell)grid.getCell("secondSeparator")).getValue();
			
			// replace & by &amp;
			firstSep = firstSep.replaceAll("&", "&amp;");
			secondSep = secondSep.replaceAll("&", "&amp;");
			
			// writing
			w.write("\t<linestructures>"); w.newLine();
			w.write("\t\t<firstSeparator>"+ firstSep +"</firstSeparator>"); w.newLine();
			w.write("\t\t<secondSeparator>"+ secondSep +"</secondSeparator>"); w.newLine();
			w.write("\t</linestructures>"); w.newLine();
			
			
			// 3. Lemma Equivalences File
			String lemmaFile = ((LemmaFileCell)grid.getCell("lemmaFile")).getValue();
			if( lemmaFile != null ) {
				w.write("\t<lemmaequivalences>"); w.newLine();
				w.write("\t\t<file>"+ lemmaFile +"</file>"); w.newLine();
				w.write("\t\t<useInMSP>"+ ((UseInMSPCell)grid.getCell("useInMSP")).boolValue() +"</useInMSP>"); w.newLine();
				w.write("\t</lemmaequivalences>"); w.newLine();
			} else {
				w.write("\t<lemmaequivalences />"); w.newLine();
			}
			
			
			// 4. Category Equivalences File
			String categoryFile = ((CategoryFileCell)grid.getCell("categoryFile")).getValue();
			if( categoryFile != null ) {
				w.write("\t<categoryequivalences>"); w.newLine();
				w.write("\t\t<file>"+ categoryFile +"</file>"); w.newLine();
				w.write("\t</categoryequivalences>"); w.newLine();
			} else {
				w.write("\t<categoryequivalences />"); w.newLine();
			}
			
			
			// 5. Version
			VersionCell version = (VersionCell)grid.getCell("version");
			w.write("\t<version>"); w.newLine();
			w.write("\t\t<weighting>"+ version.isWeighting() + "</weighting>"); w.newLine();
			w.write("\t\t<entropy>" + version.isEntropy() + "</entropy>"); w.newLine();
			w.write("\t\t<mode>" + version.getMode() + "</mode>"); w.newLine();
			w.write("\t\t<subsamplesizemode>" + version.getSubSampleMode() + "</subsamplesizemode>"); w.newLine();
			w.write("\t\t<subsamplesize>" + version.getSubSampleSize() + "</subsamplesize>"); w.newLine();
			w.write("\t\t<numberofsamplesmode>" + version.getNumberOfSamplesMode() + "</numberofsamplesmode>"); w.newLine();
			w.write("\t\t<numberofsamples>" + version.getNumberOfSamples() + "</numberofsamples>"); w.newLine();
			w.write("\t</version>"); w.newLine();
			
			//- closing of the project
			w.write("</project>"); w.newLine();
			
			//- closing the writer
			w.close();
		}
	}
	
}
