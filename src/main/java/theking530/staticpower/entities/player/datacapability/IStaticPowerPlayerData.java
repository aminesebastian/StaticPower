package theking530.staticpower.entities.player.datacapability;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory.DigistoreCraftingTerminalHistoryEntry;

public interface IStaticPowerPlayerData  {
	public void addToCraftingHistory(ItemStack crafting, IInventory craftMatrix);

	public List<DigistoreCraftingTerminalHistoryEntry> getCraftingHistory();
}