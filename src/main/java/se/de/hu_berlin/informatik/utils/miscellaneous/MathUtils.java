package se.de.hu_berlin.informatik.utils.miscellaneous;

/**
 * Provides math-related utility methods.
 * 
 * @author Simon
 *
 */
public class MathUtils {

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
	public static double roundToXDecimalPlaces(double numberToRound, int x) {
		if (x < 0) {
			x = 0;
		}
		double factor = Math.pow(10, x);
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
	public static double roundToXDecimalPlaces(String numberToRound, int x) throws NumberFormatException {
		if (x < 0) {
			x = 0;
		}
		double factor = Math.pow(10, x);
		return Math.round(Double.parseDouble(numberToRound)*factor)/factor;
	}
	
	/**
	 * Returns the maximum of the given numbers.
	 * @param numbers
	 * the numbers
	 * @return
	 * the maximum, or NaN if no values are given
	 */
	public static double getMax(double... numbers) {
		if (numbers.length == 0) {
			return Double.NaN;
		}
		double max = Double.NEGATIVE_INFINITY;
		for (double number : numbers) {
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
	public static double getMin(double... numbers) {
		if (numbers.length == 0) {
			return Double.NaN;
		}
		double min = Double.POSITIVE_INFINITY;
		for (double number : numbers) {
			min = number < min ? number : min;
		}
		return min;
	}
	
}
