package local.soundanalysis.machinelearning;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import static local.soundanalysis.math.Operation.*;

import java.io.Serializable;

/**
 * Class that implemented deeplearning4j library. used for learning simple
 * features using multilayer neural network
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class LearningCore implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4466988652812503977L;

	private int sizeOfInput;
	private int sizeOfOutput;
	private MultiLayerNetwork neuralNet;

	/**
	 * Constructor to instantiate LearningCore
	 * 
	 * @param seed
	 *            size of seed
	 * @param iterations
	 *            how much iterations for learning the new features
	 * @param learningRate
	 *            smaller means more details, bigger means faster processing
	 * @param neuronIn
	 *            number of input node
	 * @param neuronOut
	 *            number of output node
	 * @param neuronHidden
	 *            number of hidden node
	 * @throws IllegalArgumentException
	 */
	public LearningCore(int seed, int iterations, double learningRate, int neuronIn, int neuronOut)
			throws IllegalArgumentException {
		if (neuronIn <= 0 || seed <= 0 || iterations <= 0 || learningRate <= 0 || neuronOut <= 0)
			throw new IllegalArgumentException("parameters must be greater than zero");

		this.sizeOfInput = neuronIn;
		this.sizeOfOutput = neuronOut;

		int neuronHidden = ((neuronIn + neuronOut) * 2) / 3;
		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder().iterations(iterations)
				.learningRate(learningRate).seed(seed).useDropConnect(false)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).biasInit(0).miniBatch(false);

		DenseLayer.Builder hiddenLayerBuilder = new DenseLayer.Builder().nIn(neuronIn).nOut(neuronHidden)
				.activation(Activation.RELU).weightInit(WeightInit.RELU).dist(new NormalDistribution(0.0, 1.0));

		Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD).nIn(neuronHidden).nOut(neuronOut)
				.activation(Activation.SIGMOID).weightInit(WeightInit.XAVIER).dist(new NormalDistribution(0.0, 1.0));

		ListBuilder listBuilder = builder.list().layer(0, hiddenLayerBuilder.build())
				.layer(1, outputLayerBuilder.build()).pretrain(false).backprop(true);

		neuralNet = new MultiLayerNetwork(listBuilder.build());
		neuralNet.init();
		neuralNet.setListeners(new ScoreIterationListener(100));
	}

	/**
	 * Getter for input size
	 * 
	 * @return size of input nodes
	 */
	public int getSizeOfInput() {
		return sizeOfInput;
	}

	/**
	 * Getter for output size
	 * 
	 * @return size of output nodes
	 */
	public int getSizeOfOutput() {
		return sizeOfOutput;
	}

	/**
	 * Method to learn new feature
	 * 
	 * @param features
	 *            feature for learning, size of features must be same as input
	 *            nodes
	 * @param output
	 *            the expected output, 1 means true, 0 means false. size of
	 *            output must be same as output nodes
	 */
	public void learnNewFeature(double[] features, double[] output) {
		if (output.length != sizeOfOutput)
			throw new IllegalArgumentException("output length must be same as size of output neuron");
		if (features.length != sizeOfInput)
			throw new IllegalArgumentException("input length must be same as size of input neuron");

		INDArray input = Nd4j.create(new double[][] { features });
		INDArray labels = Nd4j.create(new double[][] { output });
		DataSet ds = new DataSet(input, labels);
		neuralNet.fit(ds);
	}

	/**
	 * Method to learn new features
	 * 
	 * @param features
	 *            features for learning, size of individual feature must be same
	 *            as input nodes
	 * @param output
	 *            the expected output, 1 means true, 0 means false. size of
	 *            individual output must be same as output nodes
	 */
	public void learnNewFeatures(double[][] features, double[][] output) {
		if (!isArraysSimetric(features) || !isArraysSimetric(output))
			throw new IllegalArgumentException("arrays must be simetrical in size");
		if (output[0].length != sizeOfOutput)
			throw new IllegalArgumentException("output length must be same as size of output neuron");
		if (features[0].length != sizeOfInput)
			throw new IllegalArgumentException("input length must be same as size of input neuron");

		INDArray input = Nd4j.create(features);
		INDArray labels = Nd4j.create(output);
		DataSet ds = new DataSet(input, labels);
		neuralNet.fit(ds);
	}

	/**
	 * Methods to test the LearningCore
	 * 
	 * @param features
	 *            the features to test, size of features must be same as input
	 *            nodes
	 * @return results, if the result ~ 1 it means LearningCore recognize the
	 *         results as one of learned features
	 */
	public double[] test(double[] features) {
		if (features.length != sizeOfInput)
			throw new IllegalArgumentException("input length must be same as size of input neuron");
		INDArray test = Nd4j.create(new double[][] { features });
		INDArray output = neuralNet.output(test);
		return extractOutput(output, this.sizeOfOutput);
	}

	private static double[] extractOutput(INDArray output, int length) {
		double[] result = new double[length];
		for (int i = 0; i < length; i++) {
			result[i] = output.getDouble(i);
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((neuralNet == null) ? 0 : neuralNet.hashCode());
		result = prime * result + sizeOfInput;
		result = prime * result + sizeOfOutput;
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
		LearningCore other = (LearningCore) obj;
		if (neuralNet == null) {
			if (other.neuralNet != null)
				return false;
		} else if (!neuralNet.equals(other.neuralNet))
			return false;
		if (sizeOfInput != other.sizeOfInput)
			return false;
		if (sizeOfOutput != other.sizeOfOutput)
			return false;
		return true;
	}

}
