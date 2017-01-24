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
	 * @param samples
	 * @param divider
	 * @param index
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static double[] getFrame(double[] samples, int divider, int index) throws IllegalArgumentException {
		int frameSize = samples.length / divider;
		if (frameSize == 0)
			throw new IllegalArgumentException("frameSize are too small");
		double[] frame = new double[frameSize];
		int startIndex = frameSize * index;
		for (int i = 0; i < frameSize; i++) {
			int j = i + startIndex;
			if (j < samples.length)
				frame[i] = samples[j];
			else
				frame[i] = 0;
		}
		return frame;
	}

	/**
	 * 
	 * @param samples
	 * @param divider
	 * @return
	 */
	public static double[][] getFrames(double[] samples, int divider) {
		if (divider <= 0)
			throw new IllegalArgumentException("divider must be greater than 0");
		double[][] frames = new double[divider][];
		for (int i = 0; i < divider; i++) {
			frames[i] = getFrame(samples, divider, i);
		}
		return frames;
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
