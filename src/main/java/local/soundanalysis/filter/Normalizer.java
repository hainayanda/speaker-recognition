package local.soundanalysis.filter;

import local.soundanalysis.model.signal.Sound;

public class Normalizer {

	private Normalizer() {
	}

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

	public static void normalize(Sound sound) {
		Sound normal = normalizer(sound);
		sound.setSamples(normal.getSamples(), normal.getSampleRate());
	}

}
