public class NERElement {
    private String name;
	private int SentenceID;
	private int TokenID;
	private int TotalPosition;
	
	public int getSentenceID() {
        return SentenceID;
    }
    public void setSentenceID(int SentenceID) {
        this.SentenceID = SentenceID;
    }
    public int getTokenID() {
        return TokenID;
    }
    public void setTokenID(int TokenID) {
        this.TokenID = TokenID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTotalPosition() {
        return TotalPosition;
    }
    
    public void setTotalPosition(int TotalPosition) {
        this.TotalPosition = TotalPosition;
    }
    
    @Override
    public String toString() {
    	return this.name + ", in Satz #" + this.SentenceID + "and at position " + this.TokenID + " with the absolute position" + this.TotalPosition + "\n";
    }

}