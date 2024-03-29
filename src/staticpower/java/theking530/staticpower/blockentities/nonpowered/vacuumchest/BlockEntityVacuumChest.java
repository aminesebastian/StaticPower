package theking530.staticpower.blockentities.nonpowered.vacuumchest;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.AllSidesOutput;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidTankComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent;
import theking530.staticcore.blockentity.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.OutputServoComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.init.StaticCoreUpgradeTypes;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModUpgradeTypes;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.ExperienceVacuumUpgrade;
import theking530.staticpower.items.upgrades.TeleportUpgrade;

public class BlockEntityVacuumChest extends BlockEntityBase implements MenuProvider {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityVacuumChest> TYPE = new BlockEntityTypeAllocator<>("vacuum_chest", (type, pos, state) -> new BlockEntityVacuumChest(pos, state),
			ModBlocks.VacuumChest);

	public static final int DEFAULT_RANGE = 6;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final InventoryComponent inventory;
	public final InventoryComponent filterSlotInventory;
	public final FluidContainerInventoryComponent fluidContainerComponent;

	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidTankComponent fluidTankComponent;
	public final FluidOutputServoComponent fluidOutputServo;
	public final SideConfigurationComponent ioSideConfiguration;
	public final RedstoneControlComponent redstoneControlComponent;

	@UpdateSerialize
	protected float vacuumDiamater;
	@UpdateSerialize
	protected boolean shouldTeleport;
	@UpdateSerialize
	protected boolean shouldVacuumExperience;

