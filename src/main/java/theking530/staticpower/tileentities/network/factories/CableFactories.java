package theking530.staticpower.tileentities.network.factories;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.CableType;

public class CableFactories {
	private static final CableFactories INSTANCE = new CableFactories();
	private HashMap<CableType, ICableWrapperFactory> Factories = new HashMap<CableType, ICableWrapperFactory>();

	public static CableFactories get() {
		return INSTANCE;
	}

	public void registerCableWrapperFactory(CableType type, ICableWrapperFactory factory) {
		Factories.put(type, factory);
	}

	public AbstractCableWrapper create(CableType type, World world, CompoundNBT nbt) {
		if (Factories.containsKey(type)) {
			return Factories.get(type).create(world, nbt);
		}
		throw new RuntimeException(String.format("Factory not registered for cable type: %1$s.", type));
	}
}
