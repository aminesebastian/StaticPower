package theking530.staticpower.cables.digistore.crafting.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
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

	public CompoundNBT serialize() {
		// Create the output.
		CompoundNBT outputNbt = new CompoundNBT();

		// Store the steps.
		ListNBT stepNBTList = new ListNBT();
		for (AutoCraftingStep step : steps) {
			stepNBTList.add(step.serialize());
		}
		outputNbt.put("steps", stepNBTList);

		return outputNbt;
	}

	public static CraftingStepsBundle read(CompoundNBT nbt) {
		// Read the steps.
		List<AutoCraftingStep> steps = new ArrayList<AutoCraftingStep>();
		ListNBT stepNBTList = nbt.getList("steps", Constants.NBT.TAG_COMPOUND);
		for (INBT step : stepNBTList) {
			CompoundNBT stepTag = (CompoundNBT) step;
			steps.add(AutoCraftingStep.read(stepTag));
		}

		return new CraftingStepsBundle(steps);
	}

	public static class CraftingStepsBundleContainer {
		private List<CraftingStepsBundle> bundles;

		public CraftingStepsBundleContainer(List<CraftingStepsBundle> bundles) {
			this.bundles = bundles;
		}

		public List<CraftingStepsBundle> getBundles() {
			return bundles;
		}

		public CraftingStepsBundle getBundle(int bundleIndex) {
			return bundles.get(bundleIndex);
		}

		public CompoundNBT serialize() {
			// Create the output.
			CompoundNBT outputNbt = new CompoundNBT();

			// Store the steps.
			ListNBT stepNBTList = new ListNBT();
			for (CraftingStepsBundle bundle : bundles) {
				stepNBTList.add(bundle.serialize());
			}
			outputNbt.put("bundles", stepNBTList);

			return outputNbt;
		}

		public static CraftingStepsBundleContainer read(CompoundNBT nbt) {
			// Read the steps.
			List<CraftingStepsBundle> bundles = new ArrayList<CraftingStepsBundle>();
			ListNBT stepNBTList = nbt.getList("bundles", Constants.NBT.TAG_COMPOUND);
			for (INBT step : stepNBTList) {
				CompoundNBT stepTag = (CompoundNBT) step;
				bundles.add(CraftingStepsBundle.read(stepTag));
			}

			return new CraftingStepsBundleContainer(bundles);
		}
	}
}
