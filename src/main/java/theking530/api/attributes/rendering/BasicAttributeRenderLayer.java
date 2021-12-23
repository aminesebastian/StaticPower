package theking530.api.attributes.rendering;

import java.util.List;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import theking530.api.attributes.capability.IAttributable;

public class BasicAttributeRenderLayer extends AbstractAttributeRenderLayer {
	private ResourceLocation modelLocation;

	/**
	 * Creates a basic layer from a model.
	 * 
	 * @param attributeId
	 * @param layer
	 * @param modelLocation
	 */
	public BasicAttributeRenderLayer(ResourceLocation modelLocation, int layer) {
		super(layer);
		this.modelLocation = modelLocation;
	}

	@Override
	public List<BakedQuad> getQuads(ItemStack stack, IAttributable attributable, BlockState state, Direction side, Random rand, IModelData data) {
		return Minecraft.getInstance().getModelManager().getModel(modelLocation).getQuads(state, side, rand, data);
	}
}