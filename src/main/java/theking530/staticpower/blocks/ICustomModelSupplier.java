package theking530.staticpower.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;

public interface ICustomModelSupplier {
	public boolean hasModelOverride(BlockState state);
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event);
	public default void registerAdditionalModels() {
		
	}
}
