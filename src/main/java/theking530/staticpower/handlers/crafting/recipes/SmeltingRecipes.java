package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.SPItemMaterial;

public class SmeltingRecipes {

	@SuppressWarnings("all")
	private static void registerSmeltingRecipes() {
		
		//Food
		GameRegistry.addSmelting(new ItemStack(ModItems.WheatFlour), new ItemStack(Items.BREAD), 0.1f);
		GameRegistry.addSmelting(new ItemStack(ModItems.PotatoFlour), new ItemStack(ModItems.PotatoBread), 0.1f);
		
		//Silver
		ItemStack dustSilver = SPItemMaterial.dustSilver;
		ItemStack ingotSilver = SPItemMaterial.ingotSilver;
		GameRegistry.addSmelting(dustSilver, ingotSilver, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.SilverOre)), SPItemMaterial.ingotSilver, 0.1f);
		//Tin
		ItemStack dustTin = SPItemMaterial.dustTin;
		ItemStack ingotTin = SPItemMaterial.ingotTin;
		GameRegistry.addSmelting(dustTin, ingotTin, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.TinOre)), SPItemMaterial.ingotTin, 0.1f);
		//Lead
		ItemStack dustLead = SPItemMaterial.dustLead;
		ItemStack ingotLead = SPItemMaterial.ingotLead;
		GameRegistry.addSmelting(dustLead, ingotLead, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.LeadOre)), SPItemMaterial.ingotLead, 0.1f);
		//Copper
		ItemStack dustCopper = SPItemMaterial.dustCopper;
		ItemStack ingotCopper = SPItemMaterial.ingotCopper;
		GameRegistry.addSmelting(dustCopper, ingotCopper, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.CopperOre)), SPItemMaterial.ingotCopper, 0.1f);
		//Platinum
		ItemStack dustPlatinum = SPItemMaterial.dustPlatinum;
		ItemStack ingotPlatinum = SPItemMaterial.ingotPlatinum;
		GameRegistry.addSmelting(dustPlatinum, ingotPlatinum, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.PlatinumOre)), SPItemMaterial.ingotPlatinum, 0.1f);
		//Iron
		ItemStack dustIron = SPItemMaterial.dustIron;
		ItemStack ingotIron = new ItemStack(Items.IRON_INGOT);
		GameRegistry.addSmelting(dustIron, ingotIron, 0.1f);
		//Gold
		ItemStack dustGold = SPItemMaterial.dustGold;
		ItemStack ingotGold = new ItemStack(Items.GOLD_INGOT);
		GameRegistry.addSmelting(dustGold, ingotGold, 0.1f);
		//Static
		ItemStack dustStatic = SPItemMaterial.dustStatic;
		ItemStack ingotStatic = SPItemMaterial.ingotStatic;
		GameRegistry.addSmelting(dustStatic, ingotStatic, 0.1f);
		//Energized
		ItemStack dustEnergized = SPItemMaterial.dustEnergized;
		ItemStack ingotEnergized = SPItemMaterial.ingotEnergized;
		GameRegistry.addSmelting(dustEnergized, ingotEnergized, 0.1f);
		//Inert
		ItemStack dustInert = SPItemMaterial.dustInertInfusion;
		ItemStack ingotInert = SPItemMaterial.ingotInertInfusion;
		GameRegistry.addSmelting(dustInert, ingotInert, 0.1f);
		//Nickel
		ItemStack dustNickel = SPItemMaterial.dustNickel;
		ItemStack ingotNickel = SPItemMaterial.ingotNickel;
		GameRegistry.addSmelting(dustNickel, ingotNickel, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.NickelOre)), SPItemMaterial.ingotNickel, 0.1f);
		//Aluminium
		ItemStack dustAluminium = SPItemMaterial.dustAluminium;
		ItemStack ingotAluminium = SPItemMaterial.ingotAluminium;
		GameRegistry.addSmelting(dustAluminium, ingotAluminium, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.AluminiumOre)), SPItemMaterial.ingotAluminium, 0.1f);
		
		GameRegistry.addSmelting(SPItemMaterial.dustRedstoneAlloy, SPItemMaterial.ingotRedstoneAlloy, 0.1F);
	}
	public static void registerFullRecipes() {
		registerSmeltingRecipes();
	}
}
