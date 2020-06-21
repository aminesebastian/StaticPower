package theking530.staticpower.tileentities.powered.poweredgrinder;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.api.utilities.SDMath;
import theking530.staticpower.crafting.wrappers.ProbabilityItemStackOutput;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityPoweredGrinder extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 500;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	private float bonusOutputChance;

	public TileEntityPoweredGrinder() {
		super(ModTileEntityTypes.POWERED_GRINDER);
		this.disableFaceInteraction();
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 3, MachineSideMode.Output));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", DEFAULT_MOVING_TIME, this::movingCompleted));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", DEFAULT_PROCESSING_TIME, this::processingCompleted));

		registerComponent(new InputServoComponent("InputServo", 2, inputInventory, this::inputServoFilter, 0));
		registerComponent(new OutputServoComponent("OutputServo", 1, outputInventory, 0, 1, 2));
		registerComponent(new BatteryComponent("BatteryComponent", internalInventory, 0, energyStorage.getStorage()));
	}

	/**
	 * Checks to see if the input item forms a valid recipe.
	 * 
	 * @return
	 */
	public boolean hasValidRecipe() {
		return getRecipe(inputInventory.getStackInSlot(0)).isPresent();
	}

	/**
	 * Checks if the provided itemstack forms a valid recipe.
	 * 
	 * @param itemStackInput The itemstack to check for.
	 * @return
	 */
	public Optional<GrinderRecipe> getRecipe(ItemStack itemStackInput) {
		return StaticPowerRecipeRegistry.getRecipe(GrinderRecipe.RECIPE_TYPE, new RecipeMatchParameters(itemStackInput).setStoredEnergy(energyStorage.getStorage().getEnergyStored()));
	}

	@Override
	public void process() {
		if (canStartProcess() && redstoneControlComponent.passesRedstoneCheck()) {
			moveComponent.startProcessing();
		} else if (processingComponent.isProcessing()) {
			if (!getRecipe(internalInventory.getStackInSlot(0)).isPresent()) {
				processingComponent.pauseProcessing();
			} else {
				processingComponent.continueProcessing();
				energyStorage.getStorage().extractEnergy(DEFAULT_PROCESSING_COST / DEFAULT_PROCESSING_TIME, false);
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
			if (!getWorld().isRemote) {
				transferItemInternally(inputInventory, 0, internalInventory, 0);
			}
			processingComponent.startProcessing();
		}
		return true;
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
			GrinderRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).get();
			// Ensure the output slots can take the recipe.
			if (canOutputsTakeRecipeResult(recipe)) {
				// For each output, insert the contents into the output based on the percentage
				// chance. The clear the internal inventory, mark for synchronozation, and
				// return true.
				for (ProbabilityItemStackOutput output : recipe.getOutputItems()) {
					if (SDMath.diceRoll(output.getPercentage() + bonusOutputChance)) {
						InventoryUtilities.insertItemIntoInventory(outputInventory, output.getItem().copy(), false);
					}
				}
				internalInventory.setStackInSlot(0, ItemStack.EMPTY);
				markTileEntityForSynchronization();
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to make sure we can start the processing process.
	 * 
	 * @return
	 */
	public boolean canStartProcess() {
		// Check if there is a valid recipe.
		if (hasValidRecipe() && !moveComponent.isProcessing() && !processingComponent.isProcessing() && internalInventory.getStackInSlot(0).isEmpty()) {
			// Gets the recipe and its outputs.
			Optional<GrinderRecipe> recipe = getRecipe(inputInventory.getStackInSlot(0));

			// If the items cannot be insert into the output, return false.
			if (!canOutputsTakeRecipeResult(recipe.get())) {
				return false;
			}

			// If we passed all the previous checks, return true.
			return true;
		}
		return false;
	}

	/**
	 * Simple filter to check if the itemstack should be pulled in by the servo (if
	 * enabled).
	 * 
	 * @param stack
	 * @return
	 */
	private boolean inputServoFilter(ItemStack stack) {
		return getRecipe(stack).isPresent();
	}

	/**
	 * Ensure the output slots can take the results of the crafting.
	 * 
	 * @param recipe
	 * @return
	 */
	public boolean canOutputsTakeRecipeResult(GrinderRecipe recipe) {
		return InventoryUtilities.canFullyInsertAllItemsIntoInventory(outputInventory, recipe.getRawOutputItems());
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPoweredGrinder(windowId, inventory, this);
	}
}