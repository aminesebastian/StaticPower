package theking530.staticpower.client.rendering.utilities;

import com.google.common.collect.ImmutableList.Builder;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.gui.StaticCoreSprites;
import theking530.staticcore.utilities.ModelUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.client.StaticPowerSprites;

public class SideModeQuadGenerator {
	private static final FaceBakery FACE_BAKER = new FaceBakery();

	public static void renderSideModeQuad(BlockState state, Builder<BakedQuad> newQuads, Direction side,
			MachineSideMode sideConfiguration) {
		renderSideModeQuad(state, newQuads, side, sideConfiguration);
	}

	public static void renderSideModeQuad(BlockState state, Builder<BakedQuad> newQuads, Direction side,
			MachineSideMode sideConfiguration, Vector3f offset) {
		// We can't cache the UVs, for some reason they expand over time.... Could be a
		// bug.
		renderSideModeQuad(state, newQuads, side, sideConfiguration, offset, getSpriteForMachineSide(sideConfiguration),
				new BlockFaceUV(new float[] { 0.0f, 0.0f, 16f, 16f }, 0));
	}

	public static void renderMiniSideModeQuad(BlockState state, Builder<BakedQuad> newQuads, Direction side,
			MachineSideMode sideConfiguration) {
		renderMiniSideModeQuad(state, newQuads, side, sideConfiguration);
	}

	public static void renderMiniSideModeQuad(BlockState state, Builder<BakedQuad> newQuads, Direction side,
			MachineSideMode sideConfiguration, Vector3f offset) {
		// We can't cache the UVs, for some reason they expand over time.... Could be a
		// bug.
		renderSideModeQuad(state, newQuads, side, sideConfiguration, offset,
				getSpriteForMiniMachineSide(sideConfiguration),
				new BlockFaceUV(new float[] { 0.0f, 0.0f, 16f, 16f }, 0));
	}

	public static void renderSideModeQuad(BlockState state, Builder<BakedQuad> newQuads, Direction side,
			MachineSideMode sideConfiguration, Vector3f offset, TextureAtlasSprite sideSprite,
			BlockFaceUV blockFaceUV) {
		// Add the side config color if it's not NEVER and if its enabled.
		if (sideConfiguration != MachineSideMode.Never) {
			// Vectors for quads are relative to the face direction, so we need to only work
			// in the positive direction vectors.
			Direction offsetSide = side;
			if (side == Direction.EAST) {
				offsetSide = Direction.WEST;
			} else if (side == Direction.SOUTH) {
				offsetSide = Direction.NORTH;
			} else if (side == Direction.DOWN) {
				offsetSide = Direction.UP;
			}

			BlockElementFace blockPartFace = new BlockElementFace(side, -1, sideSprite.getName().toString(),
					blockFaceUV);
			Vector3f posOffset = SDMath.transformVectorByDirection(offsetSide, new Vector3f(0.0f, 0.0f, 0.01f));
			posOffset.add(16.0f, 16.0f, 16.0f);

			Vector3f negOffset = SDMath.transformVectorByDirection(offsetSide, new Vector3f(0.0f, 0.0f, -0.01f));

			// Check if we have a facing property.
			if (state != null && state.hasProperty(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING)) {
				BlockSide blockSide = SideConfigurationUtilities.getBlockSide(side,
						state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING));

				// If we do, see if we have a requested offset. If we do, apply it.
				if (offset != null) {
					// Make sure we handle positive vs negative side offsets.
					if (blockSide.getSign() == Direction.AxisDirection.POSITIVE) {
						posOffset.add(offset.x(), offset.y(), offset.z());
					} else {
						negOffset.add(-1 * offset.x(), -1 * offset.y(), -1 * offset.z());
					}
				}
			}

			BakedQuad newQuad = FACE_BAKER.bakeQuad(negOffset, posOffset, blockPartFace, sideSprite, side,
					ModelUtilities.IDENTITY, null, true, new ResourceLocation("dummy_name"));
			newQuads.add(newQuad);
		}
	}

	public static TextureAtlasSprite getSpriteForMachineSide(MachineSideMode mode) {
		switch (mode) {
		case Input:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_INPUT);
		case Input2:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_PURPLE);
		case Input3:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_MAGENTA);
		case Output:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_OUTPUT);
		case Output2:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_GREEN);
		case Output3:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_YELLOW);
		case Output4:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_AQUA);
		case Disabled:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_DISABLED);
		case Never:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticCoreSprites.EMPTY_TEXTURE);
		default:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MACHINE_SIDE_NORMAL);
		}
	}

	public static TextureAtlasSprite getSpriteForMiniMachineSide(MachineSideMode mode) {
		switch (mode) {
		case Input:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_INPUT);
		case Input2:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_PURPLE);
		case Input3:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_MAGENTA);
		case Output:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_OUTPUT);
		case Output2:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_GREEN);
		case Output3:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_YELLOW);
		case Output4:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_AQUA);
		case Disabled:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_DISABLED);
		case Never:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticCoreSprites.EMPTY_TEXTURE);
		default:
			return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(StaticPowerSprites.MINI_MACHINE_SIDE_NORMAL);
		}
	}
}
