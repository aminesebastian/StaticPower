package theking530.staticcore.item;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;

public interface ICustomModelSupplier {
	@OnlyIn(Dist.CLIENT)	
	public boolean hasModelOverride(BlockState state);
	@OnlyIn(Dist.CLIENT)	
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelBakeEvent event);
}
