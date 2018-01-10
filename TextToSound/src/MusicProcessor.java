import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
	private Map<String, TargetInfo> m_textInput;
	// animals
	//private List<String> m_animals;
	// music string
	private String m_musicstring;
	// number of characters of the text
	private int m_textLength;
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
		
	public MusicProcessor() {};

	public MusicProcessor(ProcessedResult txtRes) throws IOException {
		m_textInput = txtRes.getOccurenceInfos();
		//m_animals = Files.readAllLines(Paths.get("data/lexicon_animals.csv"), StandardCharsets.UTF_8);
		m_musicstring = "";
		m_textLength = txtRes.getTextLength();
	}
	
	
	// --------------------------------------------------------
	// music-processing methods
	// --------------------------------------------------------
	
	public void process() {
		
		System.out.println("Number of Sections: " + getNumOfSections());
		int counter = 0;
		int numOfSections = getNumOfSections();
		// tempo definition
		m_musicstring += getTempo();
		
		HashMap<Integer, String> majorKey = new HashMap<Integer, String>();
		majorKey.put(0,"C");
		majorKey.put(2,"D");
		majorKey.put(4,"E");
		majorKey.put(5,"F");
		majorKey.put(7,"G");
		majorKey.put(9,"A");
		majorKey.put(11,"B");
		
		HashMap<Integer, String> minorKey = new HashMap<Integer, String>();
		minorKey.put(0,"C");
		minorKey.put(2,"D");
		minorKey.put(3,"Eb");
		minorKey.put(5,"F");
		minorKey.put(7,"G");
		minorKey.put(8,"Ab");
		minorKey.put(10,"Bb");
		
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
		
		//m_musicstring = "T120 L1 " + foxTheme();
		Player player = new Player();
		Pattern pattern = new Pattern(m_musicstring);
		System.out.println(m_musicstring);
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
	
	// generate animal music layer
//	private String generateLayer(int layernumber, int[] times, String animal) {
//		String layer = "L" + Integer.toString(layernumber) + " ";
//		for (int i = 0; i < times.length; i++) {
//			if (times[i] == 0) {
//				layer += silenceTheme() + " ";
//			}
//			if (times[i] > 0) {
//				layer += useTheme(animal) + " ";
//			}
//		}
//		return layer;
//	}
	
	// generate musical animal theme
	private void generateAnimalTheme(TargetInfo animal) {
		String theme = "";
		Boolean animal_character = animal.getTargetCharacter();
		int animal_size = animal.getTargetPhys();
		String animal_name = animal.getTarget();
		
		animal.setTheme(theme);
	}

}
