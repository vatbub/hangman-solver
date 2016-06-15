package languages;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class TabFile {
	
	private String[] columnHeaders;
	private ArrayList<String[]> values = new ArrayList<String[]>();
	
	public TabFile(URL file) throws IOException{
		readFile(file);
	}
	
	public void readFile(URL file) throws IOException{
		
		// open the file
		Scanner scan = new Scanner(file.openStream());
		
		// get the column headers
		columnHeaders = scan.nextLine().split("	");
		
		while (scan.hasNextLine()){
			values.add(scan.nextLine().split("	"));
		}
		
		scan.close();
	}
	
	/**
	 * Returns the column header at the given index
	 * @param index The index of the column header to be returned
	 * @return The column header of the column with the specified index
	 */
	public String getColumnHeader(int index){
		return columnHeaders[index];
	}
	
	/**
	 * Returns the column count of the *.tab file
	 * @return The column count of this file
	 */
	public int getColumnCount(){
		return columnHeaders.length;
	}
	
	/**
	 * Returns the row count of the *.tab file excluding the column headers.
	 * @return The row count of this file
	 */
	public int getRowCount(){
		return values.size();
	}
	
	/**
	 * Returns the value at the specified position in the grid
	 * @param row The row index of the desired value
	 * @param column The column index of the desired value
	 * @return The value at the specified position
	 */
	public String getValueAt(int row, int column){
		return values.get(row)[column];
	}
}
