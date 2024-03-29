package theking530.staticcore.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import codechicken.lib.model.CachedFormat;
import codechicken.lib.model.Quad;
import codechicken.lib.model.pipeline.BakedPipeline;
import codechicken.lib.model.pipeline.transformers.QuadAlphaOverride;
import codechicken.lib.model.pipeline.transformers.QuadClamper;
import codechicken.lib.model.pipeline.transformers.QuadCornerKicker;
import codechicken.lib.model.pipeline.transformers.QuadFaceStripper;
import codechicken.lib.model.pipeline.transformers.QuadReInterpolator;
import codechicken.lib.model.pipeline.transformers.QuadTinter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticcore.cablenetwork.AbstractCableBlock;
import theking530.staticcore.cablenetwork.CableRenderingState;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.cablenetwork.data.CableConnectionState.CableConnectionType;
import theking530.staticcore.utilities.math.Vector3D;

/**
 * MASSIVE THANKS to covers1624. Huge help and fantastic code to learn from!
 */
@OnlyIn(Dist.CLIENT)
public class CoverBuilder {

	public static final double THICK_THICKNESS = 2D / 16D;
	public static final double THIN_THICKNESS = 1D / 16D;

	public static final AABB[] THICK_FACADE_BOXES = new AABB[] { new AABB(0.0, 0.0, 0.0, 1.0, THICK_THICKNESS, 1.0), new AABB(0.0, 1.0 - THICK_THICKNESS, 0.0, 1.0, 1.0, 1.0),
			new AABB(0.0, 0.0, 0.0, 1.0, 1.0, THICK_THICKNESS), new AABB(0.0, 0.0, 1.0 - THICK_THICKNESS, 1.0, 1.0, 1.0), new AABB(0.0, 0.0, 0.0, THICK_THICKNESS, 1.0, 1.0),
			new AABB(1.0 - THICK_THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0) };

	public static final AABB[] THIN_FACADE_BOXES = new AABB[] { new AABB(0.0, 0.0, 0.0, 1.0, THIN_THICKNESS, 1.0), new AABB(0.0, 1.0 - THIN_THICKNESS, 0.0, 1.0, 1.0, 1.0),
			new AABB(0.0, 0.0, 0.0, 1.0, 1.0, THIN_THICKNESS), new AABB(0.0, 0.0, 1.0 - THIN_THICKNESS, 1.0, 1.0, 1.0), new AABB(0.0, 0.0, 0.0, THIN_THICKNESS, 1.0, 1.0),
			new AABB(1.0 - THIN_THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0) };

	private final ThreadLocal<BakedPipeline> pipelines = ThreadLocal.withInitial(() -> BakedPipeline.builder()
			// Clamper is responsible for clamping the vertex to the bounds specified.
			.addElement("clamper", QuadClamper.FACTORY)
			// Strips faces if they match a mask.
			.addElement("face_stripper", QuadFaceStripper.FACTORY)
			// Kicks the edge inner corners in, solves Z fighting
			.addElement("corner_kicker", QuadCornerKicker.FACTORY)
			// Re-Interpolates the UV's for the quad.
			.addElement("interp", QuadReInterpolator.FACTORY)
			// Tints the quad if we need it to. Disabled by default.
			.addElement("tinter", QuadTinter.FACTORY, false)
			// Overrides the quad's alpha if we are forcing transparent facades.
			.addElement("transparent", QuadAlphaOverride.FACTORY, false, e -> e.setAlphaOverride(0x4C / 255F)).build()//
	);
	private final ThreadLocal<Quad> collectors = ThreadLocal.withInitial(Quad::new);

