import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	// major and minor key
	private HashMap<Integer, Integer> majorKey = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> minorKey = new HashMap<Integer, Integer>();
	// note duration
	private HashMap<Double, Double> noteDuration = new HashMap<Double, Double>();
	// emotion to minor and major key - 0=minor, 1=major
	private int[] m_emotionKeys = {0,1,0,0,1,0,1,1};
	// most major or minor?
	private int m_key;
	// stimuli - 0=quiet, 1=alive, 2=aroused
	private String[] m_stimuli = {"quiet","alive","aroused"};
	private int[] m_emotionStim = {1,2,1,1,1,0,2,0};
	// possible note durations for the stimuli
	private int[] m_ambient_quiet = {1, 2};
	private int[] m_ambient_alive = {1, 2};
	private int[] m_ambient_aroused = {4};
	// emotions
	private String[] m_emotions = {"Anger", "Anticipation",
			"Disgust", "Fear", "Joy", "Sadness", "Surprise", "Trust"};
	// emotion chords
	private String[][] m_angerChords = {
			{"(60+64+67+71)"},
			{"(62+70) (62+65+69+72)"}};
	private String[][] m_anticipationChords = {
			{"(59+62+65) (64+67+71) (62+65+69) (67+71+74)"} };
	private String[][] m_disgustChords = {
			{"(64+72)"},
			{"(59+62+65+69) (70)"}};
	private String[][] m_fearChords = {
			{"(62+68)"},
			{"(65+73) (65+7)"}};
	private String[][] m_joyChords = {
			{"(60+64+67)", "(65+69+72)"},
			{"(62+66+69) (67+71+74)", "(59+63+66) (64+67+71)"}};
	private String[][] m_sadnessChords = {
			{"(53+57+60+64)", "(57+60+64+65)"},
			{"(64+67+71) (57+60+64)", "(69+73+76) (62+65+69)"}};
	private String[][] m_surpriseChords = {
			{"(65+69+73) (65+69+73) (65+69+73) (65+69+72)"} };
	private String[][] m_trustChords = {
			{"(53+57+60)", "(53+57+60+62)", "(67+71+74)"},
			{"(59+63+66) (64+67+71)", "(69+73+76) (62+65+69)"}};
	private String[][][] m_emotionChords = {m_angerChords, m_anticipationChords,
			m_disgustChords, m_fearChords, m_joyChords, m_sadnessChords,
			m_surpriseChords, m_trustChords};
	

	
	
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
	// Getter
	// --------------------------------------------------------
	
	public int getM_numOfSections() {
		return m_numOfSections;
	}
	
	
	// --------------------------------------------------------
	// Setter
	// --------------------------------------------------------

	public void setM_numOfSections(int m_numOfSections) {
		this.m_numOfSections = m_numOfSections;
	}
	
	
	// --------------------------------------------------------
	// music-processing methods
	// --------------------------------------------------------
	
	public void process(EmotionResult EmotionResults) {
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
		generateKey(EmotionResults);
		m_musicstring += generateMusicalAmbient(EmotionResults);

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
	public int genereateNumOfSections() {
		int numOfSections = 10 + (int) Math.log10(m_textLength);
		return numOfSections;
	}
	
	// get the tempo
	private String getTempo() {
		return "T120 ";
	}
	
	// --------
	// find and set key - minor/major
	// --------
	private void generateKey(EmotionResult EmotionResults) {
		int key = 0;
		Double counter = 0.0;
		int emotion = 0;
		Double[] emotionSum = new Double[8];
		for (int i = 0; i < emotionSum.length; i++) {
			emotionSum[i] = 0.0;
		}
		for (List<Double> i : EmotionResults.getSectionEmotion()) {
			for (int j = 0; j < 8; j++) {
				emotionSum[j] += i.get(j);
			}
		}
		for (int i = 0; i < 8; i++) {
			if (emotionSum[i] > counter) {
				counter = emotionSum[i];
				key = m_emotionKeys[i];
				emotion = i;
			}
		}
		m_key = key;
		if (m_key == 0) {
			System.out.println("Key: Minor");
		}
		else if (m_key == 1) {
			System.out.println("Key: Major");
		}
		else {
			System.out.println("Failure while key generation!");
		}
		System.out.println("Based on the most appeared emotion: " + m_emotions[emotion] +
				" (Times: " + emotionSum[emotion] + ")");
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
	private String generateMusicalAmbient(EmotionResult EmotionResults) {
		String musicalAmbient = "";
		Double[][] highestEmotions = new Double[4][m_numOfSections];
		Double counter = 0.0;
		int index = 0;
		int k = 0;
		
		// get highest emotions per section
		for (List<Double> i : EmotionResults.getSectionEmotion()) {
			for (int j = 0; j < 8; j++) {
				if (i.get(j) > counter) {
					counter = i.get(j);
					index = j;
				}
			}
			// index -> highest emotion in this section
			highestEmotions[0][k] = (double) index;
			System.out.print("Emotion: " + m_emotions[index]);
			// counter -> appearance of this emotion in this section
			highestEmotions[1][k] = counter;
			System.out.print(" (Times: " + counter + ")");
			highestEmotions[2][k] = (double) m_emotionStim[index];
			System.out.println(" (Character: " + m_stimuli[m_emotionStim[index]] + ")");
			counter = 0.0;
			k++;
		}
		
		// find min and max for each character (alive, quiet, neutral)
		Double quietMax = 0.0;
		Double quietMin = 0.0;
		Double aliveMax = 0.0;
		Double aliveMin = 0.0;
		Double arousedMax = 0.0;
		Double arousedMin = 0.0;
		for (int i = 0; i < m_numOfSections; i++) {
			if (highestEmotions[2][i] == 0.0) {
				quietMin = highestEmotions[1][i];
				quietMax = highestEmotions[1][i];
			}
			if (highestEmotions[2][i] == 1.0) {
				aliveMin = highestEmotions[1][i];
				aliveMax = highestEmotions[1][i];
			}
			if (highestEmotions[2][i] == 2.0) {
				arousedMin = highestEmotions[1][i];
				arousedMax = highestEmotions[1][i];
			}
		}
		for (int i = 0; i < m_numOfSections; i++) {
			if (highestEmotions[2][i] == 0.0) {
				if (quietMin > highestEmotions[1][i]) {
					quietMin = highestEmotions[1][i];
				}
				if (quietMax < highestEmotions[1][i]) {
					quietMax = highestEmotions[1][i];
				}
			}
			if (highestEmotions[2][i] == 1.0) {
				if (aliveMin > highestEmotions[1][i]) {
					aliveMin = highestEmotions[1][i];
				}
				if (aliveMax < highestEmotions[1][i]) {
					aliveMax = highestEmotions[1][i];
				}
			}
			if (highestEmotions[2][i] == 2.0) {
				if (arousedMin > highestEmotions[1][i]) {
					arousedMin = highestEmotions[1][i];
				}
				if (arousedMax < highestEmotions[1][i]) {
					arousedMax = highestEmotions[1][i];
				}
			}
		}
		System.out.println("quietMin: " + quietMin);
		System.out.println("quietMax: " + quietMax);
		System.out.println("aliveMin: " + aliveMin);
		System.out.println("aliveMax: " + aliveMax);
		System.out.println("arousedMin: " + arousedMin);
		System.out.println("arousedMax: " + arousedMax);
		
		SimpleRegression aliveRegression = new SimpleRegression(true);
		aliveRegression.addData(new double[][] {
            {quietMin, 0},
            {quietMax, m_ambient_quiet.length-1}
		});
		SimpleRegression quietRegression = new SimpleRegression(true);
		quietRegression.addData(new double[][] {
            {aliveMin, 0},
            {aliveMax, m_ambient_alive.length-1}
		});
		SimpleRegression neutralRegression = new SimpleRegression(true);
		neutralRegression.addData(new double[][] {
            {arousedMin, 0},
            {arousedMax, m_ambient_aroused.length-1}
		});
		
		// get note duration
		for (int i = 0; i < m_numOfSections; i++) {
			if (highestEmotions[2][i] == 0.0) {
				highestEmotions[3][i] = noteDuration(aliveRegression, m_ambient_quiet, highestEmotions[1][i]);
			}
			if (highestEmotions[2][i] == 1.0) {
				highestEmotions[3][i] = noteDuration(quietRegression, m_ambient_alive, highestEmotions[1][i]);
			}
			if (highestEmotions[2][i] == 2.0) {
				highestEmotions[3][i] = noteDuration(neutralRegression, m_ambient_aroused, highestEmotions[1][i]);
			}
		}
		
		printAmbient(highestEmotions);
		musicalAmbient += "L0 ";
		for (int i = 0; i < m_numOfSections; i++) {
			musicalAmbient += setChord(highestEmotions[0][i], highestEmotions[1][i], highestEmotions[2][i], highestEmotions[3][i]);
		}
		
		System.out.println("String: " + musicalAmbient);
		
		return musicalAmbient;
	}
	
	private String setChord(Double emotion, Double times, Double stimulus, Double noteDuration) {
		String section = "";
		System.out.println("Duration: " + (int) Math.round(noteDuration));
		System.out.println("Emotion: " + (int) Math.round(emotion));
		int selection = m_emotionChords[(int) Math.round(emotion)][(int) Math.round(noteDuration)].length;
		System.out.println("Emotion: " + emotion + " --> " + selection);
		Double rest = times%selection;
		System.out.println("Rest: " + rest);
		section = m_emotionChords[(int) Math.round(emotion)][(int) Math.round(noteDuration)][(int) Math.round(rest)] + " ";
		System.out.println("Till now: " + section);
		System.out.println("Stimulus: " + stimulus);
		if (stimulus == 2.0) {
			System.out.println("YOOOOOOOOOLOOOOOOO");
			section = section.replace(" ", "/0.25 ");
		}
		else {
			if (noteDuration == 0.0) {
				System.out.println("YOOOOOOOOOLOOOOOOO11111");
				section = section.replace(" ", "/1.0 ");
			}
			if (noteDuration == 1.0) {
				System.out.println("YOOOOOOOOOLOOOOOOO2222");
				section = section.replace(" ", "/0.5 ");
			}
		}
		System.out.println("Töne: " + section);
		return section;
	}
	
	private Double noteDuration(SimpleRegression reg, int[] durations, Double times) {
		long noteDuration = Math.round(reg.predict(times));
		Double d = (double)((int) noteDuration);
		return d;
	}
	
	private void printAmbient(Double[][] info) {
		System.out.println();
		for (int i = 0; i < info[0].length; i++) {
			System.out.println("Section " + i + ":");
			for (int j = 0; j < info.length; j++) {
				if (j == 0) {
					System.out.println("   Emotion: "+m_emotions[(int) Math.round(info[j][i])]);
				}
				if (j == 1) {
					System.out.println("   Times: "+(int) Math.round(info[j][i]));
				}
				if (j == 2) {
					System.out.println("   Stimulus: "+m_stimuli[(int) Math.round(info[j][i])]);
				}
				if (j == 3) {
					if (Math.round(info[2][i]) == 0) {
						System.out.println("   Note duration: "+m_ambient_quiet[(int) Math.round(info[j][i])]);
					}
					if (Math.round(info[2][i]) == 1) {
						System.out.println("   Note duration: "+m_ambient_alive[(int) Math.round(info[j][i])]);
					}
					if (Math.round(info[2][i]) == 2) {
						System.out.println("   Note duration: "+m_ambient_aroused[(int) Math.round(info[j][i])]);
					}
				}
				//System.out.print(i+": "+j+" = "+info[i][j]);
			}
			System.out.println();
		}
	}

	
}
