import java.util.Map;

public class ProcessResAnimal {	
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------

	private int mTextLength;
	private int mNumLines;
	//private int mNumWords;
	private Map<String, TargetOccurenceInfo> mOccurenceInfos;
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	
	public ProcessResAnimal(int textLength, int numLines,
			Map<String, TargetOccurenceInfo> occInfos) {
		mTextLength = textLength;
		mNumLines = numLines;
		mOccurenceInfos = occInfos;
	}
	
	
	// --------------------------------------------------------
	// Getters
	// --------------------------------------------------------

	public int getmTextLength() {
		return mTextLength;
	}
	public int getmNumLines() {
		return mNumLines;
	}
//	public int getmNumWords() {
//		return mNumWords;
//	}
	public Map<String, TargetOccurenceInfo> getmOccurenceInfos() {
		return mOccurenceInfos;
	}
	
	
	// --------------------------------------------------------
	// Setters
	// --------------------------------------------------------

	public void setmTextLength(int textLength) {
		this.mTextLength = textLength;
	}
	public void setmNumLines(int numLines) {
		this.mNumLines = numLines;
	}
//	public void setmNumWords(int numWords) {
//		this.mNumWords = numWords;
//	}
	public void setmOccurenceInfos(Map<String, TargetOccurenceInfo> occurenceInfos) {
		this.mOccurenceInfos = occurenceInfos;
	}
	
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	public void printRes() {
		for (Map.Entry<String, TargetOccurenceInfo> entry : mOccurenceInfos.entrySet()) {
			entry.getValue().printInfo();
		}
	}
}
