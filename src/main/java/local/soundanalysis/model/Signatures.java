package local.soundanalysis.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class Signatures implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9209128309822334854L;
	
	private double[] signatures;

	/**
	 * 
	 * @param signatures
	 * @throws IllegalArgumentException
	 */
	public Signatures(double[] signatures) throws IllegalArgumentException {
		if (signatures == null)
			throw new IllegalArgumentException("Signatures cannot be null");
		this.signatures = signatures;
	}

	/**
	 * 
	 * @return
	 */
	public double[] getSignatures() {
		return signatures;
	}

	/**
	 * 
	 * @param signatures
	 * @return
	 */
	public Signatures merge(Signatures signatures){
		return mergeSignatures(new Signatures[]{this,signatures});
	}
	
	/**
	 * 
	 * @param signatures
	 * @return
	 */
	public static Signatures mergeSignatures(Signatures[] signatures){
		double[][] newSignatures = new double[signatures.length][];
		for(int i = 0; i < newSignatures.length; i++){
			newSignatures[i] = signatures[i].getSignatures();
		}
		return new Signatures(mergeSignatures(newSignatures));
	}
	
	private static double[] mergeSignatures(double[][] signatures) {
		int length = 0;
		for (int i = 0; i < signatures.length; i++) {
			length += signatures[i].length;
		}
		double[] newSignatures = new double[length];
		int index = 0;
		for (int i = 0; i < signatures.length; i++) {
			for (int j = 0; j < signatures[i].length; j++) {
				newSignatures[index] = signatures[i][j];
				index++;
			}
		}
		return newSignatures;
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public double getSignature(int index) throws IndexOutOfBoundsException {
		if (index < length() && index >= 0)
			return signatures[index];
		else if (index >= length())
			throw new IndexOutOfBoundsException(
					"failed to get signature at index " + index + " because signatures length is " + length());
		else
			throw new IndexOutOfBoundsException(
					"failed to get signature at index " + index + " because index cannot be negative");
	}

	/**
	 * 
	 * @return
	 */
	public int length() {
		return signatures.length;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(signatures);
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
		Signatures other = (Signatures) obj;
		if (!Arrays.equals(signatures, other.signatures))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Signatures [signatures=" + Arrays.toString(signatures) + "]";
	}

}
