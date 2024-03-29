package theking530.staticpower.blockentities.nonpowered.miner;

import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.heat.HeatUtilities;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.processing.IProcessor;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer;
import theking530.staticcore.blockentity.components.control.processing.machine.MachineProcessingComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent;
import theking530.staticcore.blockentity.components.heat.HeatStorageComponent.HeatManipulationAction;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent.InventoryChangeType;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.loopingsound.LoopingSoundComponent;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.teams.ServerTeam;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.rendering.renderers.RadiusPreviewRenderer;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.tools.miningdrill.DrillBit;

public abstract class AbstractTileEntityMiner extends BlockEntityBase
		implements IProcessor<MachineProcessingComponent> {
	public final InventoryComponent drillBitInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final MachineProcessingComponent processingComponent;
	public final HeatStorageComponent heatStorage;
	public final LoopingSoundComponent miningSoundComponent;

	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	private boolean shouldDrawRadiusPreview;
	private final List<BlockPos> blocks;
	private int currentBlockIndex;

	public AbstractTileEntityMiner(BlockEntityTypeAllocator<? extends AbstractTileEntityMiner> allocator, BlockPos pos,
			BlockState state) {
		super(allocator, pos, state);
		blocks = new ArrayList<BlockPos>();

		// Get the tier.
		StaticCoreTier tierObject = StaticCoreConfig.getTier(StaticPowerTiers.STATIC);
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				DefaultMachineNoFacePreset.INSTANCE));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent",
				RedstoneMode.Ignore));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(drillBitInventory = new InventoryComponent("DrillBitInventory", 1, MachineSideMode.Never)
				.setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
					public boolean canInsertItem(int slot, ItemStack stack) {
						return stack.getItem() instanceof DrillBit;
					}
				}));

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 64, MachineSideMode.Never));
		registerComponent(
				upgradesInventory = (UpgradeInventoryComponent) new UpgradeInventoryComponent("UpgradeInventory", 3)
						.setModifiedCallback(this::upgradeInventoryChanged));

		registerComponent(
				processingComponent = new MachineProcessingComponent("ProcessingComponent", getProcessingTime()));
		processingComponent.setShouldControlOnBlockState(true);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setBasePowerUsage(getFuelUsage());
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPreProductionTime(0);

		registerComponent(miningSoundComponent = new LoopingSoundComponent("MiningSoundComponent", 20));

		// Add the heat storage and the upgrade inventory to the heat component.
		registerComponent(heatStorage = new HeatStorageComponent("HeatStorageComponent", tierObject)
				.setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.COOL));
		heatStorage.setUpgradeInventory(upgradesInventory);

		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory));
		heatStorage.setCanHeat(false);
	}

	@Override
	public void process() {
		// If there are no blocks, refresh them.
		if (this.blocks.size() == 0) {
			refreshBlocksInRange(getRadius());
		}

		if (getLevel().isClientSide()) {
			if (processingComponent.isBlockStateOn()) {
				if (SDMath.diceRoll(0.5)) {
					BlockPos minedPos = getCurrentlyTargetedBlockPos();
					getLevel().addParticle(ParticleTypes.POOF, minedPos.getX() + 0.5f, minedPos.getY() + 0.5f,
							minedPos.getZ() + 0.5f, 0.0f, 0.01f, 0.0f);
				}
				miningSoundComponent.startPlayingSound(SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 0.2f, 0.5f,
						getBlockPos(), 64);
			} else {
				miningSoundComponent.stopPlayingSound();
			}
		} else {
			// If the internal inventory is not empty, try to put the items sequentially
			// into the output slot.
			for (int i = 0; i < internalInventory.getSlots(); i++) {
				ItemStack stackInSlot = internalInventory.getStackInSlot(i);
				if (stackInSlot.isEmpty()) {
					continue;
				}

				ItemStack remaining = InventoryUtilities.insertItemIntoInventory(outputInventory, stackInSlot, false);
				if (remaining.getCount() != stackInSlot.getCount()) {
					internalInventory.extractItem(i, stackInSlot.getCount() - remaining.getCount(), false);
				}
			}
		}
	}

	public abstract int getProcessingTime();

	public abstract int getHeatGeneration();

	public abstract int getRadius();

	public abstract double getFuelUsage();

	public boolean isDoneMining() {
		return currentBlockIndex == -1;
	}

	public int getBlocksRemaining() {
		return blocks.size() - (currentBlockIndex + 1);
	}

	public int getTicksRemainingUntilCompletion() {
		return getBlocksRemaining() * processingComponent.getProcessingTimer().getMaxTime()
				- processingComponent.getProcessingTimer().getCurrentTime();
	}

	/**
	 * Upgrade handler.
	 * 
	 * @param type
	 * @param stack
	 * @param slot
	 */
	protected void upgradeInventoryChanged(InventoryChangeType type, ItemStack stack, Integer slot) {
		refreshBlocksInRange(getRadius());
	}

	@Override
	public ProcessingCheckState canStartProcessing(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {
		// If we're done processing, stop.
		if (isDoneMining()) {
			return ProcessingCheckState.skip();
		}
		if (!InventoryUtilities.isInventoryEmpty(internalInventory)) {
			return ProcessingCheckState.error("Items backed up in internal inventory.");
		}
		if (!hasDrillBit()) {
			return ProcessingCheckState.error("Missing drill bit.");
		}
		if (getDrillBit().getDamageValue() >= getDrillBit().getMaxDamage()) {
			return ProcessingCheckState.error("Drill bit needs repair!");
		}
		if (!HeatUtilities.canFullyAbsorbHeat(heatStorage, getHeatGeneration())) {
			return ProcessingCheckState.error("Not enough heat capacity (Requires "
					+ GuiTextUtilities.formatHeatToString(getHeatGeneration()).getString() + ")");
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void onProcessingProgressMade(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {
		heatStorage.setCanHeat(true);
		heatStorage.heat(getHeatGeneration(), HeatTransferAction.EXECUTE);
		heatStorage.setCanHeat(false);
	}

	@Override
	public ProcessingCheckState canCompleteProcessing(MachineProcessingComponent component,
			ProcessingContainer processingContainer) {

		// Safety check. If we are out of range return true.
		if (currentBlockIndex >= blocks.size()) {
			// IF the blocks array has already been initialized, set the current block index
			// to -1.
			if (blocks.size() > 0) {
				currentBlockIndex = -1;
			}
			return ProcessingCheckState.ok();
		}

		// Done!
		if (currentBlockIndex == -1) {
			return ProcessingCheckState.error("Processing Complete!");
		}

		return ProcessingCheckState.ok();
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	public void onProcessingCompleted(MachineProcessingComponent component, ProcessingContainer processingContainer) {
		// Get the block to mine.
		BlockPos minedPos = blocks.get(currentBlockIndex);
		BlockState minedBlockState = level.getBlockState(minedPos);

		// Increment the current block index.
		currentBlockIndex++;
		// IF we have reached the final block, set the current block index to -1.
		if (currentBlockIndex >= blocks.size()) {
			currentBlockIndex = -1;
		}

		// Skip air blocks.
		if (minedBlockState.getBlock() == Blocks.AIR) {
			return;
		}

		// Play the sound.
		level.playSound(null, getBlockPos(), minedBlockState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.2f,
				0.75f);
		level.playSound(null, getCurrentlyTargetedBlockPos(), minedBlockState.getSoundType().getBreakSound(),
				SoundSource.BLOCKS, 0.2f, 0.75f);

		// Damage the drill bit.
		if (getDrillBit().hurt(1, level.random, null)) {
			level.playSound(null, getBlockPos(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
		}

		// Check if this is a mineable block. If not, just return true.
		if (!canMineBlock(minedBlockState, minedPos)) {
			return;
		}

		// Insert the mined items into the internal inventory.
		List<ItemStack> minedItems = attemptMineBlock(minedPos);
		ProductionTrackingToken<ItemStack> itemProductionToken = processingComponent
				.getProductionToken(StaticCoreProductTypes.Item.get());
		for (int i = 0; i < minedItems.size(); i++) {
			InventoryUtilities.insertItemIntoInventory(internalInventory, minedItems.get(i), false);
			itemProductionToken.produced((ServerTeam) getTeamComponent().getOwningTeam(), minedItems.get(i),
					minedItems.get(i).getCount());
			itemProductionToken.setProductionPerSecond((ServerTeam) getTeamComponent().getOwningTeam(),
					minedItems.get(i), minedItems.get(i).getCount()
							* (1 / ((Math.max(processingComponent.getProcessingTimer().getMaxTime(), 1) / 20.0))));
		}

		// Set the mined block to cobblestone.
		level.setBlock(minedPos, Blocks.AIR.defaultBlockState(), 1 | 2 | 4);

		// Raise the on mined event.
		onBlockMined(minedPos, minedBlockState);
	}

	public void onBlockMined(BlockPos pos, BlockState minedBlock) {

	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		if (shouldDraw) {
			// If we were already drawing, remove and re-do it.
			if (shouldDrawRadiusPreview) {
				RadiusPreviewRenderer.removeRadiusRenderer(this, "range");
			}

			// Get the radius.
			int radius = getRadius();

			// Set the scale equal to the range * 2 plus 1.
			Vector3f scale = new Vector3f((radius * 2) + 1, getBlockPos().getY() - 0.98f, (radius * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getBlockPos().getX(), 1.0f, getBlockPos().getZ());
			position.add(new Vector3f(-radius, 0.0f, -radius));

			// Add the entry.
			RadiusPreviewRenderer.addRadiusRenderRequest(this, "range", position, scale,
					new SDColor(1.0f, 0.1f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			RadiusPreviewRenderer.removeRadiusRenderer(this, "range");
		}

		// Update the drawing value.
		shouldDrawRadiusPreview = shouldDraw;
	}

	protected List<ItemStack> attemptMineBlock(BlockPos pos) {
		List<ItemStack> output = WorldUtilities.getBlockDrops(getLevel(), pos);
		return output;
	}

	protected void refreshBlocksInRange(int range) {
		blocks.clear();
		currentBlockIndex = 0;

		for (int i = getBlockPos().getY() - 1; i >= getLevel().getMinBuildHeight() + 2; i--) {
			List<BlockPos> tempList = new ArrayList<BlockPos>();
			BlockPos startingPos = new BlockPos(getBlockPos().getX(), i, getBlockPos().getZ());
			for (BlockPos pos : BlockPos.betweenClosed(startingPos.offset(range, 0, range),
					startingPos.offset(-range, 0, -range))) {
				// If the position is on the y level under the miner, and it exists on the same
				// X or Y plane, skip it.
				if (pos.getY() == this.getBlockPos().getY() - 1
						&& (pos.getX() == getBlockPos().getX() || pos.getZ() == getBlockPos().getZ())) {
					continue;
				}

				// If the position is NOT the mining position (just in case), add it.
				if (pos != getBlockPos()) {
					tempList.add(pos.immutable());
				}
			}
			blocks.addAll(tempList);
		}

		// Forward to the first actual block.
		boolean minableBlockFound = false;
		for (int i = 0; i < blocks.size(); i++) {
			BlockState test = level.getBlockState(blocks.get(i));
			if (canMineBlock(test, blocks.get(i))) {
				currentBlockIndex = i;
				minableBlockFound = true;
				break;
			}
		}

		// If we are currently on a block that is out of range, or no minable blocks
		// were found, set the block index to
		// -1 as we are already done.
		if (!minableBlockFound || currentBlockIndex >= blocks.size() - 1) {
			currentBlockIndex = -1;
		}
	}

	public boolean hasDrillBit() {
		return !drillBitInventory.getStackInSlot(0).isEmpty();
	}

	public ItemStack getDrillBit() {
		return drillBitInventory.getStackInSlot(0);
	}

	protected boolean canMineBlock(BlockState block, BlockPos pos) {
		if (block.isAir()) {
			return false;
		}
		if (block.hasBlockEntity()) {
			return false;
		}
		if (!block.getFluidState().isEmpty()) {
			return false;
		}
		return block.getDestroySpeed(getLevel(), pos) >= 0;
	}

	public BlockPos getCurrentlyTargetedBlockPos() {
		if (currentBlockIndex == -1 || currentBlockIndex >= blocks.size()) {
			StaticPower.LOGGER.warn(
					String.format("Attempting to render an invalid current block index: %1$s.", currentBlockIndex));
			return new BlockPos(0, 0, 0);
		}

		return blocks.size() > 0 ? blocks.get(currentBlockIndex) : new BlockPos(0, 0, 0);
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);

		return nbt;
	}

	@Override
	public void deserializeSaveNbt(CompoundTag nbt) {
		super.deserializeSaveNbt(nbt);

		// Load the blocks for mining.
		blocks.clear();
		ListTag savedBlocks = nbt.getList("blocks", Tag.TAG_COMPOUND);
		for (Tag blockTag : savedBlocks) {
			CompoundTag blockTagCompound = (CompoundTag) blockTag;
			blocks.add(BlockPos.of(blockTagCompound.getLong("pos")));
		}
	}

	@Override
	public CompoundTag serializeSaveNbt(CompoundTag nbt) {
		super.serializeSaveNbt(nbt);

		// Save the blocks marked for mining.
		ListTag savedBlocks = new ListTag();
		blocks.forEach(block -> {
			CompoundTag blockTag = new CompoundTag();
			blockTag.putLong("pos", block.asLong());
			savedBlocks.add(blockTag);
		});
		nbt.put("blocks", savedBlocks);

		return nbt;
	}
}
