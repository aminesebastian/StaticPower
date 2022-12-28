package theking530.staticpower.blockentities.power.solidgenerator;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import theking530.api.energy.PowerStack;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer;
import theking530.staticpower.blockentities.components.control.processing.ProcessingOutputContainer.CaptureType;
import theking530.staticpower.blockentities.components.control.processing.RecipeProcessingComponent;
import theking530.staticpower.blockentities.components.control.processing.interfaces.IRecipeProcessor;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.init.ModBlocks;

public class BlockEntitySolidGenerator extends BlockEntityMachine implements IRecipeProcessor<SolidFuelRecipe> {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntitySolidGenerator> TYPE = new BlockEntityTypeAllocator<>("solid_generator", (type, pos, state) -> new BlockEntitySolidGenerator(pos, state),
			ModBlocks.SolidGenerator);

	public final InventoryComponent inputInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final LoopingSoundComponent generatingSoundComponent;

	public final RecipeProcessingComponent<SolidFuelRecipe> processingComponent;

	public double powerGenerationPerTick;

	public BlockEntitySolidGenerator(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		disableFaceInteraction();

		// Register the input inventory and only let it receive items if they are
		// burnable.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return ForgeHooks.getBurnTime(stack, null) > 0;
			}
		}));

		// Setup all the other inventories.
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<SolidFuelRecipe>("ProcessingComponent", SolidFuelRecipe.RECIPE_TYPE, this));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the power distribution component.
		powerStorage.setSideConfiguration(ioSideConfiguration);
		powerStorage.setCanAcceptExternalPower(false);
		registerComponent(new PowerDistributionComponent("PowerDistributor", powerStorage));

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Set the default power generation.
		powerGenerationPerTick = StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get();
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			if (processingComponent.getIsOnBlockState()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, getBlockPos(), 32);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}
		}

		// Randomly generate smoke and flame particles.
		if (processingComponent.getIsOnBlockState()) {
			if (SDMath.diceRoll(0.25f)) {
				float randomOffset = ((2 * getLevel().getRandom().nextFloat()) - 1.0f) / 3.5f;
				Vector3f forwardVector = SDMath.translateRelativeOffset(getFacingDirection(), new Vector3f(1.0f, 0.32f, -0.5f + randomOffset));
				getLevel().addParticle(ParticleTypes.SMOKE, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(),
						getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f, 0.0f);
				getLevel().addParticle(ParticleTypes.FLAME, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(),
						getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f, 0.0f);
			}
		}

		// If we're processing, generate power. Otherwise, pause.
		if (!getLevel().isClientSide() && processingComponent.performedWorkLastTick()) {
			powerStorage.addPower(new PowerStack(powerGenerationPerTick, powerStorage.getOutputVoltage()), false);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSolidGenerator(windowId, inventory, this);
	}

	@Override
	public RecipeMatchParameters getRecipeMatchParameters(RecipeProcessingComponent<SolidFuelRecipe> component) {
		return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
	}

	@Override
	public ProcessingCheckState canStartProcessing(RecipeProcessingComponent<SolidFuelRecipe> component, SolidFuelRecipe recipe, ProcessingOutputContainer outputContainer) {
		if (!powerStorage.canFullyAcceptPower(powerGenerationPerTick)) {
			return ProcessingCheckState.powerOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	@Override
	public void processingStarted(RecipeProcessingComponent<SolidFuelRecipe> component, SolidFuelRecipe recipe, ProcessingOutputContainer outputContainer) {
		inputInventory.extractItem(0, recipe.getInput().getCount(), false);
	}

	@Override
	public void captureInputsAndProducts(RecipeProcessingComponent<SolidFuelRecipe> component, SolidFuelRecipe recipe, ProcessingOutputContainer outputContainer) {
		outputContainer.addInputItem(inputInventory.extractItem(0, recipe.getInput().getCount(), true), CaptureType.BOTH);
		outputContainer.setOutputPower(powerGenerationPerTick);
		component.setMaxProcessingTime(recipe.getFuelAmount());
	}
}
