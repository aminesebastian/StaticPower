package theking530.staticpower.tileentities.powered.electricminer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.vector.Vector3f;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent.UpgradeItemWrapper;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.nonpowered.miner.AbstractTileEntityMiner;

public class TileEntityElectricMiner extends AbstractTileEntityMiner {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityElectricMiner> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityElectricMiner(), ModBlocks.ElectricMiner);

	public final EnergyStorageComponent energyStorage;
	public final BatteryInventoryComponent batteryInventory;

	public TileEntityElectricMiner() {
		super(TYPE);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(StaticPowerTiers.ENERGIZED);
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", tierObject.defaultMachinePowerCapacity.get(), tierObject.defaultMachinePowerInput.get(),
				tierObject.defaultMachinePowerOutput.get()));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));

		// Set the processing parameters.
		processingComponent.setEnergyComponent(energyStorage);
		
		// Expand upgrades.
		this.upgradesInventory.setSize(4);
		
		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		super.process();

		// Render the particle effects.
		if (processingComponent.getIsOnBlockState()) {
			float randomOffset = (2 * getWorld().rand.nextFloat()) - 1.0f;
			randomOffset /= 3.5f;
			float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
			Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
			getWorld().addParticle(ParticleTypes.SMOKE, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f,
					0.0f);
			if (SDMath.diceRoll(0.25f)) {
				getWorld().addParticle(ParticleTypes.LARGE_SMOKE, getPos().getX() + 0.5f + randomOffset, getPos().getY() + 1.0f, getPos().getZ() + 0.5f + randomOffset, 0.0f, 0.01f, 0.0f);
			}
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerElectricMiner(windowId, inventory, this);
	}

	@Override
	public int getProcessingTime() {
		return StaticPowerConfig.SERVER.electricMinerProcessingTime.get();
	}

	@Override
	public int getHeatGeneration() {
		return (int) (StaticPowerConfig.SERVER.electricMinerHeatGeneration.get() * processingComponent.getCalculatedPowerUsageMultipler());
	}

	@Override
	public int getRadius() {
		// Get the range upgrade.
		UpgradeItemWrapper upgradeWrapper = upgradesInventory.getMaxTierItemForUpgradeType(UpgradeType.RANGE);

		// If there isn't one, return the base level.
		if (upgradeWrapper.isEmpty()) {
			return StaticPowerConfig.SERVER.electricMinerRadius.get();
		}

		// Otherwise, caluclate the new range.
		StaticPowerTier tier = upgradeWrapper.getTier();
		double newRange = tier.rangeUpgrade.get() * StaticPowerConfig.SERVER.electricMinerRadius.get();
		return (int) newRange;
	}

	@Override
	public long getFuelUsage() {
		return StaticPowerConfig.SERVER.electricMinerPowerUsage.get();
	}
}
