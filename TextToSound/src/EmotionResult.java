import java.util.List;

public class EmotionResult {
	private List<List<Double>> SectionEmotion;
	private List<List<Double>> Density;
	private Integer PosSum;
	private Integer NegSum;
	
	public  List<List<Double>> getSectionEmotion() {
        return SectionEmotion;
    }
    public void setSectionEmotion( List<List<Double>> SectionEmotion) {
        this.SectionEmotion = SectionEmotion;
    }
    
	public  List<List<Double>> getDensity() {
        return Density;
    }
    public void setDensity( List<List<Double>> Density) {
        this.Density = Density;
    }
	
	public  int getPosSum() {
        return PosSum;
    }
    public void setPosSum( int PosSum) {
        this.PosSum = PosSum;
    }
    
	public  int getNegSum() {
        return NegSum;
    }
    public void setNegSum( int NegSum) {
        this.NegSum = NegSum;
    }
    
    
    public void printResult() {
    	System.out.println("Emotion Analysis:");
    	System.out.println(SectionEmotion);
    	System.out.println("Density Analysis:");
    	System.out.println(Density);
    	System.out.println("Negative Words:" + NegSum);
    	System.out.println("Positive Words:" + PosSum);
    }
}