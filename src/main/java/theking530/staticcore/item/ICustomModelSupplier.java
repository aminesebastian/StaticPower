package theking530.staticcore.item;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;

public interface ICustomModelSupplier {
	@OnlyIn(Dist.CLIENT)
	public boolean hasModelOverride(BlockState state);

	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event);

	public default BakedModel getBaseModelOverride(ModelEvent.BakingCompleted event) {
		return null;
	}
}
