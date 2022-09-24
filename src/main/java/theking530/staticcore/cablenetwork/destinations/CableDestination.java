package theking530.staticcore.cablenetwork.destinations;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class CableDestination implements IForgeRegistryEntry<CableDestination> {
	private ResourceLocation registryName;

	public abstract boolean match(Level level, BlockPos cablePosition, Direction cableSide, BlockPos blockPosition, Direction blockSide, @Nullable BlockEntity entity);

	@Override
	public CableDestination setRegistryName(ResourceLocation name) {
		registryName = name;
		return this;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public Class<CableDestination> getRegistryType() {
		return CableDestination.class;
	}
}
