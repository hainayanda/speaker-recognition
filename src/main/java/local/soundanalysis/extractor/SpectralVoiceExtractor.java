package local.soundanalysis.extractor;

import static local.soundanalysis.filter.Filter.spectralBandPass;
import static local.soundanalysis.filter.Filter.silenceRemover;
import static local.soundanalysis.filter.Normalizer.normalizer;

import local.soundanalysis.model.signal.Sound;

public class SpectralVoiceExtractor implements SoundExtractor<Sound> {

	public Sound extract(Sound sound) throws Exception {
		if (sound == null)
			throw new NullPointerException("sound cannot be null");
		else if (sound.samplesLength() <= 0)
			throw new IllegalArgumentException("sound length must be greater than 0");
		return normalizer(silenceRemover(spectralBandPass(sound, 60, 3000)));
	}

}