	public BlockEntityVacuumChest(BlockPos pos, BlockState state) {
		super(TYPE, pos, state);
		vacuumDiamater = DEFAULT_RANGE;
		shouldTeleport = false;

		registerComponent(inventory = new InventoryComponent("Inventory", 30, MachineSideMode.Output).setShiftClickEnabled(true));
		registerComponent(filterSlotInventory = new InventoryComponent("FilterSlot", 1).setShiftClickEnabled(true).setShiftClickPriority(100));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		registerComponent(fluidContainerComponent = new FluidContainerInventoryComponent("FluidContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));
		registerComponent(fluidOutputServo = new FluidOutputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", AllSidesOutput.INSTANCE));
		registerComponent(redstoneControlComponent = new RedstoneControlComponent("RedstoneControlComponent", RedstoneMode.Ignore));

		registerComponent(new OutputServoComponent("OutputServo", 2, inventory));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!level.isClientSide && redstoneControlComponent.passesRedstoneCheck()) {
			upgradeTick();

			// Vacuum every other tick.
			if (SDMath.diceRoll(0.75)) {
				// Create the AABB to search within.
				AABB aabb = new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
				aabb = aabb.expandTowards(vacuumDiamater, vacuumDiamater, vacuumDiamater);
				aabb = aabb.move(-vacuumDiamater / 2, -vacuumDiamater / 2, -vacuumDiamater / 2);

				// Vacuum the items.
				vacuumItems(aabb);

				// Vacuum experience if requested.
				if (shouldVacuumExperience) {
					vacuumExperience(aabb);
				}
			}
		}
	}

	protected void vacuumItems(AABB bounds) {
		List<ItemEntity> droppedItems = getLevel().getEntitiesOfClass(ItemEntity.class, bounds, (ItemEntity item) -> true);
		for (ItemEntity entity : droppedItems) {

			double x = (worldPosition.getX() + 0.5D - entity.getX());
			double y = (worldPosition.getY() + 0.5D - entity.getY());
			double z = (worldPosition.getZ() + 0.5D - entity.getZ());
			ItemStack stack = entity.getItem().copy();
			if (InventoryUtilities.canFullyInsertItemIntoInventory(inventory, stack) && doesItemPassFilter(stack)) {
				double distance = Math.sqrt(x * x + y * y + z * z);
				if (distance < 0.6f || (shouldTeleport && distance < getRadius() - 0.1f)) {
					InventoryUtilities.insertItemIntoInventory(inventory, stack, false);
					entity.remove(RemovalReason.DISCARDED);
					((ServerLevel) getLevel()).sendParticles(ParticleTypes.PORTAL, worldPosition.getX() + 0.5, (double) worldPosition.getY() + 1.0, (double) worldPosition.getZ() + 0.5, 1, 0.0D, 0.0D,
							0.0D, 0.0D);
					getLevel().playSound(null, (double) worldPosition.getX(), (double) worldPosition.getY(), (double) worldPosition.getZ(), SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.5F, 1.0F);
				} else {
					double var11 = 1.0 - distance / 15.0;
					if (var11 > 0.0D) {
						var11 *= var11;
						entity.push(x / distance * var11 * 0.06, y / distance * var11 * 0.15, z / distance * var11 * 0.06);
						Vec3 entityPos = entity.position();
						((ServerLevel) getLevel()).sendParticles(ParticleTypes.PORTAL, entityPos.x, entityPos.y - 0.5, entityPos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}

	@SuppressWarnings("resource")
	protected void vacuumExperience(AABB bounds) {
		List<ExperienceOrb> xpOrbs = getLevel().getEntitiesOfClass(ExperienceOrb.class, bounds);
		for (ExperienceOrb orb : xpOrbs) {
			double x = (worldPosition.getX() + 0.5D - orb.getX());
			double y = (worldPosition.getY() + 0.5D - orb.getY());
			double z = (worldPosition.getZ() + 0.5D - orb.getZ());

			// Check if we can take the orb's xp. If not, stop.
			int tempFilled = fluidTankComponent.fill(new FluidStack(ModFluids.LiquidExperience.getSource().get(), orb.value), FluidAction.SIMULATE);
			if (tempFilled != orb.value) {
				break;
			}

			double distance = Math.sqrt(x * x + y * y + z * z);
			if (distance < 0.6f || (shouldTeleport && distance < getRadius() - 0.1f)) {
				if (true) {// experienceTank.canFill() && experienceTank.fill(new
							// FluidStack(ModFluids.LiquidExperience, orb.getXpValue()), false) > 0) {
					fluidTankComponent.fill(new FluidStack(ModFluids.LiquidExperience.getSource().get(), orb.value), FluidAction.EXECUTE);
					orb.remove(RemovalReason.DISCARDED);
					getLevel().playSound(null, (double) worldPosition.getX(), (double) worldPosition.getY(), (double) worldPosition.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F,
							(getLevel().random.nextFloat() + 1) / 2);
				}
			} else {
				double var11 = 1.0 - distance / 15.0;
				if (var11 > 0.0D) {
					var11 *= var11;
					orb.push(x / distance * var11 * 0.06, y / distance * var11 * 0.15, z / distance * var11 * 0.06);
				}
			}
		}
	}

	public boolean hasFilter() {
		if (!filterSlotInventory.getStackInSlot(0).isEmpty() && filterSlotInventory.getStackInSlot(0).getItem() instanceof ItemFilter) {
			return true;
		}
		return false;
	}

	public boolean doesItemPassFilter(ItemStack stack) {
		if (hasFilter()) {
			ItemFilter tempFilter = (ItemFilter) filterSlotInventory.getStackInSlot(0).getItem();
			return tempFilter.evaluateItemStackAgainstFilter(filterSlotInventory.getStackInSlot(0), stack);
		} else {
			return true;
		}
	}

	public float getRadius() {
		return vacuumDiamater / 2.0f;
	}

	public FluidTankComponent getTank() {
		return fluidTankComponent;
	}

	public boolean showTank() {
		return shouldVacuumExperience;
	}

	/* Update Handling */
	public void upgradeTick() {
		shouldTeleport = upgradesInventory.hasUpgradeOfType(ModUpgradeTypes.TELEPORT.get());
		shouldVacuumExperience = upgradesInventory.hasUpgradeOfType(ModUpgradeTypes.EXPERIENCE_VACUUM.get());

		// Set the enabled state of the fluid components.
		fluidTankComponent.setEnabled(shouldVacuumExperience);
		fluidContainerComponent.setEnabled(shouldVacuumExperience);
		fluidContainerComponent.setEnabled(shouldVacuumExperience);
		fluidOutputServo.setEnabled(shouldVacuumExperience);

		// Get the range upgrade.
		UpgradeItemWrapper<Double> rangeUpgrade = upgradesInventory.getMaxTierItemForUpgradeType(StaticCoreUpgradeTypes.RANGE.get());
		if (!rangeUpgrade.isEmpty()) {
			vacuumDiamater = (float) (DEFAULT_RANGE * rangeUpgrade.getUpgradeValue());
		} else {
			vacuumDiamater = DEFAULT_RANGE;
		}
	}

	public boolean canAcceptUpgrade(@Nonnull ItemStack upgrade) {
		if (upgrade != ItemStack.EMPTY) {
			if (upgrade.getItem() instanceof BaseRangeUpgrade || upgrade.getItem() instanceof TeleportUpgrade || upgrade.getItem() instanceof ExperienceVacuumUpgrade) {
				return true;
			}
		}
		return false;
	}

	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putBoolean("should_vacuum_experience", shouldVacuumExperience);
		nbt.putBoolean("should_teleport", shouldTeleport);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		shouldVacuumExperience = nbt.getBoolean("should_vacuum_experience");
		shouldTeleport = nbt.getBoolean("should_teleport");
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerVacuumChest(windowId, inventory, this);
	}
}
