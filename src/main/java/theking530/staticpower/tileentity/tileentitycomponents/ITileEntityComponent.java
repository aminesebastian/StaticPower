package theking530.staticpower.tileentity.tileentitycomponents;

public interface ITileEntityComponent {

	default public void preProcessUpdate() {
	}

	default public void postProcessUpdate() {
	}

	public String getComponentName();

	public boolean isEnabled();

	public void setEnabled(boolean isEnabled);
}
