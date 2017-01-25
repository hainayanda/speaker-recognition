package local.soundanalysis.filter;

import local.soundanalysis.model.signal.Sound;

/**
 * Class that have functions to normalize sound into -1 - 1
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class Normalizer {

	private Normalizer() {
	}

	/**
	 * Function to normalize sound
	 * 
	 * @param sound
	 *            Sound object that you want to normalize
	 * @return normalized sound
	 */
	public static Sound normalizer(Sound sound) {
		return new Sound(normalizer(sound.getSamples()), sound.getSampleRate());
	}

	/**
	 * Function to normalize samples
	 * @param samples samples data to normalize
	 * @return normalized samples
	 */
	public static double[] normalizer(double[] samples){
		double max = Double.MIN_VALUE;
		double[] result = new double[samples.length];
		for (int i = 0; i < samples.length; i++) {
			double abs = Math.abs(samples[i]);
			if (abs > max) {
				max = abs;
			}
		}
		for (int i = 0; i < samples.length; i++) {
			result[i] = samples[i]/max;
		}
		return result;
	}

	/**
	 * Functions to normalize sound
	 * 
	 * @param sound
	 *            Sound object to normalize
	 */
	public static void normalize(Sound sound) {
		normalize(sound.getSamples());
	}
	
	/**
	 * Function to normalize samples
	 * @param samples samples data to normalized
	 */
	public static void normalize(double[] samples){
		double max = Double.MIN_VALUE;
		for (int i = 0; i < samples.length; i++) {
			double abs = Math.abs(samples[i]);
			if (abs > max) {
				max = abs;
			}
		}
		for (int i = 0; i < samples.length; i++) {
			samples[i] /= max;
		}
	}

}
