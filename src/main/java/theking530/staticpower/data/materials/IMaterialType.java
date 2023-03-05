package theking530.staticpower.data.materials;

public interface IMaterialType<T> {
	public String getName();

	public Class<?> getType();

	public String getNameFormat();

	public String getTagFormat();

}
