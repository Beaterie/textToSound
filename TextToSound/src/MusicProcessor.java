import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.PatternProducer;
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
	private int[] m_majorKey = {60, 62, 64, 65, 67, 69, 71};
	private int[] m_minorKey = {60, 62, 63, 65, 67, 68, 70};

	private Double m_tempo = 120.0;
	// emotions to minor and major keys - 0=minor, 1=major
	private int[] m_emotionKeys = {0,1,0,0,1,0,1,1};
	// most major or minor?
	private int m_key;
	// which scale for which size/age of a animal/person?
	private int[] m_scales = {2, 2, 1, 1, 0, 0, -1, -1, -2, -2};
	// stimuli - 0=quiet, 1=alive, 2=aroused
	private String[] m_stimuli = {"quiet","alive","aroused"};
	// emotions to stimuli
	private int[] m_emotionStim = {1,2,1,1,1,0,2,0};
	// possible note durations for the stimuli
	private int[] m_ambient_quiet = {1, 2};
	private int[] m_ambient_alive = {1, 2};
	private int[] m_ambient_aroused = {4};
	
	private int[] m_theme_quiet = {0, 1};
	private int[] m_theme_alive = {1, 2, 3};
	private int[] m_theme_aroused = {3, 4};
	// emotions
	private String[] m_emotions = {"Anger", "Anticipation",
			"Disgust", "Fear", "Joy", "Sadness", "Surprise", "Trust"};
	// emotion chords
	private String[] m_angerChords = {
			"(60+64+67+71)/1.0", "(62+70)/0.5 (62+65+69+72)/0.5" };
	private String[] m_anticipationChords = {
			"(59+62+65)/0.5 (64+67+71)/0.5", "(62+65+69)/0.5 (67+71+74)/0.5",
			"(59+62+65)/0.25 (64+67+71)/0.25 (62+65+69)/0.25 (67+71+74)/0.25",
			"(62+65+69)/0.25 (67+71+74)/0.25 (59+62+65)/0.25 (64+67+71)/0.25"};
	private String[] m_disgustChords = {
			"(64+72)/1.0", "(59+62+65+69)/0.5 (70)/0.5" };
	private String[] m_fearChords = {
			"(62+68)/1.0", "(65+73)/0.5 (65+71)/0.5" };
	private String[] m_joyChords = {
			"(60+64+67)/1.0", "(65+69+72)/1.0", "(62+66+69)/0.5 (67+71+74)/0.5",
			"(59+63+66)/0.5 (64+67+71)/0.5", "(63+65+73)/0.5 (60+64+67)/0.5" };
	private String[] m_sadnessChords = {
			"(53+57+60+64)/1.0", "(57+60+64+65)/1.0",
			"(64+67+71)/0.5 (57+60+64)/0.5", "(69+73+76)/0.5 (62+65+69)/0.5" };
	private String[] m_surpriseChords = {
			"(65+69+73)/0.25 (65+69+73)/0.25 (65+69+73)/0.25 (65+69+72)/0.25" };
	private String[] m_trustChords = {
			"(53+57+60)/1.0", "(53+57+60+62)/1.0", "(67+71+74)/1.0",
			"(59+63+66)/0.5 (64+67+71)/0.5", "(69+73+76)/0.5 (62+65+69)/0.5" };
	private String[][] m_emotionChords = {m_angerChords, m_anticipationChords,
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
	
	public Double getSecondsPerSection() {
		return 60.0/(m_tempo/8.0);
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
		
//		majorKey.put(0,60); // C
//		majorKey.put(1,67); // G
//		majorKey.put(2,64); // E
//		majorKey.put(3,69); // A
//		majorKey.put(4,62); // D
//		majorKey.put(5,65); // F
//		majorKey.put(6,71); // B
//		
//		minorKey.put(0,60); // C
//		minorKey.put(1,67); // G
//		minorKey.put(2,63); // Eb
//		minorKey.put(3,68); // Ab
//		minorKey.put(4,62); // D
//		minorKey.put(5,65); // F
//		minorKey.put(6,70); // Bb
		
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
//		for (int y = 1; y < 16.0; y++)
//		for (Double z = 1.0; z < 16.0; z++) {
//			System.out.print("Times " + z + ", Size " + y + " --> ");
//			generateTheme(z, 2.0, y);
//		}

		//m_musicstring = "T120 L1 " + foxTheme();
		Player player = new Player();
		Pattern pattern = new Pattern(m_musicstring);
		System.out.println(m_musicstring);
		//player.saveMidi(pattern, new File("music-file.mid"));
		// play stuff
		//pattern.save(new File("twinkle.jfugue"));
	    try {
	        MidiFileManager.savePatternToMidi((PatternProducer) pattern, new File("Ambientmusic.midi"));
	        System.out.println("Midi saved as \"Ambientmusic.midi\". ");
	    } catch (Exception ex) {
	        ex.getStackTrace();
	    }
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
		return "T" + Integer.toString((int) Math.round(m_tempo)) + " ";
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
	
	private void generateAnimalMusic() {
		for (Entry<String, TargetInfo> e : m_textInput.entrySet()) {
			generateThemeLayer(e.getValue());
		}
	}
	
	// generate musical animal theme
	private void generateThemeLayer(TargetInfo animal) {
		String theme = "";
		Boolean animal_character = animal.getTargetCharacter();
		int animal_size = animal.getTargetPhys();
		String animal_name = animal.getTarget();
		
		animal.setTheme(theme);
	}
	
	private void generateTheme(Double times1, Double stimulus1, int size1,
			Double times2, Double stimulus2, int size2) {
		String durations = full(times1, stimulus1, size1) + full(times2, stimulus2, size2);
		System.out.println("Thema 1: " + durations);
	}
	
	private String full(Double threshold, Double stimulus, int size) {
		if (stimulus != 0.0) {
			return half(threshold, stimulus, size) + half(threshold+1, stimulus, size);
		}
		
		if (threshold%2 == 0) {
			return "/1.0 ";
		}
		else {
			return half(threshold+1, stimulus, size) + half(threshold, stimulus, size);
		}
	}
	
	private String half(Double threshold, Double stimulus, int size) {
		if (stimulus == 2.0) {
			return quarter(size+(int) Math.round(threshold), stimulus) + quarter(size+1, stimulus);
		}
		if (stimulus == 0.0) {
			return "/0.5 ";
		}
		
		if (threshold%3 == 0) {
			return "/0.5 ";
		}
		else {
			return quarter(size+(int) Math.round(threshold), stimulus) + quarter(size, stimulus);
		}
	}
	
	private String quarter(int threshold, Double stimulus) {
		if (threshold%2 == 0) {
			return "/0.25 ";
		}
		else {
			return eighth(threshold+1, stimulus) + eighth(threshold, stimulus);
		}
	}
	
	private String eighth(int threshold, Double stimulus) {
		if (stimulus == 1.0) {
			return "/0.125 ";
		}
		
		if (threshold%3 == 0) {
			return "/0.125 ";
		}
		else {
			return "/0.0625 /0.0625 ";
		}
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
		
		for (int j = 0; j < m_numOfSections; j++) {
			highestEmotions[3][j] = EmotionResults.getDensity().get(j).get(0);
		}
		
		// find min and max for each character (alive, quiet, neutral)
//		Double quietMax = 0.0;
//		Double quietMin = 0.0;
//		Double aliveMax = 0.0;
//		Double aliveMin = 0.0;
//		Double arousedMax = 0.0;
//		Double arousedMin = 0.0;
//		for (int i = 0; i < m_numOfSections; i++) {
//			if (highestEmotions[2][i] == 0.0) {
//				quietMin = highestEmotions[1][i];
//				quietMax = highestEmotions[1][i];
//			}
//			if (highestEmotions[2][i] == 1.0) {
//				aliveMin = highestEmotions[1][i];
//				aliveMax = highestEmotions[1][i];
//			}
//			if (highestEmotions[2][i] == 2.0) {
//				arousedMin = highestEmotions[1][i];
//				arousedMax = highestEmotions[1][i];
//			}
//		}
//		for (int i = 0; i < m_numOfSections; i++) {
//			if (highestEmotions[2][i] == 0.0) {
//				if (quietMin > highestEmotions[1][i]) {
//					quietMin = highestEmotions[1][i];
//				}
//				if (quietMax < highestEmotions[1][i]) {
//					quietMax = highestEmotions[1][i];
//				}
//			}
//			if (highestEmotions[2][i] == 1.0) {
//				if (aliveMin > highestEmotions[1][i]) {
//					aliveMin = highestEmotions[1][i];
//				}
//				if (aliveMax < highestEmotions[1][i]) {
//					aliveMax = highestEmotions[1][i];
//				}
//			}
//			if (highestEmotions[2][i] == 2.0) {
//				if (arousedMin > highestEmotions[1][i]) {
//					arousedMin = highestEmotions[1][i];
//				}
//				if (arousedMax < highestEmotions[1][i]) {
//					arousedMax = highestEmotions[1][i];
//				}
//			}
//		}
//		System.out.println("quietMin: " + quietMin);
//		System.out.println("quietMax: " + quietMax);
//		System.out.println("aliveMin: " + aliveMin);
//		System.out.println("aliveMax: " + aliveMax);
//		System.out.println("arousedMin: " + arousedMin);
//		System.out.println("arousedMax: " + arousedMax);
		
//		SimpleRegression aliveRegression = new SimpleRegression(true);
//		aliveRegression.addData(new double[][] {
//            {quietMin, 0},
//            {quietMax, m_ambient_quiet.length-1}
//		});
//		SimpleRegression quietRegression = new SimpleRegression(true);
//		quietRegression.addData(new double[][] {
//            {aliveMin, 0},
//            {aliveMax, m_ambient_alive.length-1}
//		});
//		SimpleRegression neutralRegression = new SimpleRegression(true);
//		neutralRegression.addData(new double[][] {
//            {arousedMin, 0},
//            {arousedMax, m_ambient_aroused.length-1}
//		});
		
		printAmbient(highestEmotions);
		musicalAmbient += "L0 ";
		for (int i = 0; i < m_numOfSections; i++) {
			musicalAmbient += setChord(highestEmotions[0][i], highestEmotions[1][i], highestEmotions[3][i]);
		}
		
		System.out.println("String: " + musicalAmbient);
		if (m_key == 0) {
			musicalAmbient = convertToMinor(musicalAmbient);
			System.out.println("String in minor: " + musicalAmbient);
		}
		
		return musicalAmbient;
	}
	
	private String setChord(Double emotion, Double times, Double density) {
		String section = "";
		//System.out.println("Duration: " + (int) Math.round(noteDuration));
		//System.out.println("Emotion: " + m_emotions[(int) Math.round(emotion)]);
		// Anzahl möglicher Muster
		int selection = m_emotionChords[(int) Math.round(emotion)].length;
		// Auswahl eines Musters
		Double rest = times%selection;
		section = m_emotionChords[(int) Math.round(emotion)][(int) Math.round(rest)] + " ";
		//System.out.println("Töne: " + section);
		
		// für Dauer von zwei Takten
		rest = (times*times)%selection;
		section = section + m_emotionChords[(int) Math.round(emotion)][(int) Math.round(rest)] + " ";
		return section;
	}
	
	private Double noteDuration(SimpleRegression reg, int[] durations, Double times) {
		long noteDuration = Math.round(reg.predict(times));
		Double d = (double)((int) noteDuration);
		return d;
	}
	
	private String convertToMinor(String input) {
		int note_E = 40;
		int note_Eb = 39;
		int note_A = 45;
		int note_Ab = 44;
		int note_B = 47;
		int note_Bb = 46;
		String output = input;
		for (int i = 0; i < 5; i++) {
			output = output.replaceAll(Integer.toString(note_E), Integer.toString(note_Eb));
			output = output.replaceAll(Integer.toString(note_A), Integer.toString(note_Ab));
			output = output.replaceAll(Integer.toString(note_B), Integer.toString(note_Bb));
			note_E += 12;
			note_Eb += 12;
			note_A += 12;
			note_Ab += 12;
			note_B += 12;
			note_Bb += 12;
		}
		return output;
	}
	
	private void printAmbient(Double[][] info) {
		System.out.println();
		for (int i = 0; i < info[0].length; i++) {
			System.out.println("Sektion " + i + ":");
			for (int j = 0; j < info.length; j++) {
				if (j == 0) {
					System.out.println("   Emotion: "+m_emotions[(int) Math.round(info[j][i])]);
				}
				if (j == 1) {
					System.out.println("   Häufigkeit: "+(int) Math.round(info[j][i]));
				}
				if (j == 2) {
					System.out.println("   Erregungszustand: "+m_stimuli[(int) Math.round(info[j][i])]);
				}
				if (j == 3) {
					System.out.println("   Emotionsdichte: "+info[j][i]);
//					if (Math.round(info[2][i]) == 0) {
//						System.out.println("   Note duration: "+m_ambient_quiet[(int) Math.round(info[j][i])]);
//					}
//					if (Math.round(info[2][i]) == 1) {
//						System.out.println("   Note duration: "+m_ambient_alive[(int) Math.round(info[j][i])]);
//					}
//					if (Math.round(info[2][i]) == 2) {
//						System.out.println("   Note duration: "+m_ambient_aroused[(int) Math.round(info[j][i])]);
//					}
				}
				//System.out.print(i+": "+j+" = "+info[i][j]);
			}
			System.out.println();
		}
	}

	
}
