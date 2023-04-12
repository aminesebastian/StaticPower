package theking530.staticcore.data;

@FunctionalInterface
public interface IStaticPowerGameDataFactory {
	public StaticPowerGameData createGameDataInstance(boolean isClientSide);
}
