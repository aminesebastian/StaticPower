package theking530.staticpower.cables.digistore.crafting.recipes;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.digistore.crafting.AutoCraftingStep;
import theking530.staticpower.cables.digistore.crafting.RequiredAutoCraftingMaterials;

public class CraftingStepsBundle {
	private final ItemStack output;
	private final int craftableAmount;
	private final List<AutoCraftingStep> steps;
	private final RequiredAutoCraftingMaterials billOfMaterials;

	public CraftingStepsBundle(List<AutoCraftingStep> steps) {
		this.steps = steps;

		AutoCraftingStep lastStep = steps.get(steps.size() - 1);
		this.output = lastStep.getCraftingPattern().getOutput();
		this.craftableAmount = lastStep.getAmountRemainingToCraft();
		this.billOfMaterials = new RequiredAutoCraftingMaterials(this);
	}

	public boolean hasCraftableOutput() {
		return steps.get(steps.size() - 1).getAmountRemainingToCraft() > 0;
	}

	public List<AutoCraftingStep> getSteps() {
		return steps;
	}

	public ItemStack getOutput() {
		return output;
	}

	public int getCraftableAmount() {
		return craftableAmount;
	}

	public RequiredAutoCraftingMaterials getBillOfMaterials() {
		return billOfMaterials;
	}

	public CompoundTag serialize() {
		// Create the output.
		CompoundTag outputNbt = new CompoundTag();

		// Store the steps.
		ListTag stepNBTList = new ListTag();
		for (AutoCraftingStep step : steps) {
			stepNBTList.add(step.serialize());
		}
		outputNbt.put("steps", stepNBTList);

		return outputNbt;
	}

	public static CraftingStepsBundle read(CompoundTag nbt) {
		// Read the steps.
		List<AutoCraftingStep> steps = new ArrayList<AutoCraftingStep>();
		ListTag stepNBTList = nbt.getList("steps", Constants.NBT.TAG_COMPOUND);
		for (Tag step : stepNBTList) {
			CompoundTag stepTag = (CompoundTag) step;
			steps.add(AutoCraftingStep.read(stepTag));
		}

		return new CraftingStepsBundle(steps);
	}

	public static class CraftingStepsBundleContainer {
		private List<CraftingStepsBundle> bundles;

		public CraftingStepsBundleContainer(List<CraftingStepsBundle> bundles) {
			this.bundles = bundles;
			this.bundles.sort(new Comparator<CraftingStepsBundle>() {
				@Override
				public int compare(CraftingStepsBundle first, CraftingStepsBundle second) {
					// First compare based on missing materials.
					boolean firstMissing = first.getBillOfMaterials().isMissingMaterials();
					boolean secondMissing = second.getBillOfMaterials().isMissingMaterials();
					int missingComparison = (firstMissing == secondMissing) ? 0 : (firstMissing ? 1 : -1);

					// If the missing values are not equal, just return that. Otherwise, sort such
					// that the recipes with the least number of required items come first.
					if (missingComparison != 0) {
						return missingComparison;
					} else {
						return first.getBillOfMaterials().getMaterials().size() - second.getBillOfMaterials().getMaterials().size();
					}
				}
			});
		}

		public List<CraftingStepsBundle> getBundles() {
			return bundles;
		}

		public boolean isEmpty() {
			return bundles.isEmpty();
		}

		public CraftingStepsBundle getBundle(int bundleIndex) {
			return bundles.get(bundleIndex);
		}

		public CompoundTag serialize() {
			// Create the output.
			CompoundTag outputNbt = new CompoundTag();

			// Store the steps.
			ListTag stepNBTList = new ListTag();
			for (CraftingStepsBundle bundle : bundles) {
				stepNBTList.add(bundle.serialize());
			}
			outputNbt.put("bundles", stepNBTList);

			return outputNbt;
		}

		public String serializeCompressed() {
			// Allocate a variable for the compression.
			String compressedBundles = "";
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				GZIPOutputStream gzip;
				gzip = new GZIPOutputStream(out);
				gzip.write(serialize().toString().getBytes());
				gzip.close();
				compressedBundles = (out.toString("ISO-8859-1"));
			} catch (Exception e) {
				StaticPower.LOGGER.error("An error occured when attempting to compress auto-crafting step bundles!", e);
			}

			return compressedBundles;
		}

		public static CraftingStepsBundleContainer readFromCompressedString(String compressedBundle) {
			try {
				GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressedBundle.getBytes("ISO-8859-1")));
				BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
				StringBuilder output = new StringBuilder();
				String line;
				while ((line = bf.readLine()) != null) {
					output.append(line);
				}
				String decompressedString = output.toString();
				CompoundTag decompressedNBT = (CompoundTag) TagParser.parseTag(decompressedString);
				return read(decompressedNBT);
			} catch (Exception e) {
				StaticPower.LOGGER.error("An error occured when attempting to decompress auto-crafting step bundles!", e);
			}
			return null;
		}

		public static CraftingStepsBundleContainer read(CompoundTag nbt) {
			// Read the steps.
			List<CraftingStepsBundle> bundles = new ArrayList<CraftingStepsBundle>();
			ListTag stepNBTList = nbt.getList("bundles", Constants.NBT.TAG_COMPOUND);
			for (Tag step : stepNBTList) {
				CompoundTag stepTag = (CompoundTag) step;
				bundles.add(CraftingStepsBundle.read(stepTag));
			}

			return new CraftingStepsBundleContainer(bundles);
		}
	}
}