package theking530.staticpower.cables.network;

import java.util.HashMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class CableNetworkModuleRegistry {
	private static final CableNetworkModuleRegistry INSTANCE = new CableNetworkModuleRegistry();
	private HashMap<ResourceLocation, ICableNetworkModuleFactory> Factories = new HashMap<ResourceLocation, ICableNetworkModuleFactory>();

	public static CableNetworkModuleRegistry get() {
		return INSTANCE;
	}

	public void registerCableNetworkAttachmentFactory(ResourceLocation type, ICableNetworkModuleFactory factory) {
		Factories.put(type, factory);
	}

	public AbstractCableNetworkModule create(ResourceLocation type, CompoundNBT nbt) {
		if (Factories.containsKey(type)) {
			AbstractCableNetworkModule output = Factories.get(type).create();
			output.readFromNbt(nbt);
			return output;
		}
		throw new RuntimeException(String.format("Factory not registered for network attachment type: %1$s.", type));
	}

	public AbstractCableNetworkModule create(ResourceLocation type) {
		if (Factories.containsKey(type)) {
			return Factories.get(type).create();
		}
		throw new RuntimeException(String.format("Factory not registered for network attachment type: %1$s.", type));
	}
}
