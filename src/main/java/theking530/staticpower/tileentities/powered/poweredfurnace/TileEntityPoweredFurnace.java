package theking530.staticpower.tileentities.powered.poweredfurnace;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.BatteryComponent;
import theking530.staticpower.tileentities.components.InputServoComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;
import theking530.staticpower.utilities.InventoryUtilities;

/**
 * Baseic furnace machine. Same as a Vanila furnace except powered.
 * 
 * @author Amine Sebastian
 *
 */
public class TileEntityPoweredFurnace extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent outputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;

	private int processingPowerCost;

	public TileEntityPoweredFurnace() {
		super(ModTileEntityTypes.POWERED_FURNACE);

		processingPowerCost = DEFAULT_PROCESSING_COST;

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return getRecipe(stack).isPresent();

			}
		}));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));

		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1, MachineSideMode.Never));
		registerComponent(batteryInventory = new InventoryComponent("BatteryInventory", 1, MachineSideMode.Never));

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(moveComponent = new MachineProcessingComponent("MoveComponent", 2, this::canMoveFromInputToProcessing, () -> true, this::movingCompleted, true));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 10, this::canProcess, this::canProcess, this::processingCompleted, true));
		processingComponent.setProcessingTime(DEFAULT_PROCESSING_TIME);

		registerComponent(new InputServoComponent("InputServo", 4, inputInventory, 0));
		registerComponent(new OutputServoComponent("OutputServo", 4, outputInventory, 0));
		registerComponent(new BatteryComponent("BatteryComponent", batteryInventory, 0, energyStorage.getStorage()));
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
			ItemStack output = getRecipe(inputInventory.getStackInSlot(0)).get().getRecipeOutput();
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
			transferItemInternally(inputInventory, 0, internalInventory, 0);
			markTileEntityForSynchronization();
		}
		return true;
	}

	protected boolean canProcess() {
		FurnaceRecipe recipe = getRecipe(internalInventory.getStackInSlot(0)).orElse(null);
		return recipe != null && redstoneControlComponent.passesRedstoneCheck() && energyStorage.hasEnoughPower(processingPowerCost) && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getRecipeOutput());
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
			ItemStack output = getRecipe(internalInventory.getStackInSlot(0)).get().getRecipeOutput();
			if (!output.isEmpty() && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output)) {
				outputInventory.insertItem(0, output.copy(), false);
				internalInventory.setStackInSlot(0, ItemStack.EMPTY);
				markTileEntityForSynchronization();
				return true;
			}
		}
		return false;
	}

	@Override
	public void process() {
		if (processingComponent.isProcessing() && !processingComponent.isDone()) {
			if (!getWorld().isRemote) {
				energyStorage.usePower(processingPowerCost);
			}
		}
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
	public Optional<FurnaceRecipe> getRecipe(ItemStack itemStackInput) {
		return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(itemStackInput), world);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPoweredFurnace(windowId, inventory, this);
	}
}