	public void buildFacadeQuads(@Nullable BlockState state, CableRenderingState cableState, RenderType layer, RandomSource rand, List<BakedQuad> quads, Direction dir) {
		if (!cableState.hasCover(dir)) {
			return;
		}

		// Get the blockstate for the cover and return early if its empty.
		BlockState coverBlockState = cableState.getCoverBlockState(dir);
		if (coverBlockState.isAir()) {
			return;
		}

		BakedPipeline pipeline = this.pipelines.get();
		Quad collectorQuad = this.collectors.get();
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();

		AABB fullBounds = THICK_FACADE_BOXES[dir.ordinal()];
		AABB facadeBox = fullBounds;

		BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
		BakedModel model = dispatcher.getBlockModel(coverBlockState);
		ModelData modelData = model.getModelData(cableState.getLevel(), cableState.getCableBlockPos(), coverBlockState, ModelData.EMPTY);

		List<BakedQuad> modelQuads = new ArrayList<>();
		// If we are forcing transparent facades, fake the render layer, and grab all
		// quads.
		if (layer == null) {
			for (RenderType forcedLayer : RenderType.chunkBufferLayers()) {
				modelQuads.addAll(gatherQuads(model, coverBlockState, rand, modelData, forcedLayer));
			}
		} else {
			modelQuads.addAll(gatherQuads(model, coverBlockState, rand, modelData, layer));
		}

		// Skip any empty quad lists.
		if (modelQuads.isEmpty()) {
			return;
		}

		// Grab out pipeline elements.
		QuadClamper clamper = pipeline.getElement("clamper", QuadClamper.class);
		QuadFaceStripper edgeStripper = pipeline.getElement("face_stripper", QuadFaceStripper.class);
		QuadTinter tinter = pipeline.getElement("tinter", QuadTinter.class);
		QuadCornerKicker kicker = pipeline.getElement("corner_kicker", QuadCornerKicker.class);

		List<AABB> holeStrips = getBoxes(facadeBox, state, CableUtilities.getConnectionTypeOnSide(coverBlockState, dir),
				!cableState.hasAttachment(dir) ? null : (AbstractCableAttachment) cableState.getAttachment(dir).getItem(), dir.getAxis());

		// calculate the side mask.
		int facadeMask = 0;
		for (Direction coverSide : Direction.values()) {
			if (coverSide.getAxis() != dir.getAxis()) {
				if (cableState.hasCover(coverSide)) {
					facadeMask |= 1 << coverSide.ordinal();
				}
			}
		}

		// Setup the edge stripper.
		edgeStripper.setBounds(fullBounds);
		edgeStripper.setMask(facadeMask);

		// Setup the kicker.
		kicker.setSide(dir.ordinal());
		kicker.setFacadeMask(facadeMask);
		kicker.setBox(fullBounds);
		kicker.setThickness(THICK_THICKNESS);

		for (BakedQuad quad : modelQuads) {
			// lookup the format in CachedFormat.
			CachedFormat format = CachedFormat.lookup(DefaultVertexFormat.BLOCK);
			// If this quad has a tint index, setup the tinter.
			if (quad.isTinted()) {
				tinter.setTint(blockColors.getColor(coverBlockState, cableState.getLevel(), cableState.getCableBlockPos(), quad.getTintIndex()));
			}

			for (AABB box : holeStrips) {
				// setup the clamper for this box
				clamper.setClampBounds(box);
				// Reset the pipeline, clears all enabled/disabled states.
				pipeline.reset(format);
				// Reset out collector.
				collectorQuad.reset(format);
				// Enable / disable the optional elements
				pipeline.setElementState("tinter", quad.isTinted());
				pipeline.setElementState("transparent", false);
				// Prepare the pipeline for a quad.
				pipeline.prepare(collectorQuad);
				// Pipe our quad into the pipeline.
				pipeline.put(quad);

				// Handle the edge case where the orientation coming out of the pipeline is
				// null.
				// This breaks rendering when the renderer renders AO.
				if (collectorQuad.orientation == null) {
					collectorQuad.orientation = dir;
				}

				// Add the result.
				quads.add(collectorQuad.bake());
			}
		}
	}

	/**
	 * This is slow, so should be cached.
	 *
	 * @return The model.
	 */
	public List<BakedQuad> buildFacadeItemQuads(ItemStack textureItem, Direction side, @Nullable RenderType renderType) {
		List<BakedQuad> facadeQuads = new ArrayList<>();
		BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(textureItem, null, null, 0);
		List<BakedQuad> modelQuads = gatherQuads(model, null, RandomSource.create(), ModelData.EMPTY, renderType);

		BakedPipeline pipeline = this.pipelines.get();
		Quad collectorQuad = this.collectors.get();

		// Grab pipeline elements.
		QuadClamper clamper = pipeline.getElement("clamper", QuadClamper.class);
		QuadTinter tinter = pipeline.getElement("tinter", QuadTinter.class);

		for (BakedQuad quad : modelQuads) {
			// Lookup the CachedFormat for this quads format.
			CachedFormat format = CachedFormat.lookup(DefaultVertexFormat.BLOCK);

			// Reset the pipeline.
			pipeline.reset(format);

			// Reset the collector.
			collectorQuad.reset(format);

			// If we have a tint index, setup the tinter and enable it.
			if (quad.isTinted()) {
				tinter.setTint(Minecraft.getInstance().getItemColors().getColor(textureItem, quad.getTintIndex()));
				pipeline.enableElement("tinter");
			}

			// Disable elements we don't need for items.
			pipeline.disableElement("face_stripper");
			pipeline.disableElement("corner_kicker");

			// Setup the clamper
			clamper.setClampBounds(THICK_FACADE_BOXES[side.ordinal()]);

			// Prepare the pipeline.
			pipeline.prepare(collectorQuad);

			// Pipe our quad into the pipeline.
			pipeline.put(quad);

			// Check the collector for data and add the quad if there was.
			if (collectorQuad.full) {
				facadeQuads.add(collectorQuad.bake());
			}
		}
		return facadeQuads;
	}

