package theking530.staticpower.tileentities.powered.heatsink;

import java.util.List;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.cables.heat.HeatCableComponent;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityMachine;

public class TileEntityHeatSink extends TileEntityMachine implements INamedContainerProvider {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatSink> TYPE_ALUMINIUM = new TileEntityTypeAllocator<TileEntityHeatSink>(
			(allocator) -> new TileEntityHeatSink(allocator, StaticPowerTiers.ALUMINIUM), ModBlocks.AluminiumHeatSink);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatSink> TYPE_COPPER = new TileEntityTypeAllocator<TileEntityHeatSink>(
			(allocator) -> new TileEntityHeatSink(allocator, StaticPowerTiers.COPPER), ModBlocks.CopperHeatSink);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatSink> TYPE_TIN = new TileEntityTypeAllocator<TileEntityHeatSink>((allocator) -> new TileEntityHeatSink(allocator, StaticPowerTiers.TIN),
			ModBlocks.TinHeatSink);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatSink> TYPE_SILVER = new TileEntityTypeAllocator<TileEntityHeatSink>(
			(allocator) -> new TileEntityHeatSink(allocator, StaticPowerTiers.SILVER), ModBlocks.SilverHeatSink);
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityHeatSink> TYPE_GOLD = new TileEntityTypeAllocator<TileEntityHeatSink>((allocator) -> new TileEntityHeatSink(allocator, StaticPowerTiers.GOLD),
			ModBlocks.GoldHeatSink);

	public static final float HEAT_DAMAGE_THRESHOLD = 100.0f;
	public final HeatCableComponent cableComponent;

	public TileEntityHeatSink(TileEntityTypeAllocator<TileEntityHeatSink> allocator, ResourceLocation tierName) {
		super(allocator);
		StaticPowerTier tier = TierReloadListener.getTier(tierName);
		registerComponent(cableComponent = new HeatCableComponent("HeatCableComponent", tier.getHeatSinkCapacity(), tier.getHeatSinkConductivity(), tier.getHeatSinkElectricHeatGeneration(),
				tier.getHeatSinkElectricHeatPowerUsage()).setEnergyStorageComponent(energyStorage));
		energyStorage.setMaxInput(tier.getHeatSinkElectricHeatPowerUsage() * 2);
	}

	@Override
	public void process() {
		if (!world.isRemote) {
			cableComponent.getHeatNetworkModule().ifPresent(module -> {
				// Apply damage to any entities that are on top of this block if the heat is >
				// the damage threshold.
				if (module.getHeatPerCable() >= HEAT_DAMAGE_THRESHOLD) {
					AxisAlignedBB aabb = new AxisAlignedBB(this.pos.add(0.0, 0, 0.0), this.pos.add(1.0, 2.0, 1.0));
					List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb);
					for (Entity entity : list) {
						entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0f);
					}
				}
			});
		}

		// If under water, generate bubbles.
		float randomOffset = (3 * RANDOM.nextFloat()) - 1.5f;
		if (SDMath.diceRoll(0.25f) && world.getBlockState(getPos().offset(Direction.UP)).getBlock() == Blocks.WATER) {
			randomOffset /= 3.5f;
			getWorld().addParticle(ParticleTypes.BUBBLE, getPos().getX() + 0.5f + randomOffset, getPos().getY() + 1.1f, getPos().getZ() + 0.5f + randomOffset, 0.0f, 0.5f, 0.0f);
			getWorld().addParticle(ParticleTypes.BUBBLE_POP, getPos().getX() + 0.5f + randomOffset, getPos().getY() + 1.8f, getPos().getZ() + 0.5f + randomOffset, 0.0f, 0.005f, 0.0f);
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerHeatSink(windowId, inventory, this);
	}
}
