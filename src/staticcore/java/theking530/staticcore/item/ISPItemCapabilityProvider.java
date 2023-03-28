package theking530.staticcore.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface ISPItemCapabilityProvider extends ICapabilitySerializable<CompoundTag> {
	public String getName();
}
