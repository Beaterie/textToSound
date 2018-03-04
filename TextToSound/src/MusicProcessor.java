import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.PatternProducer;
import org.jfugue.player.Player;

// Coded by Josef Roth, 115850

public class MusicProcessor {
	
	
	// --------------------------------------------------------
	// Member
	// --------------------------------------------------------
	
	// <animal, index of appear, percent>
	private Map<String, TargetInfo> m_textInput;
	// name of the fairytale.txt
	private String m_nameOfLiterature;
	// where to save the midi
	private String m_savePath;
	// music string
	private String m_musicstring;
	// number of characters of the text
	private int m_textLength;
	// number of sections
	private int m_numOfSections;
	// autoplay
	private Boolean m_autoplay;
	// major and minor key alphabets
	private int[] m_majorKey = {60, 62, 64, 65, 67, 69, 71};
	private int[] m_minorKey = {60, 62, 63, 65, 67, 68, 70};

	// tempo with default value
	private Double m_tempo = 80.0;
	// which emotion has which tempo value
	private Double[] m_emotionTemp = {130.0, 150.0, 110.0, 120.0, 100.0, 80.0, 140.0, 90.0};
	// emotions to minor and major keys - 0=minor, 1=major
	private int[] m_emotionKeys = {0, 1, 0, 0, 1, 0, 1, 1};
	// most major or minor?
	private int m_key;
	// which scale for which size/age of a animal/person?
	private int[] m_scales = {2, 2, 1, 1, 0, 0, -1, -1, -2, -2};
	// stimuli - 0=quiet, 1=alive, 2=aroused
	private String[] m_stimuli = {"quiet","alive","aroused"};
	// emotions to stimuli
	private int[] m_emotionStim = {1, 2, 1, 1, 1, 0, 2, 0};
	// array for section information (filled later)
	private Double[][] m_highestEmotions;
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

