package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.CraftHelpers;

public class EsotericEnchanterRecipes {

	public static void registerEsotericEnchanterRecipes() {
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		book.addEnchantment(Enchantments.AQUA_AFFINITY, 3);
		RegisterHelper.registerEsotericEnchanterRecipe(book, CraftHelpers.ingredientFromItem(Items.PRISMARINE_SHARD), CraftHelpers.ingredientFromItem(Items.GLASS_BOTTLE), new FluidStack(ModFluids.LiquidExperience, 1000));

	}

}
