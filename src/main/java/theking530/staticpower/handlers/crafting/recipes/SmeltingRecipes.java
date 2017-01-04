package theking530.staticpower.handlers.crafting.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.items.ModItems;

public class SmeltingRecipes {

	@SuppressWarnings("all")
	private static void registerSmeltingRecipes() {
		
		//Silver
		ItemStack dustSilver = new ItemStack(ModItems.SilverDust);
		ItemStack ingotSilver = new ItemStack(ModItems.SilverIngot, 1);
		GameRegistry.addSmelting(dustSilver, ingotSilver, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.SilverOre)), new ItemStack(ModItems.SilverIngot, 1), 0.1f);
		//Tin
		ItemStack dustTin = new ItemStack(ModItems.TinDust);
		ItemStack ingotTin = new ItemStack(ModItems.TinIngot, 1);
		GameRegistry.addSmelting(dustTin, ingotTin, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.TinOre)), new ItemStack(ModItems.TinIngot, 1), 0.1f);
		//Lead
		ItemStack dustLead = new ItemStack(ModItems.LeadDust);
		ItemStack ingotLead = new ItemStack(ModItems.LeadIngot, 1);
		GameRegistry.addSmelting(dustLead, ingotLead, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.LeadOre)), new ItemStack(ModItems.LeadIngot, 1), 0.1f);
		//Copper
		ItemStack dustCopper = new ItemStack(ModItems.CopperDust);
		ItemStack ingotCopper = new ItemStack(ModItems.CopperIngot, 1);
		GameRegistry.addSmelting(dustCopper, ingotCopper, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.CopperOre)), new ItemStack(ModItems.CopperIngot, 1), 0.1f);
		//Platinum
		ItemStack dustPlatinum = new ItemStack(ModItems.PlatinumDust);
		ItemStack ingotPlatinum = new ItemStack(ModItems.PlatinumIngot, 1);
		GameRegistry.addSmelting(dustPlatinum, ingotPlatinum, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.PlatinumOre)), new ItemStack(ModItems.PlatinumIngot, 1), 0.1f);
		//Iron
		ItemStack dustIron = new ItemStack(ModItems.IronDust);
		ItemStack ingotIron = new ItemStack(Items.IRON_INGOT, 1);
		GameRegistry.addSmelting(dustIron, ingotIron, 0.1f);
		//Gold
		ItemStack dustGold = new ItemStack(ModItems.GoldDust);
		ItemStack ingotGold = new ItemStack(Items.GOLD_INGOT, 1);
		GameRegistry.addSmelting(dustGold, ingotGold, 0.1f);
		//Static
		ItemStack dustStatic = new ItemStack(ModItems.StaticDust);
		ItemStack ingotStatic = new ItemStack(ModItems.StaticIngot, 1);
		GameRegistry.addSmelting(dustStatic, ingotStatic, 0.1f);
		//Energized
		ItemStack dustEnergized = new ItemStack(ModItems.EnergizedDust);
		ItemStack ingotEnergized = new ItemStack(ModItems.EnergizedIngot, 1);
		GameRegistry.addSmelting(dustEnergized, ingotEnergized, 0.1f);
		//Inert
		ItemStack dustInert = new ItemStack(ModItems.InertInfusionBlend);
		ItemStack ingotInert = new ItemStack(ModItems.InertIngot, 1);
		GameRegistry.addSmelting(dustInert, ingotInert, 0.1f);
		//Nickel
		ItemStack dustNickel = new ItemStack(ModItems.NickelDust);
		ItemStack ingotNickel = new ItemStack(ModItems.NickelIngot, 1);
		GameRegistry.addSmelting(dustNickel, ingotNickel, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.NickelOre)), new ItemStack(ModItems.NickelIngot, 1), 0.1f);
		//Aluminium
		ItemStack dustAluminium = new ItemStack(ModItems.AluminiumDust);
		ItemStack ingotAluminium = new ItemStack(ModItems.AluminiumIngot, 1);
		GameRegistry.addSmelting(dustAluminium, ingotAluminium, 0.1f);
		GameRegistry.addSmelting(new ItemStack(Item.getItemFromBlock(ModBlocks.AluminiumOre)), new ItemStack(ModItems.AluminiumIngot, 1), 0.1f);
		
		GameRegistry.addSmelting(new ItemStack(ModItems.RedstoneAlloyDust), new ItemStack(ModItems.RedstoneAlloyIngot), 0.1F);
	}
	public static void registerFullRecipes() {
		registerSmeltingRecipes();
	}
}
