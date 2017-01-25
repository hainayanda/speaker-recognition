package local.soundanalysis.extractor;

import local.soundanalysis.filter.Filter;
import local.soundanalysis.model.signal.Sound;

/**
 * Class that implemented Sound Extractor to extract sound with no silence
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class SilenceRemover implements SoundExtractor<Sound> {

	public Sound extract(Sound input) throws Exception {
		return Filter.silenceRemover(input);
	}

}
