package theking530.staticpower.tileentities.powered.pump;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderPump;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class TileEntityPump extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPump> TYPE_IRON = new TileEntityTypeAllocator<TileEntityPump>((type) -> new TileEntityPump(type, StaticPowerTiers.IRON),
			ModBlocks.IronPump);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPump> TYPE_BASIC = new TileEntityTypeAllocator<TileEntityPump>((type) -> new TileEntityPump(type, StaticPowerTiers.BASIC),
			ModBlocks.BasicPump);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPump> TYPE_ADVANCED = new TileEntityTypeAllocator<TileEntityPump>((type) -> new TileEntityPump(type, StaticPowerTiers.ADVANCED),
			ModBlocks.AdvancedPump);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPump> TYPE_STATIC = new TileEntityTypeAllocator<TileEntityPump>((type) -> new TileEntityPump(type, StaticPowerTiers.STATIC),
			ModBlocks.StaticPump);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPump> TYPE_ENERGIZED = new TileEntityTypeAllocator<TileEntityPump>((type) -> new TileEntityPump(type, StaticPowerTiers.ENERGIZED),
			ModBlocks.EnergizedPump);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPump> TYPE_LUMUM = new TileEntityTypeAllocator<TileEntityPump>((type) -> new TileEntityPump(type, StaticPowerTiers.LUMUM),
			ModBlocks.LumumPump);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPump> TYPE_CREATIVE = new TileEntityTypeAllocator<TileEntityPump>((type) -> new TileEntityPump(type, StaticPowerTiers.CREATIVE),
			ModBlocks.CreativePump);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_IRON.setTileEntitySpecialRenderer(TileEntityRenderPump::new);
			TYPE_BASIC.setTileEntitySpecialRenderer(TileEntityRenderPump::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(TileEntityRenderPump::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(TileEntityRenderPump::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(TileEntityRenderPump::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(TileEntityRenderPump::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(TileEntityRenderPump::new);
		}
	}

	public final FluidContainerInventoryComponent fluidContainerInventory;
	public final FluidTankComponent fluidTankComponent;
	public final MachineProcessingComponent processingComponent;
	public final BatteryInventoryComponent batteryInventory;
	private final Queue<BlockPos> positionsToPump;

	@UpdateSerialize
	public int pumpRate;

	public TileEntityPump(TileEntityTypeAllocator<TileEntityPump> allocator, ResourceLocation tier) {
		super(allocator, tier);

		// Get the tier.
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		pumpRate = tierObject.pumpRate.get();

		// Add the tank component.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get()));
		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Output);
		fluidTankComponent.setCanFill(false);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		// Add the fluid output servo to deliver fluid to adjacent blocks.
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the pump to fill buckets in the GUI.
		registerComponent(fluidContainerInventory = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Register the processing component to handle the pumping.
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", pumpRate, this::canProcess, this::canProcess, this::pump, true)
				.setRedstoneControlComponent(redstoneControlComponent).setEnergyComponent(energyStorage));

		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Battery
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));

		// Set the default side configuration.
		ioSideConfiguration.setDefaultConfiguration(MachineSideMode.Disabled, MachineSideMode.Output, MachineSideMode.Disabled, MachineSideMode.Disabled, MachineSideMode.Disabled,
				MachineSideMode.Disabled);

		// Enable face interaction.
		enableFaceInteraction();

		// Initialize the positions to pump container.
		positionsToPump = new LinkedList<BlockPos>();
	}

	@Override()
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.TOP && mode != MachineSideMode.Disabled) {
			return false;
		}
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output;
	}

	/**
	 * This method returns true so long as we have enough space to pump a block.
	 * 
	 * @return
	 */
	public ProcessingCheckState canProcess() {
		if (!this.energyStorage.hasEnoughPower(StaticPowerConfig.SERVER.pumpPowerUsage.get())) {
			return ProcessingCheckState.notEnoughPower(StaticPowerConfig.SERVER.pumpPowerUsage.get());

		}
		if ((fluidTankComponent.getFluidAmount() + FluidAttributes.BUCKET_VOLUME) > fluidTankComponent.getCapacity()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		return ProcessingCheckState.ok();
	}

	/**
	 * Pumps a single block off the queue, or attempts to rebuild the queue if
	 * something has gone wrong.
	 * 
	 * @return
	 */
	public ProcessingCheckState pump() {
		// Do nothing if the tank is near full.
		if ((fluidTankComponent.getFluidAmount() + FluidAttributes.BUCKET_VOLUME) > fluidTankComponent.getCapacity()) {
			return ProcessingCheckState.outputTankCannotTakeFluid();
		}

		// If the positions to pump is empty, try to start again.
		if (positionsToPump.size() == 0) {
			BlockPos newPos = getInitialPumpBlock();
			if (newPos != null) {
				positionsToPump.add(newPos);
			}
		}

		// If we have a position to pump, attempt to pump it.
		if (positionsToPump.size() > 0) {
			// Get the fluid state at the position to pump.
			BlockPos position = positionsToPump.poll();
			FluidState fluidState = getWorld().getFluidState(position);

			while (!fluidState.isSource() && !positionsToPump.isEmpty()) {
				position = positionsToPump.poll();
				fluidState = getWorld().getFluidState(position);
			}

			if (position != null) {
				// If the fluid is pumpable, pump it. If not, something has changed drastically,
				// rebuild the queue.
				if (fluidState.getFluid().isSource(fluidState)) {
					// Check to make sure the fluid can go into the tank if we already have a fluid.
					if (!fluidTankComponent.isEmpty() && !fluidState.getFluid().equals(fluidTankComponent.getFluid().getFluid())) {
						return ProcessingCheckState.error("Encountered fluid that cannot be placed into the output tank!");
					}

					// Play the sound.
					getWorld().playSound(null, getPos(), fluidState.getFluid() == Fluids.LAVA ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f,
							1.0f);

					// Use the power.
					energyStorage.useBulkPower(StaticPowerConfig.SERVER.pumpPowerUsage.get());

					// Pump the fluid.
					FluidStack pumpedStack = new FluidStack(fluidState.getFluid(), FluidAttributes.BUCKET_VOLUME);
					fluidTankComponent.fill(pumpedStack, FluidAction.EXECUTE);

					// Do not suck away the source block if this is a creative pump.
					if (getTier() != StaticPowerTiers.CREATIVE) {
						getWorld().setBlockState(position, Blocks.AIR.getDefaultState());
					}

					// If this is water, we just stop. No recursion as water is infinite anyway.
					if (pumpedStack.getFluid() == Fluids.WATER) {
						positionsToPump.clear();
						return ProcessingCheckState.ok();
					}
				}

				// No matter what, search around the pumped block.
				searchAroundPumpedBlock(position);
				// Log the pump queue creation.
				LOGGER.debug(
						String.format("Rebuilt Pump Queue to size: %1$d for Pump at position: %2$s in Dimension: %3$s.", positionsToPump.size(), getPos(), getWorld().getDimensionType()));
			}
		} else {
			return ProcessingCheckState.error("No sources found to pump!");
		}

		// Always return true so the machine processing component always proceeds.
		return ProcessingCheckState.ok();
	}

	private void searchAroundPumpedBlock(BlockPos position) {
		if (positionsToPump.size() >= 100) {
			return;
		}
		// Search on all six sides.
		for (Direction dir : Direction.values()) {
			BlockPos testPos = position.offset(dir);
			FluidState fluidState = getWorld().getFluidState(testPos);
			if (!fluidState.isEmpty() && !positionsToPump.contains(testPos)) {
				positionsToPump.add(testPos);
			}
		}
	}

	private @Nullable BlockPos getInitialPumpBlock() {
		// Check from the block below the pump by two blocks.
		for (int i = 1; i < 3; i++) {
			// Skip checking lower than 0.
			if (getPos().getY() - 1 <= 0) {
				continue;
			}

			// Get the block pos.
			BlockPos samplePos = new BlockPos(getPos().getX(), getPos().getY() - i, getPos().getZ());

			// If we hit a non fluid block that is not just AIR, stop.
			if (!(getWorld().getBlockState(samplePos).getBlock() instanceof FlowingFluidBlock) && getWorld().getBlockState(samplePos).getBlock() != Blocks.AIR) {
				return null;
			}

			// Search for a fluid block that is not empty.
			FluidState fluidState = getWorld().getFluidState(samplePos);
			if (!fluidState.isEmpty()) {
				return samplePos;
			}
		}
		return null;
	}

	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		super.serializeSaveNbt(nbt);

		// Serialize the queued positions.
		ListNBT queuedPositions = new ListNBT();
		positionsToPump.forEach(pos -> {
			CompoundNBT posTag = new CompoundNBT();
			posTag.putLong("pos", pos.toLong());
			queuedPositions.add(posTag);
		});
		nbt.put("queued_positions", queuedPositions);

		return nbt;
	}

	public void deserializeSaveNbt(CompoundNBT nbt) {
		super.deserializeSaveNbt(nbt);

		// Clear the queue just in case.
		positionsToPump.clear();

		// Deserialize the queued positions.
		ListNBT queuedPositions = nbt.getList("queued_positions", Constants.NBT.TAG_COMPOUND);
		for (INBT posTag : queuedPositions) {
			CompoundNBT posTagCompound = (CompoundNBT) posTag;
			positionsToPump.add(BlockPos.fromLong(posTagCompound.getLong("pos")));
		}

		LOGGER.info(String.format("Deserialized Pump at position: %1$s with: %2$d queued positions.", getPos(), positionsToPump.size()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPump(windowId, inventory, this);
	}
}
