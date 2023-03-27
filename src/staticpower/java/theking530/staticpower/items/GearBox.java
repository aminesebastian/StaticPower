package theking530.staticpower.items;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticpower.client.rendering.items.GearBoxModel;
import theking530.staticpower.init.ModCreativeTabs;

public class GearBox extends StaticPowerItem implements ICustomModelProvider {

	public GearBox() {
		super(new Item.Properties().tab(ModCreativeTabs.MATERIALS));
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new GearBoxModel(existingModel);
	}
}