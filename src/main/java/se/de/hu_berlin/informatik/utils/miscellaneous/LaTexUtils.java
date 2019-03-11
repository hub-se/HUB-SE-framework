/**
 * 
 */
package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides some methods concerning LaTex. 
 * 
 * @author Simon Heiden
 */
final public class LaTexUtils {
	
	private static final String LATEX_TABLE_DELIMITER = " & ";
	
	private static final String LATEX_TABLE_EOL = " \\\\";
	private static final String LATEX_TABLE_HLINE = "\\hline";
	
	//suppress default constructor (class should not be instantiated)
	private LaTexUtils() {
		throw new AssertionError();
	}
	
	public static List<String> generateBibTexEntry(
			final String fullLine, final String key, final String title, 
			final String journal, final String pages, final String volume, 
			final String year, final String... authors) {
		final List<String> lines = new ArrayList<>();
		lines.add("%" + fullLine);
		lines.add("@article{" + key + ",");
		if (authors.length % 2 != 0) {
			Log.abort(null, "Author count doesn't match: %s.", Misc.arrayToString(authors));
		}
		final StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < authors.length; i += 2) {
			if (first) {
				first = false;
			} else {
				builder.append(" and ");
			}
			builder.append(authors[i]).append(", ").append(authors[i+1]);
		}
		lines.add("\tauthor = {" + builder.toString() + "},");
		lines.add("\ttitle = {{" + title + "}},");
		lines.add("\tjournal = {" + journal + "},");
		lines.add("\tpages = {" + pages + "},");
		lines.add("\tvolume = {" + volume + "},");
		lines.add("\tyear = {" + year + "}");
		lines.add("}");
		
		return lines;
	}
	
	/**
	 * Generates a LaTex table from the given data.
	 * @param data
	 * a list of list of String arrays
	 * @return
	 * the generated table as a list of Strings
	 */
	public static List<String> generateLaTexTable(final List<List<String[]>> data) {
		if (data == null || data.isEmpty()) {
			Log.abort(LaTexUtils.class, "No data given.");
		}
		final List<String> lines = new ArrayList<>(data.size() + 2);
		
		lines.add(generateTableHeader(data.get(0).get(0).length));
		
		//fill table
		for (final List<String[]> row : data) {
			lines.addAll(generateTableLines(row));
		}
		
		lines.add(generateTableFooter());
		
		return lines;
	}
	
	/**
	 * Generates a LaTex table from the given data.
	 * @param data
	 * a list of list of String arrays
	 * @return
	 * the generated table as a list of Strings
	 */
	public static List<String> generateSimpleLaTexTable(final List<String[]> data) {
		if (data == null || data.isEmpty()) {
			Log.abort(LaTexUtils.class, "No data given.");
		}
		final List<String> lines = new ArrayList<>(data.size() + 2);
		
		lines.add(generateTableHeader(data.get(0).length));
		
		//fill table
		for (final String[] row : data) {
			lines.add(generateTableLine(row) + LATEX_TABLE_EOL + LATEX_TABLE_HLINE);
		}
		
		lines.add(generateTableFooter());
		
		return lines;
	}
	
	private static String generateTableHeader(final int numberOfColumns) {
		final StringBuilder builder = new StringBuilder("\\begin{center}\\begin{tabular}{|");
		for (int i = 0; i < numberOfColumns; ++i) {
			builder.append("c|");
		}
		builder.append("}" + LATEX_TABLE_HLINE);
		return builder.toString();
	}
	
	private static List<String> generateTableLines(final List<String[]> data) {
		final List<String> lines = new ArrayList<>(data.size());
		for (int i = 0; i < data.size(); ++i) {
			if (i == data.size() - 1) {
				lines.add(generateTableLine(data.get(i)) + LATEX_TABLE_EOL + LATEX_TABLE_HLINE);
			} else {
				lines.add(generateTableLine(data.get(i)) + LATEX_TABLE_EOL);
			}
		}
		return lines;
	}
	
	private static String generateTableLine(final String... data) {
		final StringBuilder builder = new StringBuilder("    ");
		boolean isFirst = true;
		for (final String item : data) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(LATEX_TABLE_DELIMITER);
			}
			if (item != null) {
				builder.append(item/*.replace("_", "\\_")*/);
			} else {
				//builder.append("");
			}
		}
		return builder.toString();
	}
	
	private static String generateTableFooter() {
		return "\\end{tabular}\\end{center}";
	}
}
