package theking530.staticcore.data.gamedata;

@FunctionalInterface
public interface IStaticCoreGameDataFactory {
	public IStaticCoreGameData createGameDataInstance(boolean isClientSide);
}
