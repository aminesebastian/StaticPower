package theking530.staticpower;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.init.ModItems;

public class StaticPowerItemGroup extends ItemGroup {
	private List<ItemStack> subTypes = null;

	public StaticPowerItemGroup() {
		super("StaticPower");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack createIcon() {
		calculateSubTypes();
		return new ItemStack(ModItems.StaticCrop);
	}

	@Override
	public void fill(NonNullList<ItemStack> items) {
		this.calculateSubTypes();
		for (final Item item : ForgeRegistries.ITEMS) {
			if (item.getGroup() == this) {
				items.add(new ItemStack(item));
			}
		}
		items.addAll(subTypes);
	}

	private void calculateSubTypes() {
		// Remove this caching for now until we gather a better system for gathering
		// subtypes AFTER tiers have been synced.
//		// If we have calculated the subtypes, do nothing.
//		if (this.subTypes != null) {
//			return;
//		}

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

		// Add the electric items.
		subTypes.add(ModItems.ElectringSolderingIron.getFilledVariant());

		// Add the batteries.
		subTypes.add(ModItems.BasicPortableBattery.getFilledVariant());
		subTypes.add(ModItems.AdvancedPortableBattery.getFilledVariant());
		subTypes.add(ModItems.StaticPortableBattery.getFilledVariant());
		subTypes.add(ModItems.EnergizedPortableBattery.getFilledVariant());
		subTypes.add(ModItems.LumumPortableBattery.getFilledVariant());
		subTypes.add(ModItems.CreativePortableBattery.getFilledVariant());

		// Add the battery packs.
		subTypes.add(ModItems.BasicBatteryPack.getFilledVariant());
		subTypes.add(ModItems.AdvancedBatteryPack.getFilledVariant());
		subTypes.add(ModItems.StaticBatteryPack.getFilledVariant());
		subTypes.add(ModItems.EnergizedBatteryPack.getFilledVariant());
		subTypes.add(ModItems.LumumBatteryPack.getFilledVariant());
		subTypes.add(ModItems.CreativeBatteryPack.getFilledVariant());
		subTypes.add(ModItems.ElectringSolderingIron.getFilledVariant());

		// Add the drills.
		subTypes.add(ModItems.BasicMiningDrill.getFilledVariant());
		subTypes.add(ModItems.AdvancedMiningDrill.getFilledVariant());
		subTypes.add(ModItems.StaticMiningDrill.getFilledVariant());
		subTypes.add(ModItems.EnergizedMiningDrill.getFilledVariant());
		subTypes.add(ModItems.LumumMiningDrill.getFilledVariant());

		// Add the chainsaws.
		subTypes.add(ModItems.BasicChainsaw.getFilledVariant());
		subTypes.add(ModItems.AdvancedChainsaw.getFilledVariant());
		subTypes.add(ModItems.StaticChainsaw.getFilledVariant());
		subTypes.add(ModItems.EnergizedChainsaw.getFilledVariant());
		subTypes.add(ModItems.LumumChainsaw.getFilledVariant());

		// Add the magnets.
		subTypes.add(ModItems.BasicMagnet.getFilledVariant());
		subTypes.add(ModItems.AdvancedMagnet.getFilledVariant());
		subTypes.add(ModItems.StaticMagnet.getFilledVariant());
		subTypes.add(ModItems.EnergizedMagnet.getFilledVariant());
		subTypes.add(ModItems.LumumMagnet.getFilledVariant());

		// Add all the capsules for all fluids.
		for (Fluid fluid : GameRegistry.findRegistry(Fluid.class)) {
			subTypes.add(ModItems.IronFluidCapsule.getFilledVariant(fluid));
			subTypes.add(ModItems.BasicFluidCapsule.getFilledVariant(fluid));
			subTypes.add(ModItems.AdvancedFluidCapsule.getFilledVariant(fluid));
			subTypes.add(ModItems.StaticFluidCapsule.getFilledVariant(fluid));
			subTypes.add(ModItems.EnergizedFluidCapsule.getFilledVariant(fluid));
			subTypes.add(ModItems.LumumFluidCapsule.getFilledVariant(fluid));
			subTypes.add(ModItems.CreativeFluidCapsule.getFilledVariant(fluid));
		}
	}
}