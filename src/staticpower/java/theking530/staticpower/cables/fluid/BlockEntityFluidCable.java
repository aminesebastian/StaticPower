package theking530.staticpower.cables.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderFluidCable;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityFluidCable extends BlockEntityBase {
	public enum FluidPipeType {
		BASIC, INDUSTRIAL, CAPILLARY
	}

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidCable> TYPE_REGULAR = new BlockEntityTypeAllocator<BlockEntityFluidCable>("cable_fluid",
			(allocator, pos, state) -> new BlockEntityFluidCable(allocator, pos, state, 2.0f / 16.0f, FluidPipeType.BASIC), ModBlocks.FluidCables.values());

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidCable> TYPE_INDUSTRIAL = new BlockEntityTypeAllocator<BlockEntityFluidCable>("cable_fluid_industrial",
			(allocator, pos, state) -> new BlockEntityFluidCable(allocator, pos, state, 3.5f / 16.0f, FluidPipeType.INDUSTRIAL), ModBlocks.IndustrialFluidCables.values());

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityFluidCable> TYPE_CAPILLARY = new BlockEntityTypeAllocator<BlockEntityFluidCable>("cable_fluid_capillary",
			(allocator, pos, state) -> new BlockEntityFluidCable(allocator, pos, state, 1.25f / 16.0f, FluidPipeType.CAPILLARY), ModBlocks.CapillaryFluidCables.values());

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_REGULAR.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_INDUSTRIAL.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
			TYPE_CAPILLARY.setTileEntitySpecialRenderer(BlockEntityRenderFluidCable::new);
		}
	}

	public FluidCableComponent fluidCableComponent;
	public float fluidRenderRadius;

	public BlockEntityFluidCable(BlockEntityTypeAllocator<BlockEntityFluidCable> allocator, BlockPos pos, BlockState state, float radius, FluidPipeType type) {
		super(allocator, pos, state);

		TierFluidCableConfiguration config = getTierObject().cableFluidConfiguration;
		int transferRate = 0;
		switch (type) {
		case BASIC:
			transferRate = config.cableFluidTransferRate.get();
			break;
		case CAPILLARY:
			transferRate = config.cableCapillaryFluidTransferRate.get();
			break;
		case INDUSTRIAL:
			transferRate = config.cableIndustrialFluidTransferRate.get();
			break;
		}

		float defaultPressureProperty = 0;
		switch (type) {
		case BASIC:
			defaultPressureProperty = config.cableFluidPressureDissipation.get();
			break;
		case CAPILLARY:
			defaultPressureProperty = config.cableCapillaryPressureDissipation.get();
			break;
		case INDUSTRIAL:
			defaultPressureProperty = config.cableIndustrialPressureDissipation.get();
			break;
		}

		PipePressureProperties pressureProperties;
		if (type == FluidPipeType.CAPILLARY) {
			pressureProperties = new PipePressureProperties(-2 * defaultPressureProperty, -defaultPressureProperty, 2 * defaultPressureProperty);
		} else {
			pressureProperties = new PipePressureProperties(2 * defaultPressureProperty, -defaultPressureProperty, -2 * defaultPressureProperty);
		}

		// Set capacity == 5 * transferRate so that a cable connected to a fluid source
		// can supply on all other sides.
		registerComponent(fluidCableComponent = new FluidCableComponent("FluidCableComponent", type, transferRate * 5, transferRate, pressureProperties));
		fluidRenderRadius = radius;
	}
}
