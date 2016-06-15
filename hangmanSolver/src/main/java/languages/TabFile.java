package languages;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import common.*;

public class TabFile {

	private String[] columnHeaders;
	private ArrayList<String[]> values = new ArrayList<String[]>();

	public TabFile(URL file) throws IOException {
		readFile(file);
	}

	public void readFile(URL file) throws IOException {

		// open the file
		Scanner scan = new Scanner(file.openStream());

		// get the column headers
		columnHeaders = scan.nextLine().split("	");

		while (scan.hasNextLine()) {
			values.add(scan.nextLine().split("	"));
		}

		scan.close();
	}

	/**
	 * Returns the column header at the given index
	 * 
	 * @param index
	 *            The index of the column header to be returned
	 * @return The column header of the column with the specified index
	 */
	public String getColumnHeader(int index) {
		return columnHeaders[index];
	}

	/**
	 * Returns the column count of the *.tab file
	 * 
	 * @return The column count of this file
	 */
	public int getColumnCount() {
		return columnHeaders.length;
	}

	/**
	 * Returns the row count of the *.tab file excluding the column headers.
	 * 
	 * @return The row count of this file
	 */
	public int getRowCount() {
		return values.size();
	}

	/**
	 * Returns the value at the specified position in the grid
	 * 
	 * @param row
	 *            The row index of the desired value
	 * @param column
	 *            The column index of the desired value
	 * @return The value at the specified position
	 */
	public String getValueAt(int row, int column) {
		return values.get(row)[column];
	}

	public List<String> getValuesWithLength(int column, int length) {
		List<String> res = new ArrayList<String>();

		for (int i = 0; i < this.getRowCount(); i++) {
			if (this.getValueAt(i, column).length() == length) {
				res.add(this.getValueAt(i, column));
			}
		}

		return res;
	}

	public String getValueWithHighestCorrelation(int column, String value, List<String> ignoredWords) {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		AtomicInteger currentIndex = new AtomicInteger(0);
		AtomicInteger maxIndex = new AtomicInteger(-1);
		AtomicDouble maxCorr = new AtomicDouble(-1);

		for (int i=0; i<Config.parallelThreadCount;i++) {
			threads.add(new Thread() {
				@Override
				public void run() {
					int index = currentIndex.getAndIncrement();
					while (index < getRowCount()) {
						if (value.length() == getValueAt(index, column).length() && !ignoredWords.contains(getValueAt(index, column))) {
							double corr = stringCorrelation(value, getValueAt(index, column));

							if (corr > maxCorr.get()) {
								maxCorr.set(corr);
								maxIndex.set(index);
							}
						}
						
						// Grab the next index
						index = currentIndex.getAndIncrement();
					}
				}
			});
			threads.get(i).start();
		}
		
		// Wait for threads
		for (int i=0; i<Config.parallelThreadCount;i++){
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return getValueAt(maxIndex.get(), column);
	}

	/**
	 * Compares two strings and returns how equal they are as a percentage. The
	 * two strings must be of equal length.
	 * 
	 * @param str1
	 *            The first string to compare
	 * @param str2
	 *            The second string to compare
	 * @return {@code 0} if the two strings are completely different, {@code 1},
	 *         if they are completely equal and values in between if they are
	 *         neither completely equal nor completely different.
	 */
	public static double stringCorrelation(String str1, String str2) {
		if (str1.length() != str2.length()) {
			throw new RuntimeException("str1 and str2 must be of equal length");
		}

		double equalLetters = 0;

		for (int i = 0; i < str1.length(); i++) {
			if (str1.substring(i, i + 1).equalsIgnoreCase(str2.substring(i, i + 1))) {
				equalLetters = equalLetters + 1;
			}
		}

		return equalLetters / str1.length();
	}
}
