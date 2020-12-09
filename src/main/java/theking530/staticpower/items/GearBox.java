package theking530.staticpower.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
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
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new GearBoxModel(baseGearItem);
	}
}
