package theking530.staticpower.machines.machinecomponents;

public class BaseComponent {

	private String COMPONENT_NAME;
	
	public BaseComponent(String componentName) {
		COMPONENT_NAME = componentName;
	}
	public String getComponentName() {
		return COMPONENT_NAME;
	}
}
