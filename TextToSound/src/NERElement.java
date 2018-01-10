public class NERElement {
    private String name;
	private int SentenceID;
	private int TokenID;
	private double TotalPosition;
	private double RelativePosition;
	
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
    public double getTotalPosition() {
        return TotalPosition;
    }
    
    public void setTotalPosition(double TotalPosition) {
        this.TotalPosition = TotalPosition;
    }
    
    public double getRelativePosition() {
        return RelativePosition;
    }
    
    public void setRelativePosition(double RelativePosition) {
        this.RelativePosition = RelativePosition;
    }
    
    @Override
    public String toString() {
    	return this.name + ": " + this.SentenceID + " " + this.TokenID + "; " + this.TotalPosition + "; " + this.RelativePosition + "\n";
    	//return this.name + ", in Sentence #" + this.SentenceID + "and at position " + this.TokenID + " with the absolute position" + this.TotalPosition + "\n";
    }
    
    

}