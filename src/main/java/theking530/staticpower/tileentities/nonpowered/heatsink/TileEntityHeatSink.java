package theking530.staticpower.tileentities.nonpowered.heatsink;

import java.util.List;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import theking530.common.utilities.SDMath;
import theking530.staticpower.cables.heat.HeatCableComponent;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.tileentities.TileEntityBase;

public class TileEntityHeatSink extends TileEntityBase implements INamedContainerProvider {
	private final HeatCableComponent cableComponent;

	public TileEntityHeatSink(TileEntityType<TileEntityHeatSink> type, ResourceLocation tier) {
		super(type);
		registerComponent(cableComponent = new HeatCableComponent("HeatCableComponent", StaticPowerDataRegistry.getTier(tier).getHeatSinkCapacity(),
				StaticPowerDataRegistry.getTier(tier).getHeatSinkConductivity()));

	}

	@Override
	public void process() {
		if (!world.isRemote) {
			cableComponent.getHeatNetworkModule().ifPresent(module -> {
				if (module.getHeatPerCable() >= 100.0f) {
					AxisAlignedBB aabb = new AxisAlignedBB(this.pos.add(0.0, 0, 0.0), this.pos.add(1.0, 2.0, 1.0));
					List<Entity> list = this.world.getEntitiesWithinAABB(Entity.class, aabb);
					for (Entity entity : list) {
						entity.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0f);
					}
				}
			});
		}

		float randomOffset = (3 * RANDOM.nextFloat()) - 1.5f;
		if (SDMath.diceRoll(0.25f) && world.getBlockState(getPos().offset(Direction.UP)).getBlock() == Blocks.WATER) {
			randomOffset /= 3.5f;
			getWorld().addParticle(ParticleTypes.BUBBLE, getPos().getX() + 0.5f + randomOffset, getPos().getY() + 1.1f, getPos().getZ() + 0.5f + randomOffset, 0.0f, 0.5f, 0.0f);
			getWorld().addParticle(ParticleTypes.BUBBLE_POP, getPos().getX() + 0.5f + randomOffset, getPos().getY() + 1.8f, getPos().getZ() + 0.5f + randomOffset, 0.0f, 0.005f, 0.0f);
		}
	}
}
