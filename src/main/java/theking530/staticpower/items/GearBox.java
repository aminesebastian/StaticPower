package theking530.staticpower.items;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticpower.client.rendering.items.GearBoxModel;

public class GearBox extends StaticPowerItem implements ICustomModelProvider {
	public final RegistryObject<StaticPowerItem> baseGearItem;

	public GearBox(RegistryObject<StaticPowerItem> baseGearItem) {
		this.baseGearItem = baseGearItem;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new GearBoxModel(baseGearItem.get());
	}
}