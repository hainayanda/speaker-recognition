package local.soundanalysis.model;

import java.util.Arrays;

public class Signatures {
	private double[] signatures;

	public Signatures(double[] signatures) throws IllegalArgumentException {
		if (signatures == null)
			throw new IllegalArgumentException("Signatures cannot be null");
		this.signatures = signatures;
	}

	public double[] getSignatures() {
		return signatures;
	}

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

	public int length() {
		return signatures.length;
	}

	@Override
	public boolean equals(Object x) {
		if (x == null)
			return false;
		if (this.getClass() != x.getClass())
			return false;
		Signatures that = (Signatures) x;
		if (that.length() != this.length())
			return false;
		for (int i = 0; i < this.length(); i++) {
			if (that.signatures[i] != this.signatures[i])
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Signatures [signatures=" + Arrays.toString(signatures) + "]";
	}

}
