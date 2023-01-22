package theking530.staticpower.data.generators.loot;

import java.util.stream.Stream;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.block.IBlockLootTableProvider;
import theking530.staticcore.block.IBlockLootTableProvider.BlockDropType;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;

public class ModBlockLootTables extends BlockLoot {

	@Override
	protected void addTables() {
		for (Block block : getKnownBlocks()) {
			IBlockLootTableProvider provider = (IBlockLootTableProvider) block;
			BlockDropType dropType = provider.getBlockDropType();
			if (dropType == BlockDropType.SELF) {
				dropSelf(block);
			}
		}

		addOre(ModBlocks.OreTin.get(), ModItems.RawTin.get());
		addOre(ModBlocks.OreDeepslateTin.get(), ModItems.RawTin.get());

		addOre(ModBlocks.OreZinc.get(), ModItems.RawZinc.get());
		addOre(ModBlocks.OreDeepslateZinc.get(), ModItems.RawZinc.get());

		addOre(ModBlocks.OreSilver.get(), ModItems.RawSilver.get());
		addOre(ModBlocks.OreDeepslateSilver.get(), ModItems.RawSilver.get());
		addOre(ModBlocks.OreNetherSilver.get(), ModItems.RawTungsten.get());

		addOre(ModBlocks.OreLead.get(), ModItems.RawLead.get());
		addOre(ModBlocks.OreDeepslateLead.get(), ModItems.RawLead.get());

		addOre(ModBlocks.OreTungsten.get(), ModItems.RawTungsten.get());
		addOre(ModBlocks.OreDeepslateTungsten.get(), ModItems.RawTungsten.get());
		addOre(ModBlocks.OreNetherTungsten.get(), ModItems.RawTungsten.get());

		addOre(ModBlocks.OreMagnesium.get(), ModItems.RawMagnesium.get());
		addOre(ModBlocks.OreDeepslateMagnesium.get(), ModItems.RawMagnesium.get());

		addOre(ModBlocks.OrePlatinum.get(), ModItems.RawPlatinum.get());
		addOre(ModBlocks.OreDeepslatePlatinum.get(), ModItems.RawPlatinum.get());
		addOre(ModBlocks.OreNetherPlatinum.get(), ModItems.RawTungsten.get());

		addOre(ModBlocks.OreAluminum.get(), ModItems.RawAluminum.get());
		addOre(ModBlocks.OreDeepslateAluminum.get(), ModItems.RawAluminum.get());

		addOre(ModBlocks.OreUranium.get(), ModItems.RawUranium.get());
		addOre(ModBlocks.OreDeepslateUranium.get(), ModItems.RawUranium.get());

		addOre(ModBlocks.OreRuby.get(), ModItems.RawAluminum.get());
		addOre(ModBlocks.OreDeepslateRuby.get(), ModItems.RawAluminum.get());

		addOre(ModBlocks.OreSapphire.get(), ModItems.RawAluminum.get());
		addOre(ModBlocks.OreDeepslateSapphire.get(), ModItems.RawAluminum.get());

		addOreDropWithCounts(ModBlocks.OreRustyIron.get(), ModItems.RustyIronScrap.get(), 4, 5);

		addCrop(ModBlocks.StaticPlant.get(), ModItems.StaticCrop.get(), ModItems.StaticSeeds.get());
		addCrop(ModBlocks.EnergizedPlant.get(), ModItems.EnergizedCrop.get(), ModItems.EnergizedSeeds.get());
		addCrop(ModBlocks.LumumPlant.get(), ModItems.LumumCrop.get(), ModItems.LumumSeeds.get());

		addLeavesDrops(ModBlocks.RubberTreeLeaves.get(), ModBlocks.RubberTreeSapling.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f);
	}

	protected void addOre(Block block, Item rawOre) {
		add(block, createOreDrop(block, rawOre));
	}

	protected void addLeavesDrops(Block block, Block sapling, float... chances) {
		add(block, createLeavesDrops(block, sapling, chances));
	}

	protected void addCrop(Block cropBlock, Item crop, Item seeds) {
		LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(cropBlock)
				.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
		add(cropBlock, createCropDrops(cropBlock, crop, seeds, condition));
	}

	protected void addOreDropWithCounts(Block block, Item item, float minimum, float maximum) {
		LootTable.Builder builder = createSilkTouchDispatchTable(block, applyExplosionDecay(block, LootItem.lootTableItem(item)
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(minimum, maximum))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
		add(block, builder);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return getApplicableBlocks()::iterator;
	}

	protected Stream<Block> getApplicableBlocks() {
		return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).filter((block) -> {
			if (block instanceof IBlockLootTableProvider) {
				IBlockLootTableProvider provider = (IBlockLootTableProvider) block;
				BlockDropType dropType = provider.getBlockDropType();
				return dropType != BlockDropType.NONE;
			}
			return false;
		});
	}

}
