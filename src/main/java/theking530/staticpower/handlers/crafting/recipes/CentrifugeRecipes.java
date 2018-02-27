package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import theking530.staticpower.assists.RegisterHelper;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.items.ItemMaterials;

public class CentrifugeRecipes {

	public static void registerCentrigureRecipes() {
		RegisterHelper.registerCentrifugeRecipe(Craft.item(Items.GUNPOWDER), 500, ItemMaterials.dustCharcoal, Craft.outputItemStack(ItemMaterials.dustSaltpeter, 2), ItemMaterials.dustSulfur);
	}
}
