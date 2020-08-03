package theking530.staticpower.tileentities.powered.fusionfurnace;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityFusionFurnace extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final int DEFAULT_PROCESSING_COST = 20;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;

	public TileEntityFusionFurnace() {
		super(ModTileEntityTypes.FUSION_FURNACE);
		this.disableFaceInteraction();

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 5, MachineSideMode.Input));

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 5, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", DEFAULT_MOVING_TIME, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true));

		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));
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
			// Gets the recipe and its outputs.
			Optional<FusionFurnaceRecipe> recipe = getRecipe(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
					inputInventory.getStackInSlot(4));

			// If the items cannot be insert into the output, return false.
			if (!InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.get().getOutput().getItem())) {
				return false;
			}

			// If we passed all the previous checks, return true.
			return energyStorage.hasEnoughPower(recipe.get().getPowerCost());
		}
		return false;
	}

	/**
	 * Once again, check to make sure the input item has not been removed or changed
	 * since we started the move process. If still valid, move a single input item
	 * to the internal inventory and being processing. Return true regardless so the
	 * movement component resets.
	 * 
	 * @return
	 */
	protected boolean movingCompleted() {
		if (hasValidRecipe()) {
			FusionFurnaceRecipe recipe = getRecipe(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3),
					inputInventory.getStackInSlot(4)).get();
			
			// Transfer items.
			for (int i = 0; i < recipe.getInputs().size(); i++) {
				transferItemInternally(recipe.getInputs().get(i).getCount(), inputInventory, i, internalInventory, i);
			}

			markTileEntityForSynchronization();
		}
		return true;
	}

	/**
	 * Indicates if we can start or continue processing. If this returns false and
	 * we are processing, processing pauses. If we are not processing and this
	 * returns true, we will start processing.
	 * 
	 * @return
	 */
	public boolean canProcess() {
		FusionFurnaceRecipe recipe = getRecipe(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1), internalInventory.getStackInSlot(2), internalInventory.getStackInSlot(3),
				internalInventory.getStackInSlot(4)).orElse(null);
		return recipe != null && redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(recipe.getPowerCost())
				&& InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getOutput().getItem());
	}

	/**
	 * Once the processing is completed, place the output in the output slot (if
	 * possible). If not, return false. This method will continue to be called until
	 * true is returned.
	 * 
	 * @return
	 */
	protected boolean processingCompleted() {
		// If on the server.
		if (!getWorld().isRemote) {
			// Get the recipe.
			FusionFurnaceRecipe recipe = getRecipe(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1), internalInventory.getStackInSlot(2), internalInventory.getStackInSlot(3),
					internalInventory.getStackInSlot(4)).orElse(null);
			if (recipe != null) {
				// Ensure the output slots can take the recipe.
				if (InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getOutput().getItem())) {

					// Insert the output into the output inventory.
					if (SDMath.diceRoll(recipe.getOutput().getOutputChance())) {
						InventoryUtilities.insertItemIntoInventory(outputInventory, recipe.getOutput().getItem().copy(), false);
					}

					// Clear the internal inventory.
					internalInventory.setStackInSlot(0, ItemStack.EMPTY);
					internalInventory.setStackInSlot(1, ItemStack.EMPTY);
					internalInventory.setStackInSlot(2, ItemStack.EMPTY);
					internalInventory.setStackInSlot(3, ItemStack.EMPTY);
					internalInventory.setStackInSlot(4, ItemStack.EMPTY);

					// Sync the tile entity.
					markTileEntityForSynchronization();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void process() {
		if (processingComponent.isPerformingWork()) {
			if (!getWorld().isRemote) {
				getRecipe(internalInventory.getStackInSlot(0)).ifPresent(recipe -> {
					energyStorage.usePower(recipe.getPowerCost());
				});
			}
		}
	}

	/**
	 * Checks to see if the input item forms a valid recipe.
	 * 
	 * @return
	 */
	public boolean hasValidRecipe() {
		return getRecipe(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1), inputInventory.getStackInSlot(2), inputInventory.getStackInSlot(3), inputInventory.getStackInSlot(4)).isPresent();
	}

	/**
	 * Checks if the provided itemstack forms a valid recipe.
	 * 
	 * @param itemStackInput The itemstack to check for.
	 * @return
	 */
	public Optional<FusionFurnaceRecipe> getRecipe(ItemStack... inputs) {
		return StaticPowerRecipeRegistry.getRecipe(FusionFurnaceRecipe.RECIPE_TYPE, new RecipeMatchParameters(inputs).setStoredEnergy(energyStorage.getStorage().getEnergyStored()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFusionFurnace(windowId, inventory, this);
	}
}