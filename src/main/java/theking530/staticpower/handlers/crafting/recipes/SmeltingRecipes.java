package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.ItemMaterials;

public class SmeltingRecipes {

	@SuppressWarnings("all")
	private static void registerSmeltingRecipes() {
		
		//Food
		GameRegistry.addSmelting(new ItemStack(ModItems.WheatFlour), new ItemStack(Items.BREAD), 0.1f);
		GameRegistry.addSmelting(new ItemStack(ModItems.PotatoFlour), new ItemStack(ModItems.PotatoBread), 0.1f);
		
		//Silver
		ItemStack dustSilver = ItemMaterials.dustSilver;
		ItemStack ingotSilver = ItemMaterials.ingotSilver;
		GameRegistry.addSmelting(dustSilver, ingotSilver, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.SilverOre)), ItemMaterials.ingotSilver, 0.1f);
		//Tin
		ItemStack dustTin = ItemMaterials.dustTin;
		ItemStack ingotTin = ItemMaterials.ingotTin;
		GameRegistry.addSmelting(dustTin, ingotTin, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.TinOre)), ItemMaterials.ingotTin, 0.1f);
		//Lead
		ItemStack dustLead = ItemMaterials.dustLead;
		ItemStack ingotLead = ItemMaterials.ingotLead;
		GameRegistry.addSmelting(dustLead, ingotLead, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.LeadOre)), ItemMaterials.ingotLead, 0.1f);
		//Copper
		ItemStack dustCopper = ItemMaterials.dustCopper;
		ItemStack ingotCopper = ItemMaterials.ingotCopper;
		GameRegistry.addSmelting(dustCopper, ingotCopper, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.CopperOre)), ItemMaterials.ingotCopper, 0.1f);
		//Platinum
		ItemStack dustPlatinum = ItemMaterials.dustPlatinum;
		ItemStack ingotPlatinum = ItemMaterials.ingotPlatinum;
		GameRegistry.addSmelting(dustPlatinum, ingotPlatinum, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.PlatinumOre)), ItemMaterials.ingotPlatinum, 0.1f);
		//Iron
		ItemStack dustIron = ItemMaterials.dustIron;
		ItemStack ingotIron = new ItemStack(Items.IRON_INGOT);
		GameRegistry.addSmelting(dustIron, ingotIron, 0.1f);
		//Gold
		ItemStack dustGold = ItemMaterials.dustGold;
		ItemStack ingotGold = new ItemStack(Items.GOLD_INGOT);
		GameRegistry.addSmelting(dustGold, ingotGold, 0.1f);
		//Static
		ItemStack dustStatic = ItemMaterials.dustStatic;
		ItemStack ingotStatic = ItemMaterials.ingotStatic;
		GameRegistry.addSmelting(dustStatic, ingotStatic, 0.1f);
		//Energized
		ItemStack dustEnergized = ItemMaterials.dustEnergized;
		ItemStack ingotEnergized = ItemMaterials.ingotEnergized;
		GameRegistry.addSmelting(dustEnergized, ingotEnergized, 0.1f);
		//Inert
		ItemStack dustInert = ItemMaterials.dustInertInfusion;
		ItemStack ingotInert = ItemMaterials.ingotInertInfusion;
		GameRegistry.addSmelting(dustInert, ingotInert, 0.1f);
		//Nickel
		ItemStack dustNickel = ItemMaterials.dustNickel;
		ItemStack ingotNickel = ItemMaterials.ingotNickel;
		GameRegistry.addSmelting(dustNickel, ingotNickel, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.NickelOre)), ItemMaterials.ingotNickel, 0.1f);
		//Aluminium
		ItemStack dustAluminium = ItemMaterials.dustAluminium;
		ItemStack ingotAluminium = ItemMaterials.ingotAluminium;
		GameRegistry.addSmelting(dustAluminium, ingotAluminium, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.AluminiumOre)), ItemMaterials.ingotAluminium, 0.1f);
		
		GameRegistry.addSmelting(ItemMaterials.dustRedstoneAlloy, ItemMaterials.ingotRedstoneAlloy, 0.1F);
	}
	public static void registerFullRecipes() {
		registerSmeltingRecipes();
	}
}
