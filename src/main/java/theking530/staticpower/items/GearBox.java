package theking530.staticpower.items;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.GearBoxModel;

/**
 * Base class for most static power items.
 * 
 * @author Amine Sebastian
 *
 */
public class GearBox extends StaticPowerItem implements ICustomModelSupplier {
	public final Item baseGearItem;

	public GearBox(String name, Item baseGearItem) {
		super(name);
		this.baseGearItem = baseGearItem;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new GearBoxModel(baseGearItem);
	}
}
