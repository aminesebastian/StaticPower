package theking530.staticpower.tileentities.powered.miner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeHooks;
import theking530.common.utilities.Color;
import theking530.common.utilities.SDMath;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.RedstoneMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class TileEntityMiner extends TileEntityBase {
	public static final int DEFAULT_MINING_TIME = 5;
	public static final int DEFAULT_MINING_COST = 10;
	public static final int DEFAULT_IDLE_COST = 1;
	public static final int DEFAULT_FUEL_MOVE_TIME = 4;
	public static final int MINING_RADIUS = 5;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent fuelInventory;
	public final InventoryComponent upgradesInventory;

	public final MachineProcessingComponent processingComponent;
	public final MachineProcessingComponent fuelComponent;
	public final MachineProcessingComponent fuelMoveComponent;

	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	private boolean shouldDrawRadiusPreview;
	private final List<BlockPos> blocks;
	private int currentBlockIndex;

	public TileEntityMiner() {
		super(ModTileEntityTypes.MINER);
		disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 64, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", this::onSidesConfigUpdate, this::checkSideConfiguration));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));

		registerComponent(fuelInventory = new InventoryComponent("FuelInventory", 1, MachineSideMode.Never));
		registerComponent(fuelMoveComponent = new MachineProcessingComponent("FuelMoveComponent", DEFAULT_FUEL_MOVE_TIME, this::canMoveFuel, this::canMoveFuel, this::fuelMoved, true));
		registerComponent(fuelComponent = new MachineProcessingComponent("FuelComponent", 0, this::canStartProcessingFuel, this::canProcesFuel, this::fuelProcessingCompleted, true)
				.setShouldControlBlockState(true));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_MINING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));

		blocks = new ArrayList<BlockPos>();
	}

	@Override
	public void process() {
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
		}

		// Randomly generate smoke and flame particles.
		if (processingComponent.isPerformingWork()) {
			float randomOffset = (2 * RANDOM.nextFloat()) - 1.0f;
			if (SDMath.diceRoll(0.25f)) {

				randomOffset /= 3.5f;
				float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
				Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
				getWorld().addParticle(ParticleTypes.SMOKE, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
				getWorld().addParticle(ParticleTypes.FLAME, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
			}
		}
	}

	public boolean canStartProcessingFuel() {
		return isValidFuel(fuelInventory.getStackInSlot(0));
	}

	public boolean canMoveFuel() {
		return isValidFuel(inputInventory.getStackInSlot(0)) && fuelInventory.getStackInSlot(0).isEmpty();
	}

	public boolean fuelMoved() {
		int burnTime = getFuelBurnTime(inputInventory.getStackInSlot(0));
		fuelComponent.setProcessingTime(burnTime);
		transferItemInternally(inputInventory, 0, fuelInventory, 0);
		return true;
	}

	public boolean canProcesFuel() {
		return currentBlockIndex < blocks.size();
	}

	public boolean fuelProcessingCompleted() {
		fuelComponent.setProcessingTime(0);
		fuelInventory.getStackInSlot(0).shrink(1);
		return true;
	}

	/**
	 * Checks to make sure we can mine.
	 * 
	 * @return
	 */
	public boolean canProcess() {
		// If there are no blocks, refresh them.
		if (this.blocks.size() == 0) {
			refreshBlocksInRange(MINING_RADIUS);
		}

		// IF we have reached the final block, set the current block index to -1 and
		// mark the tile as dirty.
		if (currentBlockIndex >= blocks.size()) {
			processingComponent.cancelProcessing();
			currentBlockIndex = -1;
			markDirty();
			return false;
		}
		return InventoryUtilities.isInventoryEmpty(internalInventory) && getRemainingFuel() > 0 && fuelComponent.isPerformingWork();
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected boolean processingCompleted() {
		if (InventoryUtilities.isInventoryEmpty(internalInventory)) {
			// Get the block to mine.
			BlockPos minedPos = blocks.get(currentBlockIndex);
			BlockState minedBlockState = world.getBlockState(minedPos);

			// Increment the current block index.
			currentBlockIndex++;

			// Skip air blocks.
			if (minedBlockState.getBlock() == Blocks.AIR) {
				return true;
			}

			// If we hit dirt, make it coarse dirt.
			if (minedBlockState.getBlock() == Blocks.DIRT || minedBlockState.getBlock() == Blocks.GRASS_BLOCK) {
				world.playSound(null, minedPos, minedBlockState.getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1.0f);
				world.setBlockState(minedPos, Blocks.COARSE_DIRT.getDefaultState(), 1 | 2);
				return true;
			}

			// Check if this is a mineable block. If not, just return true.
			if (!ModTags.MINER_ORE.contains(Item.getItemFromBlock(minedBlockState.getBlock()))) {
				return true;
			}

			// Insert the mined items into the internal inventory.
			List<ItemStack> minedItems = attemptMineBlock(minedPos);
			for (int i = 0; i < minedItems.size(); i++) {
				InventoryUtilities.insertItemIntoInventory(internalInventory, minedItems.get(i), false);
			}

			// Set the mined block to cobblestone.
			world.playSound(null, getPos(), minedBlockState.getSoundType().getBreakSound(), SoundCategory.BLOCKS, 0.5f, 1.0f);
			world.setBlockState(minedPos, Blocks.COBBLESTONE.getDefaultState(), 1 | 2);

			// Use extra fuel when actually mining.
			this.fuelComponent.setProcessingTime(Math.min(fuelComponent.getCurrentProcessingTime() + DEFAULT_MINING_COST, fuelComponent.getProcessingTime()));

			markDirty();
			return true;
		}
		return false;
	}

	public boolean getShouldDrawRadiusPreview() {
		return shouldDrawRadiusPreview;
	}

	public void setShouldDrawRadiusPreview(boolean shouldDraw) {
		if (shouldDraw) {
			// If we were already drawing, remove and re-do it.
			if (shouldDrawRadiusPreview) {
				CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
			}
			// Set the scale equal to the range * 2 plus 1.
			Vector3f scale = new Vector3f((MINING_RADIUS * 2) + 1, getPos().getY() - 0.98f, (MINING_RADIUS * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getTileEntity().getPos().getX(), 1.0f, getTileEntity().getPos().getZ());
			position.add(new Vector3f(-MINING_RADIUS, 0.0f, -MINING_RADIUS));

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

	public int getFuelBurnTime(ItemStack input) {
		return ForgeHooks.getBurnTime(input);
	}

	public boolean isValidFuel(ItemStack input) {
		return ForgeHooks.getBurnTime(input) > 0;
	}

	public int getRemainingFuel() {
		return fuelComponent.getProcessingTime() - fuelComponent.getCurrentProcessingTime();
	}

	/* Side Control */
	protected void onSidesConfigUpdate(Direction worldSpaceSide, MachineSideMode newMode) {
		Direction relativeSpaceSide = SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection());
		if (DisableFaceInteraction && ioSideConfiguration.getWorldSpaceDirectionConfiguration(relativeSpaceSide) != MachineSideMode.Never) {
			ioSideConfiguration.setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection()), MachineSideMode.Never);
		}
	}

	protected boolean checkSideConfiguration(Direction worldSpaceSide, MachineSideMode mode) {
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular || mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		currentBlockIndex = nbt.getInt("current_index");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putInt("current_index", currentBlockIndex);
		return nbt;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerMiner(windowId, inventory, this);
	}
}
