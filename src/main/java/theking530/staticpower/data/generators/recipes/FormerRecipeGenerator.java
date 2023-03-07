package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class FormerRecipeGenerator extends SPRecipeProvider<FormerRecipe> {

	public FormerRecipeGenerator(DataGenerator dataGenerator) {
		super("forming", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (!material.has(MaterialTypes.INGOT)) {
				continue;
			}

			if (material.has(MaterialTypes.NUGGET)) {
				addRecipe("nuggets/" + material.getName(), StaticPowerIngredient.of(ModItems.MoldNugget.get()), StaticPowerIngredient.of(material.get(MaterialTypes.INGOT).get()),
						StaticPowerOutputItem.of(material.get(MaterialTypes.NUGGET).get(), 9));
			}
			if (material.has(MaterialTypes.GEAR)) {
				addRecipe("gears/" + material.getName(), StaticPowerIngredient.of(ModItems.MoldGear.get()), StaticPowerIngredient.of(material.get(MaterialTypes.INGOT).get(), 2),
						StaticPowerOutputItem.of(material.get(MaterialTypes.GEAR).get()));
			}
			if (material.has(MaterialTypes.ROD)) {
				addRecipe("rods/" + material.getName(), StaticPowerIngredient.of(ModItems.MoldRod.get()), StaticPowerIngredient.of(material.get(MaterialTypes.INGOT).get()),
						StaticPowerOutputItem.of(material.get(MaterialTypes.ROD).get()));
			}
			if (material.has(MaterialTypes.PLATE)) {
				addRecipe("plates/" + material.getName(), StaticPowerIngredient.of(ModItems.MoldPlate.get()), StaticPowerIngredient.of(material.get(MaterialTypes.INGOT).get()),
						StaticPowerOutputItem.of(material.get(MaterialTypes.PLATE).get()));
			}
			if (material.has(MaterialTypes.WIRE)) {
				addRecipe("wires/" + material.getName(), StaticPowerIngredient.of(ModItems.MoldPlate.get()), StaticPowerIngredient.of(material.get(MaterialTypes.INGOT).get()),
						StaticPowerOutputItem.of(material.get(MaterialTypes.WIRE).get(), 6));
			}
		}
		
		addRecipe("blades/iron", StaticPowerIngredient.of(ModItems.MoldBlade.get()), StaticPowerIngredient.of(ModMaterials.IRON.get(MaterialTypes.INGOT).get(), 5),
				StaticPowerOutputItem.of(ModItems.IronBlade.get()));
		addRecipe("blades/bronze", StaticPowerIngredient.of(ModItems.MoldBlade.get()), StaticPowerIngredient.of(ModMaterials.BRONZE.get(MaterialTypes.INGOT).get(), 5),
				StaticPowerOutputItem.of(ModItems.BronzeBlade.get()));
		addRecipe("blades/advanced", StaticPowerIngredient.of(ModItems.MoldBlade.get()), StaticPowerIngredient.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).get(), 5),
				StaticPowerOutputItem.of(ModItems.AdvancedBlade.get()));
		addRecipe("blades/static", StaticPowerIngredient.of(ModItems.MoldBlade.get()), StaticPowerIngredient.of(ModMaterials.STATIC_METAL.get(MaterialTypes.INGOT).get(), 5),
				StaticPowerOutputItem.of(ModItems.StaticBlade.get()));
		addRecipe("blades/energized", StaticPowerIngredient.of(ModItems.MoldBlade.get()),
				StaticPowerIngredient.of(ModMaterials.ENERGIZED_METAL.get(MaterialTypes.INGOT).get(), 5), StaticPowerOutputItem.of(ModItems.EnergizedBlade.get()));
		addRecipe("blades/lumum", StaticPowerIngredient.of(ModItems.MoldBlade.get()), StaticPowerIngredient.of(ModMaterials.LUMUM_METAL.get(MaterialTypes.INGOT).get(), 5),
				StaticPowerOutputItem.of(ModItems.LumumBlade.get()));
		addRecipe("blades/tungsten", StaticPowerIngredient.of(ModItems.MoldBlade.get()), StaticPowerIngredient.of(ModMaterials.TUNGSTEN.get(MaterialTypes.INGOT).get(), 5),
				StaticPowerOutputItem.of(ModItems.TungstenBlade.get()));

		addRecipe("rubber_sheet", StaticPowerIngredient.of(ModItems.MoldPlate.get()), StaticPowerIngredient.of(ModItemTags.RUBBER),
				StaticPowerOutputItem.of(ModItems.RubberSheet.get()));

		addRecipe("blank_mold", StaticPowerIngredient.of(ModMaterials.TUNGSTEN.get(MaterialTypes.PLATE).getItemTag()),
				StaticPowerIngredient.of(ModMaterials.IRON.get(MaterialTypes.PLATE).getItemTag()), StaticPowerOutputItem.of(ModItems.MoldBlank.get()));

		addRecipe("mold_drill_bit", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(ModItemTags.MINING_DRILL_BIT),
				StaticPowerOutputItem.of(ModItems.MoldDrillBit.get()));
		addRecipe("mold_blade", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(ModItemTags.BLADE),
				StaticPowerOutputItem.of(ModItems.MoldBlade.get()));
		addRecipe("mold_block", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(Tags.Items.COBBLESTONE),
				StaticPowerOutputItem.of(ModItems.MoldBlock.get()));
		addRecipe("mold_wire", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(ModItemTags.WIRES), StaticPowerOutputItem.of(ModItems.MoldWire.get()));
		addRecipe("mold_rod", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(Tags.Items.RODS), StaticPowerOutputItem.of(ModItems.MoldRod.get()));
		addRecipe("mold_plate", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(ModItemTags.PLATES),
				StaticPowerOutputItem.of(ModItems.MoldPlate.get()));
		addRecipe("mold_nugget", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(Tags.Items.NUGGETS),
				StaticPowerOutputItem.of(ModItems.MoldNugget.get()));
		addRecipe("mold_ingot", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(Tags.Items.INGOTS),
				StaticPowerOutputItem.of(ModItems.MoldIngot.get()));
		addRecipe("mold_gear", StaticPowerIngredient.of(ModItems.MoldBlank.get()), StaticPowerIngredient.of(ModItemTags.GEARS), StaticPowerOutputItem.of(ModItems.MoldGear.get()));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient mold, StaticPowerIngredient input, StaticPowerOutputItem output) {
		addRecipe(nameOverride, input, mold, output, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient mold, StaticPowerIngredient input, StaticPowerOutputItem output, int processingTime) {
		addRecipe(nameOverride, input, mold, output, MachineRecipeProcessingSection.hardcoded(processingTime, FormerRecipe.DEFAULT_POWER_COST, 0, 0));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient mold, StaticPowerIngredient input, StaticPowerOutputItem output,
			MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SPRecipeBuilder.create(new FormerRecipe(null, input, mold, output, processing)));
	}
}
