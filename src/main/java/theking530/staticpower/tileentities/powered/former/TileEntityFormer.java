package theking530.staticpower.tileentities.powered.former;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityFormer extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final RecipeProcessingComponent<FormerRecipe> processingComponent;

	public TileEntityFormer() {
		super(ModTileEntityTypes.FORMER);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 2, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				if (slot == 1 && StaticPowerRecipeRegistry.isValidFormerMold(stack)) {
					return true;
				}
				return processingComponent.getRecipe(new RecipeMatchParameters(stack, inputInventory.getStackInSlot(1))).isPresent();
			}
		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 2));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<FormerRecipe>("ProcessingComponent", FormerRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs,
				this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(DEFAULT_PROCESSING_COST);

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0), internalInventory.getStackInSlot(1));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0), inputInventory.getStackInSlot(1));
		}
	}

	protected boolean moveInputs(FormerRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (internalInventory.getStackInSlot(0).isEmpty() && InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRecipeOutput())) {
			transferItemInternally(inputInventory, 0, internalInventory, 0);
			markTileEntityForSynchronization();
			return true;
		}
		return false;
	}

	protected boolean canProcessRecipe(FormerRecipe recipe) {
		return InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRecipeOutput());
	}

	protected boolean processingCompleted(FormerRecipe recipe) {
		// Ensure the output slots can take the recipe.
		if (InventoryUtilities.canFullyInsertItemIntoInventory(outputInventory, recipe.getRecipeOutput())) {
			ItemStack output = recipe.getRecipeOutput();
			if (!output.isEmpty() && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output)) {
				outputInventory.insertItem(0, output.copy(), false);
				internalInventory.setStackInSlot(0, ItemStack.EMPTY);
				internalInventory.setStackInSlot(1, ItemStack.EMPTY);
				markTileEntityForSynchronization();
				return true;
			}

			internalInventory.setStackInSlot(0, ItemStack.EMPTY);
			markTileEntityForSynchronization();
			return true;
		}

		// If something failed, return false and try again.
		return false;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFormer(windowId, inventory, this);
	}
}