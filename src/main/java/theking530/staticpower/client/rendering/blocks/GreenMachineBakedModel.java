package theking530.staticpower.client.rendering.blocks;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class GreenMachineBakedModel extends DefaultMachineBakedModel {
	public GreenMachineBakedModel(IBakedModel baseModel) {
		super(baseModel);
	}

	@Override
	protected TextureAtlasSprite getSpriteForMachineSide(MachineSideMode mode, AtlasTexture blocksStitchedTextures) {
		switch (mode) {
		case Input:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.GREEN_MACHINE_SIDE_INPUT);
		case Output:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.GREEN_MACHINE_SIDE_OUTPUT);
		case Disabled:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.GREEN_MACHINE_SIDE_DISABLED);
		default:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.GREEN_MACHINE_SIDE_NORMAL);
		}
	}
}
