package theking530.staticpower.tileentities.powered.fluidgenerator;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.fluids.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.fluids.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent.EnergyManipulationAction;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityFluidGenerator extends TileEntityMachine {
	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final InventoryComponent fluidContainerInventory;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final LoopingSoundComponent generatingSoundComponent;

	public TileEntityFluidGenerator() {
		super(ModTileEntityTypes.FLUID_GENERATOR);
		disableFaceInteraction();

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 2, MachineSideMode.Never));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 0, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true).setRedstoneControlComponent(redstoneControlComponent));

		registerComponent(new PowerDistributionComponent("PowerDistributor", energyStorage.getStorage()));
		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluidStack) -> {
			return getRecipe(fluidStack).isPresent();
		}).setCapabilityExposedModes(MachineSideMode.Input));
		registerComponent(new FluidInputServoComponent("InputServo", 20, fluidTankComponent, MachineSideMode.Input));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent, fluidContainerInventory, 0, 1).setMode(FluidContainerInteractionMode.DRAIN));

		// Don't allow this to receive power from external sources.
		this.energyStorage.setCapabiltiyFilter((amount, side, action) -> {
			if (action == EnergyManipulationAction.RECIEVE) {
				return false;
			}
			return true;
		});
	}

	@Override
	public void process() {
		if (!world.isRemote) {
			if (processingComponent.getIsOnBlockState()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.ENTITY_MINECART_RIDING.getRegistryName(), SoundCategory.BLOCKS, 0.1f, 0.75f, getPos(), 64);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}
		}
	}

	public ProcessingCheckState canProcess() {
		// Do nothing if the input tank is empty.
		if (fluidTankComponent.getFluidAmount() == 0) {
			return ProcessingCheckState.skip();
		}

		// If there is no valid recipe, return false.
		if (!hasValidRecipe()) {
			return ProcessingCheckState.ok();
		}

		// Check to make sure we can store power.
		if (energyStorage.canAcceptPower(1)) {
			return ProcessingCheckState.ok();
		} else {
			return ProcessingCheckState.error("Energy Storage Full!");
		}
	}

	protected ProcessingCheckState processingCompleted() {
		// If there is no valid recipe, return true. This covers the edge case where
		// someone quits mid processing and then removes the current recipe.
		if (!hasValidRecipe()) {
			return ProcessingCheckState.ok();
		}

		// Get the recipe.
		FluidGeneratorRecipe recipe = getRecipe(fluidTankComponent.getFluid()).get();
		// Add the power.
		energyStorage.getStorage().receiveEnergy(recipe.getPowerGeneration(), false);
		// Drain the used fluid.
		fluidTankComponent.drain(recipe.getFluid().getAmount(), FluidAction.EXECUTE);
		return ProcessingCheckState.ok();
	}

	// Functionality
	public boolean hasValidRecipe() {
		return getRecipe(fluidTankComponent.getFluid()).isPresent();
	}

	public Optional<FluidGeneratorRecipe> getRecipe(FluidStack inputFluid) {
		return StaticPowerRecipeRegistry.getRecipe(FluidGeneratorRecipe.RECIPE_TYPE, new RecipeMatchParameters(inputFluid));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerFluidGenerator(windowId, inventory, this);
	}
}
