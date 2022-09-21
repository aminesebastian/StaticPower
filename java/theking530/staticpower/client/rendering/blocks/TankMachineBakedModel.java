package theking530.staticpower.client.rendering.blocks;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList.Builder;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.utilities.ModelUtilities;

@OnlyIn(Dist.CLIENT)
public class TankMachineBakedModel extends DefaultMachineBakedModel {
	private static final Map<MachineSideMode, BlockFaceUV> SIDE_MODE_UV_LAYOUTS = new HashMap<MachineSideMode, BlockFaceUV>();
	private static final Map<MachineSideMode, BlockFaceUV> TOP_MODE_UV_LAYOUTS = new HashMap<MachineSideMode, BlockFaceUV>();
	private final ResourceLocation baseTexture;

	static {
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Input, new BlockFaceUV(new float[] { 10.0f, 0.0f, 13.0f, 3.0f }, 0));
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Output, new BlockFaceUV(new float[] { 13.0f, 6.0f, 16.0f, 9.0f }, 0));
		SIDE_MODE_UV_LAYOUTS.put(MachineSideMode.Disabled,
				new BlockFaceUV(new float[] { 10.0f, 3.0f, 13.0f, 6.0f }, 0));

		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Input, new BlockFaceUV(new float[] { 7.0f, 0.0f, 10.0f, 3.0f }, 0));
		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Output, new BlockFaceUV(new float[] { 10.0f, 6.0f, 13.0f, 9.0f }, 0));
		TOP_MODE_UV_LAYOUTS.put(MachineSideMode.Disabled, new BlockFaceUV(new float[] { 7.0f, 3.0f, 10.0f, 6.0f }, 0));
	}

	public TankMachineBakedModel(BakedModel baseModel, ResourceLocation baseTexture) {
		super(baseModel);
		this.baseTexture = baseTexture;
	}

	@Override
	protected TextureAtlasSprite getSpriteForMachineSide(MachineSideMode mode, TextureAtlas blocksStitchedTextures,
			Direction side) {
		return blocksStitchedTextures.getSprite(baseTexture);
	}

	@Override
	protected void renderQuadsForSide(@Nullable BlockState state, Builder<BakedQuad> newQuads, Direction side,
			TextureAtlas blocksTexture, BakedQuad originalQuad, MachineSideMode sideConfiguration) {
		// Add the original quads.
		newQuads.add(originalQuad);

		// Get the side configuration.
		if (sideConfiguration != MachineSideMode.NA) {
			// Get the texture sprite for the side.
			TextureAtlasSprite sideSprite = getSpriteForMachineSide(sideConfiguration, blocksTexture, side);

			if (side.getAxis() == Direction.Axis.Y) {
				BlockElementFace blockPartFace = new BlockElementFace(null, -1, sideSprite.getName().toString(),
						TOP_MODE_UV_LAYOUTS.get(sideConfiguration));
				BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(5.0f, -0.01f, 5.0f),
						new Vector3f(11.0f, 16.01f, 11.0f), blockPartFace, sideSprite, side,
						ModelUtilities.IDENTITY, null, true, new ResourceLocation("dummy_name"));
				newQuads.add(newQuad);
			} else {
				BlockElementFace blockPartFace = new BlockElementFace(null, -1, sideSprite.getName().toString(),
						SIDE_MODE_UV_LAYOUTS.get(sideConfiguration));
				if (side.getAxis() == Direction.Axis.X) {
					BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(0, 5.0f, 5.0f),
							new Vector3f(16.0f, 11.0f, 11.0f), blockPartFace, sideSprite, side,
							ModelUtilities.IDENTITY, null, true, new ResourceLocation("dummy_name"));
					newQuads.add(newQuad);
				} else {
					BakedQuad newQuad = FaceBaker.bakeQuad(new Vector3f(5.0f, 5.0f, 0.0f),
							new Vector3f(11.0f, 11.0f, 16.0f), blockPartFace, sideSprite, side,
							ModelUtilities.IDENTITY, null, true, new ResourceLocation("dummy_name"));
					newQuads.add(newQuad);
				}
			}
		}
	}

}
