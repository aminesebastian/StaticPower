package theking530.staticpower.data.generators.recipes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import theking530.staticcore.attributes.AttributeModifiers;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe.RecipeAttributeWrapper;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModAttributes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;

public class SmithingRecipeGenerator extends SPRecipeProvider<AutoSmithRecipe> {
	public SmithingRecipeGenerator(DataGenerator dataGenerator) {
		super("smithing", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("mining_level_promotion", StaticPowerIngredient.of(Tags.Items.GEMS_DIAMOND, 2),
				RecipeAttributeWrapper.create(ModAttributes.Promoted.get(), AttributeModifiers.Boolean.get(), true));

		addRecipe("grinding", StaticPowerIngredient.of(ModMaterials.STATIC_METAL.get(MaterialTypes.GEAR_BOX).getItemTag()),
				RecipeAttributeWrapper.create(ModAttributes.Grinding.get(), AttributeModifiers.Boolean.get(), true));

		addRecipe("hardened_diamond", StaticPowerIngredient.of(ModMaterials.DIAMOND.get(MaterialTypes.DUST).getItemTag()),
				RecipeAttributeWrapper.create(ModAttributes.DiamondHardened.get(), AttributeModifiers.Boolean.get(), true));
		addRecipe("hardened_emerald", StaticPowerIngredient.of(ModMaterials.EMERALD.get(MaterialTypes.DUST).getItemTag()),
				RecipeAttributeWrapper.create(ModAttributes.EmeraldHardened.get(), AttributeModifiers.Boolean.get(), true));
		addRecipe("hardened_ruby", StaticPowerIngredient.of(ModMaterials.RUBY.get(MaterialTypes.DUST).getItemTag()),
				RecipeAttributeWrapper.create(ModAttributes.RubyHardened.get(), AttributeModifiers.Boolean.get(), true));
		addRecipe("hardened_sapphire", StaticPowerIngredient.of(ModMaterials.SAPPHIRE.get(MaterialTypes.DUST).getItemTag()),
				RecipeAttributeWrapper.create(ModAttributes.SapphireHardened.get(), AttributeModifiers.Boolean.get(), true));

		addRecipe("fortune_lapis", StaticPowerIngredient.of(Tags.Items.GEMS_LAPIS),
				RecipeAttributeWrapper.create(ModAttributes.Fortune.get(), AttributeModifiers.NumberAdditive.get(), 1));
		addRecipe("fortune_lapis_block", StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS),
				RecipeAttributeWrapper.create(ModAttributes.Fortune.get(), AttributeModifiers.NumberAdditive.get(), 9));

		addRecipe("haste_glowstone", StaticPowerIngredient.of(Tags.Items.DUSTS_GLOWSTONE),
				RecipeAttributeWrapper.create(ModAttributes.Haste.get(), AttributeModifiers.NumberAdditive.get(), 1));
		addRecipe("haste_glowston_block", StaticPowerIngredient.of(Blocks.GLOWSTONE),
				RecipeAttributeWrapper.create(ModAttributes.Haste.get(), AttributeModifiers.NumberAdditive.get(), 4));

		addRecipe("silk_touch_honey_bottle", StaticPowerIngredient.of(Items.HONEY_BOTTLE),
				RecipeAttributeWrapper.create(ModAttributes.SilkTouch.get(), AttributeModifiers.Boolean.get(), true));
		addRecipe("smelting_potable_core", StaticPowerIngredient.of(ModItems.PortableSmeltingCore.get(), 3),
				RecipeAttributeWrapper.create(ModAttributes.Smelting.get(), AttributeModifiers.Boolean.get(), true));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient modifierMaterial, RecipeAttributeWrapper<?>... modifiers) {
		List<RecipeAttributeWrapper<?>> modifiersList = Arrays.asList(modifiers);
		addRecipe(nameOverride, StaticPowerIngredient.EMPTY, modifierMaterial, FluidIngredient.EMPTY, modifiersList, 0, null);
	}

	protected void addRecipe(String nameOverride, @Nullable StaticPowerIngredient smithTarget, StaticPowerIngredient modifierMaterial, FluidIngredient modifierFluid,
			int repairAmount, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, smithTarget, modifierMaterial, modifierFluid, Collections.emptyList(), repairAmount, processing);
	}

	protected void addRecipe(String nameOverride, @Nullable StaticPowerIngredient smithTarget, StaticPowerIngredient modifierMaterial, FluidIngredient modifierFluid,
			List<RecipeAttributeWrapper<?>> modifiers, int repairAmount, MachineRecipeProcessingSection processing) {
		addRecipe(nameOverride, SPRecipeBuilder.create(new AutoSmithRecipe(null, smithTarget, modifierMaterial, modifierFluid, modifiers, repairAmount, processing)));
	}

}
