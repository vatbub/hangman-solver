package languages;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import algorithm.HangmanSolver;
import common.*;

public class TabFile {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String targetPath = null;
		String originPath = null;

		System.out.println("Please enter the path of the original *.tab-files:");

		originPath = sc.nextLine();

		System.out.println(
				"Please enter the path where you wish to save the optimized *.tab-files (Directories will be created, existing files with same filenames will be overwritten):");

		targetPath = sc.nextLine();
		
		sc.close();

		File folder = new File(originPath);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (!file.getName().equals("LICENSE")) {
				TabFile origin;
				try {
					String originFileName = file.getAbsolutePath();
					System.out.print("Reading file '" + originFileName + "'...");
					origin = new TabFile(originFileName);
					System.out.println("Done!");
					System.out.print("Optimizing file...");
					TabFile res = TabFile.optimizeDictionaries(origin, 2, true);
					System.out.println("Done!");

					String targetFileName = targetPath + File.separator + file.getName();

					System.out.println("Saving new file as '" + targetFileName + "'...");
					res.save(targetFileName);
					System.out.println("Done!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * The column headers of this *.tab file.
	 */
	private String[] columnHeaders;
	/**
	 * The values in this *.tab file.
	 */
	private ArrayList<String[]> values = new ArrayList<String[]>();

	/**
	 * Creates a new object representation of the specified *.tab file.
	 * 
	 * @param file
	 *            The {@link URL} pointing to the desired *.tab file.
	 * @throws IOException
	 *             if the file cannot be read.
	 */
	public TabFile(URL file) throws IOException {
		readFile(file);
	}

	/**
	 * Creates an empty *.tab file.
	 */
	public TabFile() {
		createNewFile();
	}

	public TabFile(int columnCount) {
		createNewFile(columnCount);
	}

	public TabFile(String[] columnHeaders) {
		createNewFile(columnHeaders);
	}

	/**
	 * Creates a new object representation of the specified *.tab file.
	 * 
	 * @param originFileName
	 *            The absolute fileName of the *.tab file.
	 * @throws IOException
	 *             if the file cannot be read.
	 */
	public TabFile(String originFileName) throws IOException {
		this(new File(originFileName).toURI().toURL());
	}

	/**
	 * Reads the content of the specified *.tab file to this objects variables.
	 * 
	 * @param file
	 *            The file to read.
	 * @throws IOException
	 *             if the file cannot be read.
	 */
	private void readFile(URL file) throws IOException {

		// open the file
		Scanner scan = new Scanner(file.openStream(), "UTF-8");

		// get the column headers
		columnHeaders = scan.nextLine().split("	");

		while (scan.hasNextLine()) {
			values.add(scan.nextLine().split("	"));
		}

		scan.close();
	}

	/**
	 * Creates an empty *.tab file.
	 */
	private void createNewFile() {
		columnHeaders = new String[0];
	}

	private void createNewFile(int columnCount) {
		columnHeaders = new String[columnCount];
	}

	private void createNewFile(String[] columnHeaders) {
		this.columnHeaders = columnHeaders;
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
	 * Returns an array that contains all column headers.
	 * 
	 * @return An array that contains all column headers.
	 */
	public String[] getColumnHeaders() {
		return columnHeaders;
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
	 * @return The value at the specified position and {@code ""} if the
	 *         requested address is outside the bounds (no exception thrown)
	 */
	public String getValueAt(int row, int column) {
		try {
			return values.get(row)[column];
		} catch (ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}
	
	public List<List<Integer>> indexOf(String valueToFind){
		List<List<Integer>> res = new ArrayList<List<Integer>>();
		
		for (int i=0; i<this.getColumnCount(); i++){
			res.add(indexOf(valueToFind, i));
		}
		
		return res;
	}
	
	public List<Integer> indexOf(String valueToFind, int columnIndex){
		List<Integer> res = new ArrayList<Integer>();
		
		for (int i=0; i<this.getRowCount(); i++){
			if (this.getValueAt(i, columnIndex).equals(valueToFind)){
				res.add(i);
			}
		}
		
		return res;
	}

	/**
	 * Replaces the old value at the given position in the *.tab-file with the
	 * new Value. This method cannot add rows to the *.tab-file. To add rows,
	 * use {@link #addRow}
	 * 
	 * @param newValue
	 *            Thenew value of the given cell
	 * @param row
	 *            The row of the cell to be replaced.
	 * @param column
	 *            The column of the cell to be replaced.
	 */
	public void setValueAt(String newValue, int row, int column) {
		values.get(row)[column] = newValue;
	}

	public void addRow(String[] newValues) {
		if (newValues.length != getColumnCount()) {
			throw new ArrayIndexOutOfBoundsException(
					"The given values-array dows not match the column-count of this file.");
		}

		values.add(newValues);
	}

	/**
	 * Gets all values with the given length.
	 * 
	 * @param column
	 *            The column to look for values.
	 * @param length
	 *            The length of the returned values.
	 * @return A {@link List} with all values in the specified column that have
	 *         the specified length.
	 */
	public List<String> getValuesWithLength(int column, int length) {
		List<String> res = new ArrayList<String>();

		for (int i = 0; i < this.getRowCount(); i++) {
			if (this.getValueAt(i, column).length() == length) {
				res.add(this.getValueAt(i, column));
			}
		}

		return res;
	}

	/**
	 * Returns the value that has the highest {@link #stringCorrelation} with
	 * the given {@link String}.
	 * 
	 * @param column
	 *            The column to look for values.
	 * @param value
	 *            The {@link String} to be compared. Only values with equal
	 *            length as {@code value} are returned due to the way
	 *            {@link #stringCorrelation} works.
	 * @param ignoredWords
	 *            Words to be filtered out before doing the comparison.
	 * @return The value in the specified column that has the highest
	 *         correlation.
	 */
	public String getValueWithHighestCorrelation(int column, String value, List<String> ignoredWords) {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		AtomicInteger currentIndex = new AtomicInteger(0);
		AtomicInteger maxIndex = new AtomicInteger(-1);
		AtomicDouble maxCorr = new AtomicDouble(-1);

		for (int i = 0; i < Config.parallelThreadCount; i++) {
			threads.add(new Thread() {
				@Override
				public void run() {
					int index = currentIndex.getAndIncrement();
					while (index < getRowCount()) {
						if (value.equals("_a_n___") && getValueAt(index, column).equals("zahnlos")) {
							System.out.println("stopping...");
						}

						if (value.length() == getValueAt(index, column).length()
								&& !ignoredWords.contains(getValueAt(index, column))
								&& !HangmanSolver.wordContainsWrongChar(getValueAt(index, column))) {
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
		for (int i = 0; i < Config.parallelThreadCount; i++) {
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

	public void save(String fileName) {

		System.out.print("Generating empty file in memory...");
		// Generate the file
		String str = "";
		System.out.println("Done!");

		// Column headers
		System.out.print("Processing column headers...");
		for (String colHead : columnHeaders) {
			str = str + colHead;
			if (!colHead.equals(columnHeaders[columnHeaders.length - 1])) {
				str = str + "	";
			}
		}
		System.out.println("Done!");

		str = str + "\n";

		System.out.print("Processing table contents...");
		// Values
		for (String[] line : values) {
			for (String el : line) {
				str = str + el;

				if (!el.equals(line[line.length - 1])) {
					str = str + "	";
				}
			}

			str = str + "\n";
		}

		System.out.println("Done!");

		System.out.print("Writing to disc...");
		File f = new File(fileName);
		try {
			FileUtils.writeStringToFile(f, str, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");
	}

	/**
	 * Optimizes the dictionary of the app. As The dictionaries are a resource
	 * of the app, this method is currently only intended to run in a dev
	 * environment.<br>
	 * <br>
	 * Optimization means in this case that words are split up at spaces and
	 * punctuation is deleted.
	 * 
	 * @param origin
	 *            The original {@code TabFile} that is to be optimized.
	 * @param originWordColumnIndex
	 *            The column index with the words to be optimized.
	 * @param preserveColumnIndex
	 *            If {@code true}, the words are written to the same column
	 *            index as in the origin-file and the values of all other
	 *            columns are preserved, if {@code false}, the optimized word
	 *            list will be written into the first column (index: 0) and all
	 *            other columns will be deleted.
	 * @return A {@code TabFile} with the optimized word list.
	 */
	public static TabFile optimizeDictionaries(TabFile origin, int originWordColumnIndex, boolean preserveColumnIndex) {
		// Create new TabFile with one column
		String[] colHeads;

		if (preserveColumnIndex) {
			colHeads = origin.getColumnHeaders();
		} else {
			colHeads = new String[] { "words" };
		}
		TabFile res = new TabFile(colHeads);

		for (int lineIndex = 0; lineIndex < origin.getRowCount(); lineIndex++) {
			String[] line = origin.values.get(lineIndex);
			// Split at spaces
			String[] words;

			try {
				words = line[originWordColumnIndex].split(" ");
			} catch (ArrayIndexOutOfBoundsException e) {
				words = "".split(" ");
			}

			for (String word : words) {
				// Remove punctuation
				word = word.replaceAll("(" + Pattern.quote(".") + "|,)", "");

				// Add word to result
				if (preserveColumnIndex) {
					String[] tempValues = new String[origin.getColumnCount()];

					for (int i = 0; i < origin.getColumnCount(); i++) {
						if (i != originWordColumnIndex) {
							tempValues[i] = origin.getValueAt(lineIndex, i);
						} else {
							tempValues[i] = word;
						}
					}

					res.addRow(tempValues);
				} else {
					res.addRow(new String[] { word });
				}
			}
		}

		return res;
	}
}
