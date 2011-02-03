package dataflow.datastructures;

import java.util.Vector;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

import ui.panels.file.FileRow;


/**
 * Cell containing the files.
 * @author Joris Gillis
 */
public class FilesCell extends DefaultCell {
	
	private Vector<FileRow> files;
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getName()
	 */
	public String getName() {
		return "files";
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#recalculate(dataflow.datastructures.Cell)
	 */
	public void recalculate(Vector<String> children) 
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#getValue()
	 */
	public Vector<FileRow> getValue() {
		return files;
	}
	
	/*
	 * (non-Javadoc)
	 * @see dataflow.datastructures.Cell#setValue(java.lang.Object)
	 */
	public void setValue( Vector<FileRow> value ) throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		if( files == null || !equals(files, value) ) {
			// effectively copy the Vector
			files = new Vector<FileRow>();
			for( int i = 0; i < value.size(); i++ )
				files.add(new FileRow(value.get(i).getOrder(), value.get(i).getFile(), value.get(i).getDataSet()));
		}
	}
	
	/**
	 * Are these two arrays of files equal?
	 * @param f1	first array
	 * @param f2	second array
	 * @return		equal?
	 */
	protected boolean equals( Vector<FileRow> f1, Vector<FileRow> f2 ) {
		boolean equal = f1 != null && f2 != null && f1.size() == f2.size();
		
		for( int i = 0; equal && i < f1.size(); i++ )
			equal = f1.get(i).equals(f2.get(i));
		
		return equal;
	}
}
