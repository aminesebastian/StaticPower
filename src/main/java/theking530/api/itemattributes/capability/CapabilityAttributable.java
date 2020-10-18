package theking530.api.itemattributes.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityAttributable {
	@CapabilityInject(IAttributable.class)
	public static Capability<IAttributable> ATTRIBUTABLE_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IAttributable.class, new Capability.IStorage<IAttributable>() {
			@Override
			public INBT writeNBT(Capability<IAttributable> capability, IAttributable instance, Direction side) {
				if (!(instance instanceof AttributeableHandler)) {
					throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
				}
				return ((AttributeableHandler) instance).serializeNBT();
			}

			@Override
			public void readNBT(Capability<IAttributable> capability, IAttributable instance, Direction side, INBT base) {
				if (!(instance instanceof AttributeableHandler)) {
					throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
				}
				((AttributeableHandler) instance).deserializeNBT((CompoundNBT) base);
			}
		}, () -> new AttributeableHandler("default"));
	}
}
