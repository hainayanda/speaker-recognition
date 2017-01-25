package local.soundanalysis.machinelearning;

import java.io.Serializable;

import local.soundanalysis.model.signal.Sound;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class VoiceLearningCore implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5005028117212593522L;
	
	private MFCCLearning mfccCore;
	private LPCLearning lpcCore;

	/**
	 * 
	 * @param seed
	 * @param iterations
	 * @param learningRate
	 * @param outputSize
	 */
	public VoiceLearningCore(int seed, int iterations, double learningRate, int outputSize) {
		mfccCore = new MFCCLearning(seed, iterations, learningRate, outputSize);
		lpcCore = new LPCLearning(seed, iterations, learningRate, outputSize);
	}

	/**
	 * 
	 * @param sound
	 * @param output
	 */
	public void learnNewVoice(Sound sound, double[] output) {
		mfccCore.learnNewVoice(sound, output);
		lpcCore.learnNewVoice(sound, output);
	}

	/**
	 * 
	 * @param sounds
	 * @param output
	 */
	public void learnNewVoices(Sound[] sounds, double[][] output){
		mfccCore.learnNewVoices(sounds, output);
		lpcCore.learnNewVoices(sounds, output);
	}
	
	/**
	 * 
	 * @param sound
	 * @return
	 */
	public double[] testVoice(Sound sound) {
		double[] mfccResult = mfccCore.testVoice(sound);
		double[] lpcResult = mfccCore.testVoice(sound);
		double[] result = new double[lpcResult.length];
		for(int i = 0; i < lpcResult.length; i++){
			result[i] = (mfccResult[i] + lpcResult[i])/2.0;
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lpcCore == null) ? 0 : lpcCore.hashCode());
		result = prime * result + ((mfccCore == null) ? 0 : mfccCore.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoiceLearningCore other = (VoiceLearningCore) obj;
		if (lpcCore == null) {
			if (other.lpcCore != null)
				return false;
		} else if (!lpcCore.equals(other.lpcCore))
			return false;
		if (mfccCore == null) {
			if (other.mfccCore != null)
				return false;
		} else if (!mfccCore.equals(other.mfccCore))
			return false;
		return true;
	}
	
	
}
