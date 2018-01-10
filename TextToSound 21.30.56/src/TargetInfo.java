import java.util.ArrayList;

public class TargetInfo {
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	
	private String mTarget;		// Target string/token
	private int mNumTotalOcc;	// Number of total occurrences
	private int mTargetPhys;	// Target physical size or age from the scale of 0-10
	private boolean mTargetCharacter;	// Target character good/evil (0/1)
	private String mTheme;		// Target music theme
	
	// Character indexes of the target word
	private ArrayList<Integer> mOccurenceIndexes = new ArrayList<Integer>();
	
	// Percentage expression of occurence position
	private ArrayList<Float> mRelativOccPos = new ArrayList<Float>();
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	
	public TargetInfo() {
	}
	
	public TargetInfo(String target) {
		mTarget = target;
	}
	
	
	// --------------------------------------------------------
	// Getters
	// --------------------------------------------------------

	public String getTarget() {
		return mTarget;
	}

	public ArrayList<Integer> getOccurenceIndexes() {
		return mOccurenceIndexes;
	}

	public ArrayList<Float> getRelativPosOccurence() {
		return mRelativOccPos;
	}

	public int getNumTotalOcc() {
		return mNumTotalOcc;
	}
	
	public int getTargetPhys() {
		return mTargetPhys;
	}
	
	public boolean getTargetCharacter() {
		return mTargetCharacter;
	}
	
	public String getTheme() {
		return mTheme;
	}
	
	
	// --------------------------------------------------------
	// Setters
	// --------------------------------------------------------

	public void setTarget(String target) {
		mTarget = target;
	}
	
	public void setTargetPhys(int physicalAttribute) {
		mTargetPhys = physicalAttribute;
	}
	
	public void setTargetCharacter(boolean character) {
		mTargetCharacter = character;
	}

	public void setOccurenceIndexes(ArrayList<Integer> occurenceIndexes) {
		mOccurenceIndexes = occurenceIndexes;
	}

	public void setRelativPosOccurence(ArrayList<Float> relativPosOccurence) {
		mRelativOccPos = relativPosOccurence;
	}
	
	public void setNumTotalOcc(int numTotalOcc) {
		mNumTotalOcc = numTotalOcc;
	}
	
	public void setTheme(String mTheme) {
		this.mTheme = mTheme;
	}
	
	public void pushOccurenceIndexes(int index) {
		mOccurenceIndexes.add(index);
	}
	
	public void calcRelativOccPos(int textLength) {
		for (Integer matchPos : mOccurenceIndexes) {
			mRelativOccPos.add((float)(matchPos) / (float)(textLength) * 100);
		}
	}
	
	public void calcTotalOcc() {
		mNumTotalOcc = mOccurenceIndexes.size();
	}
	
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------
	
	public void printInfo() {
		int character = mTargetCharacter ? 1 : 0;
		System.out.println("==================================");
		System.out.println(mTarget + " size/age: " + mTargetPhys);
		System.out.println(mTarget + " character: " + character + "\n");
		System.out.println("Total occurrences: " + mOccurenceIndexes.size());
		System.out.println("Last occurence index: " + mOccurenceIndexes.get(mOccurenceIndexes.size()-1));
		System.out.println("Last occurence is at " + (mRelativOccPos.get(mRelativOccPos.size()-1)) + " percent of whole text length.");
		System.out.println();
		for (Integer integer : mOccurenceIndexes) {
			System.out.print(integer + " ");
		}
		System.out.println();
		for (Float f : mRelativOccPos) {
			System.out.print(f + " ");
		}
		System.out.println();
		System.out.println("==================================");
	}


	
}
