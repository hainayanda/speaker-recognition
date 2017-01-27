package local.soundanalysis.math;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class Operation {

	private Operation() {
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	public static double[] absolute(double[] data) {
		double[] abs = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			abs[i] = Math.abs(data[i]);
		}
		return abs;
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static double average(double[] data) {
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return sum / data.length;
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isPowerOfTwo(int number) {
		if (number % 2 == 0) {
			int i = 2;
			while (i < number) {
				i *= 2;
			}
			if (i != number)
				return false;
			else
				return true;
		} else
			return false;
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static int getNearestPowerOfTwo(int number) {
		int i = 2;
		while (i < number) {
			i *= 2;
		}
		return i;
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isEven(int number) {
		return (number % 2 == 0);
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isOdd(int number) {
		return !isEven(number);
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isPositive(int number) {
		return (number >= 0);
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isNegative(int number) {
		return !isPositive(number);
	}

	/**
	 * 
	 * @param number
	 * @return
	 */
	public static int round(double number) {
		int temp = ((int) number);
		double diff = number - ((double) temp);
		if (diff > 0.5) {
			if (isPositive(temp))
				return temp + 1;
			else
				return temp - 1;
		} else
			return temp;

	}
	
	/**
	 * 
	 * @param arrays
	 * @return
	 */
	public static boolean isArraysSimetric(double[][] arrays){
		int check = arrays[0].length;
		for(int i = 1; i < arrays.length; i++){
			if(arrays[i].length != check)
				return false;
		}
		return true;
	}
}
