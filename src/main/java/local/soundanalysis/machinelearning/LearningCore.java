package local.soundanalysis.machinelearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import local.soundanalysis.model.Signatures;

public class LearningCore {

	private int numClasses = 2;
	private MultiLayerNetwork neuralNet;
	private int signatureSize;
	private DataSet trainingData;
	private List<String> names = new ArrayList<String>();
	private List<Signatures> signatures;

	public LearningCore(int seed, int iterations) {
		MultiLayerConfiguration config = new NeuralNetConfiguration.Builder().seed(seed).iterations(iterations)
				.activation(Activation.TANH).weightInit(WeightInit.XAVIER).learningRate(0.1).regularization(true)
				.l2(1e-4).list().layer(0, new DenseLayer.Builder().nIn(signatureSize).nOut(numClasses).build())
				.layer(1, new DenseLayer.Builder().nIn(numClasses).nOut(numClasses).build())
				.layer(2,
						new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.activation(Activation.SOFTMAX).nIn(3).nOut(numClasses).build())
				.backprop(true).pretrain(false).build();
		neuralNet = new MultiLayerNetwork(config);
		neuralNet.init();
		neuralNet.setListeners(new ScoreIterationListener(100));
	}

	public void learnNewSignature(Signatures signature, String name) {
		neuralNet.fit(trainingData);
	}

	public void learnExistedSignature(Signatures signature, String name) {

	}

	private DataSet createDataSet(Map<Signatures, Integer> map, List<String> label) {
		double[][] input = new double[map.size()][];
		int[] output = new int[label.size()];
		for (int i = 0; i < input.length; i++) {

		}

		return null;
	}
}
