package languages;

/*-
 * #%L
 * Hangman Solver
 * %%
 * Copyright (C) 2016 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import algorithm.HangmanSolver;
import common.AppConfig;
import common.ArrayListWithSortableKey;
import common.AtomicDouble;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@SuppressWarnings("SameParameterValue")
public class TabFile {

	public static void main(String[] args) {
		if (args[0].equals("optimize")) {
			Scanner sc = new Scanner(System.in);
			String targetPath;
			String originPath;

			System.out.println("Please enter the path of the original *.tab-files:");

			originPath = sc.nextLine();

			System.out.println(
					"Please enter the path where you wish to save the optimized *.tab-files (Directories will be created, existing files with same filenames will be overwritten):");

			targetPath = sc.nextLine();

			sc.close();

			File folder = new File(originPath);
			File[] listOfFiles = folder.listFiles();

			assert listOfFiles != null;
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
		} else if (args[0].equals("merge")) {
			System.err.println(
					"Merging dictionaries is not supported anymore. Please checkout commit 1a6fa16 to merge dictionaries.");
		}
	}

	/**
	 * The column headers of this *.tab file.
	 */
	private String[] columnHeaders;
	/**
	 * The values in this *.tab file.
	 */
	private final ArrayList<ArrayListWithSortableKey<String>> values = new ArrayList<>();

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

	public TabFile(File fileToRead) throws IOException {
		this(fileToRead.toURI().toURL());
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
		this(new File(originFileName));
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
			ArrayListWithSortableKey<String> temp = new ArrayListWithSortableKey<>(
					Arrays.asList(scan.nextLine().split("	")));
			while (temp.size() < this.getColumnCount()) {
				// Fill it up
				temp.add("");
			}
			values.add(temp);
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
	 * Sets the column header at the given index.
	 * 
	 * @param newHeader
	 *            The new header
	 * @param index
	 *            The index of the header to replace.
	 */
	public void setColumnHeader(String newHeader, int index) {
		columnHeaders[index] = newHeader;
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
			return values.get(row).get(column);
		} catch (ArrayIndexOutOfBoundsException e) {
			return "";
		}
	}

	/**
	 * Searches the entire file for the given value. The comparison is case
	 * sensitive.
	 * 
	 * @param valueToFind
	 *            The value to find.
	 * @return The "outer" list is a list of columns, the the "inner" list is a
	 *         list of hits. That means that {@code indexOf("someValue").get(0)}
	 *         returns a list of row indexes where the value was found in column
	 *         0.
	 */
	public List<List<Integer>> indexOf(String valueToFind) {
		return indexOf(valueToFind, false);
	}

	/**
	 * Searches the entire file for the given value. The comparison is case
	 * insensitive.
	 * 
	 * @param valueToFind
	 *            The value to find.
	 * @return The "outer" list is a list of columns, the the "inner" list is a
	 *         list of hits. That means that {@code indexOf("someValue").get(0)}
	 *         returns a list of row indexes where the value was found in column
	 *         0.
	 */
	public List<List<Integer>> indexOfIgnoreCase(String valueToFind) {
		return indexOf(valueToFind, true);
	}

	/**
	 * Searches the entire file for the given value.
	 * 
	 * @param valueToFind
	 *            The value to find.
	 * @param ignoreCase
	 *            if {@code true}, the string comparison will be case
	 *            insensitive.
	 * @return The "outer" list is a list of columns, the the "inner" list is a
	 *         list of hits. That means that {@code indexOf("someValue").get(0)}
	 *         returns a list of row indexes where the value was found in column
	 *         0.
	 */
	public List<List<Integer>> indexOf(String valueToFind, boolean ignoreCase) {
		List<List<Integer>> res = new ArrayList<>();

		for (int i = 0; i < this.getColumnCount(); i++) {
			res.add(indexOf(valueToFind, i, ignoreCase));
		}

		return res;
	}

	/**
	 * Searches for the given value in the given column. The comparison is case
	 * insensitive.
	 * 
	 * @param valueToFind
	 *            The value to find.
	 * @param columnIndex
	 *            The index of the column to be searched.
	 * @return A list of row indexes where the value was found.
	 */
	public List<Integer> indexOfIgnoreCase(String valueToFind, int columnIndex) {
		return indexOf(valueToFind, columnIndex, true);
	}

	/**
	 * Searches for the given value in the given column. The comparison is case
	 * sensitive.
	 * 
	 * @param valueToFind
	 *            The value to find.
	 * @param columnIndex
	 *            The index of the column to be searched.
	 * @return A list of row indexes where the value was found.
	 */
	public List<Integer> indexOf(String valueToFind, int columnIndex) {
		return indexOf(valueToFind, columnIndex, false);
	}

	/**
	 * Searches for the given value in the given column.
	 * 
	 * @param valueToFind
	 *            The value to find.
	 * @param columnIndex
	 *            The index of the column to be searched.
	 * @param ignoreCase
	 *            if {@code true}, the string comparison will be case
	 *            insensitive.
	 * @return A list of row indexes where the value was found.
	 */
	public List<Integer> indexOf(String valueToFind, int columnIndex, boolean ignoreCase) {
		List<Integer> res = new ArrayList<>();

		for (int i = 0; i < this.getRowCount(); i++) {
			if (ignoreCase) {
				if (this.getValueAt(i, columnIndex).equalsIgnoreCase(valueToFind)) {
					res.add(i);
				}
			} else {
				if (this.getValueAt(i, columnIndex).equals(valueToFind)) {
					res.add(i);
				}
			}
		}

		return res;
	}

	/**
	 * Replaces the old value at the specified positions in the *.tab-file with
	 * the new value.
	 * 
	 * @param newValue
	 *            The new value o fthe given cells
	 * @param columnsAndRows
	 *            A list of column- and row indexes where the value will be
	 *            replaced. See the return value of {@link #indexOf(String)} to
	 *            see how the list needs to be built up.
	 * @see #indexOf(String)
	 */
	public void setValueAt(String newValue, List<List<Integer>> columnsAndRows) {
		for (int c = 0; c < columnsAndRows.size(); c++) {
			setValueAt(newValue, columnsAndRows.get(c), c);
		}
	}

	/**
	 * Replaces the old value at the specified positions in the *.tab-file with
	 * the new value.
	 * 
	 * @param newValue
	 *            The new value of the given cells
	 * @param rows
	 *            A list of rows the values will be replaced
	 * @param column
	 *            The column of the cells to be replaced
	 * @see #indexOf(String, int)
	 */
	public void setValueAt(String newValue, List<Integer> rows, int column) {
		for (int row : rows) {
			setValueAt(newValue, row, column);
		}
	}

	/**
	 * Replaces the old value at the given position in the *.tab-file with the
	 * new Value. This method cannot add rows to the *.tab-file. To add rows,
	 * use {@link #addRow}
	 * 
	 * @param newValue
	 *            The new value of the given cell
	 * @param row
	 *            The row of the cell to be replaced.
	 * @param column
	 *            The column of the cell to be replaced.
	 */
	public void setValueAt(String newValue, int row, int column) {
		values.get(row).set(column, newValue);
	}

	public void addRow(String[] newValues) {
		if (newValues.length != getColumnCount()) {
			throw new ArrayIndexOutOfBoundsException(
					"The given values-array dows not match the column-count of this file. (The file has "
							+ this.getColumnCount() + " columns and you wanted to add " + newValues.length
							+ " columns)");
		}

		values.add(new ArrayListWithSortableKey<>(Arrays.asList(newValues)));
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
		List<String> res = new ArrayList<>();

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
		ArrayList<Thread> threads = new ArrayList<>();
		AtomicInteger currentIndex = new AtomicInteger(0);
		AtomicInteger maxIndex = new AtomicInteger(-1);
		AtomicDouble maxCorr = new AtomicDouble(-1);
		
		List<String> ignoredWordsCopy = new ArrayList<>(ignoredWords);
		
		// split all entries up that contain a space
		List<String> stringsToSplit = new ArrayList<>();
		
		// Find words to split
		for (String word:ignoredWordsCopy){
			if (word.contains(" ")){
				stringsToSplit.add(word);
			}
		}
		
		// Actually to the splitting
		for (String word:stringsToSplit){
			ignoredWordsCopy.remove(word);
			ignoredWordsCopy.addAll(Arrays.asList(word.split(" ")));
		}

		for (int i = 0; i < AppConfig.getParallelThreadCount(); i++) {
			threads.add(new Thread(() -> {
                int index = currentIndex.getAndIncrement();
                while (index < getRowCount()) {
                    if (value.length() == getValueAt(index, column).length()
                            && !ignoredWordsCopy.contains(getValueAt(index, column))
                            && !HangmanSolver.currentWordContainsWrongChar(getValueAt(index, column))) {
                        double corr = stringCorrelation(value, getValueAt(index, column));

                        if (corr > maxCorr.get()) {
                            maxCorr.set(corr);
                            maxIndex.set(index);
                        }
                    }

                    // Grab the next index
                    index = currentIndex.getAndIncrement();
                }
            }));
			threads.get(i).start();
		}

		// Wait for threads
		for (int i = 0; i < AppConfig.getParallelThreadCount(); i++) {
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
			throw new IllegalArgumentException("str1 and str2 must be of equal length");
		}

		double equalLetters = 0;

		for (int i = 0; i < str1.length(); i++) {
			if (str1.substring(i, i + 1).equalsIgnoreCase(str2.substring(i, i + 1))) {
				equalLetters = equalLetters + 1;
			}
		}

		return equalLetters / str1.length();
	}

	/**
	 * Sorts this TabFile.
	 * 
	 * @param sortKey
	 *            The column that will be compared to sort values.
	 */
	public void sort(int sortKey) {
		setSortKey(sortKey);
		Collections.sort(values);
	}

	public void sortDescending(int sortKey) {
		setSortKey(sortKey);
		values.sort(Collections.reverseOrder());
	}

	private void setSortKey(int sortKey) {
		for (ArrayListWithSortableKey<String> line : values) {
			line.setSortKey(sortKey);
		}
	}

	/**
	 * Saves this TabFile at the specified location.
	 * 
	 * @param fileName
	 *            The absolute qualified filename where the file should be
	 *            saved. Existing files will be overwritten.
	 */
	public void save(String fileName) {
		save(new File(fileName));
	}

	/**
	 * Saves this TabFile to the specified {@link File}
	 * 
	 * @param destinationFile
	 *            The {@link File} where this TabFile shall be saved in.
	 *            Existing files will be overwritten.
	 */
	public void save(File destinationFile) {

		System.out.print("Generating empty file in memory...");
		// Generate the file
		StringBuilder str = new StringBuilder();
		System.out.println("Done!");

		// Column headers
		System.out.print("Processing column headers...");
		for (String colHead : columnHeaders) {
			str.append(colHead);
			if (!colHead.equals(columnHeaders[columnHeaders.length - 1])) {
				str.append("	");
			}
		}
		System.out.println("Done!");

		str.append("\n");

		System.out.print("Processing table contents...");
		// Values
		for (ArrayListWithSortableKey<String> line : values) {
			for (String el : line) {
				str.append(el);

				if (!el.equals(line.get(line.size() - 1))) {
					str.append("	");
				}
			}

			str.append("\n");
			// str = str + String.join(" ", line) + "\n";
		}

		System.out.println("Done!");

		System.out.print("Writing to disc...");

		try {
			FileUtils.writeStringToFile(destinationFile, str.toString(), "UTF-8");
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
			ArrayListWithSortableKey<String> line = origin.values.get(lineIndex);
			// Split at spaces
			String[] words;

			try {
				words = line.get(originWordColumnIndex).split(" ");
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

	public static TabFile mergeDictionaries(TabFile cldrFile, TabFile wiktFile, int columnIndex) {
		return mergeDictionaries(cldrFile, wiktFile, columnIndex, true);
	}

	public static TabFile mergeDictionaries(TabFile cldrFile, TabFile wiktFile, int columnIndex,
			boolean preserveColumnIndex) {

		if (cldrFile.getColumnCount() != wiktFile.getColumnCount()) {
			throw new RuntimeException("cldrFile and wiktFile must have an equal columnCount.");
		}

		String[] colHeads;

		if (preserveColumnIndex) {
			colHeads = wiktFile.getColumnHeaders();
		} else {
			colHeads = new String[] { "words" };
		}
		TabFile res = new TabFile(colHeads);

		// Copy wikt file
		for (int i = 0; i < wiktFile.getRowCount(); i++) {
			String word = wiktFile.getValueAt(i, columnIndex);

			// Add word to result
			if (preserveColumnIndex) {
				String[] tempValues = new String[cldrFile.getColumnCount()];

				for (int t = 0; t < wiktFile.getColumnCount(); t++) {
					if (t != columnIndex) {
						tempValues[t] = wiktFile.getValueAt(i, t);
					} else {
						tempValues[t] = word;
					}
				}

				res.addRow(tempValues);
			} else {
				res.addRow(new String[] { word });
			}
		}

		// Copy cldr file
		for (int i = 0; i < cldrFile.getRowCount(); i++) {
			String word = cldrFile.getValueAt(i, columnIndex);

			// Only add word if word cannot be found in wikt file
			List<Integer> index = wiktFile.indexOf(word, columnIndex);

			if (index.size() == 0) {
				// Add word to result
				if (preserveColumnIndex) {
					String[] tempValues = new String[cldrFile.getColumnCount()];

					for (int t = 0; t < cldrFile.getColumnCount(); t++) {
						if (i != columnIndex) {
							tempValues[t] = cldrFile.getValueAt(i, t);
						} else {
							tempValues[t] = word;
						}
					}

					res.addRow(tempValues);
				} else {
					res.addRow(new String[] { word });
				}
			}
		}

		res.setColumnHeader("wiktionary-cldr-merge", 0);

		return res;
	}
}
