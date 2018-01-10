import java.io.IOException;
import java.util.Map;

public class ProcessedResult {	
	
	// --------------------------------------------------------
	// Members
	// --------------------------------------------------------

	private int mTextLength;
	private int mNumLines;
	//private int mNumWords;
	private Map<String, TargetInfo> mOccurenceInfos;
	
	
	// --------------------------------------------------------
	// Constructors
	// --------------------------------------------------------
	
	public ProcessedResult(int textLength, int numLines,
			Map<String, TargetInfo> occInfos) throws IOException {
		mTextLength = textLength;
		mNumLines = numLines;
		mOccurenceInfos = occInfos;
	}
	
	
	// --------------------------------------------------------
	// Getters
	// --------------------------------------------------------

	public int getTextLength() {
		return mTextLength;
	}
	public int getNumLines() {
		return mNumLines;
	}
//	public int getmNumWords() {
//		return mNumWords;
//	}
	public Map<String, TargetInfo> getOccurenceInfos() {
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
	public void setmOccurenceInfos(Map<String, TargetInfo> occurenceInfos) {
		this.mOccurenceInfos = occurenceInfos;
	}
	
	
	// --------------------------------------------------------
	// Methods
	// --------------------------------------------------------

	public void printRes() {
		System.out.println("Processed text length: " + mTextLength);
		System.out.println("Number of lines: " + mNumLines);
		for (Map.Entry<String, TargetInfo> entry : mOccurenceInfos.entrySet()) {
			entry.getValue().printInfo();
		}
	}
}
