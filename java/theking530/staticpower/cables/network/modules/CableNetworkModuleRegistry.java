package theking530.staticpower.cables.network.modules;

import java.util.HashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class CableNetworkModuleRegistry {
	private static final HashMap<ResourceLocation, ICableNetworkModuleFactory> FACTORIES = new HashMap<ResourceLocation, ICableNetworkModuleFactory>();

	public static void registerCableNetworkAttachmentFactory(ResourceLocation type, ICableNetworkModuleFactory factory) {
		FACTORIES.put(type, factory);
	}

	public static CableNetworkModule create(ResourceLocation type, CompoundTag nbt) {
		if (FACTORIES.containsKey(type)) {
			CableNetworkModule output = FACTORIES.get(type).create(type);
			output.readFromNbt(nbt);
			return output;
		}
		throw new RuntimeException(String.format("Factory not registered for network attachment type: %1$s.", type));
	}

	public static CableNetworkModule create(ResourceLocation type) {
		if (FACTORIES.containsKey(type)) {
			return FACTORIES.get(type).create(type);
		}
		throw new RuntimeException(String.format("Factory not registered for network attachment type: %1$s.", type));
	}
}
