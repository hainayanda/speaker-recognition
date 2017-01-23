package local.soundanalysis.extractor;

public interface SoundExtractor<I, O> {
	public O extract(I input) throws Exception;
}
