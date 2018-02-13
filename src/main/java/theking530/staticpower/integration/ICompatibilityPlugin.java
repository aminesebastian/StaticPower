package theking530.staticpower.integration;

public interface ICompatibilityPlugin {

	public void register();
	
	public boolean shouldRegister();
	public boolean isRegistered();

	public String getPluginName();
}
