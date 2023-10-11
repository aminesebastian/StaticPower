package theking530.api.upgrades;

public class UpgradeType<T> {
	private final T defaultValue;

	public UpgradeType(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public T getDefaultValue() {
		return defaultValue;
	}
}
