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


package ui.panels.file;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Only accept files that end with .cex. That means, frequency files.
 * @author Joris Gillis
 */
public class FreqFileFilter extends FileFilter {
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) {
		String extension = getExtension(f);
		boolean accept = false;
		
		if( f != null && (
				(extension != null && 
						(extension.equals("cex") || 
						extension.equals("frq") || 
						extension.equals("freq"))) 
				|| f.isDirectory()) )
			accept = true;
		
		return accept;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Frequency Files";
	}
	
	/**
     * Get the extension of a file.
     * @param f		the file
     */  
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}
