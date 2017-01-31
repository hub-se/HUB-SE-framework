package se.de.hu_berlin.informatik.utils.miscellaneous;

import java.util.List;

/**
 * Provides math-related utility methods.
 * 
 * @author Simon
 *
 */
final public class MathUtils {

	//suppress default constructor (class should not be instantiated)
	private MathUtils() {
		throw new AssertionError();
	}

	/**
	 * Rounds the given double to a given number of decimal places.
	 * @param numberToRound
	 * the double to round
	 * @param x
	 * the number of decimal places
	 * @return
	 * the rounded double
	 */
	public static double roundToXDecimalPlaces(final double numberToRound, final int x) {
		if (x < 0) {
			throw new IllegalArgumentException("Argument has to be positive.");
		}
		final double factor = Math.pow(10, x);
		return Math.round(numberToRound*factor)/factor;
	}
	
	/**
	 * Rounds the given double to a given number of decimal places.
	 * @param numberToRound
	 * the double to round
	 * @param x
	 * the number of decimal places
	 * @return
	 * the rounded double
	 */
	public static double roundToXDecimalPlaces(final String numberToRound, final int x) 
			throws NumberFormatException {
		if (x < 0) {
			throw new IllegalArgumentException("Argument has to be positive.");
		}
		final double factor = Math.pow(10, x);
		return Math.round(Double.parseDouble(numberToRound)*factor)/factor;
	}
	
	/**
	 * Returns the maximum of the given numbers.
	 * @param numbers
	 * the numbers
	 * @return
	 * the maximum, or NaN if no values are given
	 */
	public static double getMax(final double... numbers) {
		if (numbers.length == 0) {
			return Double.NaN;
		}
		double max = Double.NEGATIVE_INFINITY;
		for (final double number : numbers) {
			max = number > max ? number : max;
		}
		return max;
	}
	
	/**
	 * Returns the minimum of the given numbers.
	 * @param numbers
	 * the numbers
	 * @return
	 * the minimum, or NaN if no values are given
	 */
	public static double getMin(final double... numbers) {
		if (numbers.length == 0) {
			return Double.NaN;
		}
		double min = Double.POSITIVE_INFINITY;
		for (final double number : numbers) {
			min = number < min ? number : min;
		}
		return min;
	}

	/**
	 * Returns the maximum of the given numbers.
	 * @param numbers
	 * the numbers
	 * @return
	 * the maximum, or NaN if no values are given
	 */
	public static double getMax(final List<Double> numbers) {
		if (numbers.isEmpty()) {
			return Double.NaN;
		}
		double max = Double.NEGATIVE_INFINITY;
		for (final double number : numbers) {
			max = number > max ? number : max;
		}
		return max;
	}
	
	/**
	 * Returns the minimum of the given numbers.
	 * @param numbers
	 * the numbers
	 * @return
	 * the minimum, or NaN if no values are given
	 */
	public static double getMin(final List<Double> numbers) {
		if (numbers.isEmpty()) {
			return Double.NaN;
		}
		double min = Double.POSITIVE_INFINITY;
		for (final double number : numbers) {
			min = number < min ? number : min;
		}
		return min;
	}
	
	public static <T extends Number> double getMedian(final List<T> numbers) {
		if (numbers.isEmpty()) {
			return Double.NaN;
		}
		numbers.sort(null);
		int size = numbers.size();
		if (size % 2 == 0) {
			//even number of elements
			return numbers.get(size/2 - 1).doubleValue() + numbers.get(size/2).doubleValue() / 2.0;
		} else {
			//odd number of elements
			return numbers.get(size/2).doubleValue();
		}
	}
	
	public static <T extends Number> double getMean(final List<T> numbers) {
		if (numbers.isEmpty()) {
			return Double.NaN;
		}
		int count = 0;
		double sum = 0;
		for (Number value : numbers) {
			if (value.doubleValue() == Double.NaN) {
				continue;
			}
			sum += value.doubleValue();
			++count;
		}
		return sum / count;
	}
	
}
