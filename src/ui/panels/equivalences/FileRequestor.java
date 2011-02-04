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


package ui.panels.equivalences;

import java.io.File;

/**
 * The one that requests a file from a FileProvider. The FileRequestor can ask the FileProvider to
 * deliver him a file. When the FileProvider has a file, he will send it to the FileRequestor.
 * @author Joris Gillis
 */
public interface FileRequestor {
	
	/**
	 * Receive a file.
	 * @param file	requested file
	 */
	public void receiveFile( File file );
	
}
