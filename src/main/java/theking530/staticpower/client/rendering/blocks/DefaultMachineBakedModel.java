package theking530.staticpower.client.rendering.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.utilities.ModelUtilities;

@OnlyIn(Dist.CLIENT)
public class DefaultMachineBakedModel extends AbstractBakedModel {
	@SuppressWarnings("deprecation")
	protected static final TextureAtlas BLOCKS_TEXTURE = ForgeModelBakery.instance().getSpriteMap()
			.getAtlas(TextureAtlas.LOCATION_BLOCKS);
	private static final Logger LOGGER = LogManager.getLogger(DefaultMachineBakedModel.class);
	private static final ModelProperty<Optional<MachineSideMode[]>> SIDE_CONFIG = new ModelProperty<>();
	private final HashMap<Direction, Boolean> sideConfigurationRenderControl;
	private final HashMap<BlockSide, Vector3f> sideOffsets;

	public DefaultMachineBakedModel(BakedModel baseModel) {
		super(baseModel);
		sideConfigurationRenderControl = new HashMap<Direction, Boolean>();
		sideOffsets = new HashMap<BlockSide, Vector3f>();

		// Populate with defaults.
		for (Direction dir : Direction.values()) {
			sideConfigurationRenderControl.put(dir, true);
		}
		sideConfigurationRenderControl.put(null, true);
	}

	/**
	 * Set a side offset for any side i/o rendering.
	 * 
	 * @param side   The block side of the block to modify.
	 * @param offset The offset to apply RELATIVE TO THE CENTER OF THE BLOCK.
	 *               Example: A negative value will bring the side render CLOSER to
	 *               the center of the block.
	 * @return
	 */
	public DefaultMachineBakedModel setSideOffset(BlockSide side, Vector3f offset) {
		sideOffsets.put(side, offset);
		return this;
	}

	public DefaultMachineBakedModel setSideConfigVisiblity(Direction side, boolean visible) {
		sideConfigurationRenderControl.put(side, visible);
		return this;
	}

