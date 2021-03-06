package se.de.hu_berlin.informatik.utils.files.csv;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import se.de.hu_berlin.informatik.utils.files.FileUtils;
import se.de.hu_berlin.informatik.utils.files.csv.stringprocessor.CSVStringsToDoubleArrayListProcessor;
import se.de.hu_berlin.informatik.utils.files.csv.stringprocessor.CSVStringsToIntegerArrayListProcessor;
import se.de.hu_berlin.informatik.utils.files.csv.stringprocessor.CSVStringsToMirroredStringArrayListProcessor;
import se.de.hu_berlin.informatik.utils.files.csv.stringprocessor.CSVStringsToStringArrayListProcessor;
import se.de.hu_berlin.informatik.utils.files.processors.FileLineProcessor;
import se.de.hu_berlin.informatik.utils.files.processors.FileLineProcessor.StringProcessor;
import se.de.hu_berlin.informatik.utils.files.processors.ListToFileWriter;

/**
 * Contains utility methods to cope with reading and writing CSV files.
 *
 * @author Simon Heiden
 */
public final class CSVUtils {

	/**
	 * used CSV delimiter
	 */
	public static final char CSV_DELIMITER = ';';
	public static final String CSV_DELIMITER_STRING = ";";
	/**
	 * used quote character to quote fields
	 */
	public static final String CSV_QUOTE = "\"";

	// suppress default constructor (class should not be instantiated)
	private CSVUtils() {
		throw new AssertionError();
	}

	/**
	 * Reads a CSV data file and parses its contents into a list of Double
	 * arrays.
	 * @param csvFile
	 * the path to the CSV file
	 * @return a list of Double arrays
	 */
	public static List<Double[]> readCSVFileToListOfDoubleArrays(Path csvFile) {
		return readCSVFileToListOfDoubleArrays(csvFile, 0);
	}

	/**
	 * Reads a CSV data file and parses its contents into a list of Double
	 * arrays.
	 * @param csvFile
	 * the path to the CSV file
	 * @param skipFirstLines
	 * skip the given number of lines at the start of the file
	 * @return a list of Double arrays
	 */
	public static List<Double[]> readCSVFileToListOfDoubleArrays(Path csvFile, int skipFirstLines) {
		return new FileLineProcessor<List<Double[]>>(new CSVStringsToDoubleArrayListProcessor(), false)
				.skipFirstLines(skipFirstLines).submit(csvFile).getResult();
	}

	/**
	 * Reads a CSV data file and parses its contents into a list of Integer
	 * arrays.
	 * @param csvFile
	 * the path to the CSV file
	 * @return a list of Integer arrays
	 */
	public static List<Integer[]> readCSVFileToListOfIntegerArrays(Path csvFile) {
		return readCSVFileToListOfIntegerArrays(csvFile, 0);
	}

	/**
	 * Reads a CSV data file and parses its contents into a list of Integer
	 * arrays.
	 * @param csvFile
	 * the path to the CSV file
	 * @param skipFirstLines
	 * skip the given number of lines at the start of the file
	 * @return a list of Integer arrays
	 */
	public static List<Integer[]> readCSVFileToListOfIntegerArrays(Path csvFile, int skipFirstLines) {
		return new FileLineProcessor<List<Integer[]>>(new CSVStringsToIntegerArrayListProcessor(), false)
				.skipFirstLines(skipFirstLines).submit(csvFile).getResult();
	}

	/**
	 * Reads a CSV data file and parses its contents into a list of String
	 * arrays.
	 * @param csvFile
	 * the path to the CSV file
	 * @param mirrored
	 * whether the CSV file shall be parsed vertically
	 * @return a list of String arrays
	 */
	public static List<String[]> readCSVFileToListOfStringArrays(Path csvFile, boolean mirrored) {
		StringProcessor<List<String[]>> processor;
		if (mirrored) {
			processor = new CSVStringsToMirroredStringArrayListProcessor();
		} else {
			processor = new CSVStringsToStringArrayListProcessor();
		}

		return new FileLineProcessor<List<String[]>>(processor, false).submit(csvFile).getResult();
	}

	/**
	 * Reads a CSV data file containg the given pattern and parses its contents
	 * into a list of String arrays.
	 * @param containingDir
	 * the path to the directory containing the CSV file
	 * @param pattern
	 * a pattern for the file name
	 * @param mirrored
	 * whether the CSV file shall be parsed vertically
	 * @return a list of String arrays, or null in case no file was found
	 */
	public static List<String[]> readCSVFileToListOfStringArrays(File containingDir, String pattern, boolean mirrored) {
		File allCSV = FileUtils.searchFileContainingPattern(containingDir, pattern);
		if (allCSV != null) {
			return CSVUtils.readCSVFileToListOfStringArrays(allCSV.toPath(), mirrored);
		}
		return null;
	}

	/**
	 * Reads a CSV data file containg the given pattern and parses its contents
	 * into a list of String arrays.
	 * @param containingDir
	 * the path to the directory containing the CSV file
	 * @param pattern
	 * a pattern for the file name
	 * @param mirrored
	 * whether the CSV file shall be parsed vertically
	 * @return a list of String arrays, or null in case no file was found
	 */
	public static List<String[]> readCSVFileToListOfStringArrays(Path containingDir, String pattern, boolean mirrored) {
		return readCSVFileToListOfStringArrays(containingDir.toFile(), pattern, mirrored);
	}

