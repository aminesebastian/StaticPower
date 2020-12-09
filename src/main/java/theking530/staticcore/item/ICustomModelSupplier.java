package theking530.staticcore.item;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;

public interface ICustomModelSupplier {
	@OnlyIn(Dist.CLIENT)	
	public boolean hasModelOverride(BlockState state);
	@OnlyIn(Dist.CLIENT)	
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event);
}
