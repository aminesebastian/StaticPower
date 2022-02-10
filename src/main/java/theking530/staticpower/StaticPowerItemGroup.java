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
import net.minecraftforge.registries.RegistryManager;
import theking530.staticpower.cables.attachments.cover.CableCover;
import theking530.staticpower.init.ModItems;

public class StaticPowerItemGroup extends CreativeModeTab {
	private List<ItemStack> subTypes = null;

	public StaticPowerItemGroup() {
		super("StaticPower");
	}

	@OnlyIn(Dist.CLIENT)
	public ItemStack makeIcon() {
		calculateSubTypes();
		return new ItemStack(ModItems.StaticCrop);
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
					final ItemStack facade = ModItems.CableCover.makeCoverForBlock(block.defaultBlockState());
					if (!facade.isEmpty()) {
						this.subTypes.add(facade);
					}
				}
			} catch (final Exception e) {
				StaticPower.LOGGER.warn(String.format("Failed to add cover for block: %1$s to the creative tab.", block.getRegistryName()), e);
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
		for (Fluid fluid : RegistryManager.ACTIVE.getRegistry(Fluid.class)) {
			// Skip the flowing fluids.
			if (fluid.defaultFluidState().getAmount() != 8) {
				continue;
			}

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