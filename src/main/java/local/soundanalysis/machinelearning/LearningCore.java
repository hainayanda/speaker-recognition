package local.soundanalysis.machinelearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import local.soundanalysis.model.Coeficients;
import local.soundanalysis.model.Signatures;

public class LearningCore {

	private int neuronIn;
	private int neuronOut;
	private MultiLayerNetwork neuralNet;

	public LearningCore(int seed, int iterations, double learningRate, int neuronIn, int neuronOut, int neuronHidden)
			throws IllegalArgumentException {
		if (neuronIn <= 0 || seed <= 0 || iterations <= 0 || learningRate <= 0 || neuronOut <= 0 || neuronHidden <= 0)
			throw new IllegalArgumentException("parameters must be greater than zero");

		this.neuronIn = neuronIn;
		this.neuronOut = neuronOut;

		NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder().iterations(iterations)
				.learningRate(learningRate).seed(seed).useDropConnect(false)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).biasInit(0).miniBatch(false);

		DenseLayer.Builder hiddenLayerBuilder = new DenseLayer.Builder().nIn(neuronIn).nOut(neuronHidden)
				.activation(Activation.SIGMOID).weightInit(WeightInit.DISTRIBUTION).dist(new UniformDistribution(0, 1));

		Builder outputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
				.nIn(neuronHidden).nOut(neuronOut).activation(Activation.SOFTMAX).weightInit(WeightInit.DISTRIBUTION)
				.dist(new UniformDistribution(0, 1));

		ListBuilder listBuilder = builder.list().layer(0, hiddenLayerBuilder.build())
				.layer(1, outputLayerBuilder.build()).pretrain(false).backprop(true);

		neuralNet = new MultiLayerNetwork(listBuilder.build());
		neuralNet.init();
		neuralNet.setListeners(new ScoreIterationListener(100));
	}

	public int sizeOfInput() {
		return neuronIn;
	}

	public int sizeOfOutput() {
		return neuronOut;
	}

	public void learnNewSignature(Signatures[] signatures, int[] output) {
		if (output.length != neuronOut)
			throw new IllegalArgumentException("output length must be same as size of output neuron");

		double[] input = mergeSignatures(signatures);
		if (input.length != neuronIn)
			throw new IllegalArgumentException("input length must be same aas size of input neuron");
		
		
	}

	public static double[] mergeSignatures(Signatures[] signatures) {
		int length = 0;
		for (int i = 0; i < signatures.length; i++) {
			length += signatures[i].length();
		}
		double[] newSignatures = new double[length];
		int index = 0;
		for (int i = 0; i < signatures.length; i++) {
			for (int j = 0; j < signatures[i].length(); j++) {
				newSignatures[index] = signatures[i].getSignature(j);
				index++;
			}
		}
		return newSignatures;
	}

}
