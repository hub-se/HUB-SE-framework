package se.de.hu_berlin.informatik.utils.statistics;

public enum StatisticsOptions {
	/**
	 * When merging two numerical elements, prefer the bigger one.
	 */
	PREF_BIGGER,
	/**
	 * When merging two numerical elements, prefer the smaller one.
	 */
	PREF_SMALLER,
	/**
	 * When merging two boolean elements, prefer true-values.
	 */
	PREF_TRUE,
	/**
	 * When merging two boolean elements, prefer false-values.
	 */
	PREF_FALSE,
	/**
	 * When merging two elements, prefer the newer (non-null) one.
	 */
	PREF_NEW,
	/**
	 * When merging two elements, prefer the older (non-null) one.
	 */
	PREF_OLD,
	/**
	 * When merging two String elements, concat (non-null) ones.
	 */
	CONCAT
}
