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


package msp;

import msp.defaults.Defaults;

import org.apache.log4j.Logger;

import ui.MSPMeterWindow;
import ui.results.ResultsWindow;
import dataflow.Grid;
import dataflow.datastructures.CumulatedDataCubeCell;
import dataflow.datastructures.FirstSeparatorCell;
import dataflow.datastructures.LemmadDataCubeCell;
import dataflow.datastructures.OriginalDataCubeCell;
import dataflow.datastructures.ResultsCell;
import dataflow.datastructures.SecondSeparatorCell;
import dataflow.datastructures.VersionCell;
import dataflow.datastructures.WorkingDataCubeCell;

/**
 * The one booting everything up.
 * @author Joris Gillis
 */
public class Architect {
	
	// everywhere the same
	private static Logger logger = Logger.getLogger(Architect.class);
	
	/**
	 * Constructing a new MSPMeter
	 */
	public Architect() {
		//- Creating the Grid
		Grid grid = Grid.instance();
		
		//- Setting up the GUI
		MSPMeterWindow window = new MSPMeterWindow();
		ResultsWindow resultsWindow = new ResultsWindow();
		grid.addProgressController(resultsWindow);
		grid.setMSPMeterWindow(window);
		
		//- Adding the (internal) cells
		OriginalDataCubeCell originalDC = new OriginalDataCubeCell();
		LemmadDataCubeCell lemmadDC = new LemmadDataCubeCell();
		CumulatedDataCubeCell cumulatedDC = new CumulatedDataCubeCell();
		WorkingDataCubeCell workingDC = new WorkingDataCubeCell();
		ResultsCell results = new ResultsCell();
		
		grid.addCell(originalDC);
		grid.addCell(lemmadDC);
		grid.addCell(workingDC);
		grid.addCell(cumulatedDC);
		grid.addCell(results);
		
		
		//- Adding the flows
		grid.setFlow("files", originalDC.getName());
		grid.setFlow("firstSeparator", originalDC.getName());
		grid.setFlow("secondSeparator", originalDC.getName());
		
		grid.setFlow("files", "LiveLineParsing");
		grid.setFlow("firstSeparator", "LiveLineParsing");
		grid.setFlow("secondSeparator", "LiveLineParsing");
		
		grid.setFlow("lemmaEquivalences", lemmadDC.getName());
		grid.setFlow("useInMSP", lemmadDC.getName());
		grid.setFlow(originalDC.getName(), lemmadDC.getName());
		
		grid.setFlow("lemmaEquivalences", workingDC.getName());
		grid.setFlow("useInMSP", workingDC.getName());
		grid.setFlow("categoryEquivalences", workingDC.getName());
		grid.setFlow(lemmadDC.getName(), workingDC.getName());
		
		grid.setFlow(workingDC.getName(), cumulatedDC.getName());
		
		grid.setFlow("logBase", results.getName());
		grid.setFlow("version", results.getName());
		grid.setFlow(workingDC.getName(), results.getName());
		grid.setFlow(cumulatedDC.getName(), results.getName());
		
		grid.setFlow(results.getName(), "resultsTable");
		grid.setFlow(results.getName(), "resultsChart");
		
		grid.setFlow(results.getName(), "sampleInformationPanel");
		
		grid.setFlow(workingDC.getName(), "versionPanel");
		grid.setFlow(cumulatedDC.getName(), "versionPanel");
		
		grid.setFlow("lemmaEquivalences", "categoryEquivalencesPanel");
		grid.setFlow("subLemmas", "categoryEquivalencesPanel");
		grid.setFlow("allLemmas", "categoryEquivalencesPanel");
		grid.setFlow(lemmadDC.getName(), "categoryEquivalencesPanel");
		
		grid.setFlow(originalDC.getName(), "lemmaEquivalencesPanel");
		
		grid.setFlow(workingDC.getName(), "cubeInformation");
		grid.setFlow(cumulatedDC.getName(), "cubeInformation");
		
		//- Defaults
		try {
			Defaults d = Defaults.instance();
			
			// lineparsing
			((FirstSeparatorCell)grid.getCell("firstSeparator")).setValue(d.getString("firstSeparator"));
			((SecondSeparatorCell)grid.getCell("secondSeparator")).setValue(d.getString("secondSeparator"));
			
			// version
			((VersionCell)grid.getCell("version")).setValue(d.getBoolean("weighting"), d.getBoolean("entropy"),
					d.getInteger("mode"), d.getInteger("subSampleMode"), d.getInteger("subSampleSize"),
					d.getInteger("numberOfSamplesMode"), d.getDouble("numberOfSamples") );
			
		} catch( Exception e ) {
			logger.error("Defaults not set! "+ e.getMessage());
		}
		
		// we start in saved mode
		grid.saved();
	}
	
}
