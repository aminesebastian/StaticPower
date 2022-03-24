package theking530.staticpower.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.Research.ResearchBuilder;
import theking530.staticpower.init.ModItems;

public class ResearchProvider extends AbstractResearchProvider {

	public final Research TestResearch = this.register(ResearchBuilder.Create("test", "test").description("this is a test").visualOffset(5, 7).icon(ModItems.AdvancedBlade)
			.preReqs(new ResourceLocation(StaticPower.MOD_ID, "basic_research")).tier1(20));

	public ResearchProvider(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}
}