	public MusicProcessor(ProcessedResult txtRes, String litName, String savePath, Boolean ap) throws IOException {
		m_textInput = txtRes.getOccurenceInfos();
		m_nameOfLiterature = litName;
		m_savePath = savePath;
		m_musicstring = "";
		m_textLength = txtRes.getTextLength();
		m_numOfSections = genereateNumOfSections();
		m_autoplay = ap;
		m_highestEmotions = new Double[4][m_numOfSections];
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
	// General Methods
	// --------------------------------------------------------
	
	/**
	 * Main method which calls every single step/method for generating the music.
	 * Allows to auto-play the generated midi-file (activated by default).
	 * Saves the midi-files in the music-folder.
	 */
	public void process(EmotionResult EmotionResults, Map<String, EmotionResult> AnimalEmotionResults) {
		
		System.out.println();
		System.out.println("-----------------------------------------------------------------");
		System.out.println("---------------------- General Information ----------------------");
		System.out.println();
		
		System.out.println("Number of Sections: " + m_numOfSections);
		generateKey(EmotionResults);
		generateTempo(EmotionResults);
		
		m_musicstring += jFugueTempo();
		m_musicstring += generateMusicalAmbient(EmotionResults);
		m_musicstring += generateThemeLayers(AnimalEmotionResults);
		
		
		Pattern pattern = new Pattern(m_musicstring);
		//System.out.println(m_musicstring);

	    try {
	        MidiFileManager.savePatternToMidi((PatternProducer) pattern, new File(m_savePath +
	        		m_nameOfLiterature.substring(0, m_nameOfLiterature.length()-4) + "-music.midi"));
	        System.out.println("Midi saved as " +
	        		m_nameOfLiterature.substring(0, m_nameOfLiterature.length()-4) + "-music.midi !");
	    } catch (Exception ex) {
	        ex.getStackTrace();
	    }
	    
	    if (m_autoplay == true) {
			Player player = new Player();
			player.play(pattern);
		}
	}
	
	/**
	 * Generates the number of sections subject to the number of characters of the novel/fairytale.
	 */
	public int genereateNumOfSections() {
		int numOfSections = 8 + (int) Math.log10(m_textLength);
		return numOfSections;
	}
	
	/**
	 * Generates the key for the whole piece of music (minor/major).
	 * The emotion with the highest frequency sets the key.
	 */
	private void generateKey(EmotionResult EmotionResults) {
		int key = 0;
		Double counter = 0.0;
		int emotion = 0;
		Double[] emotionSum = new Double[8];
		for (int i = 0; i < emotionSum.length; i++) {
			emotionSum[i] = 0.0;
		}
		
		// find highest frequency
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
		
		// set the key of the emotion to overall key
		m_key = key;
		if (m_key == 0) {
			System.out.print("Key: Minor ");
		}
		else if (m_key == 1) {
			System.out.print("Key: Major ");
		}
		else {
			System.out.println("Failure while key generation!");
		}
		System.out.println(" --> Based on the most appeared emotion: " + m_emotions[emotion] +
				" (Frequency: " + emotionSum[emotion] + ")");
	}
	
	/**
	 * Generates the tempo for the whole piece of music (90-150 BPM).
	 * The emotion with the highest frequency sets the tempo.
	 */
	private void generateTempo(EmotionResult EmotionResults) {
		Double tempo = 80.0;
		Double counter = 0.0;
		int emotion = 0;
		Double[] emotionSum = new Double[8];
		for (int i = 0; i < emotionSum.length; i++) {
			emotionSum[i] = 0.0;
		}
		
		// find highest frequency
		for (List<Double> i : EmotionResults.getSectionEmotion()) {
			for (int j = 0; j < 8; j++) {
				emotionSum[j] += i.get(j);
			}
		}
		for (int i = 0; i < 8; i++) {
			if (emotionSum[i] > counter) {
				counter = emotionSum[i];
				tempo = m_emotionTemp[i];
				emotion = i;
			}
		}
		
		// set the new tempo
		m_tempo = tempo;
		System.out.println("Tempo " + m_tempo + " --> Based on the most appeared emotion: " + m_emotions[emotion] +
				" (Frequency: " + emotionSum[emotion] + ")");
	}
	
	/**
	 * Returns the tempo in the necessary JFugue-MusicString-format.
	 */
	private String jFugueTempo() {
		return "T" + Integer.toString((int) Math.round(m_tempo)) + " ";
	}
	
	
	// --------------------------------------------------------
	// Methods for the animal-/person-themes
	// --------------------------------------------------------
	
	/**
	 * Starts the whole generation of the themes and the theme layers.
	 * Prints some information about the input parameters.
	 * Generates the theme layers subject to the occurrences of the
	 * animals/persons with their themes.
	 */
	private String generateThemeLayers(Map<String, EmotionResult> AnimalEmotionResults) {
		
		String music = "";
		Map<String, String> ranking = new HashMap<String, String>();
		
		System.out.println("------------------------------------------------------------------");
		System.out.println("---------------------- Animal/Person Themes ----------------------");
		System.out.println();
		
		// iterate over the found animal/person emotion vectors,
		// generate the themes and save them in a map named "ranking"
		int num = 0;
		Double[] values = new Double[6];
		for (Entry<String, EmotionResult> e : AnimalEmotionResults.entrySet()) {
			if (ranking.size() < 15) {			
				
				// get the necessary parameters of the animal/person
				values = importantThemeParameters(e.getValue().getSectionEmotion().get(0));
				
				if (m_textInput.containsKey(e.getKey().toLowerCase())) {
					System.out.println("Animal/Person #" + num + ":   " + e.getKey().toLowerCase());
					System.out.print("    Emotion Vector: ............ (");
					for (int i = 0; i < e.getValue().getSectionEmotion().get(0).size()-2; i++) {
						System.out.print(e.getValue().getSectionEmotion().get(0).get(i));
						if (i != e.getValue().getSectionEmotion().get(0).size()-3) {
							System.out.print(", ");
						}
					}
					System.out.println(")");
					
					// generate theme
					ranking.put(e.getKey().toLowerCase(), generateAnimalTheme(
							values[0], values[1], values[2], values[3], values[4], values[5],
							m_textInput.get(e.getKey().toLowerCase()).getTargetPhys(),
							e.getKey().toLowerCase(), num)
							);
					
					System.out.println();
					num++;
				}
			}
		}
		
		int counter = 1;
		Double steps = 100.0/m_numOfSections;
		Double limitDown = 0.0;
		Double limitUp = steps;
		Boolean check = false;
		String themeLayer = "";
		
		// generate the full theme layers
		for (Entry<String, String> e : ranking.entrySet()) {
			m_textInput.get(e.getKey()).setTheme(e.getValue());
			themeLayer = "V" + counter + " ";
			
			// find the position of the animals/persons and
			// set the themes at the same position in the MusicString
			limitDown = 0.0;
			limitUp = steps;
			for (int i = 0; i < m_numOfSections; i++) {
				for (int j = 0; j < m_textInput.get(e.getKey()).getRelativPosOccurence().size(); j++) {
					if (m_textInput.get(e.getKey()).getRelativPosOccurence().get(j) < limitUp &&
							m_textInput.get(e.getKey()).getRelativPosOccurence().get(j) > limitDown) {
						check = true;
						break;
					}
				}
				if (check == true) {
					themeLayer += e.getValue();
				}
				// if no occurrence, set rests in the MusicString 
				else {
					themeLayer += "R/1.0 R/1.0 ";
				}
				check = false;
				limitDown += steps;
				limitUp += steps;
			}
			// last rest for ending chord
			themeLayer += "R/1.0 ";
	
			//System.out.println(e.getKey()  + " theme:");
			//System.out.println(themeLayer);
			music += themeLayer;
			counter++;
		}
		System.out.println("---------------------------");
		
		return music;
	}
	
	/**
	 * Returns the important parameters of the animals/persons emotion vector.
	 * (emotion with the highest frequency,
	 * emotion with the second highest frequency,
	 * stimuli of these both emotions)
	 */
	private Double[] importantThemeParameters(List<Double> emotionvector) {
		Double emotion1 = 0.0;
		Double emotion2 = 0.0;
		Double frequency1 = 0.0;
		Double frequency2 = 0.0;
		Double stimulus1 = 0.0;
		Double stimulus2 = 0.0;
		int index = 0;
		for (int i = 0; i < emotionvector.size()-2; i++) {
			if (frequency1 < emotionvector.get(i)) {
				frequency1 = emotionvector.get(i);
				stimulus1 = (double) m_emotionStim[i];
				index = i;
				emotion1 = (double) i;
			}
			if (frequency2 < emotionvector.get(i) && index != i) {
				frequency2 = emotionvector.get(i);
				stimulus2 = (double) m_emotionStim[i];
				emotion2 = (double) i;
			}
		}
		return new Double[] {emotion1, frequency1, stimulus1, emotion2, frequency2, stimulus2};
	}
	
	/**
	 * Generates the animals/persons theme based on the important parameters
	 * found with "importantThemeParameters" and prints these information
	 * with the generated theme layer.
	 */
	private String generateAnimalTheme(Double emotion1, Double times1, Double stimulus1,
			Double emotion2, Double times2, Double stimulus2, int size, String name, int number) {
		
		//System.out.println("Animal/Person #" + number + ":   " + name);
		System.out.println("    Size/Age: .................. " + size);
		System.out.println("    Emotion 1: ................. " + m_emotions[(int) Math.round(emotion1)]);
		System.out.println("    Frequency 1: ............... " + (int) Math.round(times1));
		System.out.println("    Agitation State 1: ......... " + m_stimuli[(int) Math.round(stimulus1)]);
		System.out.println("    Emotion 2: ................. " + m_emotions[(int) Math.round(emotion2)]);
		System.out.println("    Frequency 2: ............... " + (int) Math.round(times2));
		System.out.println("    Agitation State 2: ......... " + m_stimuli[(int) Math.round(stimulus2)]);
		
		// find the duration of the notes of the theme
		String[] duration1 = full(times1, stimulus1, size).split(" ");
		String[] duration2 = full(times2, stimulus2, size).split(" ");
		
		String music = "";
		int[] alphabet = new int[7];
		
		// set alphabet of usable notes subject to the overall key
		if (m_key == 0) {
			for (int i = 0; i < m_minorKey.length; i++) {
				alphabet[i] = m_minorKey[i] + 12 * m_scales[size];
			}
		}
		else {
			for (int i = 0; i < m_majorKey.length; i++) {
				alphabet[i] = m_majorKey[i] + 12 * m_scales[size];
			}
		}
		
		// give the notes of the theme their individual pitch
		// and add generate the theme as MusicString
		for (int i = 0; i < duration1.length; i++) {
			music += Integer.toString(alphabet[(int) ((times1+stimulus1)*m_highestEmotions[1][i%m_numOfSections])%7]) + duration1[i] + " ";
		}
		for (int i = 0; i < duration2.length; i++) {
			music += Integer.toString(alphabet[(int) ((times2+stimulus2)*m_highestEmotions[1][i%m_numOfSections])%7]) + duration2[i] + " ";
		}
		
		System.out.println("    Musical Theme: ............. " + music);
		
		return music;
	}

	/**
	 * Generates note duration based on the threshold, the stimulus and the size.
	 * Can go deeper into shorter durations if the stimulus allows it and calls "half(..)".
	 * Can return full notes.
	 */
	private String full(Double threshold, Double stimulus, int size) {
		if (stimulus != 0.0) {
			return half(threshold, stimulus, size) + half(threshold+1, stimulus, size);
		}
		
		if (threshold%2 == 0) {
			return "/1.0000 ";
		}
		else {
			return half(threshold+1, stimulus, size) + half(threshold, stimulus, size);
		}
	}
	
	/**
	 * Generates note duration based on the threshold, the stimulus and the size.
	 * Can go deeper into shorter durations if the stimulus allows it and calls "quarter(..)".
	 * Can return half notes.
	 */
	private String half(Double threshold, Double stimulus, int size) {
		if (stimulus == 2.0) {
			return quarter(size+(int) Math.round(threshold), stimulus) + quarter(size+1, stimulus);
		}
		if (stimulus == 0.0) {
			return "/0.5000 ";
		}
		
		if (threshold%3 == 0) {
			return "/0.5000 ";
		}
		else {
			return quarter(size+(int) Math.round(threshold), stimulus) + quarter(size, stimulus);
		}
	}
	
	/**
	 * Generates note duration based on the threshold, the stimulus and the size.
	 * Can go deeper into shorter durations if the stimulus allows it and calls "eighth(..)".
	 * Can return quarter notes.
	 */
	private String quarter(int threshold, Double stimulus) {
		if (stimulus == 1.0) {
			return "/0.2500 ";
		}
		if (threshold%2 == 0) {
			return "/0.2500 ";
		}
		else {
			return eighth(threshold+1, stimulus) + eighth(threshold, stimulus);
		}
	}
	
	/**
	 * Generates note duration based on the threshold, the stimulus and the size.
	 * Can't go deeper into shorter durations and builds the end of the possible duration-tree.
	 * Can return eighth and sixteenth notes.
	 */
	private String eighth(int threshold, Double stimulus) {
		if (threshold%3 == 0) {
			return "/0.1250 ";
		}
		else {
			return "/0.0625 /0.0625 ";
		}
	}
	
	
	// --------------------------------------------------------
	// Methods for the musical ambient (the base melody)
	// --------------------------------------------------------
		
	/**
	 * Starts the whole generation of the musical ambient layer (the base melody).
	 * Prints the full generated layer.
	 * Fills the "m_highestEmotions[][]"-Member with the important data
	 * for the generation of the musical ambient layer. 
	 */
	private String generateMusicalAmbient(EmotionResult EmotionResults) {
		
		System.out.println();
		System.out.println("-----------------------------------------------------------------");
		System.out.println("---------------------- Ambient Music Layer ----------------------");
		
		String musicalAmbient = "";
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
			m_highestEmotions[0][k] = (double) index;
			// counter -> appearance of this emotion in this section
			m_highestEmotions[1][k] = counter;
			m_highestEmotions[2][k] = (double) m_emotionStim[index];
			counter = 0.0;
			k++;
		}
		
		for (int j = 0; j < m_numOfSections; j++) {
			m_highestEmotions[3][j] = EmotionResults.getDensity().get(j).get(0);
		}
		
		printAmbient();
		musicalAmbient += "V0 ";
		for (int i = 0; i < m_numOfSections; i++) {
			musicalAmbient += setChord(m_highestEmotions[0][i], m_highestEmotions[1][i]);//, m_highestEmotions[3][i]);
		}
		musicalAmbient += "(60+64+67)/1.0 ";
		
		if (m_key == 0) {
			musicalAmbient = convertToMinor(musicalAmbient);
			System.out.println("String converted to Minor-Key.");
		}
		System.out.println("Musical Ambient Layer (Base Melody): " + musicalAmbient);
		
		return musicalAmbient;
	}
	
