package theking530.staticpower.tileentities.nonpowered.vacuumchest;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.initialization.ModUpgrades;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;
import theking530.staticpower.items.upgrades.ExperienceVacuumUpgrade;
import theking530.staticpower.items.upgrades.TeleportUpgrade;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.TileEntityInventoryComponent;
import theking530.staticpower.tileentities.utilities.SideModeList.Mode;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityVacuumChest extends TileEntityBase implements Predicate<ItemEntity> {

	private float vacuumDiamater;
	private float initialVacuumDiamater;
	private boolean shouldTeleport;
	private boolean shouldVacuumExperience;
	private FluidTank experienceTank;

	public final TileEntityInventoryComponent inventory;
	public final TileEntityInventoryComponent filterSlot;

	public TileEntityVacuumChest() {
		super(ModTileEntityTypes.VACCUM_CHEST, 3);
		initialVacuumDiamater = 6;
		vacuumDiamater = initialVacuumDiamater;
		shouldTeleport = false;
		experienceTank = new FluidTank(5000);
		registerComponent(inventory = new TileEntityInventoryComponent("inventory", 30, Mode.Regular));
		registerComponent(filterSlot = new TileEntityInventoryComponent("filterSlot", 1, Mode.Never));
	}

	@Override
	public void process() {
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		aabb = aabb.expand(vacuumDiamater, vacuumDiamater, vacuumDiamater);
		aabb = aabb.offset(-vacuumDiamater / 2, -vacuumDiamater / 2, -vacuumDiamater / 2);
		vacuumItems(aabb);
		if (shouldVacuumExperience) {
			vacuumExperience(aabb);
		}
	}

	protected void vacuumItems(AxisAlignedBB bounds) {
		List<ItemEntity> droppedItems = getWorld().getEntitiesWithinAABB(ItemEntity.class, bounds, this);
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
							InventoryUtilities.insertItemIntoInventory(inventory, stack);
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
					experienceTank.fill(new FluidStack(Fluids.WATER, 10), FluidAction.EXECUTE);
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
		if (!filterSlot.getStackInSlot(0).isEmpty() && filterSlot.getStackInSlot(0).getItem() instanceof ItemFilter) {
			return true;
		}
		return false;
	}

	public boolean doesItemPassFilter(ItemStack stack) {
		if(hasFilter()) {
			ItemFilter tempFilter = (ItemFilter)filterSlot.getStackInSlot(0).getItem();
			return tempFilter.evaluateItemStackAgainstFilter(filterSlot.getStackInSlot(0), stack);
		}else{
			return true;
		}
	}

	public float getRadius() {
		return vacuumDiamater / 2.0f;
	}

	public FluidTank getTank() {
		return experienceTank;
	}

	public boolean showTank() {
		return shouldVacuumExperience;
	}

	@Override
	public boolean isSideConfigurable() {
		return false;
	}

	/* Update Handling */
	@Override
	public void upgradeTick() {
		if (hasUpgrade(ModUpgrades.BasicRangeUpgrade)) {
			BaseRangeUpgrade tempUpgrade = (BaseRangeUpgrade) getUpgrade(ModUpgrades.BasicRangeUpgrade).getItem();
			vacuumDiamater = tempUpgrade.getValueMultiplied(initialVacuumDiamater, tempUpgrade.getUpgradeValueAtIndex(getUpgrade(ModUpgrades.BasicRangeUpgrade), 0));
		} else {
			vacuumDiamater = initialVacuumDiamater;
		}
		if (hasUpgrade(ModUpgrades.TeleportUpgrade)) {
			shouldTeleport = true;
		} else {
			shouldTeleport = false;
		}
		if (hasUpgrade(ModUpgrades.ExperienceVacuumUpgrade)) {
			shouldVacuumExperience = true;
		} else {
			shouldVacuumExperience = false;
		}
		if (hasUpgrade(ModUpgrades.BasicTankUpgrade)) {
			BaseTankUpgrade tempUpgrade = (BaseTankUpgrade) getUpgrade(ModUpgrades.BasicTankUpgrade).getItem();
			experienceTank.setCapacity((int) (tempUpgrade.getValueMultiplied(10000, tempUpgrade.getUpgradeValueAtIndex(getUpgrade(ModUpgrades.BasicTankUpgrade), 0))));
		} else {
			experienceTank.setCapacity(10000);
		}
	}

	@Override
	public boolean canAcceptUpgrade(@Nonnull ItemStack upgrade) {
		if (upgrade != ItemStack.EMPTY) {
			if (upgrade.getItem() instanceof BaseRangeUpgrade || upgrade.getItem() instanceof TeleportUpgrade || upgrade.getItem() instanceof ExperienceVacuumUpgrade) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return net.minecraftforge.common.util.LazyOptional.of(() -> {
				return inventory;
			}).cast();
		}
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return net.minecraftforge.common.util.LazyOptional.of(() -> {
				return experienceTank;
			}).cast();
		}
		return super.getCapability(cap, side);
	}

	/**
	 * This method indicates whether or not the provided {@link ItemEntity} will be
	 * able to be vacuumed. This should always return true for all items.
	 */
	@Override
	public boolean test(ItemEntity t) {
		return true;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerVacuumChest(windowId, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent(ModBlocks.VacuumChest.getTranslationKey());
	}

}
