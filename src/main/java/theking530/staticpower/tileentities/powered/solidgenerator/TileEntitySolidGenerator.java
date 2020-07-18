package theking530.staticpower.tileentities.powered.solidgenerator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.PowerDistributionComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class TileEntitySolidGenerator extends TileEntityMachine {
	public static final int DEFAULT_POWER_GENERATION = 20;
	public static final int DEFAULT_MOVING_TIME = 4;
	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent upgradesInventory;

	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;

	public int powerGenerationPerTick;

	public TileEntitySolidGenerator() {
		super(ModTileEntityTypes.SOLID_GENERATOR);
		this.disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return ForgeHooks.getBurnTime(stack) > 0;
			}
		}));

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", 2, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 5, this::canProcess, this::canProcess, this::processingCompleted, true));
		registerComponent(new PowerDistributionComponent("PowerDistributor", energyStorage.getStorage()));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		powerGenerationPerTick = DEFAULT_POWER_GENERATION;
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canMoveFromInputToProcessing() {
		if (!redstoneControlComponent.passesRedstoneCheck()) {
			return false;
		}
		// Check if there is a valid recipe.
		if (hasValidRecipe() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
			// If we passed all the previous checks, return true.
			return energyStorage.canAcceptPower(powerGenerationPerTick);
		}
		return false;
	}

	public boolean canProcess() {
		return !internalInventory.getStackInSlot(0).isEmpty() && redstoneControlComponent.passesRedstoneCheck();
	}

	@Override
	public void process() {
		if (processingComponent.isProcessing()) {
			if (!getWorld().isRemote) {
				if (energyStorage.addPower(powerGenerationPerTick)) {
					processingComponent.continueProcessing();
				} else {
					processingComponent.pauseProcessing();
				}
			}
		}
	}

	/**
	 * Once again, check to make sure the input item has not been removed or changed
	 * since we started the move process. If still valid, move a single input item
	 * to the internal inventory and being processing.
	 * 
	 * @return
	 */
	protected boolean movingCompleted() {
		if (hasValidRecipe()) {
			transferItemInternally(inputInventory, 0, internalInventory, 0);
			processingComponent.setProcessingTime(ForgeHooks.getBurnTime(internalInventory.getStackInSlot(0)));
			return true;
		}
		return false;
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		return true;
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canStartProcess() {
		// Check if there is a valid recipe.
		if (hasValidRecipe() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
			return true;
		}
		return false;
	}

	// Functionality
	public boolean hasValidRecipe() {
		return ForgeHooks.getBurnTime(inputInventory.getStackInSlot(0)) > 0;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerSolidGenerator(windowId, inventory, this);
	}
}
