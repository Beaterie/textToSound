
public class EmotionElement {
	private String name;
	private boolean anger = false;
	private boolean anticipation = false;
	private boolean disgust = false;
	private boolean fear = false;
	private boolean joy = false;
	private boolean sadness = false;
	private boolean surprise = false;
	private boolean trust = false;
	private boolean positive = false;
	private boolean negative = false;
	
	
	//THE WORD ITSELF
	public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
	
    //ANGER
    public boolean getAnger() {
    	return anger;
    }
    
    public void setAnger(boolean anger) {
    	this.anger = anger;
    }
    
    //ANTICIPATION
    public boolean getAnticipation() {
    	return anticipation;
    }
    
    public void setAnticipation(boolean anticipation) {
    	this.anticipation = anticipation;
    }
    
    //DISGUST
    public boolean getDisgust() {
    	return disgust;
    }
    
    public void setDisgust(boolean disgust) {
    	this.disgust = disgust;
    }
    
    //FEAR
    public boolean getFear() {
    	return fear;
    }
    
    public void setFear(boolean fear) {
    	this.fear = fear;
    }

    //JOY
    public boolean getJoy() {
    	return joy;
    }
    
    public void setJoy(boolean joy) {
    	this.joy = joy;
    }
    
    //SADNESS
    public boolean getSadness() {
    	return sadness;
    }
    
    public void setSadness(boolean sadness) {
    	this.sadness = sadness;
    }
    
    //SURPRISE
    public boolean getSurprise() {
    	return surprise;
    }
    
    public void setSurprise(boolean surprise) {
    	this.surprise = surprise;
    }
    
    //TRUST
    public boolean getTrust() {
    	return trust;
    }
    
    public void setTrust(boolean trust) {
    	this.trust = trust;
    }
    
    //POSITIVE
    public boolean getPositive() {
    	return positive;
    }
    
    public void setPositive(boolean positive) {
    	this.positive = positive;
    }
    
    //NEGATIVE
    public boolean getNegative() {
    	return negative;
    }
    
    public void setNegative(boolean negative) {
    	this.negative = negative;
    }
    
    
    @Override
    public String toString() {
    	return this.name + ": " + 	this.anger + " "+ this.anticipation+ " " + this.disgust+ " " + this.fear+ " " + this.joy+ " " + this.sadness+ " " + this.surprise+ " " + this.trust + " "+ this.positive+ " " + this.negative + "\n";
    }
    

    
}
