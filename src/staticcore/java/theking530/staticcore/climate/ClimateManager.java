package theking530.staticcore.climate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import theking530.staticcore.StaticCore;
import theking530.staticcore.data.gamedata.BasicStaticCoreGameData;
import theking530.staticcore.data.gamedata.StaticCoreGameDataManager.StaticCoreDataAccessor;

public class ClimateManager extends BasicStaticCoreGameData {
	public static final ResourceLocation ID = new ResourceLocation(StaticCore.MOD_ID, "climate");
	private final Map<ResourceLocation, DimensionClimateState> dimensionStates;

	public ClimateManager(boolean isClientSide) {
		super(ID, isClientSide);
		dimensionStates = new HashMap<>();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void tick(Level level) {
		if (level.isClientSide()) {
			return;
		}

		for (Entry<ResourceLocation, DimensionClimateState> entry : dimensionStates.entrySet()) {
			if (level.getServer().forgeGetWorldMap().containsKey(level.dimension())) {
				entry.getValue().tick(level);
			}
		}
	}

	@Override
	public void deserialize(CompoundTag tag) {
		CompoundTag dimensionsTag = tag.getCompound("dimensions");

		for (Entry<ResourceLocation, DimensionClimateState> entry : dimensionStates.entrySet()) {
			if (dimensionsTag.contains(entry.getKey().toString())) {
				entry.getValue().deserializeNBT(dimensionsTag.getCompound(entry.getKey().toString()));
			}
		}
	}

	@Overwrite
	public CompoundTag serialize(CompoundTag tag) {
		CompoundTag dimensionsTag = new CompoundTag();
		for (Entry<ResourceLocation, DimensionClimateState> entry : dimensionStates.entrySet()) {
			dimensionsTag.put(entry.getKey().toString(), entry.getValue().serializeNBT());
		}
		tag.put("dimensions", dimensionsTag);
		return tag;
	}

	public DimensionClimateState getClimateData(Level level) {
		if (!dimensionStates.containsKey(level.dimension().location())) {
			dimensionStates.put(level.dimension().location(), new DimensionClimateState());
		}
		return dimensionStates.get(level.dimension().location());
	}

	public static ClimateManager get(LevelAccessor level) {
		return StaticCoreDataAccessor.get(level).getGameData(ID);
	}

	public static ClimateManager getServer() {
		return StaticCoreDataAccessor.get(false).getGameData(ID);
	}
}
