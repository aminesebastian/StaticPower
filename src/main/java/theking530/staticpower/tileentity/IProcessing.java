package theking530.staticpower.tileentity;

public interface IProcessing {
	
	public int getProcessingTime();
	public int getCurrentProgress();
	public float getProcessingPercentage();
	public boolean isProcessing();
}
