package theking530.staticpower;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.initialization.ModBlocks;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.items.cableattachments.CableCover;

public class StaticPowerItemGroup extends ItemGroup {
	private List<ItemStack> subTypes = null;

	public StaticPowerItemGroup() {
		super("StaticPower");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		calculateSubTypes();
		return new ItemStack(ModBlocks.Digistore);
	}

	@Override
	public void fill(NonNullList<ItemStack> items) {
		this.calculateSubTypes();
		items.addAll(subTypes);
	}

	private void calculateSubTypes() {
		if (this.subTypes != null) {
			return;
		}
		this.subTypes = new ArrayList<>(1000);

		for (final Block block : ForgeRegistries.BLOCKS) {
			try {
				// Skip blocks with tile entities.
				if (!CableCover.isValidForCover(block)) {
					continue;
				}

				Item blockItem = block.asItem();
				if (blockItem != Items.AIR && blockItem.getGroup() != null) {
					final ItemStack facade = ModItems.CableCover.makeCoverForBlock(block.getDefaultState());
					if (!facade.isEmpty()) {
						this.subTypes.add(facade);
					}
				}
			} catch (final Throwable t) {
				System.out.println(t);
			}
		}
	}
}