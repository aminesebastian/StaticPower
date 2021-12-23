package theking530.staticpower.entities.player.datacapability;

import java.util.List;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory.DigistoreCraftingTerminalHistoryEntry;

public interface IStaticPowerPlayerData  {
	public void addToCraftingHistory(ItemStack crafting, Container craftMatrix);

	public List<DigistoreCraftingTerminalHistoryEntry> getCraftingHistory();
}