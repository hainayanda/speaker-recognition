package local.soundanalysis.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import local.soundanalysis.model.signal.Fourier;
import local.soundanalysis.model.signal.Sound;

public class Printer {

	public static void printSound(Sound sound, String nameFile) {
		printDouble(sound.getSamples(), nameFile);
	}

	public static void printSpectrum(Sound sound, String nameFile) {
		Fourier series = Fourier.fastFourierTransform(sound);
		printDouble(series.getSpectra().getSpectrum(), nameFile);
	}

	public static void printDouble(double[] samples, String nameFile) {
		BufferedWriter writer = null;
		int length = samples.length;
		DecimalFormat df = new DecimalFormat("#.#");
		df.setMaximumFractionDigits(8);

		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			strBuilder.append(df.format(samples[i]));
			strBuilder.append("\n");
		}
		try {
			writer = new BufferedWriter(new FileWriter(nameFile));
			writer.write(strBuilder.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void printBytes(byte[] samples, String nameFile) {
		BufferedWriter writer = null;
		int length = samples.length;

		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			strBuilder.append(samples[i]);
			strBuilder.append("\n");
		}
		try {
			writer = new BufferedWriter(new FileWriter(nameFile));
			writer.write(strBuilder.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
