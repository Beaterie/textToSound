import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;

public class MusicProcessor {
	
	// --------------------------------------------------------
	// Member
	// --------------------------------------------------------
	
	// <animal, index of appear, percent>
	private Map<String, TargetOccurenceInfo> textInput;
	// length of full text
	private int length;
	// animals
	private List<String> animals;
	// music string
	private String musicstring;
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
		
	public MusicProcessor() {};
	
	public MusicProcessor(Map<String, TargetOccurenceInfo> ti) throws IOException {
		textInput = ti;
		length = 0;
		animals = Files.readAllLines(Paths.get("data/lexicon_animals.txt"), StandardCharsets.UTF_8);
		musicstring = "";
	};
	
	// --------------------------------------------------------
	// music-processing methods
	// --------------------------------------------------------
	
	public void process() {
		int counter = 0;
		musicstring += "T220 ";
		System.out.println("hier starte ich");
		// for each appearing animal
		for (Entry<String, TargetOccurenceInfo> e : textInput.entrySet()) {
			System.out.println("hier war ich");
			// check animal preset list
			for (int i = 0; i < animals.size(); i++) {
				System.out.println("hier bin ich so oft gewesen: " + i);
				// if existing
				if (e.getKey().equals(animals.get(i))) {
					System.out.println("MATCH");
					counter += 1;
					// ten zeros
					int[] appearance = {0,0,0,0,0,0,0,0,0,0,0};
					List<Float> percentList = e.getValue().getRelativPosOccurence();
					System.out.println("percentList size: " + percentList.size());
					// ten sections
					for (int j = 1; j <= 10; j++) {
						System.out.println("Abschnitt j: " + j);
						for (int k = 0; k < percentList.size(); k++) {
							System.out.println("Abschnitt k: " + k);
							// match appearance?
							if (percentList.get(k) < (float) j*10 && percentList.get(k) > (float) (j-1)*10) {
								appearance[j] = ++appearance[j];
							}
						}
					}
					// now the appearance of the animal i is saved
					// generate music for the animal i
					System.out.println(animals.get(i) + " has this appearance-structure:");
					for (int z = 0; z < appearance.length; z++) {
						System.out.println(appearance[z]);
					}
					musicstring += generateSection(appearance, i, counter);
					musicstring += " ";
					System.out.println(musicstring);
					// search can end
					i = animals.size();
				}
			}
		}
		Player player = new Player();
		Pattern pattern = new Pattern(musicstring);
		player.play(pattern);
	}
	
	private String generateSection(int[] appearance, int animalnumber, int counter) {
		String s = "L" + Integer.toString(counter) + " ";
		String octave = Integer.toString(animalnumber % 10 + 1);
		octave = "4";
		String note = "";
		if (counter == 1) {
			note = "C";
		}
		if (counter == 2) {
			note = "E";
		}
		if (counter == 3) {
			note = "G";
		}
		if (counter >= 4) {
			note = "C";
		}
		for (int i = 0; i < appearance.length; i++) {
			if (appearance[i] >= 3) {
				s += note + octave + "q ";
				s += note + octave + "q ";
				s += note + octave + "q ";
				s += note + octave + "q ";
			}
			else if (appearance[i] == 2) {
				s += note + octave + "h ";
				s += note + octave + "h ";
			}
			else if (appearance[i] == 1) {
				s += note + octave + "w ";
			}
			else {
				s += "Rw ";
			}
		}
		return s;
	}

}
