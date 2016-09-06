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
public class LaTexUtils {
	
	public static final String LATEX_TABLE_DELIMITER = " & ";
	
	public static final String LATEX_TABLE_EOL = " \\\\";
	public static final String LATEX_TABLE_HLINE = "\\hline";
	
	public static List<String> generateBibTexEntry(String fullLine, String key, String title, String journal, String pages, String volume, String year, String... authors) {
		List<String> lines = new ArrayList<>();
		lines.add("%" + fullLine);
		lines.add("@article{" + key + ",");
		if (authors.length % 2 != 0) {
			Log.abort(null, "Author count doesn't match: %s.", Misc.arrayToString(authors));
		}
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (int i = 0; i < authors.length; i += 2) {
			if (first) {
				first = false;
			} else {
				builder.append(" and ");
			}
			builder.append(authors[i] + ", " + authors[i+1]);
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
	public static List<String> generateLaTexTable(List<List<String[]>> data) {
		if (data == null || data.size() == 0) {
			Log.abort(LaTexUtils.class, "No data given.");
		}
		List<String> lines = new ArrayList<>(data.size() + 2);
		
		lines.add(generateTableHeader(data.get(0).get(0).length));
		
		//fill table
		for (List<String[]> row : data) {
			lines.addAll(generateTableLines(row));
		}
		
		lines.add(generateTableFooter());
		
		return lines;
	}
	
	private static String generateTableHeader(int numberOfColumns) {
		StringBuilder builder = new StringBuilder("\\begin{center}\\begin{tabular}{|");
		for (int i = 0; i < numberOfColumns; ++i) {
			builder.append("c|");
		}
		builder.append("}" + LATEX_TABLE_HLINE);
		return builder.toString();
	}
	
	private static List<String> generateTableLines(List<String[]> data) {
		List<String> lines = new ArrayList<>(data.size());
		for (int i = 0; i < data.size(); ++i) {
			if (i == data.size() - 1) {
				lines.add(generateTableLine(data.get(i)) + LATEX_TABLE_EOL + LATEX_TABLE_HLINE);
			} else {
				lines.add(generateTableLine(data.get(i)) + LATEX_TABLE_EOL);
			}
		}
		return lines;
	}
	
	private static String generateTableLine(String[] data) {
		StringBuilder builder = new StringBuilder("    ");
		boolean isFirst = true;
		for (String item : data) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(LATEX_TABLE_DELIMITER);
			}
			if (item != null) {
				builder.append(item.replace("_", "\\_"));
			} else {
				builder.append("");
			}
		}
		return builder.toString();
	}
	
	private static String generateTableFooter() {
		return "\\end{tabular}\\end{center}";
	}
}
