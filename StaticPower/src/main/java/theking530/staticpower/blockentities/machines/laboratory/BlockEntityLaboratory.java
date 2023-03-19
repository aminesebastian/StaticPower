package theking530.staticpower.blockentities.machines.laboratory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.control.processing.MachineProcessingComponent;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.InputOnlyNoFace;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.InputServoComponent;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.research.gui.ResearchManager.ResearchInstance;
import theking530.staticcore.teams.Team;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.BlockEntityMachine;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;

public class BlockEntityLaboratory extends BlockEntityMachine {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityLaboratory> TYPE = new BlockEntityTypeAllocator<>("laboratory",
			(type, pos, state) -> new BlockEntityLaboratory(pos, state), ModBlocks.Laboratory);

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

	public BlockEntityLaboratory(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);

		// Setup the input inventory to only accept items that have a valid recipe.
		registerComponent(inputInventory = new InventoryComponent("InputInventory", 7, MachineSideMode.Input).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			public boolean canInsertItem(int slot, ItemStack stack) {
				switch (slot) {
				case 0:
					return stack.getItem() == ModItems.ResearchTier1.get();
				case 1:
					return stack.getItem() == ModItems.ResearchTier2.get();
				case 2:
					return stack.getItem() == ModItems.ResearchTier3.get();
				case 3:
					return stack.getItem() == ModItems.ResearchTier4.get();
				case 4:
					return stack.getItem() == ModItems.ResearchTier5.get();
				case 5:
					return stack.getItem() == ModItems.ResearchTier6.get();
				case 6:
					return stack.getItem() == ModItems.ResearchTier7.get();
				default:
					return false;
				}
			}
		}));

		// Setup all the other inventories.
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", powerStorage));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		// Setup the processing component.
		registerComponent(processingComponent = new MachineProcessingComponent("ProcessingComponent", 100, this::canStartProcessing, this::canContinueProcessing,
				this::processingCompleted, true));

		// Initialize the processing component to work with the redstone control
		// component, upgrade component and energy component.
		processingComponent.setShouldControlBlockState(true);
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setPowerComponent(powerStorage);
		processingComponent.setRedstoneControlComponent(redstoneControlComponent);
		processingComponent.setProcessingPowerUsage(StaticPowerConfig.SERVER.laboratoryPowerUsage.get());

		// Setup the I/O servo.
		registerComponent(new InputServoComponent("InputServo", 4, inputInventory));

		// Set the energy storage upgrade inventory.
		powerStorage.setUpgradeInventory(upgradesInventory);
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
				getLevel().addParticle(ParticleTypes.ELECTRIC_SPARK, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(),
						getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f, 0.0f);
				getLevel().addParticle(ParticleTypes.BUBBLE, getBlockPos().getX() + forwardVector.x(), getBlockPos().getY() + forwardVector.y(),
						getBlockPos().getZ() + forwardVector.z(), 0.0f, 0.01f, 0.0f);
			}
		}
	}

	protected ProcessingCheckState canStartProcessing() {
		if (getTeamComponent().getOwningTeam() == null) {
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
		if (getTeamComponent().getOwningTeam() == null) {
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
		if (getTeamComponent().getOwningTeam() == null) {
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

	public Optional<ResearchInstance> getCurrentResearchInstance() {
		Team team = getTeamComponent().getOwningTeam();
		if (team != null) {
			if (team.getResearchManager().getSelectedResearch() != null) {
				return Optional.of(team.getResearchManager().getSelectedResearch());
			}
		}
		return Optional.empty();
	}

	@Override
	protected SideConfigurationPreset getDefaultSideConfiguration() {
		return InputOnlyNoFace.INSTANCE;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerLaboratory(windowId, inventory, this);
	}
}
