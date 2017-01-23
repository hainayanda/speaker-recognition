package local.soundanalysis.algorithm;

import local.soundanalysis.model.Coeficients;
import local.soundanalysis.model.signal.Sound;

public class LinearPredictive {

	private LinearPredictive() {
	}

	public static Coeficients getSignatures(Sound sound, int signatureSize) {
		double[] samples = sound.getSamples();
		double error = 0;
		double[] coeficients = new double[signatureSize];
		double[] autoCorrelation = new double[signatureSize + 1];

		int i, j;
		j = signatureSize + 1;
		while (j-- != 0) {
			double temp = 0.0F;
			for (i = j; i < samples.length; i++)
				temp += samples[i] * samples[i - j];
			autoCorrelation[j] = temp;
		}

		error = autoCorrelation[0];
		for (i = 0; i < signatureSize; i++) {
			double temp = -autoCorrelation[i + 1];
			if (error == 0) {
				for (int k = 0; k < signatureSize; k++)
					coeficients[k] = 0.0f;
			}
			for (j = 0; j < i; j++)
				temp -= coeficients[j] * autoCorrelation[i - j];

			temp /= error;
			coeficients[i] = temp;

			for (j = 0; j < i / 2; j++) {
				double tmp = coeficients[j];
				coeficients[j] += temp * coeficients[i - 1 - j];
				coeficients[i - 1 - j] += temp * tmp;
			}
			if (i % 2 != 0)
				coeficients[j] += coeficients[j] * temp;
			error *= 1.0 - temp * temp;
		}

		return new Coeficients(coeficients, error);
	}
}