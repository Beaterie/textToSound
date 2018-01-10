
public class SectionElement {
	
	private int anger;
	private int anticipation;
	private int disgust;
	private int fear;
	private int joy;
	private int sadness;
	private int surprise;
	private int trust;
	private int positive;
	private int negative;
	
	
    //ANGER
    public int getAnger() {
    	return anger;
    }
    
    public void setAnger(int anger) {
    	this.anger = anger;
    }
    
    //ANTICIPATION
    public int getAnticipation() {
    	return anticipation;
    }
    
    public void setAnticipation(int anticipation) {
    	this.anticipation = anticipation;
    }
    
    //DISGUST
    public int getDisgust() {
    	return disgust;
    }
    
    public void setDisgust(int disgust) {
    	this.disgust = disgust;
    }
    
    //FEAR
    public int getFear() {
    	return fear;
    }
    
    public void setFear(int fear) {
    	this.fear = fear;
    }

    //JOY
    public int getJoy() {
    	return joy;
    }
    
    public void setJoy(int joy) {
    	this.joy = joy;
    }
    
    //SADNESS
    public int getSadness() {
    	return sadness;
    }
    
    public void setSadness(int sadness) {
    	this.sadness = sadness;
    }
    
    //SURPRISE
    public int getSurprise() {
    	return surprise;
    }
    
    public void setSurprise(int surprise) {
    	this.surprise = surprise;
    }
    
    //TRUST
    public int getTrust() {
    	return trust;
    }
    
    public void setTrust(int trust) {
    	this.trust = trust;
    }
    
    //POSITIVE
    public int getPositive() {
    	return positive;
    }
    
    public void setPositive(int positive) {
    	this.positive = positive;
    }
    
    //NEGATIVE
    public int getNegative() {
    	return negative;
    }
    
    public void setNegative(int negative) {
    	this.negative = negative;
    }
    
    
    @Override
    public String toString() {
    	return this.anger + " "+ this.anticipation+ " " + this.disgust+ " " + this.fear+ " " + this.joy+ " " + this.sadness+ " " + this.surprise+ " " + this.trust + " "+ this.positive+ " " + this.negative + "\n";
    }

}