	/**
	 * Turns an integer array into CSV lines.
	 * @param dataArray
	 * an array containing the data elements
	 * @param columnCount
	 * the number of columns
	 * @return the combined CSV strings to write to a file
	 */
	public static List<String> toCsv(final int[] dataArray, int columnCount) {
		final StringBuilder line = new StringBuilder();
		if (dataArray.length % columnCount != 0) {
			throw new IllegalArgumentException();
		}

		List<String> lines = new ArrayList<>();

		for (int i = 0; i < dataArray.length; ++i) {
			line.append(dataArray[i]);
			if ((i + 1) % columnCount == 0) {
				// add the line to the list and begin a new line
				lines.add(line.toString());
				line.setLength(0);
			} else {
				// put delimiter between the values
				line.append(CSV_DELIMITER);
			}
		}
		return lines;
	}

	/**
	 * Turns a byte array into CSV lines.
	 * @param dataArray
	 * an array containing the data elements
	 * @param columnCount
	 * the number of columns
	 * @return the combined CSV strings to write to a file
	 */
	public static List<String> toCsv(final byte[] dataArray, int columnCount) {
		final StringBuilder line = new StringBuilder();
		if (dataArray.length % columnCount != 0) {
			throw new IllegalArgumentException();
		}

		List<String> lines = new ArrayList<>();

		for (int i = 0; i < dataArray.length; ++i) {
			line.append(dataArray[i]);
			if ((i + 1) % columnCount == 0) {
				// add the line to the list and begin a new line
				lines.add(line.toString());
				line.setLength(0);
			} else {
				// put delimiter between the values
				line.append(CSV_DELIMITER);
			}
		}
		return lines;
	}

	/**
	 * Turns a list of Object arrays into CSV lines.
	 * @param <T>
	 * the type of objects in the arrays
	 * @param objectArrayList
	 * an list of arrays containing the data elements
	 * @param mirrored
	 * whether to mirror the lines diagonally (rows to columns)
	 * @return the combined CSV strings to write to a file
	 */
	public static <T> List<String> toCsv(final List<T[]> objectArrayList, boolean mirrored) {
		if (mirrored) {
			if (objectArrayList.size() == 0) {
				return new ArrayList<>(0);
			}
			// assert same length of arrays
			int arrayLength = objectArrayList.get(0).length;
			for (Object[] element : objectArrayList) {
				assert element.length == arrayLength;
			}

			List<String> lines = new ArrayList<>();

			for (int i = 0; i < arrayLength; ++i) {
				lines.add(columnToCsvLine(objectArrayList, i));
			}

			return lines;
		} else {
			List<String> lines = new ArrayList<>(objectArrayList.size());

			for (Object[] element : objectArrayList) {
				lines.add(toCsvLine(element));
			}

			return lines;
		}
	}

	/**
	 * Turns a list of Object arrays into CSV lines.
	 * @param <T>
	 * the type of objects in the arrays
	 * @param objectArrayList
	 * an list of arrays containing the data elements
	 * @return the combined CSV strings to write to a file
	 */
	public static <T> List<String> toCsv(final List<T[]> objectArrayList) {
		return toCsv(objectArrayList, false);
	}

	/**
	 * Turns a list of Object arrays into CSV lines.
	 * @param <T>
	 * the type of objects in the arrays
	 * @param objectArrayList
	 * an list of arrays containing the data elements
	 * @param mirrored
	 * whether to mirror the lines diagonally (rows to columns)
	 * @param output
	 * the output path
	 */
	public static <T> void toCsvFile(final List<T[]> objectArrayList, boolean mirrored, Path output) {
		List<String> lines = toCsv(objectArrayList, mirrored);

		new ListToFileWriter<List<String>>(output, true).asModule().submit(lines);
	}

	/**
	 * Turns a list of Object arrays into CSV lines.
	 * @param <T>
	 * the type of objects in the arrays
	 * @param objectArrayList
	 * an list of arrays containing the data elements
	 * @param output
	 * the output path
	 */
	public static <T> void toCsvFile(final List<T[]> objectArrayList, Path output) {
		toCsvFile(objectArrayList, false, output);
	}

	/**
	 * Turns an Object array into a CSV line.
	 * @param objectArray
	 * an array containing the data elements
	 * @return the combined CSV string to write to a file
	 */
	public static String toCsvLine(final Object[] objectArray) {
		final StringBuilder line = new StringBuilder();

		for (int i = 0; i < objectArray.length; ++i) {
			line.append(
					objectArray[i] == null ? "null"
							: objectArray[i].toString().replace(CSV_DELIMITER, '_')
									.replaceAll(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE));
			if (i < objectArray.length - 1) {
				// put delimiter between the values
				line.append(CSV_DELIMITER);
			}
		}
		return line.toString();
	}

	/**
	 * Turns a column of an array list into a CSV line. The arrays are expected
	 * to have at least size {@code columnIndex + 1}.
	 * @param <T>
	 * the type of elements in the arrays
	 * @param objectArrayList
	 * a list with arrays containing the data elements
	 * @param columnIndex
	 * the index of the column to consider
	 * @return the combined CSV string to write to a file
	 */
	private static <T extends Object> String columnToCsvLine(final List<T[]> objectArrayList, int columnIndex) {
		final StringBuilder line = new StringBuilder();

		for (int i = 0; i < objectArrayList.size(); ++i) {
			line.append(
					objectArrayList.get(i)[columnIndex].toString().replace(CSV_DELIMITER, '_')
							.replaceAll(CSV_QUOTE, CSV_QUOTE + CSV_QUOTE));
			if (i < objectArrayList.size() - 1) {
				// put delimiter between the values
				line.append(CSV_DELIMITER);
			}
		}
		return line.toString();
	}

	public static String[] fromCsvLine(String metricsLine) {
		return metricsLine.split(CSVUtils.CSV_DELIMITER_STRING);
	}
}
