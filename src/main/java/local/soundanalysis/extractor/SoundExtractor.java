package local.soundanalysis.extractor;

import local.soundanalysis.model.signal.Sound;

/**
 * Interface to extract sound from data given
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 * @param <T>
 *            the output sound
 */
public interface SoundExtractor<T> {
	/**
	 * method to extract sound from given input
	 * 
	 * @param input
	 *            the input data to be extracted
	 * @return the sound extracted
	 * @throws Exception
	 */
	public Sound extract(T input) throws Exception;
}
