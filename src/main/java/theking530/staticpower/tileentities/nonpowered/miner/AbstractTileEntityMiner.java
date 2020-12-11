package theking530.staticpower.tileentities.nonpowered.miner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.Constants;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.tools.miningdrill.DrillBit;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent;
import theking530.staticpower.tileentities.components.heat.HeatStorageComponent.HeatManipulationAction;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class AbstractTileEntityMiner extends TileEntityConfigurable {
	public final InventoryComponent drillBitInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;

	public final MachineProcessingComponent processingComponent;
	public final HeatStorageComponent heatStorage;
	public final LoopingSoundComponent miningSoundComponent;

	private boolean shouldDrawRadiusPreview;
	private final List<BlockPos> blocks;
	private int currentBlockIndex;
	private int miningRadius;
	private float heatGeneration;

	public AbstractTileEntityMiner(TileEntityTypeAllocator<? extends AbstractTileEntityMiner> allocator) {
		super(allocator);
		disableFaceInteraction();
		blocks = new ArrayList<BlockPos>();
		miningRadius = getBaseRadius();
		heatGeneration = getHeatGeneration();

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(drillBitInventory = new InventoryComponent("DrillBitInventory", 1, MachineSideMode.Never).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof DrillBit;
			}
		}));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 64, MachineSideMode.Never));

		registerComponent(
				processingComponent = new MachineProcessingComponent("ProcessingComponent", getProcessingTime(), this::canProcess, this::canProcess, this::processingCompleted, true));
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(getPowerUsage());

		registerComponent(miningSoundComponent = new LoopingSoundComponent("MiningSoundComponent", 20));

		registerComponent(
				heatStorage = new HeatStorageComponent("HeatStorageComponent", 10000.0f, 1.0f).setCapabiltiyFilter((amount, direction, action) -> action == HeatManipulationAction.COOL));
		registerComponent(new OutputServoComponent("OutputServo", 20, outputInventory));
		heatStorage.getStorage().setCanHeat(false);
	}

	@Override
	public void process() {
		// If there are no blocks, refresh them.
		if (this.blocks.size() == 0) {
			refreshBlocksInRange(miningRadius);
		}

		if (!world.isRemote) {
			// If the internal inventory is not empty, try to put the items sequentially
			// into the output slot.
			if (!InventoryUtilities.isInventoryEmpty(internalInventory)) {
				for (int i = 0; i < internalInventory.getSlots(); i++) {
					ItemStack stackInSlot = internalInventory.getStackInSlot(i);
					ItemStack remaining = InventoryUtilities.insertItemIntoInventory(outputInventory, stackInSlot, false);
					if (remaining.getCount() != stackInSlot.getCount()) {
						internalInventory.extractItem(i, stackInSlot.getCount() - remaining.getCount(), false);
					}
				}
			}

			if (processingComponent.isPerformingWork()) {
				heatStorage.getStorage().setCanHeat(true);
				heatStorage.getStorage().heat(heatGeneration, false);
				heatStorage.getStorage().setCanHeat(false);
			}

			if (processingComponent.getIsOnBlockState()) {
				miningSoundComponent.startPlayingSound(SoundEvents.ENTITY_MINECART_RIDING.getRegistryName(), SoundCategory.BLOCKS, 0.2f, 0.5f, getPos(), 64);
			} else {
				miningSoundComponent.stopPlayingSound();
			}
		}
	}

	protected abstract int getProcessingTime();

	protected abstract int getHeatGeneration();

	protected abstract int getBaseRadius();

	protected abstract int getPowerUsage();

	public boolean isDoneMining() {
		return currentBlockIndex == -1;
	}

	public int getBlocksRemaining() {
		return blocks.size() - (currentBlockIndex + 1);
	}

	public int getTicksRemainingUntilCompletion() {
		return getBlocksRemaining() * processingComponent.getMaxProcessingTime() - processingComponent.getCurrentProcessingTime();
	}

	/**
	 * Checks to make sure we can mine.
	 * 
	 * @return
	 */
	public ProcessingCheckState canProcess() {
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
		if (!heatStorage.getStorage().canFullyAbsorbHeat(heatGeneration)) {
			return ProcessingCheckState.error("Not enough heat capacity.");
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
	@SuppressWarnings("deprecation")
	protected ProcessingCheckState processingCompleted() {
		if (InventoryUtilities.isInventoryEmpty(internalInventory) && hasDrillBit()) {
			// Safety check. If we are out of range return true.
			if (currentBlockIndex >= blocks.size()) {
				// IF the blocks array has already been initialized, set the current block index
				// to -1.
				if (blocks.size() > 0) {
					currentBlockIndex = -1;
				}
				return ProcessingCheckState.ok();
			}

			// Get the block to mine.
			BlockPos minedPos = blocks.get(currentBlockIndex);
			BlockState minedBlockState = world.getBlockState(minedPos);

			// Increment the current block index.
			currentBlockIndex++;
			// IF we have reached the final block, set the current block index to -1.
			if (currentBlockIndex >= blocks.size()) {
				processingComponent.cancelProcessing();
				currentBlockIndex = -1;
			}

			// Skip air blocks.
			if (minedBlockState.getBlock() == Blocks.AIR) {
				return ProcessingCheckState.ok();
			}

			// Play the sound.
			world.playSound(null, getPos(), minedBlockState.getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.2f, 0.75f);

			// Damage the drill bit.
			if (getDrillBit().attemptDamageItem(1, world.rand, null)) {
				world.playSound(null, getPos(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
			}

			// Check if this is a mineable block. If not, just return true.
			if (!ModTags.MINER_ORE.contains(Item.getItemFromBlock(minedBlockState.getBlock()))) {
				return ProcessingCheckState.ok();
			}

			// Insert the mined items into the internal inventory.
			List<ItemStack> minedItems = attemptMineBlock(minedPos);
			for (int i = 0; i < minedItems.size(); i++) {
				InventoryUtilities.insertItemIntoInventory(internalInventory, minedItems.get(i), false);
			}

			// Set the mined block to cobblestone.
			world.setBlockState(minedPos, Blocks.AIR.getDefaultState(), 1 | 2);

			// Raise the on mined event.
			onBlockMined(minedPos, minedBlockState);

			// Mark the te as dirty.
			markDirty();
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.error("Items backed up in internal inventory.");
	}

	public void onBlockMined(BlockPos pos, BlockState minedBlock) {

	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public int getMiningRadius() {
		return miningRadius;
	}

	public void setMiningRadius(int miningRadius) {
		this.miningRadius = miningRadius;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		if (shouldDraw) {
			// If we were already drawing, remove and re-do it.
			if (shouldDrawRadiusPreview) {
				CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
			}
			// Set the scale equal to the range * 2 plus 1.
			Vector3f scale = new Vector3f((miningRadius * 2) + 1, getPos().getY() - 0.98f, (miningRadius * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getTileEntity().getPos().getX(), 1.0f, getTileEntity().getPos().getZ());
			position.add(new Vector3f(-miningRadius, 0.0f, -miningRadius));

			// Add the entry.
			CustomRenderer.addCubeRenderer(getTileEntity(), "range", position, scale, new Color(1.0f, 0.1f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
		}

		// Update the drawing value.
		shouldDrawRadiusPreview = shouldDraw;
	}

	protected List<ItemStack> attemptMineBlock(BlockPos pos) {
		List<ItemStack> output = WorldUtilities.getBlockDrops(getWorld(), pos);
		return output;
	}

	protected void refreshBlocksInRange(int range) {
		blocks.clear();

		for (int i = getPos().getY() - 1; i >= 2; i--) {
			List<BlockPos> tempList = new ArrayList<BlockPos>();
			BlockPos startingPos = new BlockPos(getPos().getX(), i, getPos().getZ());
			for (BlockPos pos : BlockPos.getAllInBoxMutable(startingPos.add(range, 0, range), startingPos.add(-range, 0, -range))) {
				if (pos != getPos()) {
					tempList.add(pos.toImmutable());
				}
			}
			blocks.addAll(tempList);
		}

		// If we are currently on a block that is out of range, set the block index to
		// -1 as we are already done.
		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = -1;
		}
	}

	public boolean hasDrillBit() {
		return !drillBitInventory.getStackInSlot(0).isEmpty();
	}

	public ItemStack getDrillBit() {
		return drillBitInventory.getStackInSlot(0);
	}

	public BlockPos getCurrentlyTargetedBlockPos() {
		return blocks.size() > 0 ? blocks.get(currentBlockIndex) : new BlockPos(0, 0, 0);
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
		miningRadius = nbt.getInt("radius");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);
		nbt.putInt("radius", miningRadius);

		return nbt;
	}

	@Override
	public void deserializeSaveNbt(CompoundNBT nbt) {
		super.deserializeSaveNbt(nbt);

		// Load the blocks for mining.
		blocks.clear();
		ListNBT savedBlocks = nbt.getList("blocks", Constants.NBT.TAG_COMPOUND);
		for (INBT blockTag : savedBlocks) {
			CompoundNBT blockTagCompound = (CompoundNBT) blockTag;
			blocks.add(BlockPos.fromLong(blockTagCompound.getLong("pos")));
		}
	}

	@Override
	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		super.serializeSaveNbt(nbt);

		// Save the blocks marked for mining.
		ListNBT savedBlocks = new ListNBT();
		blocks.forEach(block -> {
			CompoundNBT blockTag = new CompoundNBT();
			blockTag.putLong("pos", block.toLong());
			savedBlocks.add(blockTag);
		});
		nbt.put("blocks", savedBlocks);

		return nbt;
	}
}
