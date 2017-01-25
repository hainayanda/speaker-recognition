package local.soundanalysis.machinelearning;

import local.soundanalysis.algorithm.MelFrequencyAnalysis;
import local.soundanalysis.model.signal.Sound;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class MFCCLearning extends LearningCore{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8750486835270909327L;

	/**
	 * 
	 * @param seed
	 * @param iterations
	 * @param learningRate
	 * @param neuronIn
	 * @param neuronOut
	 * @param neuronHidden
	 * @throws IllegalArgumentException
	 */
	private MFCCLearning(int seed, int iterations, double learningRate, int neuronIn, int neuronOut, int neuronHidden)
			throws IllegalArgumentException {
		super(seed, iterations, learningRate, neuronIn, neuronOut, neuronHidden);
	}

	/**
	 * 
	 * @param seed
	 * @param iterations
	 * @param learningRate
	 * @param outputSize
	 */
	public MFCCLearning(int seed, int iterations, double learningRate, int outputSize){
		super(seed, iterations, learningRate, 13, outputSize, 26);
	}
	
	/**
	 * 
	 * @param sound
	 * @param output
	 */
	public void learnNewVoice(Sound sound, double[] output){
		learnNewFeature(MelFrequencyAnalysis.extractCoefficients(sound, 13).getSignatures(), output);
	}
	
	/**
	 * 
	 * @param sounds
	 * @param output
	 */
	public void learnNewVoices(Sound[] sounds, double[][] output){
		double[][] signatures = new double[sounds.length][];
		for(int i = 0; i < sounds.length; i++){
			signatures[i] = MelFrequencyAnalysis.extractCoefficients(sounds[i], 13).getSignatures();
		}
		learnNewFeatures(signatures, output);
	}
	
	/**
	 * 
	 * @param sound
	 * @return
	 */
	public double[] testVoice(Sound sound){
		return test(MelFrequencyAnalysis.extractCoefficients(sound, 13).getSignatures());
	}
}
