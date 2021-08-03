package theking530.staticpower.entities.player.datacapability;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory.DigistoreCraftingTerminalHistoryEntry;

public class StaticPowerPlayerData implements IStaticPowerPlayerData, INBTSerializable<CompoundNBT> {
	private DigistoreCraftingTerminalHistory craftingHistory;

	public StaticPowerPlayerData() {
		craftingHistory = new DigistoreCraftingTerminalHistory(4);
	}

	@Override
	public void addToCraftingHistory(ItemStack crafting, IInventory craftMatrix) {
		if (craftMatrix != null) {
			ItemStack[] recipe = new ItemStack[craftMatrix.getSizeInventory()];
			for (int i = 0; i < recipe.length; i++) {
				recipe[i] = craftMatrix.getStackInSlot(i).copy();
			}
			craftingHistory.addCraft(recipe, crafting.copy());
		}
	}

	public List<DigistoreCraftingTerminalHistoryEntry> getCraftingHistory() {
		return craftingHistory.getHistory();
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();
		output.put("crafting_history", craftingHistory.serializeNBT());
		return output;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		craftingHistory.deserializeNBT(nbt.getCompound("crafting_history"));
	}
}