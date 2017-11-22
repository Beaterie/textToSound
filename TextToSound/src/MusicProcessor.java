import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public MusicProcessor(Map<String, TargetOccurenceInfo> ti, int l) throws IOException {
		textInput = ti;
		length = l;
		animals = Files.readAllLines(Paths.get("../data/lexicon_animals.txt"), StandardCharsets.UTF_8);
		musicstring = "";
	};
	
	// --------------------------------------------------------
	// music-processing methods
	// --------------------------------------------------------
	
	public void process() {
		
		// for each appearing animal
		for (Entry<String, TargetOccurenceInfo> e : textInput.entrySet()) {
			// check animal preset list
			for (int i = 0; i < animals.size(); i++) {
				// if existing
				if (e.getKey() == animals.get(i)) {
					List<Integer> appearance = new ArrayList<Integer>(10);
					List<Float> percentList = e.getValue().getRelativPosOccurence();
					// ten sections
					for (int j = 10; j <= 100; j = j + 10) {
						for (int k = 0; k < percentList.size(); k++) {
							// match appearance?
							if (percentList.get(k) < (float) j && percentList.get(k) > (float) (j - 10)) {
								appearance.set(j, appearance.get(j)+1);
							}
						}
					}
					// now the appearance of the animal i is saved
					// generate music for the animal i
					System.out.println(animals.get(i) + " has this appearance-structure: " + appearance);
					// search can end
					i = animals.size();
				}
			}
		}
	}

}
