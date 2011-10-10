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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import ui.panels.equivalences.CategoryEquivalencesPanel;
import ui.panels.equivalences.LemmaEquivalencesPanel;
import ui.panels.file.FilePanel;
import ui.panels.file.FileRow;
import ui.panels.file.LineParsingPanel;
import ui.panels.version.VersionPanel;


/**
 * Read in the XML file of an existing project. 
 * @author Joris Gillis
 */
public class ProjectReader implements ContentHandler {
	
	//-- Variables to contain information from XML file
	protected Vector<FileRow> files;
	protected FileRow currentFR;
	protected String fileName = "";
	
	protected String firstSeparator;
	protected String secondSeparator;
	
	protected String lemmaEquivalencesFile;
	
	protected String categoryEquivalencesFile;
	
	protected boolean weighting;
	protected boolean entropy;
	protected int mode;
	protected int subSampleSizeMode;
	protected int subSampleSize;
	protected int numberOfSamplesMode;
	protected double numberOfSamples;
	
	
	//-- State variables: specifying in which element we are
	protected boolean sFiles;
	protected boolean sFilesName;
	protected boolean sFilesOrder;
	protected boolean sFilesAnnotation;
	
	protected boolean sLinestructures;
	protected boolean sFirstSeparator;
	protected boolean sSecondSeparator;
	
	protected boolean sLemmaEquivalences;
	protected boolean sLemmaEquivalencesFile;
	
	protected boolean sCategoryEquivalences;
	protected boolean sCategoryEquivalencesFile;
	
	protected boolean sVersion;
	protected boolean sVersionWeighting;
	protected boolean sVersionEntropy;
	protected boolean sVersionMode;
	protected boolean sVersionSubSampleSizeMode;
	protected boolean sVersionSubSampleSize;
	protected boolean sVersionNumberOfSamplesMode;
	protected boolean sVersionNumberOfSamples;
	
	
	//-- Panels
	protected FilePanel fp;
	protected LineParsingPanel lpp;
	protected LemmaEquivalencesPanel lep;
	protected CategoryEquivalencesPanel cep;
	protected VersionPanel vp;
	
	// everywhere the same
	protected static Logger logger = Logger.getLogger(ProjectReader.class);
	
	
	/**
	 * Create a new ProjectReader, provided with the panels. The panels are necessary to 
	 * load the values into the program.
	 * @param fp	file panel
	 * @param lpp	line parsing panel
	 * @param lep	lemma equivalences panel
	 * @param cep	category equivalences panel
	 * @param vp	version panel
	 */
	public ProjectReader( FilePanel fp, LineParsingPanel lpp, LemmaEquivalencesPanel lep, 
			CategoryEquivalencesPanel cep, VersionPanel vp ) {
		this.fp		= fp;
		this.lpp	= lpp;
		this.lep	= lep;
		this.cep	= cep;
		this.vp		= vp;
	}
	
	
	/**
	 * Reads in the project stored in the given file.
	 * @param fileName	file from which we will read the project
	 */
	public void readProject( String fileName ) throws SAXException, IOException {
		// Validation
		boolean validated = false;
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(new URL("http://www.clips.ua.ac.be/msp/project.xsd"));
			Validator validator = schema.newValidator();
			validator.validate(new SAXSource(new InputSource(new FileReader(fileName))), null);
			validated = true;
		} catch( Exception e ) {
			logger.error("Fautl while reading in project file: "+ fileName +"\n"+ e.getMessage());
			throw new SAXException("The specified project file does not have a correct format.");
		}
		
