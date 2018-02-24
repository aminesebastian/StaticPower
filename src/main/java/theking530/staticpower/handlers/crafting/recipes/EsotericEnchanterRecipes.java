package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.handlers.crafting.StaticPowerIngredient;

public class EsotericEnchanterRecipes {

	public static void registerEsotericEnchanterRecipes() {
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.AQUA_AFFINITY), Craft.item(Items.BOOK), Craft.item(Items.PRISMARINE_SHARD), Craft.item(Items.GLASS_BOTTLE), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.BANE_OF_ARTHROPODS), Craft.item(Items.BOOK), Craft.item(Items.FERMENTED_SPIDER_EYE), Craft.item(Items.STRING), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.BLAST_PROTECTION), Craft.item(Items.BOOK), Craft.block(Blocks.TNT), Craft.block(Blocks.OBSIDIAN), new FluidStack(ModFluids.LiquidExperience, 650));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.BINDING_CURSE), Craft.item(Items.BOOK), Craft.item(Items.SLIME_BALL), Craft.item(Items.FISHING_ROD), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.VANISHING_CURSE), Craft.item(Items.BOOK), Craft.item(Items.ENDER_EYE), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.DEPTH_STRIDER), Craft.item(Items.BOOK), Craft.item(Items.LEATHER_BOOTS), Craft.item(Items.GOLDEN_BOOTS), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.EFFICIENCY), Craft.item(Items.BOOK), Craft.block(Blocks.REDSTONE_BLOCK), Craft.item(Items.QUARTZ), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FEATHER_FALLING), Craft.item(Items.BOOK), Craft.item(Items.FEATHER), Craft.block(Blocks.SLIME_BLOCK), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FIRE_ASPECT), Craft.item(Items.BOOK), Craft.item(Items.BLAZE_ROD), Craft.item(Items.MAGMA_CREAM), new FluidStack(ModFluids.LiquidExperience, 1250));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FIRE_PROTECTION), Craft.item(Items.BOOK), Craft.item(Items.MAGMA_CREAM), Craft.item(Items.NETHERBRICK), new FluidStack(ModFluids.LiquidExperience, 1500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FLAME), Craft.item(Items.BOOK), Craft.item(Items.BLAZE_POWDER), Craft.item(Items.GHAST_TEAR), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FORTUNE), Craft.item(Items.BOOK), Craft.item(Items.DIAMOND), Craft.itemstack(new ItemStack(Items.DYE, 1, 4)), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FROST_WALKER), Craft.item(Items.BOOK), Craft.block(Blocks.ICE), Craft.item(Items.WATER_BUCKET), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.INFINITY), Craft.item(Items.BOOK), Craft.item(Items.NETHER_STAR), Craft.item(Items.ARROW), new FluidStack(ModFluids.LiquidExperience, 4000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.KNOCKBACK), Craft.item(Items.BOOK), Craft.block(Blocks.PISTON), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.LOOTING), Craft.item(Items.BOOK), Craft.item(Items.DIAMOND), Craft.item(Items.EMERALD), new FluidStack(ModFluids.LiquidExperience, 1250));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.LUCK_OF_THE_SEA), Craft.item(Items.BOOK), Craft.item(Items.PRISMARINE_SHARD), Craft.item(Items.GOLD_NUGGET), new FluidStack(ModFluids.LiquidExperience, 750));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.LURE), Craft.item(Items.BOOK), Craft.item(Items.FISHING_ROD), Craft.item(Items.FISH), new FluidStack(ModFluids.LiquidExperience, 750));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.MENDING), Craft.item(Items.BOOK), Craft.item(Items.NETHER_STAR), Craft.item(Items.GOLDEN_APPLE), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.PROTECTION), Craft.item(Items.BOOK), Craft.item(Items.IRON_CHESTPLATE), new FluidStack(ModFluids.LiquidExperience, 750));
		
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.POWER), Craft.item(Items.BOOK), Craft.block(Blocks.PISTON), Craft.block(Blocks.TNT), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.PROJECTILE_PROTECTION), Craft.item(Items.BOOK), Craft.item(Items.IRON_CHESTPLATE), Craft.item(Items.ARROW), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.PUNCH), Craft.item(Items.BOOK), Craft.block(Blocks.PISTON), Craft.block(Blocks.SLIME_BLOCK), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.RESPIRATION), Craft.item(Items.BOOK), Craft.item(Items.GLASS_BOTTLE), Craft.item(Items.PRISMARINE_CRYSTALS), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SHARPNESS), Craft.item(Items.BOOK), Craft.item(Items.DIAMOND), Craft.item(Items.QUARTZ), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SILK_TOUCH), Craft.item(Items.BOOK), Craft.item(Items.SLIME_BALL), Craft.block(Blocks.GRASS), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SMITE), Craft.item(Items.BOOK), Craft.item(Items.DIAMOND_SWORD), new FluidStack(ModFluids.LiquidExperience, 2750));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SWEEPING), Craft.item(Items.BOOK), Craft.item(Items.IRON_SWORD), Craft.item(Items.IRON_SWORD), new FluidStack(ModFluids.LiquidExperience, 2500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.THORNS), Craft.item(Items.BOOK), Craft.block(Blocks.CACTUS), new FluidStack(ModFluids.LiquidExperience, 2250));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.UNBREAKING), Craft.item(Items.BOOK), Craft.block(Blocks.DIAMOND_BLOCK), Craft.block(Blocks.OBSIDIAN), new FluidStack(ModFluids.LiquidExperience, 1000));
		
		
		java.util.Iterator<Enchantment> it = Enchantment.REGISTRY.iterator();
		do {
			registerCombinationRecipe(it.next());
		}while(it.hasNext());
	}
	public static void registerCombinationRecipe(Enchantment enchantment) {
		if(enchantment.getMaxLevel() <= 1) {
			return;
		}
		for(int i=enchantment.getMinLevel(); i<enchantment.getMaxLevel(); i++) {
			RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(enchantment, i+1), new StaticPowerIngredient(createEnchantedBook(enchantment, i)),new StaticPowerIngredient(createEnchantedBook(enchantment, i)), new FluidStack(ModFluids.LiquidExperience, 1000*(i+1)));
		}
	}
	public static ItemStack createEnchantedBook(Enchantment enchantment, int level) {
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		ItemEnchantedBook.addEnchantment(book, new EnchantmentData(enchantment, level));
		return book;
	}
	public static ItemStack createEnchantedBook(Enchantment enchantment) {
		return createEnchantedBook(enchantment, 1);
	}
}
