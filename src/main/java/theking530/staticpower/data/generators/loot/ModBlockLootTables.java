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
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.NewModMaterials;

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

		for (MaterialBundle bundle : NewModMaterials.MATERIALS.values()) {
			if (bundle.hasGeneratedMaterial(MaterialTypes.OVERWORLD_ORE)) {
				addOre(bundle.get(MaterialTypes.OVERWORLD_ORE).get(), bundle.get(MaterialTypes.RAW_MATERIAL).get());
			}
			if (bundle.hasGeneratedMaterial(MaterialTypes.NETHER_ORE)) {
				addOre(bundle.get(MaterialTypes.NETHER_ORE).get(), bundle.get(MaterialTypes.RAW_MATERIAL).get());
			}
			if (bundle.hasGeneratedMaterial(MaterialTypes.DEEPSLATE_ORE)) {
				addOre(bundle.get(MaterialTypes.DEEPSLATE_ORE).get(), bundle.get(MaterialTypes.RAW_MATERIAL).get());
			}
		}

		addOreDropWithCounts(ModBlocks.OreRustyIron.get(), ModItems.RustyIronScrap.get(), 4, 5);

		addCrop(ModBlocks.StaticCrop.get(), ModItems.StaticFruit.get(), ModItems.StaticSeeds.get());
		addCrop(ModBlocks.EnergizedCrop.get(), ModItems.EnergizedFruit.get(), ModItems.EnergizedSeeds.get());
		addCrop(ModBlocks.LumumCrop.get(), ModItems.LumumFruit.get(), ModItems.LumumSeeds.get());

		addLeavesDrops(ModBlocks.RubberTreeLeaves.get(), ModBlocks.RubberTreeSapling.get(), 0.05f, 0.0625f, 0.083333336f, 0.1f);
	}

	public void add(Block p_124166_, LootTable.Builder p_124167_) {
		super.add(p_124166_, p_124167_);
	}

	public void addOre(Block block, Item rawOre) {
		add(block, createOreDrop(block, rawOre));
	}

	public void addLeavesDrops(Block block, Block sapling, float... chances) {
		add(block, createLeavesDrops(block, sapling, chances));
	}

	public void addCrop(Block cropBlock, Item crop, Item seeds) {
		LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(cropBlock)
				.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
		add(cropBlock, createCropDrops(cropBlock, crop, seeds, condition));
	}

	public void addOreDropWithCounts(Block block, Item item, float minimum, float maximum) {
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