		if( validated ) {
			// Parsing
			SAXParser parser = new SAXParser();
			parser.setContentHandler(this);
			
			parser.parse(fileName);
		}
	}
	
	
	//===========================================================================
	// SAX PARSING
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		String data = new String(ch).substring(start, start+length);
		
		if( sFiles ) {
			if( sFilesName )
				// Sometimes the data gets chopped up, thus collect and 
				// instantiate file if all data is received!
				fileName += data;
			else if( sFilesOrder )
				currentFR.setOrder(Integer.parseInt(data));
			else if( sFilesAnnotation )
				currentFR.setDataSet(data);
		} else if( sLinestructures ) {
			if( sFirstSeparator )
				firstSeparator += data.replaceAll("\\\\", "");
			else if( sSecondSeparator )
				secondSeparator += data.replaceAll("\\\\", "");
		} else if( sLemmaEquivalences && sLemmaEquivalencesFile )
			lemmaEquivalencesFile = data;
		else if( sCategoryEquivalences && sCategoryEquivalencesFile )
			categoryEquivalencesFile = data;
		else if( sVersion ) {
			if( sVersionWeighting )
				weighting = Boolean.parseBoolean(data);
			else if( sVersionEntropy )
				entropy = Boolean.parseBoolean(data);
			else if( sVersionMode )
				mode = Integer.parseInt(data);
			else if( sVersionSubSampleSizeMode )
				subSampleSizeMode = Integer.parseInt(data);
			else if( sVersionSubSampleSize )
				subSampleSize = Integer.parseInt(data);
			else if( sVersionNumberOfSamplesMode )
				numberOfSamplesMode = Integer.parseInt(data);
			else if( sVersionNumberOfSamples )
				numberOfSamples = Double.parseDouble(data);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		// informing the panels
		fp.addFiles(files);
		lpp.setFirstSeparator(firstSeparator);
		lpp.setSecondSeparator(secondSeparator);
		fp.apply();
		lpp.apply();
		
		lep.setFile(lemmaEquivalencesFile);
		cep.setFile(categoryEquivalencesFile);
		lep.apply();
		cep.apply();
		
		vp.setWeighting(weighting);
		vp.setEntropy(entropy);
		vp.setMode(mode);
		vp.setSubSampleSizeMode(subSampleSizeMode);
		vp.setSubSampleSize(subSampleSize);
		vp.setNumberOfSamplesMode(numberOfSamplesMode);
		vp.setNumberOfSamples(numberOfSamples);
		vp.apply();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if( sFiles ) {
			// files
			if( name.equals("filename") ) {
				currentFR = new FileRow(0, new File(fileName), "");
				sFilesName = false;
				fileName = "";
			} else if( name.equals("order") )
				sFilesOrder = false;
			else if( name.equals("annotation") ) {
				sFilesAnnotation = false;
				files.add(currentFR);
				currentFR = null;
			}
			else if( name.equals("files") )
				sFiles = false;
		}
		else if( sLinestructures ) {
			// line structures
			if( name.equals("firstSeparator") )
				sFirstSeparator = false;
			else if( name.equals("secondSeparator") )
				sSecondSeparator = false;
			else if( name.equals("linestructures") )
				sLinestructures = false;
		}
		else if( sLemmaEquivalences ) {
			// lemma equivalences
			if( name.equals("file") )
				sLemmaEquivalencesFile = false;
			else if( name.equals("lemmaequivalences") )
				sLemmaEquivalences = false;
		}
		else if( sCategoryEquivalences ) {
			// category equivalences
			if( name.equals("file") )
				sCategoryEquivalencesFile = false;
			else if( name.equals("categoryequivalences") )
				sCategoryEquivalences = false;
		}
		else if( sVersion ) {
			// version
			if( name.equals("weighting") )
				sVersionWeighting = false;
			else if( name.equals("entropy") )
				sVersionEntropy = false;
			else if( name.equals("mode") )
				sVersionMode = false;
			else if( name.equals("subsamplesizemode") )
				sVersionSubSampleSizeMode = false;
			else if( name.equals("subsamplesize") )
				sVersionSubSampleSize = false;
			else if( name.equals("numberofsamplesmode") )
				sVersionNumberOfSamplesMode = false;
			else if( name.equals("numberofsamples") )
				sVersionNumberOfSamples = false;
			else if( name.equals("version") )
				sVersion = false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
		if( sFiles || name.equals("files") ) {
			// files
			sFiles = true;
			
			if( name.equals("filename") )
				sFilesName = true;
			else if( name.equals("order") )
				sFilesOrder = true;
			else if( name.equals("annotation") )
				sFilesAnnotation = true;
		}
		else if( sLinestructures || name.equals("linestructures") ) {
			// line structures
			sLinestructures = true;
			
			if( name.equals("firstSeparator") )
				sFirstSeparator = true;
			else if( name.equals("secondSeparator") )
				sSecondSeparator = true;
		}
		else if( sLemmaEquivalences || name.equals("lemmaequivalences") ) {
			// lemma equivalences
			sLemmaEquivalences = true;
			
			if( name.equals("file") )
				sLemmaEquivalencesFile = true;
		}
		else if( sCategoryEquivalences || name.equals("categoryequivalences") ) {
			// category equivalences
			sCategoryEquivalences = true;
			
			if( name.equals("file") )
				sCategoryEquivalencesFile = true;
		}
		else if( sVersion || name.equals("version") ) {
			// version
			sVersion = true;
			
			if( name.equals("weighting") )
				sVersionWeighting = true;
			else if( name.equals("entropy") )
				sVersionEntropy = true;
			else if( name.equals("mode") )
				sVersionMode = true;
			else if( name.equals("subsamplesizemode") )
				sVersionSubSampleSizeMode = true;
			else if( name.equals("subsamplesize") )
				sVersionSubSampleSize = true;
			else if( name.equals("numberofsamplesmode") )
				sVersionNumberOfSamplesMode = true;
			else if( name.equals("numberofsamples") )
				sVersionNumberOfSamples = true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		// initialize the variables
		files		= new Vector<FileRow>();
		currentFR	= null;
		
		firstSeparator		= new String();
		secondSeparator		= new String();
		
		lemmaEquivalencesFile		= null;
		categoryEquivalencesFile	= null;
		
		weighting	= false;
		entropy		= false;
		mode 		= 0;
		subSampleSize	= 1;
		
		// resetting the states
		sFiles				= false;
		sFilesName			= false;
		sFilesOrder			= false;
		sFilesAnnotation	= false;
		
		sLinestructures		= false;
		sFirstSeparator		= false;
		sSecondSeparator	= false;
		
		sLemmaEquivalences		= false;
		sLemmaEquivalencesFile	= false;
		
		sCategoryEquivalences		= false;
		sCategoryEquivalencesFile	= false;
		
		sVersion					= false;
		sVersionWeighting			= false;
		sVersionEntropy				= false;
		sVersionMode				= false;
		sVersionSubSampleSizeMode	= false;
		sVersionSubSampleSize		= false;
		sVersionNumberOfSamplesMode	= false;
		sVersionNumberOfSamples		= false;
	}
	
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}
	public void endPrefixMapping(String prefix) throws SAXException {}
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
	public void processingInstruction(String target, String data) throws SAXException {}
	public void setDocumentLocator(Locator locator) {}
	public void skippedEntity(String name) throws SAXException {}
}
