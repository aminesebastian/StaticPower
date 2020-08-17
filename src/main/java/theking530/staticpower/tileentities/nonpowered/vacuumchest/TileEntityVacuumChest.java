package theking530.staticpower.tileentities.nonpowered.vacuumchest;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.ExperienceVacuumUpgrade;
import theking530.staticpower.items.upgrades.IUpgradeItem.UpgradeType;
import theking530.staticpower.items.upgrades.TeleportUpgrade;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityVacuumChest extends TileEntityConfigurable implements INamedContainerProvider {
	public static final int DEFAULT_RANGE = 6;
	public static final int DEFAULT_TANK_SIZE = 5000;

	public final InventoryComponent inventory;
	public final InventoryComponent filterSlotInventory;
	public final InventoryComponent fluidContainerInventory;
	public final UpgradeInventoryComponent upgradesInventory;
	public final FluidTankComponent fluidTankComponent;
	public final FluidContainerComponent fluidContainerComponent;
	public final FluidOutputServoComponent fluidOutputServo;

	protected float vacuumDiamater;
	protected boolean shouldTeleport;
	protected boolean shouldVacuumExperience;

	public TileEntityVacuumChest() {
		super(ModTileEntityTypes.VACCUM_CHEST);
		vacuumDiamater = DEFAULT_RANGE;
		shouldTeleport = false;

		registerComponent(inventory = new InventoryComponent("Inventory", 30, MachineSideMode.Output));
		registerComponent(filterSlotInventory = new InventoryComponent("FilterSlot", 1));
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));

		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", DEFAULT_TANK_SIZE).setCapabilityExposedModes(MachineSideMode.Output).setUpgradeInventory(upgradesInventory));
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 2));
		registerComponent(fluidContainerComponent = new FluidContainerComponent("FluidContainerServo", fluidTankComponent, fluidContainerInventory, 0, 1).setMode(FluidContainerInteractionMode.FILL));
		registerComponent(fluidOutputServo = new FluidOutputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));

		registerComponent(new OutputServoComponent("OutputServo", 2, inventory));
	}

	@Override
	public void process() {
		// Handle the upgrade tick on the server.
		if (!world.isRemote) {
			upgradeTick();

			// Create the AABB to search within.
			AxisAlignedBB aabb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
			aabb = aabb.expand(vacuumDiamater, vacuumDiamater, vacuumDiamater);
			aabb = aabb.offset(-vacuumDiamater / 2, -vacuumDiamater / 2, -vacuumDiamater / 2);

			// Vacuum the items.
			vacuumItems(aabb);

			// Vacuum experience if requested.
			if (shouldVacuumExperience) {
				vacuumExperience(aabb);
			}
		}
	}

	protected void vacuumItems(AxisAlignedBB bounds) {
		List<ItemEntity> droppedItems = getWorld().getEntitiesWithinAABB(ItemEntity.class, bounds, (ItemEntity item) -> true);
		for (ItemEntity entity : droppedItems) {

			double x = (pos.getX() + 0.5D - entity.getPosX());
			double y = (pos.getY() + 0.5D - entity.getPosY());
			double z = (pos.getZ() + 0.5D - entity.getPosZ());
			ItemStack stack = entity.getItem().copy();
			if (InventoryUtilities.canFullyInsertItemIntoInventory(inventory, stack) && doesItemPassFilter(stack)) {
				double distance = Math.sqrt(x * x + y * y + z * z);
				if (distance < 1.1 || (shouldTeleport && distance < getRadius() - 0.1f)) {
					if (InventoryUtilities.canFullyInsertItemIntoInventory(inventory, stack)) {
						if (!getWorld().isRemote) {
							InventoryUtilities.insertItemIntoInventory(inventory, stack, false);
						}
						entity.remove();
						getWorld().addParticle(ParticleTypes.PORTAL, (double) pos.getX() + 0.5, (double) pos.getY() + 1.0, (double) pos.getZ() + 0.5, 0.0D, 0.0D, 0.0D);
						getWorld().playSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
					}
				} else {
					double var11 = 1.0 - distance / 15.0;
					if (var11 > 0.0D) {
						var11 *= var11;
						entity.addVelocity(x / distance * var11 * 0.06, y / distance * var11 * 0.15, z / distance * var11 * 0.06);
						Vec3d entityPos = entity.getPositionVector();
						getWorld().addParticle(ParticleTypes.PORTAL, entityPos.x, entityPos.y - 0.5, entityPos.z, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}
	}

	protected void vacuumExperience(AxisAlignedBB bounds) {
		List<ExperienceOrbEntity> xpOrbs = getWorld().getEntitiesWithinAABB(ExperienceOrbEntity.class, bounds);
		for (ExperienceOrbEntity orb : xpOrbs) {
			double x = (pos.getX() + 0.5D - orb.getPosX());
			double y = (pos.getY() + 0.5D - orb.getPosY());
			double z = (pos.getZ() + 0.5D - orb.getPosZ());

			double distance = Math.sqrt(x * x + y * y + z * z);
			if (distance < 1.1 || (shouldTeleport && distance < getRadius() - 0.1f)) {
				if (true) {// experienceTank.canFill() && experienceTank.fill(new
							// FluidStack(ModFluids.LiquidExperience, orb.getXpValue()), false) > 0) {
					fluidTankComponent.fill(new FluidStack(ModFluids.LiquidExperience.Fluid, orb.xpValue), FluidAction.EXECUTE);
					markTileEntityForSynchronization();
					orb.remove();
					getWorld().playSound((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5F,
							(RANDOM.nextFloat() + 1) / 2, false);
				}
			} else {
				double var11 = 1.0 - distance / 15.0;
				if (var11 > 0.0D) {
					var11 *= var11;
					orb.addVelocity(x / distance * var11 * 0.06, y / distance * var11 * 0.15, z / distance * var11 * 0.06);
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
		shouldTeleport = upgradesInventory.hasUpgradeOfClass(TeleportUpgrade.class);
		shouldVacuumExperience = upgradesInventory.hasUpgradeOfClass(ExperienceVacuumUpgrade.class);

		// Set the enabled state of the fluid components.
		fluidTankComponent.setEnabled(shouldVacuumExperience);
		fluidContainerInventory.setEnabled(shouldVacuumExperience);
		fluidContainerComponent.setEnabled(shouldVacuumExperience);
		fluidOutputServo.setEnabled(shouldVacuumExperience);

		// Get the range upgrade.
		UpgradeItemWrapper rangeUpgrade = upgradesInventory.getMaxTierItemForUpgradeType(UpgradeType.RANGE);
		if (!rangeUpgrade.isEmpty()) {
			vacuumDiamater = DEFAULT_RANGE * rangeUpgrade.getTier().getRangeUpgrade();
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

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.putBoolean("should_vacuum_experience", shouldVacuumExperience);
		nbt.putBoolean("should_teleport", shouldTeleport);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		shouldVacuumExperience = nbt.getBoolean("should_vacuum_experience");
		shouldTeleport = nbt.getBoolean("should_teleport");
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerVacuumChest(windowId, inventory, this);
	}
}
