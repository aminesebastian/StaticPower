package theking530.staticpower.entities.logitisticstrain;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.world.World;
import theking530.staticpower.init.ModEntities;

public class TestTrainEntity extends AbstractMinecartEntity {

	public TestTrainEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public TestTrainEntity(World worldIn, double x, double y, double z) {
		super(ModEntities.TestTrainEntity.getType(), worldIn, x, y, z);
	}

	@Override
	public Type getMinecartType() {
		return AbstractMinecartEntity.Type.RIDEABLE;
	}
}
