package theking530.staticcore.client;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;

public interface ICustomModelProvider {

	@OnlyIn(Dist.CLIENT)
	public boolean hasModelOverride(BlockState state);

	@OnlyIn(Dist.CLIENT)
	public default BakedModel getBlockModeOverride(BlockState state, @Nullable BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	public default BakedModel getItemModelOverride(ModelEvent.BakingCompleted event) {
		return null;
	}
}
