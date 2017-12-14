import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Math;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
//import javax.sound.midi.*;

public class MusicProcessor {
	
	// --------------------------------------------------------
	// Member
	// --------------------------------------------------------
	
	// <animal, index of appear, percent>
	private Map<String, TargetOccurenceInfo> m_textInput;
	// animals
	private List<String> m_animals;
	// music string
	private String m_musicstring;
	// number of characters of the text
	private int m_textLength;
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
		
	public MusicProcessor() {};
	
//	public MusicProcessor(Map<String, TargetOccurenceInfo> ti) throws IOException {
//		textInput = ti;
//		animals = Files.readAllLines(Paths.get("data/lexicon_animals.txt"), StandardCharsets.UTF_8);
//		musicstring = "";
//	};

	public MusicProcessor(ProcessResult txtRes) throws IOException {
		Map<String, TargetOccurenceInfo> map = txtRes.getOccurenceInfos();
		m_textInput = map;
		m_animals = Files.readAllLines(Paths.get("data/lexicon_animals.txt"), StandardCharsets.UTF_8);
		m_musicstring = "";
		m_textLength = txtRes.getTextLength();
	}
	
	// --------------------------------------------------------
	// music-processing methods
	// --------------------------------------------------------
	
	public void process() {
		
		System.out.println("Number of Sections: " + getNumOfSections());
		//System.out.println("Number of Characters: " + m_textLength);
		int counter = 0;
		int numOfSections = getNumOfSections();
		// tempo definition
		m_musicstring += getTempo();
		
		// for each appearing animal
		for (Entry<String, TargetOccurenceInfo> e : m_textInput.entrySet()) {
			// check animal preset list
			for (int i = 0; i < m_animals.size(); i++) {
				// if existing
				if (e.getKey().equals(m_animals.get(i))) {
					counter += 1;
					int[] appearance = new int[numOfSections];
					List<Float> percentList = e.getValue().getRelativPosOccurence();
					// for each section
					for (int j = 0; j < numOfSections; j++) {
						for (int k = 0; k < percentList.size(); k++) {
							// match appearance?
							//System.out.print((j+1)*(100.0/(float)numOfSections) + " ");
							if (	percentList.get(k) < (j+1)*(100.0/(float)numOfSections) &&
									percentList.get(k) > (j)*(100.0/(float)numOfSections)	) {
								appearance[j] = ++appearance[j];
							}
						}
						//System.out.print(appearance[j] + " ");
					}
					// now the appearance of the animal i is saved
					// generate music for the animal i
					m_musicstring += generateLayer(counter, appearance, e.getKey());
					//System.out.println(generateLayer(counter, appearance, e.getKey()));
					// search can end
					i = m_animals.size();
				}
			}
		}
		
		//m_musicstring = "T120 L1 " + foxTheme();
		Player player = new Player();
		Pattern pattern = new Pattern(m_musicstring);
		//player.saveMidi(pattern, new File("music-file.mid"));
		// play stuff
		player.play(pattern);
	}
	
	// get the number of sections subject to
	// the number of characters of the text
	private int getNumOfSections() {
		int numOfSections = 10 + (int) Math.log10(m_textLength);
		return numOfSections;
	}
	
	// get the tempo
	private String getTempo() {
		return "T120 ";
	}
	
	// use matching theme
	private String useTheme(String animal) {
		if (animal.equals("bird")) {
			return birdTheme();
		}
		else if (animal.equals("fox")) {
			return foxTheme();
		}
		else if (animal.equals("crow")) {
			return crowTheme();
		}
		else if (animal.equals("swallow")) {
			return swallowTheme();
		}
		else if (animal.equals("dog")) {
			return dogTheme();
		}
		else {
			return silenceTheme();
		}
	}
	
	// generate animal music layer
	private String generateLayer(int layernumber, int[] times, String animal) {
		String layer = "L" + Integer.toString(layernumber) + " ";
		for (int i = 0; i < times.length; i++) {
			if (times[i] == 0) {
				layer += silenceTheme() + " ";
			}
			if (times[i] > 0) {
				layer += useTheme(animal) + " ";
			}
		}
		return layer;
	}
	
	// animal themes
	// each has the same length (4 beats/Takte)
	private String birdTheme() {
		String music = "36/1.0 32/0.5 34/0.5 36/1.0 24/1.0";
		return music;
	}
	private String foxTheme() {
		String music = "67/0.125 R/0.125 71/0.125 69/0.125 67/0.125 R/0.125 71/0.125 69/0.125 " +
				"65/0.25 65/0.25 67/0.25 67/0.25";
		return music;
	}
	private String crowTheme() {
		String music = "R/0.25 40/0.25 41/0.25 43/0.25 R/0.25 40/0.25 41/0.25 43/0.25 " +
				"45/0.25 43/0.25 36/0.25 41/0.25 41/0.5 R/0.5";
		return music;
	}
	private String swallowTheme() {
		String music = "55/0.5 60/0.5 59/0.375 57/0.125 55/0.25 55/0.25 48/1.0 R/1.0";
		return music;
	}
	private String dogTheme() {
		String music = "65/0.125 R/0.125 65/0.125 R/0.125 R/0.5 65/0.125 R/0.125 R/0.5 " +
				"60/0.25 59/0.25 R/0.5 67/0.5 60/0.5";
		return music;
	}
	private String silenceTheme() {
		String silence = "R/4.0";
		return silence;
	}

}
