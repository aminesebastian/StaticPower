package theking530.staticpower.data.generators;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.generators.tags.ModBiomeTagProvider;
import theking530.staticpower.data.generators.tags.ModBlockTagProvider;
import theking530.staticpower.data.generators.tags.ModItemTagProvider;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StaticPowerDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		generator.addProvider(true, new ModCraftingRecipeProvider(generator));
		generator.addProvider(true, new ModLootTableProvider(generator));
		generator.addProvider(true, new ModBlockStateProvider(generator, existingFileHelper));
		generator.addProvider(true, new ModItemModelProvider(generator, existingFileHelper));
		generator.addProvider(true, new ModAdditionalModelProvider(generator, existingFileHelper));

		ModBlockTagProvider blockTags = new ModBlockTagProvider(generator, existingFileHelper);
		generator.addProvider(true, blockTags);
		generator.addProvider(true, new ModFluidTagProvider(generator, existingFileHelper));
		generator.addProvider(true, new ModItemTagProvider(generator, blockTags, existingFileHelper));
		generator.addProvider(true, new ModBiomeTagProvider(generator, existingFileHelper));
	}
}