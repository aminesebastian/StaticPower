package theking530.staticpower.machines.tileentitycomponents;

public interface ITileEntityComponent {

	public void update();
	public String getComponentName();
	public boolean isEnabled();
	public void setEnabled(boolean isEnabled);
}
