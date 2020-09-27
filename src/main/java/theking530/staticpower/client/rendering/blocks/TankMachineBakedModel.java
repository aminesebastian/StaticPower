package theking530.staticpower.client.rendering.blocks;

import static net.minecraftforge.client.model.SimpleModelTransform.IDENTITY;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList.Builder;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockFaceUV;
import net.minecraft.client.renderer.model.BlockPartFace;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

@OnlyIn(Dist.CLIENT)
public class TankMachineBakedModel extends DefaultMachineBakedModel {
	private static final Map<MachineSideMode, BlockFaceUV> SIDE_MODE_UV_LAYOUTS = new HashMap<MachineSideMode, BlockFaceUV>();
	private static final Map<MachineSideMode, BlockFaceUV> TOP_MODE_UV_LAYOUTS = new HashMap<MachineSideMode, BlockFaceUV>();

	static {
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Input, new BlockFaceUV(new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, 0));
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Output, new BlockFaceUV(new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, 0));
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Disabled, new BlockFaceUV(new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, 0));

		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Input, new BlockFaceUV(new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, 0));
		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Output, new BlockFaceUV(new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, 0));
		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Disabled, new BlockFaceUV(new float[] { 0.0f, 0.0f, 0.0f, 0.0f }, 0));
	}

	public TankMachineBakedModel(IBakedModel baseModel) {
		super(baseModel);
	}

	@Override
	protected TextureAtlasSprite getSpriteForMachineSide(MachineSideMode mode, AtlasTexture blocksStitchedTextures, Direction side) {
		return blocksStitchedTextures.getSprite(StaticPowerSprites.TANK_SIDE);
	}

	@Override
	protected void renderQuadsForSide(Builder<BakedQuad> newQuads, Direction side, TextureAtlasSprite sideSprite, BakedQuad originalQuad, MachineSideMode sideConfiguration) {
		SIDE_MODE_UV_LAYOUTS.clear();
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Input, new BlockFaceUV(new float[] { 10.0f, 0.0f, 13.0f, 3.0f }, 0));
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Output, new BlockFaceUV(new float[] { 13.0f, 6.0f, 16.0f, 9.0f }, 0));
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Disabled, new BlockFaceUV(new float[] { 10.0f, 3.0f, 13.0f, 6.0f }, 0));
		TOP_MODE_UV_LAYOUTS.clear();
		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Input, new BlockFaceUV(new float[] { 7.0f, 0.0f, 10.0f, 3.0f }, 0));
		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Output, new BlockFaceUV(new float[] { 10.0f, 6.0f, 13.0f, 9.0f }, 0));
		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Disabled, new BlockFaceUV(new float[] { 7.0f, 3.0f, 10.0f, 6.0f }, 0));
		
		
		newQuads.add(originalQuad);
		if (sideConfiguration != MachineSideMode.Regular) {
			if (side.getAxis() == Direction.Axis.Y) {
				BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), TOP_MODE_UV_LAYOUTS.get(sideConfiguration));
				BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(5.0f, -0.01f, 5.0f), new Vector3f(11.0f, 16.01f, 11.0f), blockPartFace, sideSprite, side, IDENTITY, null, true, new ResourceLocation("dummy_name"));
				newQuads.add(newQuad);
			} else {
				BlockPartFace blockPartFace = new BlockPartFace(null, -1, sideSprite.getName().toString(), SIDE_MODE_UV_LAYOUTS.get(sideConfiguration));
				if (side.getAxis() == Direction.Axis.X) {
					BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(0, 5.0f, 5.0f), new Vector3f(16.0f, 11.0f, 11.0f), blockPartFace, sideSprite, side, IDENTITY, null, true, new ResourceLocation("dummy_name"));
					newQuads.add(newQuad);
				} else {
					BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(5.0f, 5.0f, 0.0f), new Vector3f(11.0f, 11.0f, 16.0f), blockPartFace, sideSprite, side, IDENTITY, null, true, new ResourceLocation("dummy_name"));
					newQuads.add(newQuad);
				}
			}
		}
	}

}
