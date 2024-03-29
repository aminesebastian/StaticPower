package theking530.staticpower.entities.player.datacapability;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.DigistoreCraftingTerminalHistory.DigistoreCraftingTerminalHistoryEntry;

/**
 * Hate that we have to do fluid movement logic in here. All that code is
 * temporary and going away with the update to 1.19.2.
 * 
 * @author amine
 *
 */
public class StaticPowerPlayerData implements IStaticPowerPlayerData, INBTSerializable<CompoundTag> {
	protected boolean firstTick = true;
	private final DigistoreCraftingTerminalHistory craftingHistory;

	public StaticPowerPlayerData() {
		craftingHistory = new DigistoreCraftingTerminalHistory(4);
	}

	@Override
	public void tick(PlayerTickEvent event) {
		firstTick = false;
	}

	@Override
	public void addToCraftingHistory(ItemStack crafting, Container craftMatrix) {
		if (craftMatrix != null) {
			ItemStack[] recipe = new ItemStack[craftMatrix.getContainerSize()];
			for (int i = 0; i < recipe.length; i++) {
				recipe[i] = craftMatrix.getItem(i).copy();
			}
			craftingHistory.addCraft(recipe, crafting.copy());
		}
	}

	public List<DigistoreCraftingTerminalHistoryEntry> getCraftingHistory() {
		return craftingHistory.getHistory();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("crafting_history", craftingHistory.serializeNBT());
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		craftingHistory.deserializeNBT(nbt.getCompound("crafting_history"));
	}
}