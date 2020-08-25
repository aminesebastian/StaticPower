package theking530.staticpower.tileentities;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

/**
 * @author Amine
 *
 */
public abstract class TileEntityMachine extends TileEntityConfigurable {
	public final EnergyStorageComponent energyStorage;

	public boolean isUpdateQueued = true;

	public TileEntityMachine(TileEntityType<?> tileEntityType) {
		this(tileEntityType, StaticPowerTiers.BASIC);
	}

	public TileEntityMachine(TileEntityType<?> tileEntityType, ResourceLocation tier) {
		super(tileEntityType);
		disableFaceInteraction();
		StaticPowerTier tierObject = StaticPowerDataRegistry.getTier(tier);
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", tierObject.getDefaultMachinePowerCapacity(), tierObject.getDefaultMachinePowerInput(),
				tierObject.getDefaultMachinePowerOutput()));
	}
}
