package theking530.staticpower.tileentities.powered.poweredfurnace;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.TileEntityBatteryComponent;
import theking530.staticpower.tileentities.components.TileEntityInputServoComponent;
import theking530.staticpower.tileentities.components.TileEntityInventoryComponent;
import theking530.staticpower.tileentities.components.TileEntityOutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityPoweredFurnace extends TileEntityMachine {
	public final TileEntityInventoryComponent inputInventory;
	public final TileEntityInventoryComponent outputInventory;
	public final TileEntityInventoryComponent internalInventory;
	public final TileEntityInventoryComponent batterySlot;

	public TileEntityPoweredFurnace() {
		super(ModTileEntityTypes.POWERED_FURNACE, 3);
		initializeBasicMachine(2, 1000, 100000, 80, 180);
		
		registerComponent(inputInventory = new TileEntityInventoryComponent("inputInventory", 1, MachineSideMode.Input));
		registerComponent(internalInventory = new TileEntityInventoryComponent("internalInventory", 1, MachineSideMode.Never));
		registerComponent(outputInventory = new TileEntityInventoryComponent("outputInventory", 1, MachineSideMode.Output));
		registerComponent(batterySlot = new TileEntityInventoryComponent("batterySlot", 1, MachineSideMode.Never));

		registerComponent(new TileEntityInputServoComponent(this, 2, inputInventory, 0));
		registerComponent(new TileEntityOutputServoComponent(this, 1, outputInventory, 0));
		registerComponent(new TileEntityBatteryComponent("BatteryComponent", batterySlot, 0, energyStorage));
	}

	// Functionality
	@Override
	public boolean hasValidRecipe() {
		return getRecipe(inputInventory.getStackInSlot(0)).isPresent();
	}

	public Optional<FurnaceRecipe> getRecipe(ItemStack itemStackInput) {
		return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(itemStackInput), world);
	}

	@Override
	public boolean canProcess() {
		return hasValidRecipe() && getEnergyStorage().getEnergyStored() > getProcessingEnergy();
	}

	@Override
	public void process() {
		if (!getWorld().isRemote) {
			if (!isProcessing() && !isMoving() && canProcess()) {
				moveTimer = 1;
			}
			if (!isProcessing() && isMoving() && canProcess()) {
				if (moveTimer < moveSpeed) {
					moveTimer++;
				} else {
					transferItemInternally(inputInventory, 0, internalInventory, 0);
					processingTimer = 1;
					moveTimer = 0;
					markTileEntityForSynchronization();
				}
			} else {
				moveTimer = 0;
			}
			if (isProcessing() && !isMoving()) {
				if (processingTimer < processingTime) {
					useEnergy(getProcessingEnergy() / processingTime);
					processingTimer++;
					markTileEntityForSynchronization();
				} else {
					ItemStack output = getRecipe(internalInventory.getStackInSlot(0)).get().getRecipeOutput();
					if (!output.isEmpty() && InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, output)) {
						outputInventory.insertItem(0, output.copy(), false);
						internalInventory.setStackInSlot(0, ItemStack.EMPTY);
						processingTimer = 0;
						markTileEntityForSynchronization();
					}
				}
			}
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPoweredFurnace(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModBlocks.PoweredFurnace.getTranslationKey());
	}
}
