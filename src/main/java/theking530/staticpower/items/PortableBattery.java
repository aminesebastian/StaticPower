package theking530.staticpower.items;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.items.PortableBatteryItemModel;

public class PortableBattery extends StaticPowerEnergyStoringItem implements ICustomModelSupplier {
	public final ResourceLocation tier;

	public PortableBattery(ResourceLocation tier) {
		super(0);
		this.tier = tier;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new PortableBatteryItemModel(existingModel);
	}

	@Override
	public long getCapacity() {
		return StaticPowerConfig.getTier(tier).portableBatteryCapacity.get();
	}
}
