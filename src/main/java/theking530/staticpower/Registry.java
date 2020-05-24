package theking530.staticpower;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.IForgeRegistryEntry;
import theking530.staticpower.blocks.BaseItemBlock;
import theking530.staticpower.blocks.IItemBlockProvider;
import theking530.staticpower.client.ItemRenderRegistry;
import theking530.staticpower.client.model.fluidcapsule.ModelFluidCapsule.LoaderFluidCapsule;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.items.MultiItem;
import theking530.staticpower.utilities.Reference;
import theking530.staticpower.utilities.SidePicker.Side;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Registry {

	public static List<Item> ITEMS = new LinkedList<>();
	public List<Block> BLOCKS = new LinkedList<>();
	public static List<MultiItem> MULTI_ITEM = new LinkedList<MultiItem>();


    public void PreRegisterItem(Item item) {
    	ITEMS.add(item);
    	if(item instanceof MultiItem) {
        	ItemRenderRegistry.addMultiItem((MultiItem)item);
        	MULTI_ITEM.add((MultiItem)item);
    	}else{
        	ItemRenderRegistry.addItem(item);
    	}
    }
    public void PreRegisterBlock(Block block) {
    	BLOCKS.add(block);
    	if(block instanceof IItemBlockProvider) {
    		IItemBlockProvider provider = (IItemBlockProvider)block;
    		if(provider.getItemBlock() != null) {
            	ITEMS.add(provider.getItemBlock());	
    		}
    	}else{
        	ITEMS.add(new BaseItemBlock(block, block.getRegistryName()));	
    	}
    	if(!(block instanceof IFluidBlock)) {
        	ItemRenderRegistry.addBlock(block);
    	}
    }
    
    
    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
    	ITEMS.forEach(event.getRegistry()::register);
    	event.getRegistry().registerAll((Item[]) ITEMS.toArray());
    	for(MultiItem item : MULTI_ITEM) {
    		item.registerOreDictionaryEntries();
    	}
    }
    public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final String name) {
    	return setup(entry, new ResourceLocation(Reference.MOD_ID, name));
    }

    public static <T extends IForgeRegistryEntry<T>> T setup(final T entry, final ResourceLocation registryName) {
    	entry.setRegistryName(registryName);
    	return entry;
    }
    
	@OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTextureStitchedPre(TextureStitchEvent.Pre e) {
    	LoaderFluidCapsule.INSTANCE.register(e.getMap());
    }
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
    	BLOCKS.forEach(e.getRegistry()::register);
    }

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {	
		//OBJLoader.INSTANCE.add(Reference.MOD_ID);
	    ItemRenderRegistry.initItemRenderers();
	    ModFluids.initBlockRendering();
	    ModFluids.initItemRendering();	    
    }
}
