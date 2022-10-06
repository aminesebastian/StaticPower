package theking530.staticpower.blockentities.power.turbine;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.PowerStack;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.blockentities.components.control.processing.ProcessingCheckState;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidTankComponent;
import theking530.staticpower.blockentities.components.items.InputServoComponent;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.components.loopingsound.LoopingSoundComponent;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.client.rendering.blockentity.BlockEntityRenderTurbine;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.tools.TurbineBlades;

public class BlockEntityTurbine extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTurbine> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new BlockEntityTurbine(pos, state),
			ModBlocks.Turbine);

	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setTileEntitySpecialRenderer(BlockEntityRenderTurbine::new);
		}
	}

	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<TurbineRenderingState> TURBINE_RENDERING_STATE = new ModelProperty<>();

	public final InventoryComponent turbineBladeInventory;
	public final FluidTankComponent inputFluidTankComponent;
	public final FluidTankComponent outputFluidTankComponent;
	public final UpgradeInventoryComponent upgradesInventory;
	public final LoopingSoundComponent generatingSoundComponent;
	private final TurbineRenderingState renderingState;

	@UpdateSerialize
	private boolean isGenerating;

	public BlockEntityTurbine(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		enableFaceInteraction();
		this.isGenerating = false;
		this.renderingState = new TurbineRenderingState();

		// Get the tier.
		StaticPowerTier tier = getTierObject();

		// Register the input inventory and only let it receive items if they are
		// burnable.
		registerComponent(turbineBladeInventory = new InventoryComponent("TurbineInventory", 1, MachineSideMode.Input).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getItem() instanceof TurbineBlades;
			}
		}));

		// Setup all the other inventories.
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(generatingSoundComponent = new LoopingSoundComponent("GeneratingSoundComponent", 20));

		// Setup the power distribution component.
		powerStorage.setSideConfiguration(ioSideConfiguration);
		powerStorage.setCanAcceptExternalPower(false);
		registerComponent(new PowerDistributionComponent("PowerDistributor", powerStorage));

		// Setup the fluid tanks
		registerComponent(inputFluidTankComponent = new FluidTankComponent("InputFluid", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Input)
				.setUpgradeInventory(upgradesInventory).setAutoSyncPacketsEnabled(true).setExposeAsCapability(false));
		registerComponent(outputFluidTankComponent = new FluidTankComponent("OutputFluid", tier.defaultTankCapacity.get()).setCapabilityExposedModes(MachineSideMode.Output)
				.setUpgradeInventory(upgradesInventory));

		// Setup the I/O servos.
		registerComponent(new InputServoComponent("InputServo", 2, turbineBladeInventory));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, outputFluidTankComponent, MachineSideMode.Output));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide) {
			boolean generated = false;

			// Check redstone control.
			if (this.redstoneControlComponent.passesRedstoneCheck()) {
				// Get the recipe if one exists.
				TurbineRecipe recipe = getRecipe().orElse(null);

				if (recipe != null) {
					if (getProcessingState(recipe).isOk()) {
						// Get the recieve amount.
						int recieveAmount = getGenerationPerTick();

						// Generate the power.
						powerStorage.addPower(new PowerStack(recieveAmount, powerStorage.getOutputVoltage()), false);

						// Start the sound.
						if (!isGenerating) {
							isGenerating = true;
							generatingSoundComponent.startPlayingSound(SoundEvents.BLASTFURNACE_FIRE_CRACKLE.getRegistryName(), SoundSource.BLOCKS, 2.0f, 1.5f, getBlockPos(), 32);
						}

						// Draw the input and fill the output.
						inputFluidTankComponent.drain(recipe.getInput(), FluidAction.EXECUTE);
						if (recipe.hasOutput()) {
							outputFluidTankComponent.fill(recipe.getOutput(), FluidAction.EXECUTE);
						}

						// Damage the turbine blades.
						if (turbineBladeInventory.getStackInSlot(0).hurt(1, level.random, null)) {
							level.playSound(null, getBlockPos(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
						}

						// Mark as having generated.
						generated = true;
					}
				}
			}

			if (!generated && isGenerating) {
				isGenerating = false;
				generatingSoundComponent.stopPlayingSound();
			}

			// Perform the fluid suck from below.
			suckFluidFromBelow();
		} else {
			if (isGenerating) {
				// Render water particles.
				if (SDMath.diceRoll(0.4f)) {
					float randomOffset = (2 * getLevel().random.nextFloat()) - 1.0f;
					randomOffset /= 2f;
					getLevel().addParticle(ParticleTypes.FALLING_WATER, getBlockPos().getX() + 0.5 + randomOffset, getBlockPos().getY() - 0.5,
							getBlockPos().getZ() + 0.5 + randomOffset, 0.0f, 0.01f, 0.0f);
				}
			}
		}
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side == BlockSide.TOP) {
			return mode == MachineSideMode.Input;
		}
		if (side == BlockSide.BOTTOM) {
			return mode == MachineSideMode.Never;
		}
		return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output;
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return new DefaultSideConfiguration().setSide(BlockSide.LEFT, true, MachineSideMode.Output).setSide(BlockSide.RIGHT, true, MachineSideMode.Output)
				.setSide(BlockSide.FRONT, true, MachineSideMode.Output).setSide(BlockSide.BACK, true, MachineSideMode.Output);
	}

	@Override
	protected void getAdditionalModelData(ModelDataMap.Builder builder) {
		builder.withInitial(TURBINE_RENDERING_STATE, renderingState);
	}

	public boolean isGenerating() {
		return isGenerating;
	}

	public int getGenerationPerTick() {
		// Get the recipe.
		TurbineRecipe recipe = getRecipe().orElse(null);

		// If it exists.
		if (recipe != null) {
			// If the blades are installed, use the blade power multiplier, otherwise
			// return the default of the recipe.
			if (hasTurbineBlades()) {
				TurbineBlades bladeItem = getTurbileBladesItem();
				return (int) (recipe.getGenerationAmount() * StaticPowerConfig.getTier(bladeItem.getTier()).turbineBladeGenerationBoost.get());
			} else {
				return recipe.getGenerationAmount();
			}
		}
		return 0;
	}

	public ProcessingCheckState getProcessingState(TurbineRecipe recipe) {
		// If the items can be insert into the output, transfer the items and return
		// true.
		if (!powerStorage.canFullyAcceptPower(getGenerationPerTick())) {
			return ProcessingCheckState.powerOutputFull();
		}

		// If the fluid output is full, return error.
		if (recipe.hasOutput() && outputFluidTankComponent.getFluid().getAmount() + recipe.getOutput().getAmount() > outputFluidTankComponent.getCapacity()) {
			return ProcessingCheckState.fluidOutputFull();
		}

		// Check to make sure we have turbine blades.
		if (!hasTurbineBlades()) {
			return ProcessingCheckState.error("Missing Turbine Blades!");
		}

		return ProcessingCheckState.ok();
	}

	public Optional<TurbineRecipe> getRecipe() {
		RecipeMatchParameters matchParams = new RecipeMatchParameters(inputFluidTankComponent.getFluid());
		return StaticPowerRecipeRegistry.getRecipe(TurbineRecipe.RECIPE_TYPE, matchParams);
	}

	public boolean hasTurbineBlades() {
		// Get the blades.
		ItemStack blades = turbineBladeInventory.getStackInSlot(0);

		// IF there are none, return false.
		if (blades.isEmpty()) {
			return false;
		}

		// Check the durability.
		return blades.getDamageValue() < blades.getMaxDamage();
	}

	public TurbineBlades getTurbileBladesItem() {
		if (hasTurbineBlades()) {
			ItemStack blades = turbineBladeInventory.getStackInSlot(0);
			if (blades.getItem() instanceof TurbineBlades) {
				return (TurbineBlades) blades.getItem();
			}
		}
		return null;
	}

	protected void suckFluidFromBelow() {
		// Suck up fluid below if it is a valid input and we have enough space.
		if (inputFluidTankComponent.getFluidAmount() + 1000 <= inputFluidTankComponent.getCapacity()) {
			// Get the fluid state of the block below us.
			FluidState fluidState = getLevel().getFluidState(getBlockPos().relative(Direction.DOWN));

			// If its not empty.
			if (!fluidState.isEmpty()) {
				// Create a whole bucket's worth of fluid stack.
				FluidStack fluid = new FluidStack(fluidState.getType(), 1000);

				// Make recipe match parameters.
				RecipeMatchParameters matchParams = new RecipeMatchParameters(fluid);

				// If there is a recipe for that fluid, attempt to suck it up.
				if (StaticPowerRecipeRegistry.getRecipe(TurbineRecipe.RECIPE_TYPE, matchParams).isPresent()) {
					// Simulate a fill using the fluid.
					int filled = inputFluidTankComponent.fill(new FluidStack(fluid, 1000), FluidAction.SIMULATE);

					// If we fill a full bucket, then fill for real.
					if (filled == 1000) {
						inputFluidTankComponent.fill(new FluidStack(fluid, 1000), FluidAction.EXECUTE);
						// Then set the block to empty.
						getLevel().setBlockAndUpdate(getBlockPos().relative(Direction.DOWN), Blocks.AIR.defaultBlockState());
					}
				}
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTurbine(windowId, inventory, this);
	}

	public class TurbineRenderingState {
		public static final float ACCELERATION = 120;
		public static final float DECELERATION = 180;
		public static final float MAX_SPEED = 1080;

		public float speed;
		public float rotationAngle;
		public ResourceLocation bladesTier;

		public TurbineRenderingState() {
			rotationAngle = 0.0f;
			speed = 0.0f;
		}

		public void rotate(float deltaTime) {
			rotationAngle += (speed * deltaTime);
			if (isGenerating) {
				if (speed < MAX_SPEED) {
					speed = Math.min(MAX_SPEED, speed + (ACCELERATION * deltaTime));
				}
			} else {
				if (speed > 0) {
					speed = Math.max(0, speed - (DECELERATION * deltaTime));
				}
			}
		}
	}
}
