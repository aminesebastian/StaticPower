package theking530.staticpower.tileentity;

public interface ITileEntityComponent {

	public void preProcessUpdate();

	public void postProcessUpdate();

	public String getComponentName();

	public boolean isEnabled();

	public void setEnabled(boolean isEnabled);
}
