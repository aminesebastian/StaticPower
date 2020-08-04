package theking530.staticpower.tileentities.powered.miner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import theking530.common.utilities.Color;
import theking530.staticpower.client.rendering.CustomRenderer;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityMiner extends TileEntityMachine {
	public static final int DEFAULT_MINING_TIME = 1000;
	public static final int DEFAULT_MINING_COST = 1000;
	public static final int DEFAULT_IDLE_COST = 2;
	public static final int MINIG_RADIUS = 3;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent upgradesInventory;

	public final MachineProcessingComponent processingComponent;

	private boolean shouldDrawRadiusPreview;
	private final List<BlockPos> blocks;
	private int currentBlockIndex;

	public TileEntityMiner() {
		super(ModTileEntityTypes.MINER);
		this.disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 64, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_MINING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));

		blocks = new ArrayList<BlockPos>();
	}

	@Override
	public void process() {
		if (!world.isRemote) {
			energyStorage.usePower(DEFAULT_IDLE_COST);

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
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canStartProcess() {
		return InventoryUtilities.isInventoryEmpty(internalInventory) && energyStorage.hasEnoughPower(DEFAULT_MINING_COST);
	}

	public boolean canProcess() {
		return energyStorage.hasEnoughPower(DEFAULT_MINING_COST) && InventoryUtilities.isInventoryEmpty(internalInventory);
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		if (energyStorage.hasEnoughPower(DEFAULT_MINING_COST) && InventoryUtilities.isInventoryEmpty(internalInventory)) {
			// Use the mining energy.
			energyStorage.usePower(DEFAULT_MINING_COST);

			// Insert the mined items into the internal inventory.
			List<ItemStack> minedItems = attemptMineBlock(blocks.get(currentBlockIndex));
			for (int i = 0; i < minedItems.size(); i++) {
				InventoryUtilities.insertItemIntoInventory(internalInventory, minedItems.get(i), false);
			}

			// Increment the current block index.
			currentBlockIndex++;
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
			Vector3f scale = new Vector3f((MINIG_RADIUS * 2) + 1, 1.0f, (MINIG_RADIUS * 2) + 1);
			// Shift over so we center the range around the farmer.
			Vector3f position = new Vector3f(getTileEntity().getPos().getX(), getTileEntity().getPos().getY(), getTileEntity().getPos().getZ());
			position.add(new Vector3f(-MINIG_RADIUS, 0.0f, -MINIG_RADIUS));

			// Add the entry.
			CustomRenderer.addCubeRenderer(getTileEntity(), "range", position, scale, new Color(0.1f, 1.0f, 0.2f, 0.25f));
		} else {
			// Remove the entry.
			CustomRenderer.removeCubeRenderer(getTileEntity(), "range");
		}

		// Update the drawing value.
		shouldDrawRadiusPreview = shouldDraw;
	}

	protected List<ItemStack> attemptMineBlock(BlockPos pos) {
		List<ItemStack> output = new ArrayList<ItemStack>();
		return output;
	}

	protected void refreshBlocksInRange(int range) {
		blocks.clear();
		for (BlockPos pos : BlockPos.getAllInBoxMutable(getPos().add(-range, 0, -range), getPos().add(range, 0, range))) {
			if (pos != getPos()) {
				blocks.add(pos.toImmutable());
			}
		}
		blocks.add(getPos().add(range, 0, range));

		if (currentBlockIndex > blocks.size() - 1) {
			currentBlockIndex = 0;
		}
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
