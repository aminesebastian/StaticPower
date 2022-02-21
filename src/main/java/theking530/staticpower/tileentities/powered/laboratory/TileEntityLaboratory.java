package theking530.staticpower.tileentities.powered.laboratory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.teams.research.ResearchManager.ResearchInstance;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.MachineProcessingComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class TileEntityLaboratory extends TileEntityMachine {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityLaboratory> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityLaboratory(pos, state), ModBlocks.Laboratory);

	/**
	 * Indicates how many times faster this block will perform compared to the
	 * vanila furnace.
	 */
	public static final float DEFAULT_PROCESSING_TIME_MULT = 1.3f;

	public final InventoryComponent inputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final MachineProcessingComponent processingComponent;

	@UpdateSerialize
	private String owningTeam;

	public TileEntityLaboratory(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, StaticPowerTiers.BASIC);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 7, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				switch (slot) {
				case 0:
					return stack.getItem() == ModItems.ResearchTier1;
				case 1:
					return stack.getItem() == ModItems.ResearchTier2;
				case 2:
					return stack.getItem() == ModItems.ResearchTier3;
				case 3:
					return stack.getItem() == ModItems.ResearchTier4;
				case 4:
					return stack.getItem() == ModItems.ResearchTier5;
				case 5:
					return stack.getItem() == ModItems.ResearchTier6;
				case 6:
					return stack.getItem() == ModItems.ResearchTier7;
				default:
					return false;
				}
			}
		}));

		// Setup all the other inventories.
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 100, this::canStartProcessing, this::canContinueProcessing, this::processingCompleted, true));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.laboratoryPowerUsage.get());

		// Setup the I/O servo.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		super.process();

		// Randomly generate smoke and flame particles.
		if (processingComponent.getIsOnBlockState()) {
			if (SDMath.diceRoll(0.25f)) {
				@SuppressWarnings("resource")
				float randomOffset = (2 * getLevel().random.nextFloat()) - 1.0f;
				randomOffset /= 3.5f;

				float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
				Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset - 0.5f, 0.32f, forwardOffset));
				getLevel().addParticle(ParticleTypes.ELECTRIC_SPARK, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(), getBlockPos().getZ() + forwardVector.z(), 0.0f,
						0.01f, 0.0f);
				getLevel().addParticle(ParticleTypes.BUBBLE, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(), getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f,
						0.0f);
			}
		}
	}

	protected ProcessingCheckState canStartProcessing() {
		if (!getOwningTeam().isPresent()) {
			return ProcessingCheckState.error("Missing Team!");
		}

		if (!getCurrentResearchInstance().isPresent()) {
			return ProcessingCheckState.error("Missing Selected Research!");
		}

		if (!doInputsSatisfySelectedResearch()) {
			return ProcessingCheckState.error("Missing items");
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState canContinueProcessing() {
		if (!getOwningTeam().isPresent()) {
			return ProcessingCheckState.cancel();
		}
		if (!getCurrentResearchInstance().isPresent()) {
			return ProcessingCheckState.cancel();
		}

		if (!doInputsSatisfySelectedResearch()) {
			return ProcessingCheckState.cancel();
		}

		return ProcessingCheckState.ok();
	}

	protected ProcessingCheckState processingCompleted() {
		if (!getOwningTeam().isPresent()) {
			return ProcessingCheckState.cancel();
		}
		if (!getCurrentResearchInstance().isPresent()) {
			return ProcessingCheckState.cancel();
		}

		Set<Integer> slotsToUse = getSlotsThatSatisfyResearch();
		if (slotsToUse.size() == 0) {
			return ProcessingCheckState.cancel();
		}

		ResearchInstance instance = getCurrentResearchInstance().orElse(null);
		for (int slot : slotsToUse) {
			instance.getResearchManager().addProgressToSelectedResearch(slot, 1);
			inputInventory.getStackInSlot(slot).shrink(1);
		}
		return ProcessingCheckState.ok();
	}

	protected boolean doInputsSatisfySelectedResearch() {
		return getSlotsThatSatisfyResearch().size() > 0;
	}

	protected Set<Integer> getSlotsThatSatisfyResearch() {
		Set<Integer> slotsToConsume = new HashSet<Integer>();
		getCurrentResearchInstance().ifPresent((instance) -> {
			// There is nothing to move if the instance is finished.
			if (instance.isCompleted()) {
				return;
			}
			for (StaticPowerIngredient ing : instance.getTrackedResearch().getRequirements()) {
				for (int i = 0; i < inputInventory.getSlots(); i++) {
					if (slotsToConsume.contains(i)) {
						continue;
					}

					if (ing.test(inputInventory.getStackInSlot(i))) {
						if (instance.getRequirementFullfillment(i) < ing.getCount()) {
							slotsToConsume.add(i);
						}
					}
				}
			}
		});
		return slotsToConsume;
	}

	@Override
	public void onPlaced(BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player) {
			Team placerTeam = TeamManager.get().getTeamForPlayer((Player) placer).orElse(null);
			if (placerTeam != null) {
				owningTeam = placerTeam.getId().toString();
			}
		}
	}

	public void setTeam(UUID teamId) {
		this.owningTeam = teamId.toString();
	}

	public Optional<Team> getOwningTeam() {
		if (owningTeam == null) {
			return Optional.empty();
		}
		return TeamManager.get().getTeamById(UUID.fromString(owningTeam));
	}

	public Optional<ResearchInstance> getCurrentResearchInstance() {
		Team team = getOwningTeam().orElse(null);
		if (team != null) {
			if (team.getResearchManager().getSelectedResearch() != null) {
				return Optional.of(team.getResearchManager().getSelectedResearch());
			}
		}
		return Optional.empty();
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		return mode == MachineSideMode.Input || mode == MachineSideMode.Disabled;
	}

	@Override
	protected DefaultSideConfiguration getDefaultSideConfiguration() {
		return DEFAULT_NO_FACE_SIDE_CONFIGURATION.copy().setSide(BlockSide.LEFT, true, MachineSideMode.Input).setSide(BlockSide.BACK, true, MachineSideMode.Input)
				.setSide(BlockSide.RIGHT, true, MachineSideMode.Input).setSide(BlockSide.TOP, true, MachineSideMode.Input).setSide(BlockSide.BOTTOM, true, MachineSideMode.Input);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerLaboratory(windowId, inventory, this);
	}
}
