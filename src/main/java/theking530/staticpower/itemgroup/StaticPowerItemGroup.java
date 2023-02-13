package theking530.staticpower.itemgroup;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.init.ModItems;

public class StaticPowerItemGroup extends CreativeModeTab {
	private List<ItemStack> subTypes = null;

	public StaticPowerItemGroup() {
		super("staticpower");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon() {
		calculateSubTypes();
		return new ItemStack(ModItems.StaticFruit.get());
	}

	@Override
	public void fillItemList(NonNullList<ItemStack> items) {
		super.fillItemList(items);
		calculateSubTypes();
		items.addAll(subTypes);
	}

	private void calculateSubTypes() {
		// Preallocate a large array.
		this.subTypes = new ArrayList<>(1000);

		// Add all the covers.
		for (final Block block : ForgeRegistries.BLOCKS) {
			try {
				// Skip blocks with tile entities.
				if (!CableCover.isValidForCover(block)) {
					continue;
				}

				Item blockItem = block.asItem();
				if (blockItem != Items.AIR && blockItem.getItemCategory() != null) {
					final ItemStack facade = ModItems.CableCover.get().makeCoverForBlock(block.defaultBlockState());
					if (!facade.isEmpty()) {
						this.subTypes.add(facade);
					}
				}
			} catch (final Exception e) {
				StaticPower.LOGGER.warn(String.format("Failed to add cover for block: %1$s to the creative tab.", ForgeRegistries.BLOCKS.getKey(block)), e);
			}
		}
	}
}