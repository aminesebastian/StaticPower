package theking530.staticpower.tileentities;

import net.minecraft.util.ResourceLocation;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;

/**
 * @author Amine
 *
 */
public abstract class TileEntityMachine extends TileEntityConfigurable {
	public final EnergyStorageComponent energyStorage;

	public boolean isUpdateQueued = true;

	public TileEntityMachine(TileEntityTypeAllocator<? extends TileEntityMachine> allocator) {
		this(allocator, StaticPowerTiers.BASIC);
	}

	public TileEntityMachine(TileEntityTypeAllocator<? extends TileEntityMachine> allocator, ResourceLocation tier) {
		super(allocator);
		disableFaceInteraction();
		StaticPowerTier tierObject = TierReloadListener.getTier(tier);
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", tierObject.getDefaultMachinePowerCapacity(), tierObject.getDefaultMachinePowerInput(),
				tierObject.getDefaultMachinePowerOutput()));
	}
}
