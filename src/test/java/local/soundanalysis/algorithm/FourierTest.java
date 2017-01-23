package local.soundanalysis.algorithm;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import local.soundanalysis.model.signal.Fourier;
import local.soundanalysis.util.Printer;

public class FourierTest {
	public static double[] signal;
	public static double sampleRate = 512;

	@BeforeClass
	public static void setup() {
		signal = new double[1024];
		for (int i = 0; i < 1024; i++) {
			for (int j = 0; j < 256; j+=2)
				signal[i] += (double)(1 / (double)(j + 1)) * Math.sin(2.0 * Math.PI * (double)(j + 1) * (double)i / sampleRate);
		}
		Printer.printDouble(signal, "signal.txt");
	}

	@Test
	public void test() {
		double[] spectra = Fourier.fastFourierTransformToSpectra(signal);
		Printer.printDouble(spectra, "spectra.txt");
		double[] reverse = Fourier.reverseFourierTransform(spectra);
		Printer.printDouble(reverse, "reverse.txt");
		assertEquals(reverse.length, signal.length);
		for (int i = 0; i < signal.length; i++) {
			double diff = Math.abs(reverse[i] - signal[i]);
			assertTrue("diff is: " + diff, diff < 0.00000000001);
		}
	}

}
