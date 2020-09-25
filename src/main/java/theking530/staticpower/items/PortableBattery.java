package theking530.staticpower.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.PortableBatteryItemModel;
import theking530.staticpower.data.TierReloadListener;

public class PortableBattery extends StaticPowerEnergyStoringItem implements ICustomModelSupplier {
	public final ResourceLocation tier;

	public PortableBattery(String name, ResourceLocation tier) {
		super(name, 0);
		this.tier = tier;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)	
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new PortableBatteryItemModel(existingModel);
	}

	@Override
	public int getCapacity() {
		return TierReloadListener.getTier(tier).getPortableBatteryCapacity();
	}
}
