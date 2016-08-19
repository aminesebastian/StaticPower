package theking530.staticpower.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.assists.Tier;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.model.Models;
import theking530.staticpower.client.render.conduit.TileEntityRenderFluidConduit;
import theking530.staticpower.client.render.conduit.TileEntityRenderItemConduit;
import theking530.staticpower.client.render.conduit.TileEntityRenderStaticConduit;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderBattery;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderChest;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderCropSqueezer;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderFluidGenerator;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderFluidInfuser;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderFusionFurnace;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderPoweredFurnace;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderPoweredGrinder;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderSignalMultiplier;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderSolarPanel;
import theking530.staticpower.client.render.tileentitys.TileEntityRenderSolderingTable;
import theking530.staticpower.conduits.fluidconduit.TileEntityFluidConduit;
import theking530.staticpower.conduits.itemconduit.TileEntityItemConduit;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;
import theking530.staticpower.machines.batteries.tileentities.TileEntityEnergizedBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityLumumBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityStaticBattery;
import theking530.staticpower.machines.cropsqueezer.TileEntityCropSqueezer;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.machines.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.machines.signalmultiplier.TileEntitySignalMultiplier;
import theking530.staticpower.machines.solarpanel.TileEntitySolarPanel;
import theking530.staticpower.machines.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.tileentity.energizedchest.TileEntityEnergizedChest;
import theking530.staticpower.tileentity.lumumchest.TileEntityLumumChest;
import theking530.staticpower.tileentity.staticchest.TileEntityStaticChest;

public class ClientProxy extends CommonProxy {

	public void registerProxies(){
		OBJLoader.INSTANCE.addDomain(Reference.MODID);
		
		//Fluid Infuser
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidInfuser.class, new TileEntityRenderFluidInfuser());		
		//Crop Squeezer
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCropSqueezer.class, new TileEntityRenderCropSqueezer());
		//Fluid Generator
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidGenerator.class, new TileEntityRenderFluidGenerator());
		//Smelter
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPoweredFurnace.class, new TileEntityRenderPoweredFurnace());
		//Grinder
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPoweredGrinder.class, new TileEntityRenderPoweredGrinder());
		//SConduit
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStaticConduit.class, new TileEntityRenderStaticConduit());
		//FConduit
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidConduit.class, new TileEntityRenderFluidConduit());
		//IConduit
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemConduit.class, new TileEntityRenderItemConduit());
		//Signal Multiplier
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySignalMultiplier.class, new TileEntityRenderSignalMultiplier());	
		//Solar Panel
		//registerModelLoader(Item.getItemFromBlock(ModBlocks.StaticSolarPanel), Models.SOLARPANEL);
		//registerModelLoader(Item.getItemFromBlock(ModBlocks.EnergizedSolarPanel), Models.SOLARPANEL);
		//registerModelLoader(Item.getItemFromBlock(ModBlocks.LumumSolarPanel), Models.SOLARPANEL);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySolarPanel.class, new TileEntityRenderSolarPanel());
		//Batteries
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStaticBattery.class, new TileEntityRenderBattery(Tier.STATIC));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergizedBattery.class, new TileEntityRenderBattery(Tier.ENERGIZED));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLumumBattery.class, new TileEntityRenderBattery(Tier.LUMUM));
		
		//Chests		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityStaticChest.class, new TileEntityRenderChest(Tier.STATIC));	
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergizedChest.class, new TileEntityRenderChest(Tier.ENERGIZED));	
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLumumChest.class, new TileEntityRenderChest(Tier.LUMUM));		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFusionFurnace.class, new TileEntityRenderFusionFurnace());
		
		//SolderingTable
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySolderingTable.class, new TileEntityRenderSolderingTable());
		
		//Block Breaker
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockBreaker.class, new TileEntityRenderBlockBreaker());
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.BlockBreaker), new ItemRenderBlockBreaker());
		
	    ItemRenderRegistry.initItemRenderers();
	}
	public static void registerModelLoader(Item item, ResourceLocation modelLocation) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(modelLocation, "inventory"));
	}
}