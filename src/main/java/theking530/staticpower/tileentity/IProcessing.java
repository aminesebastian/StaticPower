package theking530.staticpower.tileentity;

public interface IProcessing {
	
	public int getProcessingTime();
	public int getProcessingProgress();
	public float getProcessingPercentage();
	public boolean isProcessing();
}
