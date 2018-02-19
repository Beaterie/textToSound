import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	
	
	// --------------------------------------------------------
	// Main method for testing
	// --------------------------------------------------------
	public static void main(String[] args) throws IOException {
		String srcFile = "data/the-happy-prince.txt";
		TextLexProcessor processor = new TextLexProcessor(srcFile, "data/lexicon_environment.txt");
		ProcessedResult result = processor.process();
		result.printRes();
		
		AmbientSoundGenerator soundGenerator  = new AmbientSoundGenerator(srcFile, 10);
	}
}
