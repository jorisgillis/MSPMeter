package ui.panels.file;

import java.io.File;

/**
 * Class functioning as a container for the FileTableModel.
 * @author Joris Gillis
 */
public class FileRow implements Comparable<FileRow> {
	
	/** Order of the file */
	private int order;
	/** Path to the file */
	private File file;
	/** Annotation, used in the chart */
	private String dataset;
	
	
	/**
	 * Construct a new row
	 * @param order		order
	 * @param file		the file
	 * @param dataset	annotation used in the chart
	 */
	public FileRow(int order, File file, String dataset) {
		this.order = order;
		this.file = file;
		this.dataset = dataset;
	}


	/**
	 * @return the order
	 */
	public int getOrder() {
		return order;
	}


	/**
	 * @param order the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}


	/**
	 * @return the filepath
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @return the annotation
	 */
	public String getDataSet() {
		return dataset;
	}


	/**
	 * @param annotation the annotation to set
	 */
	public void setDataSet(String annotation) {
		this.dataset = annotation;
	}
	
	/**
	 * Decrease the order by 1
	 */
	public void oneDown() {
		if( order > 0 )
			order--;
	}
	
	/**
	 * Increase the order by 1
	 */
	public void oneUp() {
		order++;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(FileRow fr) {
		if( order - fr.order == 0 ) {
			if( file.compareTo(fr.file) == 0 ) {
				return dataset.compareTo(fr.dataset);
			} else
				return file.compareTo(fr.file);
		} else
			return order - fr.order;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof FileRow ) {
			FileRow fr = (FileRow)obj;
			return fr.dataset.equals(dataset) && fr.file.equals(file) && fr.order == order;
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "("+ order +", " + file.getName() +", " + dataset +")";
	}
}