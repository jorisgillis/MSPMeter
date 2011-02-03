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
