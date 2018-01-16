package theking530.staticpower.handlers.crafting.wrappers;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public class GrinderOutputWrapper {

	protected final GrinderOutput outputItem1;
	protected final GrinderOutput outputItem2;
	protected final GrinderOutput outputItem3;
	
	public GrinderOutputWrapper(GrinderOutput output1, GrinderOutput output2, GrinderOutput output3) {
		outputItem1 = output1;
		outputItem2 = output2;
		outputItem3 = output3;
	}
	
	public ArrayList<GrinderOutput> getOutputItems() {
		ArrayList<GrinderOutput> tempOutput = new ArrayList();
		if(outputItem1.isValid()) {
			tempOutput.add(outputItem1);
		}
		if(outputItem2.isValid()) {
			tempOutput.add(outputItem2);
		}
		if(outputItem3.isValid()) {
			tempOutput.add(outputItem3);
		}
		return tempOutput;
	}
	public int getOutputItemCount() {
		int tempCount = 0;
		if(outputItem1.isValid()) {
			tempCount++;
		}
		if(outputItem2.isValid()) {
			tempCount++;
		}
		if(outputItem3.isValid()) {
			tempCount++;
		}
		return tempCount;
	}
	public float getItemChance(ItemStack item) {
		if(outputItem1.isValid() && outputItem1.getOutput().isItemEqual(item)) {
			return outputItem1.PERCENTAGE;
		}
		if(outputItem2.isValid() && outputItem2.getOutput().isItemEqual(item)) {
			return outputItem2.PERCENTAGE;
		}
		if(outputItem3.isValid() && outputItem3.getOutput().isItemEqual(item)) {
			return outputItem3.PERCENTAGE;
		}
		return 0f;
	}
	public static GrinderOutput getnullOutput() {
		return new GrinderOutput(null, 0);
	}
	public static class GrinderOutput {
		private final ItemStack OUTPUT_ITEM;
		private final float PERCENTAGE;
		
		public GrinderOutput(ItemStack output, float percentage) {
			OUTPUT_ITEM = output;
			PERCENTAGE = percentage;
		}
		public boolean isValid() {
			if(OUTPUT_ITEM != null) {
				return true;
			}
			return false;
		}
		public ItemStack getOutput() {
			return OUTPUT_ITEM;
		}
		public float getPercentage() {
			return PERCENTAGE;
		}
	}

}
