package theking530.staticpower.tileentities.powered.autocrafter;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderAutoCraftingTable;
import theking530.staticpower.container.FakeCraftingInventory;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.researchwrappers.CraftingResearchLockingUtilities;
import theking530.staticpower.data.crafting.researchwrappers.IResearchBlockedRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityAutoCraftingTable extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityAutoCraftingTable> TYPE = new BlockEntityTypeAllocator<TileEntityAutoCraftingTable>(
			(type, pos, state) -> new TileEntityAutoCraftingTable(pos, state), ModBlocks.AutoCraftingTable);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderAutoCraftingTable::new);
		}
	}

	public final MachineProcessingComponent moveComponent;
	public final MachineProcessingComponent processingComponent;
	public final InventoryComponent internalInventory;
	public final InventoryComponent patternInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final InventoryComponent inputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent outputInventory;
	protected final ItemStack[] filterInventory;

	public TileEntityAutoCraftingTable(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.BASIC);
		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 9, MachineSideMode.Never));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 9, MachineSideMode.Never));
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 9, MachineSideMode.Input).setSlotsLockable(true).setShiftClickEnabled(true));

		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryInventory", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(moveComponent = MachineProcessingComponent
				.createMovingProcessingComponent("MoveComponent", this::canMoveFromInputToProcessing, () -> ProcessingCheckState.ok(), this::movingCompleted, true)
				.setRedstoneControlComponent(redstoneControlComponent));
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", StaticPowerConfig.SERVER.autoCrafterProcessingTime.get(), this::canProcess, this::canProcess,
				this::processingCompleted, true).setShouldControlBlockState(true).setProcessingPowerUsage(StaticPowerConfig.SERVER.autoCrafterPowerUsage.get()));

		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.autoCrafterPowerUsage.get());

		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);

		patternInventory.setShouldDropContentsOnBreak(false);
		filterInventory = new ItemStack[9];
	}

	public ProcessingCheckState canMoveFromInputToProcessing() {
		if (!getCurrentRecipe().isPresent() || !hasRequiredItems()) {
			return ProcessingCheckState.skip();
		}
		// Check if there is a valid recipe.
		if (!processingComponent.isProcessing() && InventoryUtilities.isInventoryEmpty(internalInventory)
				&& InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, getCurrentRecipe().get().getResultItem())) {
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.skip();
	}

	protected ProcessingCheckState movingCompleted() {
		// If we still have the recipe, and the required items, move the input items
		// into the internal inventory.
		if (getCurrentRecipe().isPresent() && hasRequiredItems()) {
			// Get the recipe.
			CraftingRecipe recipe = getCurrentRecipe().orElse(null);

			// If this recipe is shaped, make sure we place the same shaped recipe's items
			// into the internal inventory. If shapeless, just put the items into the
			// internal inv.
			if (recipe instanceof ShapedRecipe) {
				ShapedRecipe sRecipe = (ShapedRecipe) recipe;
				for (int x = 0; x < 3; ++x) {
					for (int y = 0; y < 3; ++y) {
						// Get the recipe index.
						Ingredient ingredient = Ingredient.EMPTY;
						if (x >= 0 && y >= 0 && x < sRecipe.getRecipeWidth() && y < sRecipe.getRecipeHeight()) {
							ingredient = sRecipe.getIngredients().get(sRecipe.getRecipeWidth() - x - 1 + y * sRecipe.getRecipeWidth());
						}

						// Skip empty ingredients.
						if (ingredient.equals(Ingredient.EMPTY)) {
							continue;
						}

						// Capture the item.
						for (int j = 0; j < inputInventory.getSlots(); j++) {
							if (ingredient.test(inputInventory.getStackInSlot(j))) {
								ItemStack extracted = inputInventory.extractItem(j, 1, false);
								internalInventory.setStackInSlot(x + (y * 3), extracted.copy());
								break;
							}
						}
					}
				}
			} else {
				// Transfer the materials into the internal inventory.
				for (int i = 0; i < recipe.getIngredients().size(); i++) {
					// Get the used ingredient.
					Ingredient ing = recipe.getIngredients().get(i);

					// Skip holes in the recipe.
					if (ing.equals(Ingredient.EMPTY)) {
						continue;
					}

					// Remove the item.
					for (int j = 0; j < inputInventory.getSlots(); j++) {
						if (ing.test(inputInventory.getStackInSlot(j))) {
							ItemStack extracted = inputInventory.extractItem(j, 1, false);
							internalInventory.setStackInSlot(i, extracted.copy());
							break;
						}
					}
				}
			}

			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.skip();

	}

	public ProcessingCheckState canProcess() {
		// Get the current recipe.
		CraftingRecipe recipe = getCurrentProcessingRecipe().orElse(null);
		if (recipe == null) {
			InventoryUtilities.clearInventory(internalInventory);
			processingComponent.cancelProcessing();
			return ProcessingCheckState.cancel();
		}
		if (InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.outputsCannotTakeRecipe();
	}

	protected ProcessingCheckState processingCompleted() {
		// Get the recipe from the internal inventory. If this is null, then we reloaded
		// in the middle of processing and removed the recipe. Return true and do
		// nothing.
		CraftingRecipe recipe = getCurrentProcessingRecipe().orElse(null);
		if (recipe == null) {
			return ProcessingCheckState.ok();
		}

		// If we can insert the soldered item into the output slot, do it. Otherwise,
		// return false and keep spinning.
		if (InventoryUtilities.canFullyInsertStackIntoSlot(outputInventory, 0, recipe.getResultItem())) {
			// Insert the soldered item into the output.
			outputInventory.insertItem(0, recipe.getResultItem().copy(), false);
			// Clear the internal inventory.
			for (int i = 0; i < internalInventory.getSlots(); i++) {
				internalInventory.setStackInSlot(i, ItemStack.EMPTY);
			}
			return ProcessingCheckState.ok();
		}
		return ProcessingCheckState.outputsCannotTakeRecipe();
	}

	public Optional<CraftingRecipe> getCurrentRecipe() {
		// Get the inventory in the form of an itemstack array.
		ItemStack[] pattern = new ItemStack[9];
		for (int i = 0; i < patternInventory.getSlots(); i++) {
			pattern[i] = patternInventory.getStackInSlot(i);
		}
		return getRecipeForItems(pattern);
	}

	public Optional<CraftingRecipe> getCurrentProcessingRecipe() {
		// Get the inventory in the form of an itemstack array.
		ItemStack[] pattern = new ItemStack[9];
		for (int i = 0; i < internalInventory.getSlots(); i++) {
			pattern[i] = internalInventory.getStackInSlot(i);
		}
		return getRecipeForItems(pattern);
	}

	public Optional<CraftingRecipe> getRecipeForItems(ItemStack... inputs) {
		// Created a simulated crafting inventory.
		FakeCraftingInventory craftingInv = new FakeCraftingInventory(3, 3);

		for (int i = 0; i < inputs.length; i++) {
			craftingInv.setItem(i, inputs[i]);
		}
		Optional<CraftingRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInv, level);

		// Check if this is a blocked recipe that the requirements have been met.
		if (recipe.isPresent()) {
			if (recipe.get() instanceof IResearchBlockedRecipe) {
				if (!CraftingResearchLockingUtilities.canTeamCraftRecipe(getTeamComponent().getOwningTeam(), recipe.get())) {
					return Optional.empty();
				}
			}
		}
		return recipe;
	}

	public boolean hasRequiredItems() {
		// Check if we have a recipe.
		Optional<CraftingRecipe> recipe = getCurrentRecipe();

		// If there is no recipe, return false.
		if (!recipe.isPresent()) {
			return false;
		}

		// Create a duplicate inventory.
		ItemStackHandler duplicateInventory = new ItemStackHandler(inputInventory.getSlots());
		for (int i = 0; i < inputInventory.getSlots(); i++) {
			duplicateInventory.setStackInSlot(i, inputInventory.getStackInSlot(i).copy());
		}

		// Allocate a flag to indicate if we found an ingredient match.
		boolean flag = false;

		// Iterate through all the ingredients in the recipe.
		for (int i = 0; i < recipe.get().getIngredients().size(); i++) {
			// Set the flag to false.
			flag = false;

			// Get the ingredient.
			Ingredient ing = recipe.get().getIngredients().get(i);

			// Skip empty ingredients.
			if (ing.equals(Ingredient.EMPTY)) {
				continue;
			}

			// Look for an item that matches the ingredient inside the inventory. If we find
			// one, extract the item from the duplicate and continue. If one is not found,
			// we cannot craft this item, return false.
			for (int j = 0; j < duplicateInventory.getSlots(); j++) {
				if (ing.test(duplicateInventory.getStackInSlot(j))) {
					duplicateInventory.extractItem(j, 1, false);
					flag = true;
					break;
				}
			}

			if (!flag) {
				return false;
			}
		}

		// If all the above are met, return true.
		return true;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerAutoCraftingTable(windowId, inventory, this);
	}

	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		// Serialize the filter inventory.
		for (int i = 0; i < filterInventory.length; i++) {
			CompoundTag filterItemTag = new CompoundTag();

			// If the filter slot is not null, write the filter item to the nbt. Otherwise,
			// add nothing.
			if (filterInventory[i] != null) {
				filterInventory[i].save(filterItemTag);
				nbt.put("filter#" + i, filterItemTag);
			}
		}
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		for (int i = 0; i < filterInventory.length; i++) {
			if (nbt.contains("filter#" + i)) {
				filterInventory[i] = ItemStack.of(nbt.getCompound("filter#" + i));
			} else {
				filterInventory[i] = null;
			}
		}
	}
}
