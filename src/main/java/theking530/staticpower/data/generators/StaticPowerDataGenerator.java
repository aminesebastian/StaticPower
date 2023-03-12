package theking530.staticpower.data.generators;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.generators.recipes.AlloyFurnaceRecipeGenerator;
import theking530.staticpower.data.generators.recipes.BlastingRecipeGenerator;
import theking530.staticpower.data.generators.recipes.BottlerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.CarpenterRecipeGenerator;
import theking530.staticpower.data.generators.recipes.CasterRecipeGenerator;
import theking530.staticpower.data.generators.recipes.CauldronRecipeGenerator;
import theking530.staticpower.data.generators.recipes.CondensationRecipeGenerator;
import theking530.staticpower.data.generators.recipes.CraftingRecipeGenerator;
import theking530.staticpower.data.generators.recipes.CrucibleRecipeGenerator;
import theking530.staticpower.data.generators.recipes.EvaporationRecipeGenerator;
import theking530.staticpower.data.generators.recipes.FermenterRecipeGenerator;
import theking530.staticpower.data.generators.recipes.FertilizerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.FluidGeneratorRecipeGenerator;
import theking530.staticpower.data.generators.recipes.FluidInfusionRecipeGenerator;
import theking530.staticpower.data.generators.recipes.FormerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.FusionFurnaceRecipeGenerator;
import theking530.staticpower.data.generators.recipes.GrindingRecipeGenerator;
import theking530.staticpower.data.generators.recipes.HammerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.HydroponicFarmerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.LumberMillRecipeGenerator;
import theking530.staticpower.data.generators.recipes.MixerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.RefineryRecipeGenerator;
import theking530.staticpower.data.generators.recipes.SmeltingRecipeGenerator;
import theking530.staticpower.data.generators.recipes.SmithingRecipeGenerator;
import theking530.staticpower.data.generators.recipes.SmokingRecipeGenerator;
import theking530.staticpower.data.generators.recipes.SolderingRecipeGenerator;
import theking530.staticpower.data.generators.recipes.SqueezerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.TumblerRecipeGenerator;
import theking530.staticpower.data.generators.recipes.TurbineRecipeGenerator;
import theking530.staticpower.data.generators.recipes.VulcanizerRecipeGenerator;
import theking530.staticpower.data.generators.tags.ModBiomeTagProvider;
import theking530.staticpower.data.generators.tags.ModBlockTagProvider;
import theking530.staticpower.data.generators.tags.ModFluidTagProvider;
import theking530.staticpower.data.generators.tags.ModItemTagProvider;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StaticPowerDataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		generator.addProvider(true, new CraftingRecipeGenerator(generator));
		generator.addProvider(true, new GrindingRecipeGenerator(generator));
		generator.addProvider(true, new AlloyFurnaceRecipeGenerator(generator));
		generator.addProvider(true, new HammerRecipeGenerator(generator));
		generator.addProvider(true, new BlastingRecipeGenerator(generator));
		generator.addProvider(true, new BottlerRecipeGenerator(generator));
		generator.addProvider(true, new FertilizerRecipeGenerator(generator));
		generator.addProvider(true, new SmeltingRecipeGenerator(generator));
		generator.addProvider(true, new CauldronRecipeGenerator(generator));
		generator.addProvider(true, new CasterRecipeGenerator(generator));
		generator.addProvider(true, new CondensationRecipeGenerator(generator));
		generator.addProvider(true, new EvaporationRecipeGenerator(generator));
		generator.addProvider(true, new FermenterRecipeGenerator(generator));
		generator.addProvider(true, new FluidGeneratorRecipeGenerator(generator));
		generator.addProvider(true, new RefineryRecipeGenerator(generator));
		generator.addProvider(true, new SmokingRecipeGenerator(generator));
		generator.addProvider(true, new TumblerRecipeGenerator(generator));
		generator.addProvider(true, new CrucibleRecipeGenerator(generator));
		generator.addProvider(true, new HydroponicFarmerRecipeGenerator(generator));
		generator.addProvider(true, new MixerRecipeGenerator(generator));
		generator.addProvider(true, new TurbineRecipeGenerator(generator));
		generator.addProvider(true, new VulcanizerRecipeGenerator(generator));
		generator.addProvider(true, new FormerRecipeGenerator(generator));
		generator.addProvider(true, new SqueezerRecipeGenerator(generator));
		generator.addProvider(true, new FusionFurnaceRecipeGenerator(generator));
		generator.addProvider(true, new FluidInfusionRecipeGenerator(generator));
		generator.addProvider(true, new SolderingRecipeGenerator(generator));
		generator.addProvider(true, new CarpenterRecipeGenerator(generator));
		generator.addProvider(true, new LumberMillRecipeGenerator(generator));
		generator.addProvider(true, new SmithingRecipeGenerator(generator));
		
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