import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class generates ambient sound background for the MIDI music.
 * The lexicon_environment is used to detect which environments are 
 * present in the text and where they are present. The Generator will 
 * mix sounds from EnvironmentSoundLib according to the text info to a
 * single ambient sound file.
 * @author jiani
 *
 */
public class AmbientSoundGenerator {
	
	private int mNumSections;
	private String mSrcFileName;
	
	// --------------------------------------------------------
	// Constructor
	// --------------------------------------------------------
	public AmbientSoundGenerator(String srcFile, int numSections) {
		mNumSections = numSections;
		mSrcFileName = srcFile;
	}
	
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	
	/**
	 * The map that maps the environment vocabulary to the sounds
	 * in the sound library.
	 * <BR><BR>
	 * The key of the map is the name of the environment, and the 
	 * value is an array of the sound files' names.
	 */
	private final Map<String, String[]> EnvironmentSoundMap;
	{
		EnvironmentSoundMap = new HashMap<String, String[]>();
		EnvironmentSoundMap.put("bush", new String[]{"bushes.mp3"});
		EnvironmentSoundMap.put("lightning", new String[] {"lightning1.mp3"});
		EnvironmentSoundMap.put("rain", new String[] {"rain.mp3"});
		EnvironmentSoundMap.put("rainy", new String[] {"rain.mp3"});
		EnvironmentSoundMap.put("sea", new String[] {"sea.mp3"});
		EnvironmentSoundMap.put("shore", new String[] {"sea.mp3"});
		EnvironmentSoundMap.put("river", new String[] {"spring-river-lake.mp3","spring-river.mp3"});
		EnvironmentSoundMap.put("lake", new String[] {"spring-river-lake.mp3","spring-river.mp3"});
		EnvironmentSoundMap.put("thunder", new String[] {"thunder1.mp3"});
		EnvironmentSoundMap.put("wind", new String[] {"wind1.mp3"});
		EnvironmentSoundMap.put("forest", new String[] {"forest-woods-mountain.mp3"});
		EnvironmentSoundMap.put("woods", new String[] {"forest-woods-mountain.mp3"});
		EnvironmentSoundMap.put("park", new String[] {"park-peaceful.mp3"});
		EnvironmentSoundMap.put("plaza", new String[] {"plaza-city.mp3"});
		EnvironmentSoundMap.put("market", new String[] {"market.mp3"});
		EnvironmentSoundMap.put("bonfire", new String[] {"bonfire.mp3"});
		EnvironmentSoundMap.put("city", new String[] {"plaza-city.mp3", "city-cars-street.mp3"});
		EnvironmentSoundMap.put("town", new String[] {"town.mp3"});
		EnvironmentSoundMap.put("car", new String[] {"city-cars-street.mp3"});
		EnvironmentSoundMap.put("street", new String[] {"city-cars-street.mp3"});
		EnvironmentSoundMap.put("church", new String[] {"church.mp3"});
		EnvironmentSoundMap.put("mountain", new String[] {"forest-woods-mountain.mp3"});
	}
	
	private final String EnvironmentSoundLibPath = "data/EnvironmentSoundLib/";
	
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------
	
	/**
	 * Store the relative occurrence info of the environment into sections
	 * for further computing.
	 */
	private ArrayList<ArrayList<Float>> genSectionInfo(ArrayList<Float> relativOccPos) {
		ArrayList<ArrayList<Float>> occInfoInSections = new ArrayList<>();
		for (int j = 0; j < 100; j+=100/mNumSections) {
			ArrayList<Float> section = new ArrayList<>();
			for (Float position : relativOccPos) {
				if (position < j || position == j) {
					section.add(position);
				}
			}
		}
		return occInfoInSections;
	}
	
	/**
	 * Generate an audio file for a single environment.
	 * (If this environment occurs in a section, the corresponding sound will 
	 * be played in this section.)
	 * @return name of the newly mixed file
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	private String mixAmbientSound(String environmentName, ArrayList<ArrayList<Float>> occInfoSections) 
			throws UnsupportedAudioFileException, IOException {
		AudioInputStream sound = AudioSystem.getAudioInputStream( new File(
				EnvironmentSoundLibPath + 
				EnvironmentSoundMap.get(environmentName)[new Random().nextBoolean()? 0:1]));
		
		return "";
	}
	
	/**
	 * Merge the generated mp3 files for each environment into a new file
	 */
	private void mergeParts() {
		
	}
	
	/**
	 * Generate ambient sound mix for the text.
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	public void generateAmbient(ProcessedResult result, int numSections) 
			throws UnsupportedAudioFileException, IOException {
		// Initialize ArrayList containing the generated audio file Paths
		ArrayList<String> audioFiles = new ArrayList<>();
		// Generate a new audio file for all environments found according to their occurrence info
		for (Map.Entry<String, TargetInfo> entry : result.getOccurenceInfos().entrySet()) {
			
			String environment = entry.getKey();
			// The relative occurrence info of current environment entry
			ArrayList<Float> relOccInfo = entry.getValue().getRelativPosOccurence();
			// Store the occurrence info into sections 
			ArrayList<ArrayList<Float>> occInfoInSections = genSectionInfo(relOccInfo);
			String newFileName = mixAmbientSound(environment, occInfoInSections);
			audioFiles.add(newFileName);
		}
		
		mergeParts();
	}
	
	
	// --------------------------------------------------------
	// Main method for testing
	// --------------------------------------------------------
	public static void main(String[] args) throws IOException {
		String srcFile = "data/the-happy-prince.txt";
		TextLexProcessor processor = new TextLexProcessor(srcFile, "data/lexicon_environment.txt");
		ProcessedResult result = processor.process();
		result.printRes();
		
		AmbientSoundGenerator soundGenerator  = new AmbientSoundGenerator(srcFile, 10);
		//soundGenerator.generateAmbient();
		
	}
}
