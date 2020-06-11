package theking530.staticpower.tileentities.network.factories.cables;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;

public class CableWrapperRegistry {
	private static final CableWrapperRegistry INSTANCE = new CableWrapperRegistry();
	private HashMap<ResourceLocation, ICableWrapperFactory> Factories = new HashMap<ResourceLocation, ICableWrapperFactory>();

	public static CableWrapperRegistry get() {
		return INSTANCE;
	}

	public void registerCableWrapperFactory(ResourceLocation type, ICableWrapperFactory factory) {
		Factories.put(type, factory);
	}

	public AbstractCableWrapper create(ResourceLocation type, World world, CompoundNBT nbt) {
		if (Factories.containsKey(type)) {
			return Factories.get(type).create(world, nbt);
		}
		throw new RuntimeException(String.format("Factory not registered for cable type: %1$s.", type));
	}

	public AbstractCableWrapper create(ResourceLocation type, World world, BlockPos position) {
		if (Factories.containsKey(type)) {
			return Factories.get(type).create(world, position);
		}
		throw new RuntimeException(String.format("Factory not registered for cable type: %1$s.", type));
	}
}
