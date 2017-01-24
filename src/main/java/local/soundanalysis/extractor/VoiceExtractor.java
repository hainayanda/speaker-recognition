package local.soundanalysis.extractor;

import static local.soundanalysis.filter.Filter.*;
import static local.soundanalysis.filter.Normalizer.*;

import local.soundanalysis.model.signal.Sound;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class VoiceExtractor implements SoundExtractor<Sound, Sound> {

	/**
	 * 
	 */
	public Sound extract(Sound sound) throws Exception {
		if (sound == null)
			throw new NullPointerException("sound cannot be null");
		else if (sound.samplesLength() <= 0)
			throw new IllegalArgumentException("sound length must be greater than 0");
		return normalizer(silenceRemover(emaBandPass(sound, 60, 3000)));
	}
}