	/**
	 * Generates the box segments around the specified hole. If the specified hole
	 * is null, a Singleton of the Facade box is returned.
	 *
	 * @param fb   The Facade's box.
	 * @param hole The hole to 'cut'.
	 * @param axis The axis the facade is on.
	 *
	 * @return The box segments.
	 */
	private static List<AABB> getBoxes(AABB fb, @Nullable BlockState state, CableConnectionType connectionState, AbstractCableAttachment attachment, Axis axis) {
		// Setup the bounds.
		Vector3D bounds = null;

		// If we're connected to another cable, set the bounds to the requested hole
		// size.
		if (connectionState == CableConnectionType.CABLE && state != null) {
			if (state.getBlock() instanceof AbstractCableBlock) {
				AbstractCableBlock cableBlock = (AbstractCableBlock) state.getBlock();
				final float holeSize = cableBlock.coverHoleSize / 16.0f;
				bounds = new Vector3D(holeSize, holeSize, holeSize);
			}
		} else if (attachment != null) {
			// If we have an attachment, create a hole the size of the attachment.
			bounds = attachment.getBounds().copy().divide(16.0f);
		} else {
			// Otherwise, return early.
			return Collections.singletonList(fb);
		}

		List<AABB> boxes = new ArrayList<>();
		switch (axis) {
		case Y:
			boxes.add(new AABB(fb.minX, fb.minY, fb.minZ, 0.5f - bounds.getX(), fb.maxY, fb.maxZ));
			boxes.add(new AABB(0.5f + bounds.getX(), fb.minY, fb.minZ, fb.maxX, fb.maxY, fb.maxZ));

			boxes.add(new AABB(0.5f - bounds.getX(), fb.minY, fb.minZ, 0.5f + bounds.getX(), fb.maxY, 0.5f - bounds.getY()));
			boxes.add(new AABB(0.5f - bounds.getX(), fb.minY, 0.5f + bounds.getX(), 0.5f + bounds.getY(), fb.maxY, fb.maxZ));

			break;
		case Z:
			boxes.add(new AABB(fb.minX, fb.minY, fb.minZ, fb.maxX, 0.5f - bounds.getY(), fb.maxZ));
			boxes.add(new AABB(fb.minX, 0.5f + bounds.getY(), fb.minZ, fb.maxX, fb.maxY, fb.maxZ));

			boxes.add(new AABB(fb.minX, 0.5f - bounds.getY(), fb.minZ, 0.5f - bounds.getX(), 0.5f + bounds.getY(), fb.maxZ));
			boxes.add(new AABB(0.5f + bounds.getX(), 0.5f - bounds.getY(), fb.minZ, fb.maxX, 0.5f + bounds.getY(), fb.maxZ));

			break;
		case X:
			boxes.add(new AABB(fb.minX, fb.minY, fb.minZ, fb.maxX, 0.5f - bounds.getY(), fb.maxZ));
			boxes.add(new AABB(fb.minX, 0.5f + bounds.getY(), fb.minZ, fb.maxX, fb.maxY, fb.maxZ));

			boxes.add(new AABB(fb.minX, 0.5f - bounds.getY(), fb.minZ, fb.maxX, 0.5f + bounds.getY(), 0.5f - bounds.getX()));
			boxes.add(new AABB(fb.minX, 0.5f - bounds.getY(), 0.5f + bounds.getX(), fb.maxX, 0.5f + bounds.getY(), fb.maxZ));
			break;
		}

		return boxes;
	}

	// Helper to gather all quads from a model into a list.
	private static List<BakedQuad> gatherQuads(BakedModel model, BlockState state, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
		List<BakedQuad> modelQuads = new ArrayList<>();
		for (Direction face : Direction.values()) {
			modelQuads.addAll(model.getQuads(state, face, rand, data, renderType));
		}
		modelQuads.addAll(model.getQuads(state, null, rand, data, renderType));
		return modelQuads;
	}
}