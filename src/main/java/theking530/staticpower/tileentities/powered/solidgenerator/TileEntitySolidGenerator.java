package theking530.staticpower.tileentities.powered.solidgenerator;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent.EnergyManipulationAction;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;

public class TileEntitySolidGenerator extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntitySolidGenerator> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntitySolidGenerator(pos, state), ModBlocks.SolidGenerator);

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final LoopingSoundComponent generatingSoundComponent;

	public final RecipeProcessingComponent<SolidFuelRecipe> processingComponent;

	public long powerGenerationPerTick;

	public TileEntitySolidGenerator(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.IRON);
		disableFaceInteraction();

		// Register the input inventory and only let it receive items if they are
		// burnable.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return ForgeHooks.getBurnTime(stack, null) > 0;
			}
		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1).setShouldDropContentsOnBreak(false));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<SolidFuelRecipe>("ProcessingComponent", SolidFuelRecipe.RECIPE_TYPE, 1, this::getMatchParameters,
				this::moveInputs, this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the power distribution component.
		registerComponent(new PowerDistributionComponent("PowerDistributor", energyStorage.getStorage()));

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Set the default power generation.
		powerGenerationPerTick = StaticPowerConfig.SERVER.solidFuelGenerationPerTick.get();

		// Don't allow this to receive power from external sources.
		energyStorage.setCapabiltiyFilter((amount, side, action) -> {
			if (action == EnergyManipulationAction.RECIEVE) {
				return false;
			}
			return true;
		});

		// Set the max power I/O to infinite.
		energyStorage.setMaxInput(Integer.MAX_VALUE);
		energyStorage.setMaxOutput(Integer.MAX_VALUE);
	}

	protected RecipeMatchParameters getMatchParameters(RecipeProcessingLocation location) {
		if (location == RecipeProcessingLocation.INTERNAL) {
			return new RecipeMatchParameters(internalInventory.getStackInSlot(0));
		} else {
			return new RecipeMatchParameters(inputInventory.getStackInSlot(0));
		}
	}

	protected ProcessingCheckState moveInputs(SolidFuelRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!energyStorage.canAcceptPower(powerGenerationPerTick)) {
			return ProcessingCheckState.powerOutputFull();
		}
		transferItemInternally(inputInventory, 0, internalInventory, 0);
		processingComponent.setMaxProcessingTime(recipe.getFuelAmount());
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(SolidFuelRecipe recipe) {
		if (!energyStorage.canAcceptPower(powerGenerationPerTick)) {
			return ProcessingCheckState.powerOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(SolidFuelRecipe recipe) {
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		return ProcessingCheckState.ok();
	}

	@Override
	public void process() {
		if (!level.isClientSide) {
			if (processingComponent.getIsOnBlockState()) {
				generatingSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE.getRegistryName(), SoundSource.BLOCKS, 1.0f, 1.0f, getBlockPos(), 32);
			} else {
				generatingSoundComponent.stopPlayingSound();
			}
		}

		// Randomly generate smoke and flame particles.
		if (processingComponent.getIsOnBlockState()) {
			if (SDMath.diceRoll(0.25f)) {
				float randomOffset = (2 * getLevel().random.nextFloat()) - 1.0f;
				randomOffset /= 3.5f;
				float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
				Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
				getLevel().addParticle(ParticleTypes.SMOKE, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(), getBlockPos().getZ() + forwardVector.z(), 0.0f,
						0.01f, 0.0f);
				getLevel().addParticle(ParticleTypes.FLAME, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(), getBlockPos().getZ() + forwardVector.z(), 0.0f,
						0.01f, 0.0f);
			}
		}

		// If we're processing, generate power. Otherwise, pause.
		if (!getLevel().isClientSide && processingComponent.isPerformingWork()) {
			energyStorage.addPower((int) (powerGenerationPerTick));
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerSolidGenerator(windowId, inventory, this);
	}
}
