package local.soundanalysis.vq;

import java.util.Arrays;
import java.util.List;

import local.soundanalysis.model.Signatures;

public class Vectors {
private int[] vectors;
	
	public Vectors(int[] vectors) throws IllegalArgumentException{
		if(vectors == null) throw new IllegalArgumentException("Vectors cannot be null");
		this.vectors = vectors;
	}
	
	public int[] getVectors() {
		return vectors;
	}
	
	public int getVector(int index) throws IndexOutOfBoundsException {
		if(index < length() && index >= 0)
			return vectors[index];
		else if(index >= length())
			throw new IndexOutOfBoundsException("failed to get vector at index " + index + " because vectors length is " + length());
		else
			throw new IndexOutOfBoundsException("failed to get vector at index " + index + " because index cannot be negative");
	}
	
	public int length(){
		return vectors.length;
	}

	public boolean equals(Vectors vectors){
		if(vectors.length() == this.length()){
			for(int i = 0; i < length(); i++){
				if(vectors.getVector(i) != getVector(i))
					return false;
			}
			return true;
		}
		else
			return false;
	}
	
	public int difference(Vectors vectors){
		int diff = 0;
		if(vectors.length() == this.length()){
			for(int i = 0; i < length(); i++){
				diff += Math.abs(vectors.getVector(i) - getVector(i));
			}
			return diff;
		}
		else
			return -1;
	}
	
	public int minDifference(Vectors[] vectors){
		int diff = Integer.MAX_VALUE;
		for(int i = 0; i < vectors.length; i++){
			int temp = difference(vectors[i]);
			if(temp < diff && temp >= 0)
				diff = temp;
		}
		return diff;
	}
	
	public int minDifference(List<Vectors> vectors){
		int diff = Integer.MAX_VALUE;
		for(int i = 0; i < vectors.size(); i++){
			int temp = difference(vectors.get(i));
			if(temp < diff && temp >= 0)
				diff = temp;
		}
		return diff;
	}
	
	public static Vectors parseSignatures(Signatures signatures, double error){
		return new Vectors(signaturesToVectors(signatures.getSignatures(), error));
	}
	
    private static int[] signaturesToVectors(double[] signatures, double error){
        int length = signatures.length;
        int[] vector = new int[length];
        for (int i = 0; i < length; i++){
            if (signatures[i] == 0f)
                vector[i] = 0;
            else if (signatures[i] > 0f)
                vector[i] = (int)(signatures[i] / error) + 1;
            else
                vector[i] = (int)(signatures[i] / error) - 1;
        }
        return vector;
    }
    
	@Override
	public String toString() {
		return "Vectors [vectors=" + Arrays.toString(vectors) + "]";
	}
}