	@Override
	@Nonnull
	public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state,
			@Nonnull IModelData tileData) {
		Optional<MachineSideMode[]> configurations = getSideConfigurations(world, pos);
		ModelDataMap modelDataMap = getEmptyIModelData();
		modelDataMap.setData(SIDE_CONFIG, configurations);
		return modelDataMap;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(@Nullable BlockState state, Direction side,
			@Nonnull Random rand, @Nonnull IModelData data) {
		// Check if the data has the SIDE_CONFIG property. If not, something has gone
		// wrong.
		if (!data.hasProperty(SIDE_CONFIG)) {
			conditionallyLogError(
					"Encountered invalid side configuration data when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}
		// Attempt to get the side configuration.
		Optional<MachineSideMode[]> sideConfigurations = data.getData(SIDE_CONFIG);

		// If we didn't get a side configuration, skip this block and just return the
		// defaults.
		if (!sideConfigurations.isPresent()) {
			conditionallyLogError("Encountered no side configuration data when attempting to bake quads for machine.");
			return BaseModel.getQuads(state, side, rand);
		}

		// Capture the default quads and retexture the sides to match the desired side
		// textures based on the configuration.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data);
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		// Get the block atlas texture.
		try {
			TextureAtlas blocksTexture = ForgeModelBakery.instance().getSpriteMap().getAtlas(TextureAtlas.LOCATION_BLOCKS);
			// Iterate through all the quads.
			for (BakedQuad quad : baseQuads) {
				// Get the rendering side.
				Direction renderingSide = side == null ? quad.getDirection() : side;

				// Get the side mode.
				MachineSideMode sideMode = sideConfigurations.get()[renderingSide.ordinal()];
				try {
					// Attempt to render the quad for the side.
					renderQuadsForSide(state, newQuads, renderingSide, blocksTexture, quad, sideMode);
				} catch (Exception e) {
					LOGGER.warn("An error occured when attempting to render the model.", e);
				}

			}
		} catch (Exception e) {
			// Silence!
		}

		// Build and return the new quad list.
		return newQuads.build();
	}

	protected void renderQuadsForSide(@Nullable BlockState state, Builder<BakedQuad> newQuads, Direction side,
			TextureAtlas blocksTexture, BakedQuad originalQuad, MachineSideMode sideConfiguration) {
		// Add the original quads.
		newQuads.add(originalQuad);

		// Add the side config color if it's not NEVER and if its enabled.
		if (sideConfigurationRenderControl.get(side) && sideConfiguration != MachineSideMode.Never) {
			// Get the texture sprite for the side.
			TextureAtlasSprite sideSprite = getSpriteForMachineSide(sideConfiguration, blocksTexture, side);

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

			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { 0.005f, 0.005f, 15.995f, 15.995f }, 0);
			BlockElementFace blockPartFace = new BlockElementFace(side, -1, sideSprite.getName().toString(),
					blockFaceUV);
			Vector3f posOffset = SDMath.transformVectorByDirection(offsetSide, new Vector3f(0.0f, 0.0f, 0.005f));
			posOffset.add(16.0f, 16.0f, 16.0f);

			Vector3f negOffset = SDMath.transformVectorByDirection(offsetSide, new Vector3f(0.0f, 0.0f, -0.005f));

			// Check if we have a facing property.
			if (state != null && state.hasProperty(StaticPowerBlockEntityBlock.FACING)) {
				BlockSide blockSide = SideConfigurationUtilities.getBlockSide(side,
						state.getValue(StaticPowerBlockEntityBlock.FACING));

				// If we do, see if we have a requested offset. If we do, apply it.
				if (sideOffsets.containsKey(blockSide)) {
					Vector3f offset = sideOffsets.get(blockSide);

					// Make sure we handle positive vs negative side offsets.
					if (blockSide.getSign() == Direction.AxisDirection.POSITIVE) {
						posOffset.add(offset.x(), offset.y(), offset.z());
					} else {
						negOffset.add(-1 * offset.x(), -1 * offset.y(), -1 * offset.z());
					}
				}
			}

			BakedQuad newQuad = FaceBaker.bakeQuad(negOffset, posOffset, blockPartFace, sideSprite, side,
					ModelUtilities.IDENTITY, null, true, new ResourceLocation("dummy_name"));
			newQuads.add(newQuad);
		}
	}

	protected TextureAtlasSprite getSpriteForMachineSide(MachineSideMode mode, TextureAtlas blocksStitchedTextures,
			Direction side) {
		switch (mode) {
		case Input:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_INPUT);
		case Input2:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_PURPLE);
		case Input3:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_MAGENTA);
		case Output:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_OUTPUT);
		case Output2:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_GREEN);
		case Output3:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_YELLOW);
		case Output4:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_AQUA);
		case Disabled:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_DISABLED);
		case Never:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.EMPTY_TEXTURE);
		default:
			return blocksStitchedTextures.getSprite(StaticPowerSprites.MACHINE_SIDE_NORMAL);
		}
	}

	protected ModelDataMap getEmptyIModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		builder.withInitial(SIDE_CONFIG, Optional.empty());
		ModelDataMap modelDataMap = builder.build();
		return modelDataMap;
	}

	protected Optional<MachineSideMode[]> getSideConfigurations(@Nonnull BlockAndTintGetter world,
			@Nonnull BlockPos blockPos) {
		if (!world.getBlockState(blockPos).hasBlockEntity()) {
			return Optional.empty();
		}

		BlockEntity rawTileEntity = world.getBlockEntity(blockPos);

		if (rawTileEntity != null && rawTileEntity instanceof BlockEntityBase) {
			BlockEntityBase configurable = (BlockEntityBase) rawTileEntity;
			if (configurable.hasComponentOfType(SideConfigurationComponent.class)) {
				return Optional
						.of(configurable.getComponent(SideConfigurationComponent.class).getWorldSpaceConfiguration());
			}
		}
		return Optional.empty();
	}

	@Override
	public boolean usesBlockLight() {
		return BaseModel.usesBlockLight();
	}
}
