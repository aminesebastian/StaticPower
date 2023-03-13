package theking530.staticpower.blockentities.machines.cropfarmer;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IFarmerHarvester {
	public HarvestResult harvest(Level level, BlockPos pos);

	public class HarvestResult {
		private final boolean harvested;
		private final boolean useAxe;
		private final boolean useHoe;
		private final List<ItemStack> results;

		public HarvestResult(boolean harvested, boolean useAxe, boolean useHoe, List<ItemStack> results) {
			this.harvested = harvested;
			this.useAxe = useAxe;
			this.useHoe = useHoe;
			this.results = results;
		}

		public boolean isEmpty() {
			return !harvested;
		}

		public List<ItemStack> getResults() {
			return results;
		}

		public boolean isUseAxe() {
			return useAxe;
		}

		public boolean isUseHoe() {
			return useHoe;
		}

		public static HarvestResult empty() {
			return new HarvestResult(false, false, false, null);
		}

		public static HarvestResult byAxe(List<ItemStack> results) {
			return new HarvestResult(true, true, false, results);
		}

		public static HarvestResult byHoe(List<ItemStack> results) {
			return new HarvestResult(true, false, true, results);
		}

		public static HarvestResult noTool(List<ItemStack> results) {
			return new HarvestResult(true, false, false, results);
		}
	}
}
