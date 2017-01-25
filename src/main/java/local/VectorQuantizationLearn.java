package local;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import local.soundanalysis.algorithm.LinearPredictive;
import local.soundanalysis.algorithm.MelFrequencyAnalysis;
import local.soundanalysis.extractor.SoundExtractor;
import local.soundanalysis.extractor.EmaVoiceExtractor;
import local.soundanalysis.model.Coefficients;
import local.soundanalysis.model.Signatures;
import local.soundanalysis.model.signal.Sound;
import local.soundanalysis.util.AudioRecorder;
import local.soundanalysis.util.Printer;
import local.soundanalysis.vq.VectorsLearningCore;
import local.soundanalysis.vq.Vectors;

public class VectorQuantizationLearn {

	public static final int LPC_SIGNATURE_LENGTH = 20;
	public static final int MFCC_SIGNATURE_LENGTH = 13;
	public static final float SAMPLE_RATE = 22050f;
	public static final int BIT_DEPTH = 16;
	public static final int RECORDING_LENGTH = 5;

	public static VectorsLearningCore lpcLearningCore;
	public static VectorsLearningCore mfccLearningCore;

	public static double lpcError = 0.005;
	public static double mfccError = 0.05;

	public static void main(String[] args) {
		lpcLearningCore = new VectorsLearningCore("lpc");
		mfccLearningCore = new VectorsLearningCore("mfcc");
	
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("enter l to learn, enter t to test");
			String input = scanner.next();
			input.toLowerCase();
			if (input.equals("l"))
				learn(recordSignatures());
			else if (input.equals("t"))
				test(recordSignatures());
		}
	
	}

	public static Signatures[] recordSignatures() {
		Signatures mfcc = null;
		Coefficients lpc = null;
		try {
			System.out.println("Recording...");
			Sound sound = AudioRecorder.record(SAMPLE_RATE, BIT_DEPTH, RECORDING_LENGTH);
			System.out.println("Finished recording");

			Printer.printSound(sound, "sound.txt");
			Printer.printSpectrum(sound, "soundSpectrum.txt");

			SoundExtractor<Sound> extractor = new EmaVoiceExtractor();
			Sound voice = extractor.extract(sound);

			Printer.printSound(voice, "voice.txt");
			Printer.printSpectrum(voice, "voiceSpectrum.txt");

			mfcc = MelFrequencyAnalysis.extractCoefficients(voice, MFCC_SIGNATURE_LENGTH);
			lpc = LinearPredictive.extractCoefficients(voice, LPC_SIGNATURE_LENGTH);

			List<double[]> mfccList = getDoublesFiles("mfccReal.dat");
			List<double[]> lpcList = getDoublesFiles("lpcReal.dat");
			
			mfccList.add(mfcc.getSignatures());
			lpcList.add(lpc.getSignatures());
			
			saveDoublesFiles(mfccList, "mfccReal.dat");
			saveDoublesFiles(lpcList, "lpcReal.dat");
			
			System.out.println(mfcc.toString());
			System.out.println(lpc.toString());
			

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Signatures[] { lpc, mfcc };

	}

	public static void learn(Signatures[] signatures) {
		System.out.println("Learning....");
		lpcLearningCore.train(Vectors.parseSignatures(signatures[0], lpcError));
		mfccLearningCore.train(Vectors.parseSignatures(signatures[1], mfccError));
		System.out.println("Finished learning....");
	}

	public static void test(Signatures[] signatures) {
		System.out.println("Test");
		int lpcDiff = lpcLearningCore.vectorsDiff(Vectors.parseSignatures(signatures[0], lpcError));
		int mfccDiff = mfccLearningCore.vectorsDiff(Vectors.parseSignatures(signatures[1], mfccError));
		System.out.println("lpcDiff: " + lpcDiff + " mfccDiff: " + mfccDiff);
	}
	
	public static List<double[]> getDoublesFiles(String name) {
		String data = name + ".dat";
		BufferedReader br = null;
		String line = "";
		String split = ", ";
		List<double[]> doubles = new ArrayList<double[]>();
		try {
			br = new BufferedReader(new FileReader(data));
			while ((line = br.readLine()) != null && !line.equals("")) {
				String[] str = line.split(split);
				double[] doubleN = new double[str.length];
				for (int i = 0; i < str.length; i++) {
					doubleN[i] = Double.parseDouble(str[i]);
				}
				doubles.add(doubleN);
			}

		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return doubles;
	}
	
	public static void saveDoublesFiles(List<double[]> doubles, String name) {
		String data = doublesToString(doubles);
		try {
			File file = new File(name + ".dat");
			file.createNewFile();
			FileWriter writer = new FileWriter(file.getAbsoluteFile());
			BufferedWriter buffWriter = new BufferedWriter(writer);
			buffWriter.write(data);
			buffWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String doublesToString(List<double[]> vectors) {
		DecimalFormat df = new DecimalFormat("#.#");
		df.setMinimumFractionDigits(9);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < vectors.size(); i++) {
			double[] vector = vectors.get(i);
			for (int j = 0; j < vector.length; j++) {
				builder.append(vector[j]);
				if (j != vector.length - 1)
					builder.append(", ");
			}
			if (i != vectors.size() - 1)
				builder.append("\n");
		}
		return builder.toString();
	}
}
