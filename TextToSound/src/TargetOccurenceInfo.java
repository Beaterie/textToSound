import java.util.ArrayList;

public class TargetOccurenceInfo {
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------
	
	// Target string/token
	private String mTarget;
	
	// Character indexes of the target word
	private ArrayList<Integer> mOccurenceIndexes = new ArrayList<Integer>();
	
	// Percentage expression of occurence position
	private ArrayList<Float> mRelativOccPos = new ArrayList<Float>();
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	
	public TargetOccurenceInfo() {
	}
	
	public TargetOccurenceInfo(String target) {
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
	
	
	// --------------------------------------------------------
	// Setters
	// --------------------------------------------------------

	public void setTarget(String target) {
		mTarget = target;
	}

	public void setOccurenceIndexes(ArrayList<Integer> occurenceIndexes) {
		mOccurenceIndexes = occurenceIndexes;
	}

	public void setRelativPosOccurence(ArrayList<Float> relativPosOccurence) {
		mRelativOccPos = relativPosOccurence;
	}
	
	public void pushOccurenceIndexes(int index) {
		mOccurenceIndexes.add(index);
	}
	
	public void calcRelativOccPos(int textLength) {
		for (Integer matchPos : mOccurenceIndexes) {
			mRelativOccPos.add((float)(matchPos) / (float)(textLength));
		}
	}
	
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------
	
	public void printInfo() {
		System.out.println("==================================");
		System.out.println("Occurance info for " + mTarget);
		System.out.println("Total occurrences: " + mOccurenceIndexes.size());
		System.out.println("First occurence index: " + mOccurenceIndexes.get(0));
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
