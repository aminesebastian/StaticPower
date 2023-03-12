package theking530.api.attributes.rendering;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import theking530.api.attributes.capability.IAttributable;

public class AttributeRenderLayer extends AbstractAttributeRenderLayer {
	private ResourceLocation modelLocation;

	/**
	 * Creates a basic layer from a model.
	 *
	 * @param layer
	 * @param modelLocation
	 */
	public AttributeRenderLayer(ResourceLocation modelLocation, int layer) {
		super(layer);
		this.modelLocation = modelLocation;
	}

	@Override
	public List<BakedQuad> getQuads(ItemStack stack, IAttributable attributable, BlockState state, Direction side, RandomSource rand, ModelData data) {
		return Minecraft.getInstance().getModelManager().getModel(modelLocation).getQuads(state, side, rand, data, null);
	}
}