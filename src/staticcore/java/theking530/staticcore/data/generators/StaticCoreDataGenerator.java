package theking530.staticcore.data.generators;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import theking530.staticcore.StaticCore;

@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StaticCoreDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		StaticCoreBlockTagProvider blockTags = new StaticCoreBlockTagProvider(generator, existingFileHelper);
		generator.addProvider(true, blockTags);
		generator.addProvider(true, new StaticCoreItemModelProvider(generator, existingFileHelper));	
		generator.addProvider(true, new StaticCoreItemTagProvider(generator, blockTags, existingFileHelper));
	}
}
