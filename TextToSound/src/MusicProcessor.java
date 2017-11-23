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
		animals = Files.readAllLines(Paths.get("data/lexicon_animals.txt"), StandardCharsets.UTF_8);
		musicstring = "";
	};
	
	public MusicProcessor(ProcessResult txtRes) throws IOException {
		Map<String, TargetOccurenceInfo> map = txtRes.getmOccurenceInfos();
		
		textInput = map;
		animals = Files.readAllLines(Paths.get("data/lexicon_animals.txt"), StandardCharsets.UTF_8);
		musicstring = "";
	}
	
	// --------------------------------------------------------
	// music-processing methods
	// --------------------------------------------------------
	
	public void process() {
		int counter = 0;
		// tempo definition
		musicstring += "T200 ";
		// for each appearing animal
		for (Entry<String, TargetOccurenceInfo> e : textInput.entrySet()) {
			// check animal preset list
			for (int i = 0; i < animals.size(); i++) {
				// if existing
				if (e.getKey().equals(animals.get(i))) {
					counter += 1;
					// ten zeros
					int[] appearance = {0,0,0,0,0,0,0,0,0,0,0};
					List<Float> percentList = e.getValue().getRelativPosOccurence();
					// ten sections
					for (int j = 1; j <= 10; j++) {
						for (int k = 0; k < percentList.size(); k++) {
							// match appearance?
							if (percentList.get(k) < (float) j*10 && percentList.get(k) > (float) (j-1)*10) {
								appearance[j] = ++appearance[j];
							}
						}
					}
					// now the appearance of the animal i is saved
					// generate music for the animal i
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
		// play stuff
		player.play(pattern);
	}
	
	private String generateSection(int[] appearance, int animalnumber, int counter) {
		// L for new layer (melody)
		String s = "L" + Integer.toString(counter) + " ";
		String octave = "";
		// animalnumber sets note
		String note = Integer.toString(animalnumber + 50);
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
				// rest
				s += "Rw ";
			}
		}
		return s;
	}

}
