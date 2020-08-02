package theking530.staticpower.tileentities.powered.crucible;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityCrucible extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent batterySlot;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;

	public TileEntityCrucible() {
		super(ModTileEntityTypes.CRUCIBLE);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batterySlot = new InventoryComponent("BatterySlot", 1, MachineSideMode.Never));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));

		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", 2, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true).setShouldControlBlockState(true));

		registerComponent(new BatteryComponent("BatteryComponent", batterySlot, 0, energyStorage.getStorage()));
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
	}

	/**
	 * Checks to see if the furnace can being processing. It checks for a valid
	 * input item, if there is enough power for one tick of processing (the
	 * processing can get stuck half way through), and checks to see if the output
	 * slot can contain the recipe output.
	 * 
	 * @return
	 */
	protected boolean canMoveFromInputToProcessing() {
		if (hasValidRecipe() && !moveComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty() && energyStorage.getStorage().getEnergyStored() >= DEFAULT_PROCESSING_COST) {
			ItemStack output = getRecipe(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1)).get().getRecipeOutput();
			return InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output);
		}
		return false;
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
			// Transfer the items to the internal inventory.
			transferItemInternally(inputInventory, 0, internalInventory, 0);
			internalInventory.setStackInSlot(1, inputInventory.getStackInSlot(1).copy());
			// Update the processing time.
			FormerRecipe recipe = getRecipe(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1)).orElse(null);
			processingComponent.setProcessingTime(recipe.getProcessingTime());
			// Trigger a block update.
			markTileEntityForSynchronization();
		}
		return true;
	}

	protected boolean canProcess() {
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
		if (!getWorld().isRemote && !internalInventory.getStackInSlot(0).isEmpty()) {
			ItemStack output = getRecipe(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1)).get().getRecipeOutput();
			if (!output.isEmpty() && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output)) {
				outputInventory.insertItem(0, output.copy(), false);
				internalInventory.setStackInSlot(0, ItemStack.EMPTY);
				internalInventory.setStackInSlot(1, ItemStack.EMPTY);
				markTileEntityForSynchronization();
				return true;
			}
		}
		return false;
	}

	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				getRecipe(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1)).ifPresent(recipe -> {
					energyStorage.usePower(recipe.getPowerCost());
				});
			}
		}
	}

	// Functionality
	public boolean hasValidRecipe() {
		return false;
	}

	public Optional<FormerRecipe> getRecipe(ItemStack itemStackInput, ItemStack mold) {
		return StaticPowerRecipeRegistry.getRecipe(FormerRecipe.RECIPE_TYPE, new RecipeMatchParameters().setItems(itemStackInput, mold).setStoredEnergy(energyStorage.getStorage().getEnergyStored()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerCrucible(windowId, inventory, this);
	}
}
