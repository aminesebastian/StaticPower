package theking530.staticpower.tileentities.powered.electricminer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.math.vector.Vector3f;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.nonpowered.miner.AbstractTileEntityMiner;

public class TileEntityElectricMiner extends AbstractTileEntityMiner {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityElectricMiner> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityElectricMiner(), ModBlocks.ElectricMiner);

	public final EnergyStorageComponent energyStorage;
	public final UpgradeInventoryComponent upgradesInventory;
	public final BatteryInventoryComponent batteryInventory;

	public TileEntityElectricMiner() {
		super(TYPE);
		StaticPowerTier tierObject = TierReloadListener.getTier(StaticPowerTiers.ENERGIZED);
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", tierObject.getDefaultMachinePowerCapacity(), tierObject.getDefaultMachinePowerInput(),
				tierObject.getDefaultMachinePowerOutput()));

		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));

		// Set the fuel usages higher as they now correlate to power usage.
		setBlockMiningFuelCost(getBlockMiningFuelCost());
		setIdleFuelCost(getIdleFuelCost());

		// Set the processing parameters.
		processingComponent.setUpgradeInventory(upgradesInventory);
		processingComponent.setEnergyComponent(energyStorage);
		processingComponent.setProcessingPowerUsage(getIdleFuelCost());
		processingComponent.setCompletedPowerUsage(getBlockMiningFuelCost());

		// Set the energy storage upgrade inventory.
		energyStorage.setUpgradeInventory(upgradesInventory);
	}

	@Override
	public void process() {
		super.process();

		// Render the particle effects.
		if (processingComponent.getIsOnBlockState()) {
			float randomOffset = (2 * RANDOM.nextFloat()) - 1.0f;
			randomOffset /= 3.5f;
			float forwardOffset = getFacingDirection().getAxisDirection() == AxisDirection.POSITIVE ? -1.05f : -0.05f;
			Vector3f forwardVector = SDMath.transformVectorByDirection(getFacingDirection(), new Vector3f(randomOffset + 0.5f, 0.32f, forwardOffset));
			getWorld().addParticle(ParticleTypes.SMOKE, getPos().getX() + forwardVector.getX(), getPos().getY() + forwardVector.getY(), getPos().getZ() + forwardVector.getZ(), 0.0f, 0.01f, 0.0f);
			if (SDMath.diceRoll(0.25f)) {
				getWorld().addParticle(ParticleTypes.LARGE_SMOKE, getPos().getX() + 0.5f + randomOffset, getPos().getY() + 1.0f, getPos().getZ() + 0.5f + randomOffset, 0.0f, 0.01f, 0.0f);
			}
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerElectricMiner(windowId, inventory, this);
	}
}
