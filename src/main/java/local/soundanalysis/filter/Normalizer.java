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
		double max = Double.MIN_VALUE;

		double[] samples = sound.getSamples();
		for (int i = 0; i < sound.samplesLength(); i++) {
			double abs = Math.abs(samples[i]);
			if (abs > max) {
				max = abs;
			}
		}
		for (int i = 0; i < sound.samplesLength(); i++) {
			samples[i] /= max;
		}
		return new Sound(samples, sound.getSampleRate());
	}

	/**
	 * Functions to normalize sound
	 * 
	 * @param sound
	 *            Sound object to normalize
	 */
	public static void normalize(Sound sound) {
		Sound normal = normalizer(sound);
		sound.setSamples(normal.getSamples(), normal.getSampleRate());
	}

}
