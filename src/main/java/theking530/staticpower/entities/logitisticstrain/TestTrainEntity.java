package theking530.staticpower.entities.logitisticstrain;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import theking530.staticpower.init.ModEntities;

import net.minecraft.world.entity.vehicle.AbstractMinecart.Type;

public class TestTrainEntity extends AbstractMinecart {

	public TestTrainEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	public TestTrainEntity(Level worldIn, double x, double y, double z) {
		super(ModEntities.TestTrainEntity.getType(), worldIn, x, y, z);
	}

	@Override
	public Type getMinecartType() {
		return AbstractMinecart.Type.RIDEABLE;
	}
}
