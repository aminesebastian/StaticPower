package theking530.staticpower.tileentities.cables;

public enum CableType {
	BASIC_POWER(BaseCableType.POWER);

	public enum BaseCableType {
		POWER, ITEM, FLUID
	}

	public BaseCableType[] SupportedTypes;

	CableType(BaseCableType... Types) {
		SupportedTypes = Types;
	}
}
