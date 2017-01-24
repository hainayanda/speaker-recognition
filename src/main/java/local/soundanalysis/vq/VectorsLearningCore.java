package local.soundanalysis.vq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Nayanda Haberty - nayanda1@outlook.com
 *
 */
public class VectorsLearningCore{
	
	private List<Vectors> vectors;
	private String name;

	/**
	 * 
	 * @param name
	 */
	public VectorsLearningCore(String name) {
		this.vectors = getVectors(name);
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		if (name.length() > 0)
			this.name = name;
	}

	/**
	 * 
	 * @param vector
	 */
	public void train(Vectors vector) {
		if (!isInVectors(vector, 0)) {
			vectors.add(vector);
			saveVectors(vectors, name);
		}
	}

	/**
	 * 
	 * @param vector
	 * @param error
	 * @return
	 */
	public boolean isInVectors(Vectors vector, int error) {
		return isInVectors(this.vectors, vector, error);
	}

	/**
	 * 
	 * @param vectors
	 * @param vector
	 * @param error
	 * @return
	 */
	public static boolean isInVectors(List<Vectors> vectors, Vectors vector, int error) {
		return (vector.minDifference(vectors) <= error * vector.length());
	}

	/**
	 * 
	 * @param vector
	 * @return
	 */
	public int vectorsDiff(Vectors vector) {
		return vectorsDiff(this.vectors, vector);
	}

	/**
	 * 
	 * @param vectors
	 * @param vector
	 * @return
	 */
	public static int vectorsDiff(List<Vectors> vectors, Vectors vector) {
		return vector.minDifference(vectors) / vector.length();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	private static List<Vectors> getVectors(String name) {
		String data = name + ".dat";
		BufferedReader br = null;
		String line = "";
		String split = ", ";
		List<Vectors> vectors = new ArrayList<Vectors>();
		try {
			br = new BufferedReader(new FileReader(data));
			while ((line = br.readLine()) != null && !line.equals("")) {
				String[] str = line.split(split);
				int[] vecs = new int[str.length];
				for (int i = 0; i < str.length; i++) {
					vecs[i] = Integer.parseInt(str[i]);
				}
				vectors.add(new Vectors(vecs));
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
		return vectors;
	}

	/**
	 * 
	 * @param vectors
	 * @param name
	 */
	private static void saveVectors(List<Vectors> vectors, String name) {
		String data = vectorsToString(vectors);
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

	/**
	 * 
	 * @param vectors
	 * @return
	 */
	private static String vectorsToString(List<Vectors> vectors) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < vectors.size(); i++) {
			Vectors vector = vectors.get(i);
			for (int j = 0; j < vector.length(); j++) {
				builder.append(vector.getVector(j));
				if (j != vector.length() - 1)
					builder.append(", ");
			}
			if (i != vectors.size() - 1)
				builder.append("\n");
		}
		return builder.toString();
	}

	public int size() {
		return vectors.size();
	}

	@Override
	public String toString() {
		return "VectorLearning [vectors=" + vectors.toString() + ", name=" + name + "]";
	}

}
