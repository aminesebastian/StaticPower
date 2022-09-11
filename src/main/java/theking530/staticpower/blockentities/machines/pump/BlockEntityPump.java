package theking530.staticpower.blockentities.machines.pump;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.MachineProcessingComponent;
import theking530.staticpower.blockentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.blockentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderPump;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityPump extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE_IRON = new BlockEntityTypeAllocator<BlockEntityPump>(
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.IronPump);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityPump>(
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.BasicPump);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE_ADVANCED = new BlockEntityTypeAllocator<BlockEntityPump>(
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.AdvancedPump);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE_STATIC = new BlockEntityTypeAllocator<BlockEntityPump>(
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.StaticPump);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE_ENERGIZED = new BlockEntityTypeAllocator<BlockEntityPump>(
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.EnergizedPump);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE_LUMUM = new BlockEntityTypeAllocator<BlockEntityPump>(
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.LumumPump);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPump> TYPE_CREATIVE = new BlockEntityTypeAllocator<BlockEntityPump>(
			(type, pos, state) -> new BlockEntityPump(type, pos, state), ModBlocks.CreativePump);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE_IRON.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
			TYPE_BASIC.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
			TYPE_ADVANCED.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
			TYPE_STATIC.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
			TYPE_ENERGIZED.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
			TYPE_LUMUM.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
			TYPE_CREATIVE.setTileEntitySpecialRenderer(BlockEntityRenderPump::new);
		}
	}

	public final FluidContainerInventoryComponent fluidContainerInventory;
	public final FluidTankComponent fluidTankComponent;
	public final MachineProcessingComponent processingComponent;
	public final BatteryInventoryComponent batteryInventory;
	private final Queue<BlockPos> positionsToPump;

	@UpdateSerialize
	public int pumpRate;

	public BlockEntityPump(BlockEntityTypeAllocator<BlockEntityPump> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Get the tier.
		StaticPowerTier tierObject = getTierObject();
		pumpRate = tierObject.pumpRate.get();

		// Add the tank component.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", tierObject.defaultTankCapacity.get()));
		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Output);
		fluidTankComponent.setCanFill(false);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		// Add the fluid output servo to deliver fluid to adjacent blocks.
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Register components to allow the pump to fill buckets in the GUI.
		registerComponent(
				fluidContainerInventory = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Register the processing component to handle the pumping.
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", pumpRate, this::canProcess, this::canProcess, this::pump, true)
				.setRedstoneControlComponent(redstoneControlComponent).setPowerComponent(powerStorage));

		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		// Battery
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));

		// Set the default side configuration.
		ioSideConfiguration.setDefaultConfiguration(new DefaultSideConfiguration().setSide(BlockSide.TOP, true, MachineSideMode.Input));

		// Enable face interaction.
		enableFaceInteraction();

		// Initialize the positions to pump container.
		positionsToPump = new LinkedList<BlockPos>();
	}

	@Override()
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.TOP && mode != MachineSideMode.Never) {
			return false;
		}
		return mode == MachineSideMode.Output;
	}

	/**
	 * This method returns true so long as we have enough space to pump a block.
	 * 
	 * @return
	 */
	public ProcessingCheckState canProcess() {
		if (!this.powerStorage.canSupplyPower(StaticPowerConfig.SERVER.pumpPowerUsage.get())) {
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
			FluidState fluidState = getLevel().getFluidState(position);

			while (!fluidState.isSource() && !positionsToPump.isEmpty()) {
				position = positionsToPump.poll();
				fluidState = getLevel().getFluidState(position);
			}

			if (position != null) {
				// If the fluid is pumpable, pump it. If not, something has changed drastically,
				// rebuild the queue.
				if (fluidState.getType().isSource(fluidState)) {
					// Check to make sure the fluid can go into the tank if we already have a fluid.
					if (!fluidTankComponent.isEmpty() && !fluidState.getType().equals(fluidTankComponent.getFluid().getFluid())) {
						return ProcessingCheckState.error("Encountered fluid that cannot be placed into the output tank!");
					}

					// Play the sound.
					getLevel().playSound(null, getBlockPos(), fluidState.getType() == Fluids.LAVA ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL, SoundSource.BLOCKS,
							0.5f, 1.0f);

					// Use the power.
					powerStorage.drainPower(StaticPowerConfig.SERVER.pumpPowerUsage.get(), false);

					// Pump the fluid.
					FluidStack pumpedStack = new FluidStack(fluidState.getType(), FluidAttributes.BUCKET_VOLUME);
					fluidTankComponent.fill(pumpedStack, FluidAction.EXECUTE);

					// Do not suck away the source block if this is a creative pump or its sucking
					// water.
					if (getTier() != StaticPowerTiers.CREATIVE && !fluidState.is(FluidTags.WATER)) {
						getLevel().setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
						// If this position is under the pump, place a tube block there.
						if (position.getX() == this.getBlockPos().getX() && position.getZ() == this.getBlockPos().getZ()) {
							this.getLevel().setBlock(position, ModBlocks.PumpTube.get().defaultBlockState(), 2);
						}
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
				LOGGER.debug(String.format("Rebuilt Pump Queue to size: %1$d for Pump at position: %2$s in Dimension: %3$s.", positionsToPump.size(), getBlockPos(),
						getLevel().dimensionType()));
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
			BlockPos testPos = position.relative(dir);
			FluidState fluidState = getLevel().getFluidState(testPos);
			if (!fluidState.isEmpty() && !positionsToPump.contains(testPos)) {
				positionsToPump.add(testPos);
			}
		}
	}

	private @Nullable BlockPos getInitialPumpBlock() {
		// Check from the block below the pump by two blocks.
		for (int i = 1; i < 3; i++) {
			// Skip checking lower than 0.
			if (getBlockPos().getY() - 1 <= 0) {
				continue;
			}

			// Get the block pos.
			BlockPos samplePos = new BlockPos(getBlockPos().getX(), getBlockPos().getY() - i, getBlockPos().getZ());

			// If we hit a non fluid block that is not just AIR, stop.
			if (!(getLevel().getBlockState(samplePos).getBlock() instanceof LiquidBlock) && getLevel().getBlockState(samplePos).getBlock() != Blocks.AIR) {
				return null;
			}

			// Search for a fluid block that is not empty.
			FluidState fluidState = getLevel().getFluidState(samplePos);
			if (!fluidState.isEmpty()) {
				return samplePos;
			}
		}
		return null;
	}

	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return SideConfigurationComponent.TOP_SIDE_ONLY_OUTPUT;
	}

	public CompoundTag serializeSaveNbt(CompoundTag nbt) {
		super.serializeSaveNbt(nbt);

		// Serialize the queued positions.
		ListTag queuedPositions = new ListTag();
		positionsToPump.forEach(pos -> {
			CompoundTag posTag = new CompoundTag();
			posTag.putLong("pos", pos.asLong());
			queuedPositions.add(posTag);
		});
		nbt.put("queued_positions", queuedPositions);

		return nbt;
	}

	public void deserializeSaveNbt(CompoundTag nbt) {
		super.deserializeSaveNbt(nbt);

		// Clear the queue just in case.
		positionsToPump.clear();

		// Deserialize the queued positions.
		ListTag queuedPositions = nbt.getList("queued_positions", Tag.TAG_COMPOUND);
		for (Tag posTag : queuedPositions) {
			CompoundTag posTagCompound = (CompoundTag) posTag;
			positionsToPump.add(BlockPos.of(posTagCompound.getLong("pos")));
		}

		LOGGER.info(String.format("Deserialized Pump at position: %1$s with: %2$d queued positions.", getBlockPos(), positionsToPump.size()));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPump(windowId, inventory, this);
	}
}
