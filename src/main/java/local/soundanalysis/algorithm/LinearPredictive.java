package local.soundanalysis.algorithm;

import local.soundanalysis.model.Coeficients;
import local.soundanalysis.model.signal.Sound;

public class LinearPredictive {

	private LinearPredictive(){}
	
	public static Coeficients getSignatures(Sound sound, int signatureSize){
		double[] samples = sound.getSamples();
        double error = 0;
        double[] coeficients = new double[signatureSize];
        double[] autoCorrelation = new double[signatureSize + 1];
        
        for(int i = signatureSize; i > 0; i--){
        	double temp = 0.0F;
            for (int j = i; j < samples.length; j++)
                temp += samples[j] * samples[j - i];
            autoCorrelation[i] = temp;
        }
        
        error = autoCorrelation[0];
        for (int i = 0; i < signatureSize; i++){
            double temp = -autoCorrelation[i + 1];
            if (error == 0){
                for (int j = 0; j < signatureSize; j++)
                    coeficients[j] = 0.0f;
            }
            for (int j = 0; j < i; j++)
                temp -= coeficients[j] * autoCorrelation[i - j];

            temp /= error;
            coeficients[i] = temp;
            
            int index;
            for (index = 0; index < i / 2; index++){
                double tmp = coeficients[index];
                coeficients[index] += temp * coeficients[i - 1 - index];
                coeficients[i - 1 - index] += temp * tmp;
            }
            if (i % 2 != 0)
                coeficients[index] += coeficients[index] * temp;
            error *= 1.0 - temp * temp;
        }
        
        return new Coeficients(coeficients, error);
	}
}