	/**
	 * Determinates the chords of a section by given parameters:
	 * The emotion with the highest frequency and the frequency itself.
	 */
	private String setChord(Double emotion, Double times) {
		String section = "";
		// number of possible chord pattern
		int selection = m_emotionChords[(int) Math.round(emotion)].length;
		// determinate which pattern to use
		Double rest = times%selection;
		section = m_emotionChords[(int) Math.round(emotion)][(int) Math.round(rest)] + " ";
		
		// do it again to fill the second time (each section covers two 4/4 times)
		rest = (times*times)%selection;
		section = section + m_emotionChords[(int) Math.round(emotion)][(int) Math.round(rest)] + " ";
		return section;
	}	

	/**
	 * Converts a given MusicString into Minor if necessary.
	 */
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
	
	/**
	 * Prints the important parameters of each section
	 * which are used for the musical ambient generation.
	 */
	private void printAmbient() {
		System.out.println();
		for (int i = 0; i < m_highestEmotions[0].length; i++) {
			System.out.println("Section #" + i + ":");
			for (int j = 0; j < m_highestEmotions.length; j++) {
				if (j == 0) {
					System.out.println("   Emotion: ............. "+m_emotions[(int) Math.round(m_highestEmotions[j][i])]);
				}
				if (j == 1) {
					System.out.println("   Frequency: ........... "+(int) Math.round(m_highestEmotions[j][i]));
				}
				if (j == 2) {
					System.out.println("   Agitation State: ..... "+m_stimuli[(int) Math.round(m_highestEmotions[j][i])]);
				}
				if (j == 3) {
					System.out.println("   Emotion Density: ..... "+m_highestEmotions[j][i]);
				}
			}
			System.out.println();
		}
	}

	
}
