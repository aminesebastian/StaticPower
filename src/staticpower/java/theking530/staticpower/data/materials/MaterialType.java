package theking530.staticpower.data.materials;

public class MaterialType<T> implements IMaterialType<T> {

	private String name;
	private String nameFormat;
	private String tagFormat;
	private Class<?> type;

	public MaterialType(String name, String nameFormat, Class<?> type) {
		this(name, nameFormat, null, type);
	}

	public MaterialType(String name, String nameFormat, String tagFormat, Class<?> type) {
		if (nameFormat == null) {
			throw new RuntimeException("Cannot create a material type without a name format!");
		}
		this.name = name;
		this.nameFormat = nameFormat;
		this.tagFormat = tagFormat;
		this.type = type;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public String getNameFormat() {
		return nameFormat;
	}

	@Override
	public String getTagFormat() {
		return tagFormat;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
