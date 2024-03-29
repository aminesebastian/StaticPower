package theking530.staticpower.client.rendering.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.Quad;
import codechicken.lib.model.pipeline.BakedPipeline;
import codechicken.lib.model.pipeline.transformers.QuadClamper;
import codechicken.lib.model.pipeline.transformers.QuadReInterpolator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticcore.client.models.AbstractBakedModel;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.blockentities.digistorenetwork.severrack.BlockEntityDigistoreServerRack;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.RotatedModelCache;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.items.DigistoreMonoCard;
import theking530.staticpower.items.DigistoreStackedCard;

@OnlyIn(Dist.CLIENT)
public class ServerRackModel extends AbstractBakedModel {
	private static final CachedFormat BAR_FORMAT = CachedFormat.lookup(DefaultVertexFormat.BLOCK);
	private final ThreadLocal<BakedPipeline> pipelines = ThreadLocal.withInitial(() -> BakedPipeline.builder()
			// Clamper is responsible for clamping the vertex to the bounds specified.
			.addElement("clamper", QuadClamper.FACTORY)
			// Re-Interpolates the UV's for the quad.
			.addElement("interp", QuadReInterpolator.FACTORY).build()//
	);
	private final ThreadLocal<Quad> collectors = ThreadLocal.withInitial(Quad::new);

	public ServerRackModel(BakedModel baseModel) {
		super(baseModel);
	}

	@Override
	@Nonnull
	public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
		return tileData;
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromModelData(@Nullable BlockState state, Direction side, @Nonnull RandomSource rand, @Nonnull ModelData data, RenderType renderLayer) {
		// If the property is not there, return early.
		if (!data.has(BlockEntityDigistoreServerRack.CARD_RENDERING_STATE)) {
			return Collections.emptyList();
		}

		// Get the data used in rendering.
		Direction facing = state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING);
		ItemStack[] cards = data.get(BlockEntityDigistoreServerRack.CARD_RENDERING_STATE).cards;

		// Create the output array.
		ImmutableList.Builder<BakedQuad> newQuads = new ImmutableList.Builder<BakedQuad>();

		// Add all the base quads for the server rack to the output.
		List<BakedQuad> baseQuads = BaseModel.getQuads(state, side, rand, data, renderLayer);
		newQuads.addAll(baseQuads);

		for (int i = 0; i < 8; i++) {
			if (cards[i].isEmpty()) {
				continue;
			}

			// Get the model of the card.
			BakedModel model = Minecraft.getInstance().getModelManager().getModel(((DigistoreCard) cards[i].getItem()).model);

			// Calculate the offset for the current card's model.
			float xOffset = ((i / 4) * UNIT * 6.5f) - (UNIT * 3.25f);
			float yOffset = 2.125f * UNIT + (UNIT * 3.25f * (i % 4));
			yOffset = 1.0f - yOffset - (2.0f * UNIT);
			float zOffset = -1.0f * UNIT;

			// Create a vector from that offset.
			Vector3f offset;
			Vector3f scale;
			if (cards[i].getItem() instanceof DigistoreStackedCard) {
				scale = new Vector3f(1.0f, 1.3f, 1.0f);
				offset = SDMath.transformVectorByDirection(facing, new Vector3f(xOffset, yOffset + 0.13f, zOffset));
			} else {
				scale = new Vector3f(1.0f, 1.0f, 1.0f);
				offset = SDMath.transformVectorByDirection(facing, new Vector3f(xOffset, yOffset, zOffset));
			}

			// Transform the card's quads.
			List<BakedQuad> bakedCardQuads = transformQuads(model, offset, scale, Quaternion.fromXYZDegrees(RotatedModelCache.getRotation(facing)), side, state, rand, renderLayer);
			newQuads.addAll(bakedCardQuads);

			// If we are rendering a mono card, render the filled bar.
			if (side == null && cards[i].getItem() instanceof DigistoreMonoCard) {
				// Get the digistore inventory.
				IDigistoreInventory inv = DigistoreCard.getInventory(cards[i]);

				// Get the filled percentage for the current card.
				float filledPercentage = (float) inv.getTotalContainedCount() / inv.getItemCapacity();

				// Get the model of the card bar.
				BakedModel barModel;
				if (filledPercentage < 1.0f) {
					barModel = Minecraft.getInstance().getModelManager().getModel(StaticPowerAdditionalModels.DIGISTORE_SINGULAR_CARD_BAR);
				} else {
					barModel = Minecraft.getInstance().getModelManager().getModel(StaticPowerAdditionalModels.DIGISTORE_SINGULAR_CARD_BAR_FULL);
				}

				// Adjust the filled percentage to work with the fill bar.
				filledPercentage *= (4.75f / 16.0f);

				// If there are quads for the bar model, render them.
				if (barModel.getQuads(state, side, rand, ModelData.EMPTY, renderLayer).size() > 0) {
					// Create an array that we will populate with the bar's quad.
					List<BakedQuad> barQuadList = new ArrayList<BakedQuad>();

					// Since we know that the bar will be one quad, we just fetch it.
					BakedQuad barQuad = barModel.getQuads(state, side, rand, ModelData.EMPTY, renderLayer).get(0);

					// Get the pipeline
					BakedPipeline pipeline = this.pipelines.get();
					Quad collectorQuad = this.collectors.get();

					QuadClamper clamper = pipeline.getElement("clamper", QuadClamper.class);
					AABB barBounds = new AABB(new Vec3((10.25f / 16.0f) - filledPercentage, 0.0f, 0.0f), new Vec3((10.25f / 16.0f), 1.0f, 1.0f));
					clamper.setClampBounds(barBounds);

					// Reset the pipeline, clears all enabled/disabled states.
					pipeline.reset(BAR_FORMAT);

					// Reset our collector.
					collectorQuad.reset(BAR_FORMAT);
					pipeline.prepare(collectorQuad);
					pipeline.put(barQuad);
					// barQuad.pipe(pipeline);

					// Add the transformed quad.
					if (collectorQuad.full) {
						barQuadList.add(collectorQuad.bake());
					}

					// Modify the existing offset to move forward by a fraction of a unit.
					offset.add(SDMath.transformVectorByDirection(facing, new Vector3f(-0.0001f, -0.0001f, -0.0001f)));

					// Add all the bar's quads transformed with the same offset and rotation as the
					// card.
					newQuads.addAll(transformQuads(barQuadList, offset, new Vector3f(1.0f, 1.0f, 1.0f), Quaternion.fromXYZDegrees(RotatedModelCache.getRotation(facing))));
				}
			}
		}

		return newQuads.build();
	}

	@Override
	public boolean usesBlockLight() {
		return BaseModel.usesBlockLight();
	}
}
