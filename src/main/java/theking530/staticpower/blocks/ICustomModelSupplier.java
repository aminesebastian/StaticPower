package theking530.staticpower.blocks;

import net.minecraft.client.renderer.model.IBakedModel;

public interface ICustomModelSupplier {
	public boolean hasModelOverride();

	public IBakedModel getModelOverride(IBakedModel originalModel);
}
