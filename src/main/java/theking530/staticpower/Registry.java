package theking530.staticpower;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.BaseItemBlock;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.ItemRenderRegistry;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.tileentity.solderingtable.TileEntitySolderingTable;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class Registry {

	public List<Item> ITEMS = new LinkedList<>();
	public List<Block> BLOCKS = new LinkedList<>();
    
    public void preInit(FMLPreInitializationEvent e) {
    	 MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void PreRegisterItem(Item item) {
    	ITEMS.add(item);
    }
    public void PreRegisterBlock(Block block) {
    	BLOCKS.add(block);
    	ITEMS.add(new BaseItemBlock(block, block.getUnlocalizedName()));
    }
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
    	BLOCKS.forEach(e.getRegistry()::register);
        //if (IntegrationFunkyLocomotion.isLoaded()) {
            //MoveFactoryRegisterer.register(blocksToRegister);
        //}
    }
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
    	ITEMS.forEach(e.getRegistry()::register);
    }

	@SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {	
        OBJLoader.INSTANCE.addDomain(Reference.MODID.toLowerCase());
    	
	    ItemRenderRegistry.initItemRenderers();
	    ModFluids.initBlockRendering();
	    ModFluids.initItemRendering();
	    
        ModelLoader.setCustomMeshDefinition(ModItems.BaseFluidCapsule, stack -> ModelDynBucket.LOCATION);
        ModelBakery.registerItemVariants(ModItems.BaseFluidCapsule, ModelDynBucket.LOCATION);
	    
    }
}
