package theking530.staticpower.client.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.CableRenderingState;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.thirdparty.codechicken.lib.model.CachedFormat;
import theking530.thirdparty.codechicken.lib.model.Quad;
import theking530.thirdparty.codechicken.lib.model.pipeline.BakedPipeline;
import theking530.thirdparty.codechicken.lib.model.pipeline.transformers.QuadAlphaOverride;
import theking530.thirdparty.codechicken.lib.model.pipeline.transformers.QuadClamper;
import theking530.thirdparty.codechicken.lib.model.pipeline.transformers.QuadCornerKicker;
import theking530.thirdparty.codechicken.lib.model.pipeline.transformers.QuadFaceStripper;
import theking530.thirdparty.codechicken.lib.model.pipeline.transformers.QuadReInterpolator;
import theking530.thirdparty.codechicken.lib.model.pipeline.transformers.QuadTinter;

/**
 * The FacadeBuilder builds for facades..
 *
 * @author covers1624
 */
public class CoverBuilder {

	public static final double THICK_THICKNESS = 2D / 16D;
	public static final double THIN_THICKNESS = 1D / 16D;

	public static final AxisAlignedBB[] THICK_FACADE_BOXES = new AxisAlignedBB[] { new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, THICK_THICKNESS, 1.0), new AxisAlignedBB(0.0, 1.0 - THICK_THICKNESS, 0.0, 1.0, 1.0, 1.0),
			new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, THICK_THICKNESS), new AxisAlignedBB(0.0, 0.0, 1.0 - THICK_THICKNESS, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, THICK_THICKNESS, 1.0, 1.0),
			new AxisAlignedBB(1.0 - THICK_THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0) };

	public static final AxisAlignedBB[] THIN_FACADE_BOXES = new AxisAlignedBB[] { new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, THIN_THICKNESS, 1.0), new AxisAlignedBB(0.0, 1.0 - THIN_THICKNESS, 0.0, 1.0, 1.0, 1.0),
			new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, THIN_THICKNESS), new AxisAlignedBB(0.0, 0.0, 1.0 - THIN_THICKNESS, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, THIN_THICKNESS, 1.0, 1.0),
			new AxisAlignedBB(1.0 - THIN_THICKNESS, 0.0, 0.0, 1.0, 1.0, 1.0) };

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

	public void buildFacadeQuads(CableRenderingState cableState, RenderType layer, Random rand, List<BakedQuad> quads, Direction dir) {
		BlockState blockState = cableState.covers[dir.ordinal()];
		BakedPipeline pipeline = this.pipelines.get();
		Quad collectorQuad = this.collectors.get();
		BlockColors blockColors = Minecraft.getInstance().getBlockColors();

		int sideIndex = dir.ordinal();
		AxisAlignedBB fullBounds = THICK_FACADE_BOXES[sideIndex];
		AxisAlignedBB facadeBox = fullBounds;

		BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
		IBakedModel model = dispatcher.getModelForState(blockState);
		IModelData modelData = model.getModelData(cableState.world, cableState.pos, blockState, EmptyModelData.INSTANCE);

		List<BakedQuad> modelQuads = new ArrayList<>();
		// If we are forcing transparent facades, fake the render layer, and grab all
		// quads.
		if (layer == null) {
			for (RenderType forcedLayer : RenderType.getBlockRenderTypes()) {
				// Check if the block renders on the layer we want to force.
				if (RenderTypeLookup.canRenderInLayer(blockState, forcedLayer)) {
					// Force the layer and gather quads.
					ForgeHooksClient.setRenderLayer(forcedLayer);
					modelQuads.addAll(gatherQuads(model, blockState, rand, modelData));
				}
			}

			// Reset.
			ForgeHooksClient.setRenderLayer(layer);
		} else {
			modelQuads.addAll(gatherQuads(model, blockState, rand, modelData));
		}

		// No quads.. Cool, next!
		if (modelQuads.isEmpty()) {
			return;
		}

		// Grab out pipeline elements.
		QuadClamper clamper = pipeline.getElement("clamper", QuadClamper.class);
		QuadFaceStripper edgeStripper = pipeline.getElement("face_stripper", QuadFaceStripper.class);
		QuadTinter tinter = pipeline.getElement("tinter", QuadTinter.class);
		QuadCornerKicker kicker = pipeline.getElement("corner_kicker", QuadCornerKicker.class);

		List<AxisAlignedBB> holeStrips = getBoxes(facadeBox, cableState.attachmentItems[sideIndex].isEmpty() ? null : (AbstractCableAttachment) cableState.attachmentItems[sideIndex].getItem(), dir.getAxis());

		// Set global element states.

		// calculate the side mask.
		int facadeMask = 0;
		for (Direction coverSide : Direction.values()) {
			if (coverSide.getAxis() != dir.getAxis()) {
				if (cableState.hasCoverOnSide(coverSide)) {
					facadeMask |= 1 << coverSide.ordinal();
				}
			}
		}
		// Setup the edge stripper.
		edgeStripper.setBounds(fullBounds);
		edgeStripper.setMask(facadeMask);

		// Setup the kicker.
		kicker.setSide(sideIndex);
		kicker.setFacadeMask(facadeMask);
		kicker.setBox(fullBounds);
		kicker.setThickness(THICK_THICKNESS);

		for (BakedQuad quad : modelQuads) {
			// lookup the format in CachedFormat.
			CachedFormat format = CachedFormat.lookup(DefaultVertexFormats.BLOCK);
			// If this quad has a tint index, setup the tinter.
			if (quad.hasTintIndex()) {
				tinter.setTint(blockColors.getColor(blockState, cableState.world, cableState.pos, quad.getTintIndex()));
			}
			for (AxisAlignedBB box : holeStrips) {
				// setup the clamper for this box
				clamper.setClampBounds(box);
				// Reset the pipeline, clears all enabled/disabled states.
				pipeline.reset(format);
				// Reset out collector.
				collectorQuad.reset(format);
				// Enable / disable the optional elements
				pipeline.setElementState("tinter", quad.hasTintIndex());
				pipeline.setElementState("transparent", false);
				// Prepare the pipeline for a quad.
				pipeline.prepare(collectorQuad);

				// Pipe our quad into the pipeline.
				quad.pipe(pipeline);
				// Check if the collector got any data.
				if (collectorQuad.full) {
					// Add the result.
					quads.add(collectorQuad.bake());
				}
			}
		}
	}

	/**
	 * This is slow, so should be cached.
	 *
	 * @return The model.
	 */
	public List<BakedQuad> buildFacadeItemQuads(ItemStack textureItem, Direction side) {
		List<BakedQuad> facadeQuads = new ArrayList<>();
		IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(textureItem, null, null);
		List<BakedQuad> modelQuads = gatherQuads(model, null, new Random(), EmptyModelData.INSTANCE);

		BakedPipeline pipeline = this.pipelines.get();
		Quad collectorQuad = this.collectors.get();

		// Grab pipeline elements.
		QuadClamper clamper = pipeline.getElement("clamper", QuadClamper.class);
		QuadTinter tinter = pipeline.getElement("tinter", QuadTinter.class);

		for (BakedQuad quad : modelQuads) {
			// Lookup the CachedFormat for this quads format.
			CachedFormat format = CachedFormat.lookup(DefaultVertexFormats.BLOCK);
			// Reset the pipeline.
			pipeline.reset(format);
			// Reset the collector.
			collectorQuad.reset(format);
			// If we have a tint index, setup the tinter and enable it.
			if (quad.hasTintIndex()) {
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
			quad.pipe(pipeline);
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
	private static List<AxisAlignedBB> getBoxes(AxisAlignedBB fb, AbstractCableAttachment attachment, Axis axis) {
		if (attachment == null) {
			return Collections.singletonList(fb);
		}

		List<AxisAlignedBB> boxes = new ArrayList<>();
		final Vector3D bounds = attachment.getBounds().clone().divide(16.0f);

		switch (axis) {
		case Y:
			boxes.add(new AxisAlignedBB(fb.minX, fb.minY, fb.minZ, 0.5f - bounds.getX(), fb.maxY, fb.maxZ));
			boxes.add(new AxisAlignedBB(0.5f + bounds.getX(), fb.minY, fb.minZ, fb.maxX, fb.maxY, fb.maxZ));

			boxes.add(new AxisAlignedBB(0.5f - bounds.getX(), fb.minY, fb.minZ, 0.5f + bounds.getX(), fb.maxY, 0.5f - bounds.getY()));
			boxes.add(new AxisAlignedBB(0.5f - bounds.getX(), fb.minY, 0.5f + bounds.getX(), 0.5f + bounds.getY(), fb.maxY, fb.maxZ));

			break;
		case Z:
			boxes.add(new AxisAlignedBB(fb.minX, fb.minY, fb.minZ, fb.maxX, 0.5f - bounds.getY(), fb.maxZ));
			boxes.add(new AxisAlignedBB(fb.minX, 0.5f + bounds.getY(), fb.minZ, fb.maxX, fb.maxY, fb.maxZ));

			boxes.add(new AxisAlignedBB(fb.minX, 0.5f - bounds.getY(), fb.minZ, 0.5f - bounds.getX(), 0.5f + bounds.getY(), fb.maxZ));
			boxes.add(new AxisAlignedBB(0.5f + bounds.getX(), 0.5f - bounds.getY(), fb.minZ, fb.maxX, 0.5f + bounds.getY(), fb.maxZ));

			break;
		case X:
			boxes.add(new AxisAlignedBB(fb.minX, fb.minY, fb.minZ, fb.maxX, 0.5f - bounds.getY(), fb.maxZ));
			boxes.add(new AxisAlignedBB(fb.minX, 0.5f + bounds.getY(), fb.minZ, fb.maxX, fb.maxY, fb.maxZ));

			boxes.add(new AxisAlignedBB(fb.minX, 0.5f - bounds.getY(), fb.minZ, fb.maxX, 0.5f + bounds.getY(), 0.5f - bounds.getX()));
			boxes.add(new AxisAlignedBB(fb.minX, 0.5f - bounds.getY(), 0.5f + bounds.getX(), fb.maxX, 0.5f + bounds.getY(), fb.maxZ));
			break;
		}

		return boxes;
	}

	// Helper to gather all quads from a model into a list.
	private static List<BakedQuad> gatherQuads(IBakedModel model, BlockState state, Random rand, IModelData data) {
		List<BakedQuad> modelQuads = new ArrayList<>();
		for (Direction face : Direction.values()) {
			modelQuads.addAll(model.getQuads(state, face, rand, data));
		}
		modelQuads.addAll(model.getQuads(state, null, rand, data));
		return modelQuads;
	}
}