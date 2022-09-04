package theking530.staticpower.tileentities.powered.fluidgenerator;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.client.rendering.tileentity.TileEntityRenderFluidGenerator;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent.EnergyManipulationAction;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;

public class TileEntityFluidGenerator extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityFluidGenerator> TYPE = new BlockEntityTypeAllocator<TileEntityFluidGenerator>(
			(type, pos, state) -> new TileEntityFluidGenerator(pos, state), ModBlocks.FluidGenerator);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(TileEntityRenderFluidGenerator::new);
		}
	}

	public final InventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;
	public final FluidContainerInventoryComponent fluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;
	public final LoopingSoundComponent generatingSoundComponent;

	public TileEntityFluidGenerator(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.BASIC);
		disableFaceInteraction();

		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3, MachineSideMode.Never));

		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 0, this::canProcess, this::canProcess, this::processingCompleted, true)
				.setShouldControlBlockState(true).setRedstoneControlComponent(redstoneControlComponent));

		registerComponent(new PowerDistributionComponent("PowerDistributor", energyStorage));
		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, (fluidStack) -> {
			return getRecipe(fluidStack).isPresent();
		}));
		fluidTankComponent.setCapabilityExposedModes(MachineSideMode.Input);
		fluidTankComponent.setAutoSyncPacketsEnabled(true);

		registerComponent(new FluidInputServoComponent("InputServo", 20, fluidTankComponent, MachineSideMode.Input));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.DRAIN));

		// Don't allow this to receive power from external sources.
		this.energyStorage.setCapabiltiyFilter((amount, side, action) -> {
			if (action == EnergyManipulationAction.RECIEVE) {
				return false;
			}
			return true;
		});
		energyStorage.setAutoSyncPacketsEnabled(true);
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			if (processingComponent.getIsOnBlockState()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.MINECART_RIDING.getRegistryName(), SoundSource.BLOCKS, 0.1f, 0.75f, getBlockPos(), 64);
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

		energyStorage.setMaxInput(recipe.getPowerGeneration());
		energyStorage.setMaxOutput(recipe.getPowerGeneration());
		// Add the power.
		energyStorage.receivePower(recipe.getPowerGeneration(), false);
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
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerFluidGenerator(windowId, inventory, this);
	}
}
