package local.soundanalysis.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import local.soundanalysis.model.signal.Sound;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class AudioRecorder {

	private AudioRecorder() {
	}

	/**
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Sound record() throws IllegalArgumentException {
		return record(16000f, 16, 5);
	}

	/**
	 * 
	 * @param sampleRate
	 * @param bitDepth
	 * @param length
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Sound record(float sampleRate, int bitDepth, int length) throws IllegalArgumentException {
		if (bitDepth % 8 != 0)
			throw new IllegalArgumentException("bitDepth parameter must be in byte size, you are trying to input "
					+ bitDepth + " as parameter, which is incorrect");
		else if (length <= 0)
			throw new IllegalArgumentException("length parameter must be greater than 0, you are trying to input "
					+ bitDepth + " as parameter, which is incorrect");

		AudioFormat format = new AudioFormat(sampleRate, bitDepth, 1, true, true);
		TargetDataLine microphone;
		try {
			microphone = AudioSystem.getTargetDataLine(format);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);

			int numBytesRead;
			int CHUNK_SIZE = 1024;
			byte[] data;

			int bytesMaxSize = length * ((int) sampleRate * (bitDepth) / 8);
			int bytesRead = 0;

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			microphone.start();
			while (bytesRead < bytesMaxSize) {
				data = new byte[microphone.getBufferSize() / 5];
				numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
				bytesRead += numBytesRead;
				out.write(data, 0, numBytesRead);
			}
			microphone.close();
			out.close();
			byte[] sampleData = out.toByteArray();
			double[] samples = getSamplesFromBytes(sampleData, bitDepth);
			return new Sound(samples, (int) sampleRate);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static double[] getSamplesFromBytes(byte[] sampleData, int bitDepth) {
		int byteDepth = bitDepth / 8;
		if (byteDepth == 1)
			return getSamplesFromOne(sampleData);
		else if (byteDepth == 2)
			return getSamplesFromTwo(sampleData);
		else if (byteDepth == 4)
			return getSamplesFomFour(sampleData);
		else
			throw new IllegalArgumentException("bitDepth invalid!");
	}

	private static double[] getSamplesFomFour(byte[] sampleData) {
		double[] result = new double[sampleData.length / 4];
		for (int i = 0; i < result.length; i++) {
			int temp = (short) ((sampleData[i * 4] & 0xFF) | ((sampleData[i * 4 + 1] & 0xFF) << 8)
					| ((sampleData[i * 4 + 2] & 0xFF) << 16) | ((sampleData[i * 4 + 3] & 0xFF) << 24));
			result[i] = ((double) temp) / 4294967295.0;
		}
		return result;
	}

	private static double[] getSamplesFromTwo(byte[] sampleData) {
		double[] result = new double[sampleData.length / 2];
		for (int i = 0; i < result.length; i++) {
			short temp = (short) ((sampleData[i * 2] & 0xFF) | ((sampleData[i * 2 + 1] & 0xFF) << 8));
			result[i] = ((double) temp) / 65535.0;
		}
		return result;
	}

	private static double[] getSamplesFromOne(byte[] sampleData) {
		double[] result = new double[sampleData.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = ((double) sampleData[i]) / 255.0;
		}
		return result;
	}
}
