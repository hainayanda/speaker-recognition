package local.soundanalysis.machinelearning;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
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
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import static local.soundanalysis.math.Operation.*;

import java.io.Serializable;

public class LearningCore implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4466988652812503977L;
	
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

	public void learnNewFeature(double[] features, double[] output) {
		if (output.length != neuronOut)
			throw new IllegalArgumentException("output length must be same as size of output neuron");
		if (features.length != neuronIn)
			throw new IllegalArgumentException("input length must be same as size of input neuron");

		INDArray input = Nd4j.create(new double[][] { features });
		INDArray labels = Nd4j.create(new double[][] { output });
		DataSet ds = new DataSet(input, labels);
		neuralNet.fit(ds);
	}

	public void learnNewFeatures(double[][] features, double[][] output) {
		if(!isArraysSimetric(features) || !isArraysSimetric(output))
			throw new IllegalArgumentException("arrays must be simetrical in size");
		if (output[0].length != neuronOut)
			throw new IllegalArgumentException("output length must be same as size of output neuron");
		if (features[0].length != neuronIn)
			throw new IllegalArgumentException("input length must be same as size of input neuron");
		
		INDArray input = Nd4j.create(features);
		INDArray labels = Nd4j.create(output);
		DataSet ds = new DataSet(input, labels);
		neuralNet.fit(ds);
	}

	public double[] test(double[] features){
		if (features.length != neuronIn)
			throw new IllegalArgumentException("input length must be same as size of input neuron");
		INDArray test = Nd4j.create(new double[][] { features });
		INDArray output = neuralNet.output(test);
		return extractOutput(output, this.neuronOut);
	}
	
	public static double[] extractOutput(INDArray output, int length){
		double[] result = new double[length];
		for(int i = 0; i < length; i++){
			result[i] = output.getDouble(0, i);
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((neuralNet == null) ? 0 : neuralNet.hashCode());
		result = prime * result + neuronIn;
		result = prime * result + neuronOut;
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
		if (neuronIn != other.neuronIn)
			return false;
		if (neuronOut != other.neuronOut)
			return false;
		return true;
	}
	
	
}
