package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.handlers.crafting.Craft;
import theking530.staticpower.items.ItemMaterials;
import theking530.staticpower.items.MiscItems;

public class SmeltingRecipes {

	@SuppressWarnings("all")
	private static void registerSmeltingRecipes() {
		
		//Food
		GameRegistry.addSmelting(MiscItems.wheatFlour, new ItemStack(Items.BREAD), 0.1f);
		GameRegistry.addSmelting(MiscItems.potatoFlour, Craft.outputStack(MiscItems.potatoBread), 0.1f);
		
		//Silver
		GameRegistry.addSmelting(ItemMaterials.dustSilver, ItemMaterials.ingotSilver, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.SilverOre)), ItemMaterials.ingotSilver, 0.1f);
		//Tin
		GameRegistry.addSmelting(ItemMaterials.dustTin, ItemMaterials.ingotTin, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.TinOre)), ItemMaterials.ingotTin, 0.1f);
		//Lead
		GameRegistry.addSmelting(ItemMaterials.dustLead, ItemMaterials.ingotLead, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.LeadOre)), ItemMaterials.ingotLead, 0.1f);
		//Copper
		GameRegistry.addSmelting(ItemMaterials.dustCopper, ItemMaterials.ingotCopper, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.CopperOre)), ItemMaterials.ingotCopper, 0.1f);
		//Platinum
		GameRegistry.addSmelting(ItemMaterials.dustPlatinum, ItemMaterials.ingotPlatinum, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.PlatinumOre)), ItemMaterials.ingotPlatinum, 0.1f);
		//Iron
		GameRegistry.addSmelting(ItemMaterials.dustIron, new ItemStack(Items.IRON_INGOT), 0.1f);
		//Gold
		GameRegistry.addSmelting(ItemMaterials.dustGold, new ItemStack(Items.GOLD_INGOT), 0.1f);
		//Static
		GameRegistry.addSmelting(ItemMaterials.dustStatic, ItemMaterials.ingotStatic, 0.1f);
		//Energized
		GameRegistry.addSmelting(ItemMaterials.dustEnergized, ItemMaterials.ingotEnergized, 0.1f);
		//Inert
		GameRegistry.addSmelting(ItemMaterials.dustInertInfusion, ItemMaterials.ingotInertInfusion, 0.1f);
		//Nickel
		GameRegistry.addSmelting(ItemMaterials.dustNickel, ItemMaterials.ingotNickel, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.NickelOre)), ItemMaterials.ingotNickel, 0.1f);
		//Aluminium
		GameRegistry.addSmelting(ItemMaterials.dustAluminium, ItemMaterials.ingotAluminium, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.AluminiumOre)), ItemMaterials.ingotAluminium, 0.1f);
		//Redstone Alloy
		GameRegistry.addSmelting(ItemMaterials.dustRedstoneAlloy, ItemMaterials.ingotRedstoneAlloy, 0.1F);
	}
	public static void registerFullRecipes() {
		registerSmeltingRecipes();
	}
}
