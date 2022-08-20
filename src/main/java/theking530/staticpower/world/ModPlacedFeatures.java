package theking530.staticpower.world;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModPlacedFeatures {

	public static final Holder<PlacedFeature> RUBBER_WOOD_TREE_PLACED = PlacementUtils.register("rubber_tree_placed", ModConfiguredFeatures.RUBBER_WOOD_TREE_SPAWN,
			VegetationPlacements.treePlacement(PlacementUtils.countExtra(0, 0.05f, 1)));

}
