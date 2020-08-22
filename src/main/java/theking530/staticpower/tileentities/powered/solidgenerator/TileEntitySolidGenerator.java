package theking530.staticpower.tileentities.powered.solidgenerator;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraftforge.common.ForgeHooks;
import theking530.common.utilities.SDMath;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent;
import theking530.staticpower.tileentities.components.control.RecipeProcessingComponent.RecipeProcessingLocation;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent.EnergyManipulationAction;
import theking530.staticpower.tileentities.components.power.PowerDistributionComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.interfaces.ItemStackHandlerFilter;

public class TileEntitySolidGenerator extends TileEntityMachine {
	public static final int DEFAULT_POWER_GENERATION = 20;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final UpgradeInventoryComponent upgradesInventory;

	public final RecipeProcessingComponent<SolidFuelRecipe> processingComponent;

	public int powerGenerationPerTick;

	public TileEntitySolidGenerator() {
		super(ModTileEntityTypes.SOLID_GENERATOR);
		disableFaceInteraction();

		// Register the input inventory and only let it receive items if they are
		// burnable.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return ForgeHooks.getBurnTime(stack) > 0;
			}
		}));

		// Setup all the other inventories.
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1).setShouldDropContentsOnBreak(false));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component to work with the redstone control component,
		// upgrade component and energy component.
		registerComponent(processingComponent = new RecipeProcessingComponent<SolidFuelRecipe>("ProcessingComponent", SolidFuelRecipe.RECIPE_TYPE, 1, this::getMatchParameters, this::moveInputs,
				this::canProcessRecipe, this::processingCompleted));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);

		// Setup the power distribution component.
		registerComponent(new PowerDistributionComponent("PowerDistributor", energyStorage.getStorage()));

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));

		// Set the default power generation.
		powerGenerationPerTick = DEFAULT_POWER_GENERATION;

		// Don't allow this to receive power from external sources.
		energyStorage.setCapabiltiyFilter((amount, side, action) -> {
			if (action == EnergyManipulationAction.RECIEVE) {
				return false;
			}
			return true;
		});
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
		if (!internalInventory.getStackInSlot(0).isEmpty()) {
			return ProcessingCheckState.internalInventoryNotEmpty();
		}
		if (!energyStorage.canAcceptPower(powerGenerationPerTick)) {
			return ProcessingCheckState.powerOutputFull();
		}
		transferItemInternally(inputInventory, 0, internalInventory, 0);
		processingComponent.setMaxProcessingTime(recipe.getFuelAmount());
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canProcessRecipe(SolidFuelRecipe recipe) {
		if (energyStorage.canAcceptPower(powerGenerationPerTick)) {
			return ProcessingCheckState.powerOutputFull();
		}
		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted(SolidFuelRecipe recipe) {
		internalInventory.setStackInSlot(0, ItemStack.EMPTY);
		markTileEntityForSynchronization();
		return ProcessingCheckState.ok();
	}

	@Override
	public void process() {
		// Randomly generate smoke and flame particles.
		if (processingComponent.getIsOnBlockState()) {
			float randomOffset = (2 * RANDOM.nextFloat()) - 1.0f;
			if (SDMath.diceRoll(0.25f)) {

				randomOffset /= 3.5f;
				float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
				Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
				getWorld().addParticle(ParticleTypes.SMOKE, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
				getWorld().addParticle(ParticleTypes.FLAME, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
			}
		}

		// If we're processing, generate power. Otherwise, pause.
		if (!getWorld().isRemote && processingComponent.isPerformingWork()) {
			energyStorage.addPower(powerGenerationPerTick);
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerSolidGenerator(windowId, inventory, this);
	}
}
