package local.soundanalysis.machinelearning;

import local.soundanalysis.algorithm.LinearPredictive;
import local.soundanalysis.model.signal.Sound;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class LPCLearning extends LearningCore{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3513021652443945883L;

	private LPCLearning(int seed, int iterations, double learningRate, int neuronIn, int neuronOut, int neuronHidden)
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
	public LPCLearning(int seed, int iterations, double learningRate, int outputSize){
		super(seed, iterations, learningRate, 20, outputSize, 40);
	}
	
	/**
	 * 
	 * @param sound
	 * @param output
	 */
	public void learnNewVoice(Sound sound, double[] output){
		learnNewFeature(LinearPredictive.extractCoefficients(sound, 20).getSignatures(), output);
	}
	
	/**
	 * 
	 * @param sounds
	 * @param output
	 */
	public void learnNewVoices(Sound[] sounds, double[][] output){
		double[][] signatures = new double[sounds.length][];
		for(int i = 0; i < sounds.length; i++){
			signatures[i] = LinearPredictive.extractCoefficients(sounds[i], 20).getSignatures();
		}
		learnNewFeatures(signatures, output);
	}
	
	/**
	 * 
	 * @param sound
	 * @return
	 */
	public double[] testVoice(Sound sound){
		return test(LinearPredictive.extractCoefficients(sound, 20).getSignatures());
	}
	
}
