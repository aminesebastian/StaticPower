package theking530.staticcore.data;

@FunctionalInterface
public interface IStaticCoreGameDataFactory {
	public StaticCoreGameData createGameDataInstance(boolean isClientSide);
}
