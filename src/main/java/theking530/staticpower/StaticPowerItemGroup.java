package theking530.staticpower;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
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
		return new ItemStack(ModItems.StaticCrop.get());
	}

	@Override
	public void fillItemList(NonNullList<ItemStack> items) {
		this.calculateSubTypes();
		for (final Item item : ForgeRegistries.ITEMS) {
			if (item.getItemCategory() == this) {
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

		// Add the electric items.
		subTypes.add(ModItems.ElectringSolderingIron.get().getFilledVariant());

		// Add the batteries.
		subTypes.add(ModItems.BasicPortableBattery.get().getFilledVariant());
		subTypes.add(ModItems.AdvancedPortableBattery.get().getFilledVariant());
		subTypes.add(ModItems.StaticPortableBattery.get().getFilledVariant());
		subTypes.add(ModItems.EnergizedPortableBattery.get().getFilledVariant());
		subTypes.add(ModItems.LumumPortableBattery.get().getFilledVariant());
		subTypes.add(ModItems.CreativePortableBattery.get().getFilledVariant());

		// Add the battery packs.
		subTypes.add(ModItems.BasicBatteryPack.get().getFilledVariant());
		subTypes.add(ModItems.AdvancedBatteryPack.get().getFilledVariant());
		subTypes.add(ModItems.StaticBatteryPack.get().getFilledVariant());
		subTypes.add(ModItems.EnergizedBatteryPack.get().getFilledVariant());
		subTypes.add(ModItems.LumumBatteryPack.get().getFilledVariant());
		subTypes.add(ModItems.CreativeBatteryPack.get().getFilledVariant());
		subTypes.add(ModItems.ElectringSolderingIron.get().getFilledVariant());

		// Add the drills.
		subTypes.add(ModItems.BasicMiningDrill.get().getFilledVariant());
		subTypes.add(ModItems.AdvancedMiningDrill.get().getFilledVariant());
		subTypes.add(ModItems.StaticMiningDrill.get().getFilledVariant());
		subTypes.add(ModItems.EnergizedMiningDrill.get().getFilledVariant());
		subTypes.add(ModItems.LumumMiningDrill.get().getFilledVariant());

		// Add the chainsaws.
		subTypes.add(ModItems.BasicChainsaw.get().getFilledVariant());
		subTypes.add(ModItems.AdvancedChainsaw.get().getFilledVariant());
		subTypes.add(ModItems.StaticChainsaw.get().getFilledVariant());
		subTypes.add(ModItems.EnergizedChainsaw.get().getFilledVariant());
		subTypes.add(ModItems.LumumChainsaw.get().getFilledVariant());

		// Add the magnets.
		subTypes.add(ModItems.BasicMagnet.get().getFilledVariant());
		subTypes.add(ModItems.AdvancedMagnet.get().getFilledVariant());
		subTypes.add(ModItems.StaticMagnet.get().getFilledVariant());
		subTypes.add(ModItems.EnergizedMagnet.get().getFilledVariant());
		subTypes.add(ModItems.LumumMagnet.get().getFilledVariant());

		// Add all the capsules for all fluids.
		for (Fluid fluid : ForgeRegistries.FLUIDS) {
			// Skip the flowing fluids.
			if (fluid.defaultFluidState().getAmount() != 8) {
				continue;
			}

			subTypes.add(ModItems.IronFluidCapsule.get().getFilledVariant(fluid));
			subTypes.add(ModItems.BasicFluidCapsule.get().getFilledVariant(fluid));
			subTypes.add(ModItems.AdvancedFluidCapsule.get().getFilledVariant(fluid));
			subTypes.add(ModItems.StaticFluidCapsule.get().getFilledVariant(fluid));
			subTypes.add(ModItems.EnergizedFluidCapsule.get().getFilledVariant(fluid));
			subTypes.add(ModItems.LumumFluidCapsule.get().getFilledVariant(fluid));
			subTypes.add(ModItems.CreativeFluidCapsule.get().getFilledVariant(fluid));
		}
	}
}