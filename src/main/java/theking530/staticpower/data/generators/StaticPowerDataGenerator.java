package theking530.staticpower.data.generators;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import theking530.staticpower.StaticPower;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StaticPowerDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		generator.addProvider(true, new ModRecipeProvider(generator));
		generator.addProvider(true, new ModLootTableProvider(generator));
		generator.addProvider(true, new ModBlockStateProvider(generator, existingFileHelper));
		generator.addProvider(true, new ModItemModelProvider(generator, existingFileHelper));
	}
}