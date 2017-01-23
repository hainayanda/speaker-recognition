package local;

import java.util.Scanner;

import local.soundanalysis.algorithm.LinearPredictive;
import local.soundanalysis.algorithm.MelFrequencyAnalysis;
import local.soundanalysis.extractor.SoundExtractor;
import local.soundanalysis.extractor.VoiceExtractor;
import local.soundanalysis.model.Coeficients;
import local.soundanalysis.model.Signatures;
import local.soundanalysis.model.signal.Sound;
import local.soundanalysis.util.AudioRecorder;
import local.soundanalysis.util.Printer;
import local.soundanalysis.vq.VectorLearning;
import local.soundanalysis.vq.Vectors;

public class VectorQuantizationLearn {

	public static final int LPC_SIGNATURE_LENGTH = 20;
	public static final int MFCC_SIGNATURE_LENGTH = 13;
	public static final float SAMPLE_RATE = 16000f;
	public static final int BIT_DEPTH = 16;
	public static final int RECORDING_LENGTH = 5;

	public static VectorLearning lpcLearningCore;
	public static VectorLearning mfccLearningCore;

	public static double lpcError = 0.1;
	public static double mfccError = 0.1;

	public static void main(String[] args) {
		lpcLearningCore = new VectorLearning("lpc");
		mfccLearningCore = new VectorLearning("mfcc");
	
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
		Coeficients lpc = null;
		try {
			System.out.println("Recording...");
			Sound sound = AudioRecorder.record(SAMPLE_RATE, BIT_DEPTH, RECORDING_LENGTH);
			System.out.println("Finished recording");

			Printer.printSound(sound, "sound.txt");
			Printer.printSpectrum(sound, "soundSpectrum.txt");

			SoundExtractor<Sound, Sound> extractor = new VoiceExtractor();
			Sound voice = extractor.extract(sound);

			Printer.printSound(voice, "voice.txt");
			Printer.printSpectrum(voice, "voiceSpectrum.txt");

			mfcc = MelFrequencyAnalysis.getSignatures(voice, MFCC_SIGNATURE_LENGTH);
			lpc = LinearPredictive.getSignatures(voice, LPC_SIGNATURE_LENGTH);

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
}
