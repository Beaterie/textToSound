import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Math;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
//import javax.sound.midi.*;

public class MusicProcessor {
	
	
	// --------------------------------------------------------
	// Member
	// --------------------------------------------------------
	
	// <animal, index of appear, percent>
	private Map<String, TargetInfo> m_textInput;
	// animals
	//private List<String> m_animals;
	// music string
	private String m_musicstring;
	// number of characters of the text
	private int m_textLength;
	// number of sections
	private int m_numOfSections;
	
	private HashMap<Integer, Integer> majorKey = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> minorKey = new HashMap<Integer, Integer>();
	private HashMap<Double, Double> noteDuration = new HashMap<Double, Double>();
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
		
	public MusicProcessor() {};

	public MusicProcessor(ProcessedResult txtRes) throws IOException {
		m_textInput = txtRes.getOccurenceInfos();
		//m_animals = Files.readAllLines(Paths.get("data/lexicon_animals.csv"), StandardCharsets.UTF_8);
		m_musicstring = "";
		m_textLength = txtRes.getTextLength();
		m_numOfSections = genereateNumOfSections();
	}
	
	
	// --------------------------------------------------------
	// music-processing methods
	// --------------------------------------------------------
	
	public void process() {
		
		System.out.println("Number of Sections: " + m_numOfSections);
		int counter = 0;
		// tempo definition
		m_musicstring += getTempo();
		
		majorKey.put(0,0); // C
		majorKey.put(1,7); // G
		majorKey.put(2,4); // E
		majorKey.put(3,9); // A
		majorKey.put(4,2); // D
		majorKey.put(5,5); // F
		majorKey.put(6,11);// B
		
		minorKey.put(0,0); // C
		minorKey.put(1,7); // G
		minorKey.put(2,3); // Eb
		minorKey.put(3,8); // Ab
		minorKey.put(4,2); // D
		minorKey.put(5,5); // F
		minorKey.put(6,10);// Bb
		
		noteDuration.put(0.0, 1.0);		// Whole
		noteDuration.put(1.0, 0.5);		// Half
		noteDuration.put(2.0, 0.25);	// Quarter
		noteDuration.put(3.0, 0.125);	// Eighth
		noteDuration.put(4.0, 0.0625);	// Sixteenth
		
		// for each appearing animal
//		for (Entry<String, TargetInfo> e : m_textInput.entrySet()) {
//			// check animal preset list
//			for (int i = 0; i < m_animals.size(); i++) {
//				// if existing
//				if (e.getKey().equals(m_animals.get(i))) {
//					counter += 1;
//					int[] appearance = new int[numOfSections];
//					List<Float> percentList = e.getValue().getRelativPosOccurence();
//					// for each section
//					for (int j = 0; j < numOfSections; j++) {
//						for (int k = 0; k < percentList.size(); k++) {
//							// match appearance?
//							//System.out.print((j+1)*(100.0/(float)numOfSections) + " ");
//							if (	percentList.get(k) < (j+1)*(100.0/(float)numOfSections) &&
//									percentList.get(k) > (j)*(100.0/(float)numOfSections)	) {
//								appearance[j] = ++appearance[j];
//							}
//						}
//						//System.out.print(appearance[j] + " ");
//					}
//					// now the appearance of the animal i is saved
//					// generate music for the animal i
//					//m_musicstring += generateLayer(counter, appearance, e.getKey());
//					//System.out.println(generateLayer(counter, appearance, e.getKey()));
//					// search can end
//					//i = m_animals.size();
//				}
//			}
//		}
		generateMusicalAmbient();
		//m_musicstring = "T120 L1 " + foxTheme();
		Player player = new Player();
		Pattern pattern = new Pattern(m_musicstring);
		System.out.println(m_musicstring);
		//player.saveMidi(pattern, new File("music-file.mid"));
		// play stuff
		player.play(pattern);
	}
	
	// generate the number of sections subject to
	// the number of characters of the text
	private int genereateNumOfSections() {
		int numOfSections = 10 + (int) Math.log10(m_textLength);
		return numOfSections;
	}
	
	// get the tempo
	private String getTempo() {
		return "T120 ";
	}
	
	// generate musical animal theme
	private void generateAnimalTheme(TargetInfo animal) {
		String theme = "";
		Boolean animal_character = animal.getTargetCharacter();
		int animal_size = animal.getTargetPhys();
		String animal_name = animal.getTarget();
		
		animal.setTheme(theme);
	}
	
	// generate musical ambient layer
	private void generateMusicalAmbient() {
		String musicalAmbient = "";
		
		
		// Später dann von Isabell
		int[] densities = {2,10,15,8,7,4,9,3,21,17,4,6,7,14};
		int min = findMinimum(densities);
		int max = findMaximum(densities);
		System.out.println("Min: " + min);
		System.out.println("Max: " + max);
		
		// Später dann von Isabell mit 16 Dichten
		List<Integer> sectionInfo = new ArrayList<Integer>();
		sectionInfo.add(2);
		sectionInfo.add(23);
		sectionInfo.add(10);
		sectionInfo.add(0);
		sectionInfo.add(24);
		
		sectionInfo.add(13);
		sectionInfo.add(1);
		sectionInfo.add(7);
		sectionInfo.add(9);
		sectionInfo.add(16);
		
		sectionInfo.add(22);
		sectionInfo.add(27);
		sectionInfo.add(5);
		sectionInfo.add(14);
		sectionInfo.add(29);
		
		sectionInfo.add(30);
		SimpleRegression Regression = new SimpleRegression(true);
		Regression.addData(new double[][] {
            {min, 0},
            {max, 4}
		});
	    double value = 0.0;
	    int dens = 0;
	    for (int i = 0; i < densities.length; i++) {
	    	dens = densities[i];
	    	value = Math.round(Regression.predict(dens));
	    	System.out.println("prediction for " + dens + " = " + value);
	    	musicalAmbient += generateAmbientSection(value, sectionInfo);
	    }
		
	}
	
	private String generateAmbientSection(double noteDurationCoded, List<Integer> sectionInfo) {
		String music = "";
		double noteDurationDecoded = noteDuration.get(noteDurationCoded);
		double times = 1.0 / noteDurationDecoded;
		SimpleRegression simpleRegression = new SimpleRegression(true);
		simpleRegression.addData(new double[][] {
            {0, 0},
            {30, 6}
		});
		for (int i = 0; i < sectionInfo.size(); i++) {
			sectionInfo.get(i);
		}
		
		return music;
	}
	
	
	
	private int findMinimum(int[] numbers) {
		int min = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] < min) {
				min = numbers[i];
			}
		}
		return min;
	}
	
	private int findMaximum(int[] numbers) {
		int max = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] > max) {
				max = numbers[i];
			}
		}
		return max;
	}
	
	

}
