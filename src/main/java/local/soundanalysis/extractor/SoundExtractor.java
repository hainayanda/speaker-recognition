package local.soundanalysis.extractor;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 * @param <I>
 * @param <O>
 */
public interface SoundExtractor<I, O> {
	/**
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public O extract(I input) throws Exception;
}
