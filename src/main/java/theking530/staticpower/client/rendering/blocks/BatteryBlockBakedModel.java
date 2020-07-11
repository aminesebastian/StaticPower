package theking530.staticpower.client.rendering.blocks;

import static net.minecraftforge.client.model.SimpleModelTransform.IDENTITY;

import com.google.common.collect.ImmutableList.Builder;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class BatteryBlockBakedModel extends DefaultMachineBakedModel {
	public BatteryBlockBakedModel(IBakedModel baseModel) {
		super(baseModel);
	}

	@Override
	protected TextureAtlasSprite getSpriteForMachineSide(MachineSideMode mode, AtlasTexture blocksStitchedTextures, Direction side) {
		if (side.getAxis() == Direction.Axis.Y) {
			return blocksStitchedTextures.getSprite(StaticPowerSprites.BASIC_BATTERY_TOP);
		}
		return blocksStitchedTextures.getSprite(StaticPowerSprites.BASIC_BATTERY_SIDE);
	}

	@Override
	protected void renderQuadsForSide(Builder<BakedQuad> newQuads, Direction side, TextureAtlasSprite sideSprite, BakedQuad originalQuad, MachineSideMode sideConfiguration) {
		if (sideConfiguration != MachineSideMode.Never) {
			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
			BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), blockFaceUV);
			BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(0, 0, 0), new Vector3f(16.0f, 16.0f, 16.0f), blockPartFace, sideSprite, side, IDENTITY, null, true, new ResourceLocation("dummy_name"));
			newQuads.add(newQuad);
		} else {
			newQuads.add(originalQuad);
		}
	}
}
