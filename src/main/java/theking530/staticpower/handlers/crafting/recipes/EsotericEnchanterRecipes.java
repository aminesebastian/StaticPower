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
import theking530.staticpower.handlers.crafting.CraftHelpers;
import theking530.staticpower.handlers.crafting.StaticPowerIngredient;

public class EsotericEnchanterRecipes {

	public static void registerEsotericEnchanterRecipes() {
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.AQUA_AFFINITY), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.PRISMARINE_SHARD), CraftHelpers.ingredientFromItem(Items.GLASS_BOTTLE), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.BANE_OF_ARTHROPODS), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.FERMENTED_SPIDER_EYE), CraftHelpers.ingredientFromItem(Items.STRING), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.BLAST_PROTECTION), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.TNT), CraftHelpers.ingredientFromBlock(Blocks.OBSIDIAN), new FluidStack(ModFluids.LiquidExperience, 650));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.BINDING_CURSE), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.SLIME_BALL), CraftHelpers.ingredientFromItem(Items.FISHING_ROD), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.VANISHING_CURSE), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.ENDER_EYE), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.DEPTH_STRIDER), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.LEATHER_BOOTS), CraftHelpers.ingredientFromItem(Items.GOLDEN_BOOTS), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.EFFICIENCY), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.REDSTONE_BLOCK), CraftHelpers.ingredientFromItem(Items.QUARTZ), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FEATHER_FALLING), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.FEATHER), CraftHelpers.ingredientFromBlock(Blocks.SLIME_BLOCK), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FIRE_ASPECT), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.BLAZE_ROD), CraftHelpers.ingredientFromItem(Items.MAGMA_CREAM), new FluidStack(ModFluids.LiquidExperience, 1250));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FIRE_PROTECTION), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.MAGMA_CREAM), CraftHelpers.ingredientFromItem(Items.NETHERBRICK), new FluidStack(ModFluids.LiquidExperience, 1500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FLAME), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.BLAZE_POWDER), CraftHelpers.ingredientFromItem(Items.GHAST_TEAR), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FORTUNE), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.DIAMOND), CraftHelpers.ingredientFromItemstack(new ItemStack(Items.DYE, 1, 4)), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.FROST_WALKER), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.ICE), CraftHelpers.ingredientFromItem(Items.WATER_BUCKET), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.INFINITY), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.NETHER_STAR), CraftHelpers.ingredientFromItem(Items.ARROW), new FluidStack(ModFluids.LiquidExperience, 4000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.KNOCKBACK), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.PISTON), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.LOOTING), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.DIAMOND), CraftHelpers.ingredientFromItem(Items.EMERALD), new FluidStack(ModFluids.LiquidExperience, 1250));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.LUCK_OF_THE_SEA), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.PRISMARINE_SHARD), CraftHelpers.ingredientFromItem(Items.GOLD_NUGGET), new FluidStack(ModFluids.LiquidExperience, 750));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.LURE), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.FISHING_ROD), CraftHelpers.ingredientFromItem(Items.FISH), new FluidStack(ModFluids.LiquidExperience, 750));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.MENDING), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.NETHER_STAR), CraftHelpers.ingredientFromItem(Items.GOLDEN_APPLE), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.PROTECTION), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.IRON_CHESTPLATE), new FluidStack(ModFluids.LiquidExperience, 750));
		
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.POWER), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.PISTON), CraftHelpers.ingredientFromBlock(Blocks.TNT), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.PROJECTILE_PROTECTION), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.IRON_CHESTPLATE), CraftHelpers.ingredientFromItem(Items.ARROW), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.PUNCH), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.PISTON), CraftHelpers.ingredientFromBlock(Blocks.SLIME_BLOCK), new FluidStack(ModFluids.LiquidExperience, 500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.RESPIRATION), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.GLASS_BOTTLE), CraftHelpers.ingredientFromItem(Items.PRISMARINE_CRYSTALS), new FluidStack(ModFluids.LiquidExperience, 2000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SHARPNESS), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.DIAMOND), CraftHelpers.ingredientFromItem(Items.QUARTZ), new FluidStack(ModFluids.LiquidExperience, 1000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SILK_TOUCH), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.SLIME_BALL), CraftHelpers.ingredientFromBlock(Blocks.GRASS), new FluidStack(ModFluids.LiquidExperience, 3000));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SMITE), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.DIAMOND_SWORD), new FluidStack(ModFluids.LiquidExperience, 2750));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.SWEEPING), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromItem(Items.IRON_SWORD), CraftHelpers.ingredientFromItem(Items.IRON_SWORD), new FluidStack(ModFluids.LiquidExperience, 2500));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.THORNS), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.CACTUS), new FluidStack(ModFluids.LiquidExperience, 2250));
		RegisterHelper.registerEsotericEnchanterRecipe(createEnchantedBook(Enchantments.UNBREAKING), CraftHelpers.ingredientFromItem(Items.BOOK), CraftHelpers.ingredientFromBlock(Blocks.DIAMOND_BLOCK), CraftHelpers.ingredientFromBlock(Blocks.OBSIDIAN), new FluidStack(ModFluids.LiquidExperience, 1000));
		
		
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
