package theking530.staticpower.client;

import java.util.HashSet;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import theking530.staticpower.StaticPower;

public class StaticPowerAdditionalModels {
	public static final HashSet<ResourceLocation> MODELS = new HashSet<ResourceLocation>();

	public static final ResourceLocation DEFAULT_MACHINE_MODEL = registerModel("block/machines/base_machine_model");

	public static final ResourceLocation CABLE_POWER_BASIC_STRAIGHT = registerModel("block/cables/power/basic/straight");
	public static final ResourceLocation CABLE_POWER_BASIC_EXTENSION = registerModel("block/cables/power/basic/extension");
	public static final ResourceLocation CABLE_POWER_BASIC_ATTACHMENT = registerModel("block/cables/power/basic/attachment");

	public static final ResourceLocation CABLE_POWER_ADVANCED_STRAIGHT = registerModel("block/cables/power/advanced/straight");
	public static final ResourceLocation CABLE_POWER_ADVANCED_EXTENSION = registerModel("block/cables/power/advanced/extension");
	public static final ResourceLocation CABLE_POWER_ADVANCED_ATTACHMENT = registerModel("block/cables/power/advanced/attachment");

	public static final ResourceLocation CABLE_POWER_STATIC_STRAIGHT = registerModel("block/cables/power/static/straight");
	public static final ResourceLocation CABLE_POWER_STATIC_EXTENSION = registerModel("block/cables/power/static/extension");
	public static final ResourceLocation CABLE_POWER_STATIC_ATTACHMENT = registerModel("block/cables/power/static/attachment");

	public static final ResourceLocation CABLE_POWER_ENERGIZED_STRAIGHT = registerModel("block/cables/power/energized/straight");
	public static final ResourceLocation CABLE_POWER_ENERGIZED_EXTENSION = registerModel("block/cables/power/energized/extension");
	public static final ResourceLocation CABLE_POWER_ENERGIZED_ATTACHMENT = registerModel("block/cables/power/energized/attachment");

	public static final ResourceLocation CABLE_POWER_LUMUM_STRAIGHT = registerModel("block/cables/power/lumum/straight");
	public static final ResourceLocation CABLE_POWER_LUMUM_EXTENSION = registerModel("block/cables/power/lumum/extension");
	public static final ResourceLocation CABLE_POWER_LUMUM_ATTACHMENT = registerModel("block/cables/power/lumum/attachment");

	public static final ResourceLocation CABLE_POWER_CREATIVE_STRAIGHT = registerModel("block/cables/power/creative/straight");
	public static final ResourceLocation CABLE_POWER_CREATIVE_EXTENSION = registerModel("block/cables/power/creative/extension");
	public static final ResourceLocation CABLE_POWER_CREATIVE_ATTACHMENT = registerModel("block/cables/power/creative/attachment");

	public static final ResourceLocation CABLE_ITEM_BASIC_STRAIGHT = registerModel("block/cables/item/basic/straight");
	public static final ResourceLocation CABLE_ITEM_BASIC_EXTENSION = registerModel("block/cables/item/basic/extension");

	public static final ResourceLocation CABLE_ITEM_ADVANCED_STRAIGHT = registerModel("block/cables/item/advanced/straight");
	public static final ResourceLocation CABLE_ITEM_ADVANCED_EXTENSION = registerModel("block/cables/item/advanced/extension");

	public static final ResourceLocation CABLE_ITEM_STATIC_STRAIGHT = registerModel("block/cables/item/static/straight");
	public static final ResourceLocation CABLE_ITEM_STATIC_EXTENSION = registerModel("block/cables/item/static/extension");

	public static final ResourceLocation CABLE_ITEM_ENERGIZED_STRAIGHT = registerModel("block/cables/item/energized/straight");
	public static final ResourceLocation CABLE_ITEM_ENERGIZED_EXTENSION = registerModel("block/cables/item/energized/extension");

	public static final ResourceLocation CABLE_ITEM_LUMUM_STRAIGHT = registerModel("block/cables/item/lumum/straight");
	public static final ResourceLocation CABLE_ITEM_LUMUM_EXTENSION = registerModel("block/cables/item/lumum/extension");

	public static final ResourceLocation CABLE_ITEM_CREATIVE_STRAIGHT = registerModel("block/cables/item/creative/straight");
	public static final ResourceLocation CABLE_ITEM_CREATIVE_EXTENSION = registerModel("block/cables/item/creative/extension");

	public static final ResourceLocation CABLE_ITEM_DEFAULT_ATTACHMENT = registerModel("block/cables/item/attachment");

	public static final ResourceLocation CABLE_DIGISTORE_STRAIGHT = registerModel("block/cables/digistore/straight");
	public static final ResourceLocation CABLE_DIGISTORE_EXTENSION = registerModel("block/cables/digistore/extension");
	public static final ResourceLocation CABLE_DIGISTORE_ATTACHMENT = registerModel("block/cables/digistore/attachment");

	public static final ResourceLocation CABLE_FLUID_BASIC_STRAIGHT = registerModel("block/cables/fluid/basic/straight");
	public static final ResourceLocation CABLE_FLUID_BASIC_EXTENSION = registerModel("block/cables/fluid/basic/extension");
	public static final ResourceLocation CABLE_FLUID_BASIC_ATTACHMENT = registerModel("block/cables/fluid/basic/attachment");

	public static final ResourceLocation CABLE_FLUID_ADVANCED_STRAIGHT = registerModel("block/cables/fluid/advanced/straight");
	public static final ResourceLocation CABLE_FLUID_ADVANCED_EXTENSION = registerModel("block/cables/fluid/advanced/extension");
	public static final ResourceLocation CABLE_FLUID_ADVANCED_ATTACHMENT = registerModel("block/cables/fluid/advanced/attachment");

	public static final ResourceLocation CABLE_FLUID_STATIC_STRAIGHT = registerModel("block/cables/fluid/static/straight");
	public static final ResourceLocation CABLE_FLUID_STATIC_EXTENSION = registerModel("block/cables/fluid/static/extension");
	public static final ResourceLocation CABLE_FLUID_STATIC_ATTACHMENT = registerModel("block/cables/fluid/static/attachment");

	public static final ResourceLocation CABLE_FLUID_ENERGIZED_STRAIGHT = registerModel("block/cables/fluid/energized/straight");
	public static final ResourceLocation CABLE_FLUID_ENERGIZED_EXTENSION = registerModel("block/cables/fluid/energized/extension");
	public static final ResourceLocation CABLE_FLUID_ENERGIZED_ATTACHMENT = registerModel("block/cables/fluid/energized/attachment");

	public static final ResourceLocation CABLE_FLUID_LUMUM_STRAIGHT = registerModel("block/cables/fluid/lumum/straight");
	public static final ResourceLocation CABLE_FLUID_LUMUM_EXTENSION = registerModel("block/cables/fluid/lumum/extension");
	public static final ResourceLocation CABLE_FLUID_LUMUM_ATTACHMENT = registerModel("block/cables/fluid/lumum/attachment");

	public static final ResourceLocation CABLE_FLUID_CREATIVE_STRAIGHT = registerModel("block/cables/fluid/creative/straight");
	public static final ResourceLocation CABLE_FLUID_CREATIVE_EXTENSION = registerModel("block/cables/fluid/creative/extension");
	public static final ResourceLocation CABLE_FLUID_CREATIVE_ATTACHMENT = registerModel("block/cables/fluid/creative/attachment");

	public static final ResourceLocation CABLE_FLUID_DEFAULT_ATTACHMENT = registerModel("block/cables/fluid/attachment");

	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_BASIC_STRAIGHT = registerModel("block/cables/industrial_fluid/basic/straight");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_BASIC_EXTENSION = registerModel("block/cables/industrial_fluid/basic/extension");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_BASIC_ATTACHMENT = registerModel("block/cables/industrial_fluid/basic/attachment");

	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_ADVANCED_STRAIGHT = registerModel("block/cables/industrial_fluid/advanced/straight");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_ADVANCED_EXTENSION = registerModel("block/cables/industrial_fluid/advanced/extension");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_ADVANCED_ATTACHMENT = registerModel("block/cables/industrial_fluid/advanced/attachment");

	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_STATIC_STRAIGHT = registerModel("block/cables/industrial_fluid/static/straight");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_STATIC_EXTENSION = registerModel("block/cables/industrial_fluid/static/extension");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_STATIC_ATTACHMENT = registerModel("block/cables/industrial_fluid/static/attachment");

	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_ENERGIZED_STRAIGHT = registerModel("block/cables/industrial_fluid/energized/straight");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_ENERGIZED_EXTENSION = registerModel("block/cables/industrial_fluid/energized/extension");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_ENERGIZED_ATTACHMENT = registerModel("block/cables/industrial_fluid/energized/attachment");

	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_LUMUM_STRAIGHT = registerModel("block/cables/industrial_fluid/lumum/straight");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_LUMUM_EXTENSION = registerModel("block/cables/industrial_fluid/lumum/extension");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_LUMUM_ATTACHMENT = registerModel("block/cables/industrial_fluid/lumum/attachment");

	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_CREATIVE_STRAIGHT = registerModel("block/cables/industrial_fluid/creative/straight");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_CREATIVE_EXTENSION = registerModel("block/cables/industrial_fluid/creative/extension");
	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_CREATIVE_ATTACHMENT = registerModel("block/cables/industrial_fluid/creative/attachment");

	public static final ResourceLocation CABLE_FLUID_INDUSTRIAL_DEFAULT_ATTACHMENT = registerModel("block/cables/industrial_fluid/attachment");

	public static final ResourceLocation CABLE_HEAT_COPPER_STRAIGHT = registerModel("block/cables/heat/copper/straight");
	public static final ResourceLocation CABLE_HEAT_COPPER_EXTENSION = registerModel("block/cables/heat/copper/extension");
	public static final ResourceLocation CABLE_HEAT_COPPER_ATTACHMENT = registerModel("block/cables/heat/copper/attachment");

	public static final ResourceLocation CABLE_HEAT_TIN_STRAIGHT = registerModel("block/cables/heat/tin/straight");
	public static final ResourceLocation CABLE_HEAT_TIN_EXTENSION = registerModel("block/cables/heat/tin/extension");
	public static final ResourceLocation CABLE_HEAT_TIN_ATTACHMENT = registerModel("block/cables/heat/tin/attachment");

	public static final ResourceLocation CABLE_HEAT_SILVER_STRAIGHT = registerModel("block/cables/heat/silver/straight");
	public static final ResourceLocation CABLE_HEAT_SILVER_EXTENSION = registerModel("block/cables/heat/silver/extension");
	public static final ResourceLocation CABLE_HEAT_SILVER_ATTACHMENT = registerModel("block/cables/heat/silver/attachment");

	public static final ResourceLocation CABLE_HEAT_GOLD_STRAIGHT = registerModel("block/cables/heat/gold/straight");
	public static final ResourceLocation CABLE_HEAT_GOLD_EXTENSION = registerModel("block/cables/heat/gold/extension");
	public static final ResourceLocation CABLE_HEAT_GOLD_ATTACHMENT = registerModel("block/cables/heat/gold/attachment");

	public static final ResourceLocation CABLE_HEAT_PLATINUM_STRAIGHT = registerModel("block/cables/heat/platinum/straight");
	public static final ResourceLocation CABLE_HEAT_PLATINUM_EXTENSION = registerModel("block/cables/heat/platinum/extension");
	public static final ResourceLocation CABLE_HEAT_PLATINUM_ATTACHMENT = registerModel("block/cables/heat/platinum/attachment");

	public static final ResourceLocation CABLE_HEAT_ALUMINIUM_STRAIGHT = registerModel("block/cables/heat/aluminium/straight");
	public static final ResourceLocation CABLE_HEAT_ALUMINIUM_EXTENSION = registerModel("block/cables/heat/aluminium/extension");
	public static final ResourceLocation CABLE_HEAT_ALUMINIUM_ATTACHMENT = registerModel("block/cables/heat/aluminium/attachment");

	public static final ResourceLocation CABLE_SCAFFOLD_STRAIGHT = registerModel("block/cables/scaffold/straight");
	public static final ResourceLocation CABLE_SCAFFOLD_EXTENSION = registerModel("block/cables/scaffold/extension");
	public static final ResourceLocation CABLE_SCAFFOLD_ATTACHMENT = registerModel("block/cables/scaffold/attachment");

	public static final ResourceLocation CABLE_BASIC_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/basic_extractor");
	public static final ResourceLocation CABLE_ADVANCED_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/advanced_extractor");
	public static final ResourceLocation CABLE_STATIC_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/static_extractor");
	public static final ResourceLocation CABLE_ENERGIZED_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/energized_extractor");
	public static final ResourceLocation CABLE_LUMUM_EXTRACTOR_ATTACHMENT = registerModel("block/cables/attachments/lumum_extractor");

	public static final ResourceLocation CABLE_BASIC_FILTER_ATTACHMENT = registerModel("block/cables/attachments/basic_filter");
	public static final ResourceLocation CABLE_ADVANCED_FILTER_ATTACHMENT = registerModel("block/cables/attachments/advanced_filter");
	public static final ResourceLocation CABLE_STATIC_FILTER_ATTACHMENT = registerModel("block/cables/attachments/static_filter");
	public static final ResourceLocation CABLE_ENERGIZED_FILTER_ATTACHMENT = registerModel("block/cables/attachments/energized_filter");
	public static final ResourceLocation CABLE_LUMUM_FILTER_ATTACHMENT = registerModel("block/cables/attachments/lumum_filter");

	public static final ResourceLocation CABLE_BASIC_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/basic_retriever");
	public static final ResourceLocation CABLE_ADVANCED_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/advanced_retriever");
	public static final ResourceLocation CABLE_STATIC_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/static_retriever");
	public static final ResourceLocation CABLE_ENERGIZED_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/energized_retriever");
	public static final ResourceLocation CABLE_LUMUM_RETRIEVER_ATTACHMENT = registerModel("block/cables/attachments/lumum_retriever");

	public static final ResourceLocation CABLE_DIGISTORE_TERMINAL_ATTACHMENT_ON = registerModel("block/cables/attachments/digistore_terminal_attachment_on");
	public static final ResourceLocation CABLE_DIGISTORE_TERMINAL_ATTACHMENT = registerModel("block/cables/attachments/digistore_terminal_attachment");

	public static final ResourceLocation CABLE_DIGISTORE_CRAFTING_TERMINAL_ATTACHMENT_ON = registerModel("block/cables/attachments/digistore_crafting_terminal_attachment_on");
	public static final ResourceLocation CABLE_DIGISTORE_CRAFTING_TERMINAL_ATTACHMENT = registerModel("block/cables/attachments/digistore_crafting_terminal_attachment");

	public static final ResourceLocation CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT_ON = registerModel("block/cables/attachments/digistore_pattern_encoder_attachment_on");
	public static final ResourceLocation CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT = registerModel("block/cables/attachments/digistore_pattern_encoder_attachment");

	public static final ResourceLocation CABLE_DIGISTORE_SCREEN_ATTACHMENT_ON = registerModel("block/cables/attachments/digistore_screen_on");
	public static final ResourceLocation CABLE_DIGISTORE_SCREEN_ATTACHMENT = registerModel("block/cables/attachments/digistore_screen_off");

	public static final ResourceLocation CABLE_DIGISTORE_LIGHT_ATTACHMENT_ON = registerModel("block/cables/attachments/digistore_light_on");
	public static final ResourceLocation CABLE_DIGISTORE_LIGHT_ATTACHMENT = registerModel("block/cables/attachments/digistore_light_off");

	public static final ResourceLocation CABLE_DIGISTORE_EXPORTER_ATTACHMENT = registerModel("block/cables/attachments/digistore_exporter");
	public static final ResourceLocation CABLE_DIGISTORE_IMPORTER_ATTACHMENT = registerModel("block/cables/attachments/digistore_importer");
	public static final ResourceLocation CABLE_DIGISTORE_IO_BUS_ATTACHMENT = registerModel("block/cables/attachments/digistore_io_bus");
	public static final ResourceLocation CABLE_DIGISTORE_REGULATOR_ATTACHMENT = registerModel("block/cables/attachments/digistore_regulator");
	public static final ResourceLocation CABLE_DIGISTORE_CRAFTING_INTERFACE_ATTACHMENT = registerModel("block/cables/attachments/digistore_crafting_interface");

	public static final ResourceLocation BASIC_DIGISTORE_CARD = registerModel("block/machines/digistore_card_basic");
	public static final ResourceLocation ADVANCVED_DIGISTORE_CARD = registerModel("block/machines/digistore_card_advanced");
	public static final ResourceLocation STATIC_DIGISTORE_CARD = registerModel("block/machines/digistore_card_static");
	public static final ResourceLocation ENERGIZED_DIGISTORE_CARD = registerModel("block/machines/digistore_card_energized");
	public static final ResourceLocation LUMUM_DIGISTORE_CARD = registerModel("block/machines/digistore_card_lumum");
	public static final ResourceLocation CREATIVE_DIGISTORE_CARD = registerModel("block/machines/digistore_card_creative");

	public static final ResourceLocation BASIC_DIGISTORE_SINGULAR_CARD = registerModel("block/machines/digistore_card_singular_basic");
	public static final ResourceLocation ADVANCVED_DIGISTORE_SINGULAR_CARD = registerModel("block/machines/digistore_card_singular_advanced");
	public static final ResourceLocation STATIC_DIGISTORE_SINGULAR_CARD = registerModel("block/machines/digistore_card_singular_static");
	public static final ResourceLocation ENERGIZED_DIGISTORE_SINGULAR_CARD = registerModel("block/machines/digistore_card_singular_energized");
	public static final ResourceLocation LUMUM_DIGISTORE_SINGULAR_CARD = registerModel("block/machines/digistore_card_singular_lumum");
	public static final ResourceLocation CREATIVE_DIGISTORE_SINGULAR_CARD = registerModel("block/machines/digistore_card_singular_creative");
	public static final ResourceLocation DIGISTORE_SINGULAR_CARD_BAR = registerModel("block/machines/digistore_singular_card_bar");
	public static final ResourceLocation DIGISTORE_SINGULAR_CARD_BAR_FULL = registerModel("block/machines/digistore_singular_card_bar_full");

	public static final ResourceLocation DIGISTORE_PATTERN_CARD_ENCODED = registerModel("item/digistore_pattern_card_encoded");

	public static final ResourceLocation DRILL_BIT_FORTUNE = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_fortune");
	public static final ResourceLocation DRILL_BIT_HASTE = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_haste");
	public static final ResourceLocation DRILL_BIT_HARDENED_DIAMOND = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_hardened_diamond");
	public static final ResourceLocation DRILL_BIT_HARDENED_EMERALD = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_hardened_emerald");
	public static final ResourceLocation DRILL_BIT_HARDENED_RUBY = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_hardened_ruby");
	public static final ResourceLocation DRILL_BIT_HARDENED_SAPPHIRE = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_hardened_sapphire");
	public static final ResourceLocation DRILL_BIT_GRINDING = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_grinding");
	public static final ResourceLocation DRILL_BIT_SILK_TOUCH = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_silk_touch");
	public static final ResourceLocation DRILL_BIT_SMELTING = registerModel("item/attribute_layers/drill_bits/drill_bit_layer_smelting");

	public static final ResourceLocation BLADE_HASTE = registerModel("item/attribute_layers/blades/blade_layer_haste");
	public static final ResourceLocation BLADE_SMELTING = registerModel("item/attribute_layers/blades/blade_layer_smelting");

	public static final ResourceLocation CHAINSAW_BLADE_HARDENED_DIAMOND = registerModel("item/attribute_layers/chainsaw_blades/chainsaw_blade_layer_hardened_diamond");
	public static final ResourceLocation CHAINSAW_BLADE_HARDENED_EMERALD = registerModel("item/attribute_layers/chainsaw_blades/chainsaw_blade_layer_hardened_emerald");
	public static final ResourceLocation CHAINSAW_BLADE_HARDENED_RUBY = registerModel("item/attribute_layers/chainsaw_blades/chainsaw_blade_layer_hardened_ruby");
	public static final ResourceLocation CHAINSAW_BLADE_HARDENED_SAPPHIRE = registerModel("item/attribute_layers/chainsaw_blades/chainsaw_blade_layer_hardened_sapphire");

	public static final ResourceLocation TURBINE_BLADES_WOOD = registerModel("block/machines/turbine_blades/turbine_blades_wood");
	public static final ResourceLocation TURBINE_BLADES_BASIC = registerModel("block/machines/turbine_blades/turbine_blades_basic");
	public static final ResourceLocation TURBINE_BLADES_ADVANCED = registerModel("block/machines/turbine_blades/turbine_blades_advanced");
	public static final ResourceLocation TURBINE_BLADES_STATIC = registerModel("block/machines/turbine_blades/turbine_blades_static");
	public static final ResourceLocation TURBINE_BLADES_ENERGIZED = registerModel("block/machines/turbine_blades/turbine_blades_energized");
	public static final ResourceLocation TURBINE_BLADES_LUMUM = registerModel("block/machines/turbine_blades/turbine_blades_lumum");
	public static final ResourceLocation TURBINE_BLADES_CREATIVE = registerModel("block/machines/turbine_blades/turbine_blades_creative");

	@OnlyIn(Dist.CLIENT)
	public static void regsiterModels() {
		for (ResourceLocation model : MODELS) {
			ModelLoader.addSpecialModel(model);
			StaticPower.LOGGER.info(String.format("Loading additional model: %1$s.", model.toString()));
		}
	}

	private static ResourceLocation registerModel(String path) {
		ResourceLocation sprite = new ResourceLocation(StaticPower.MOD_ID, path);
		MODELS.add(sprite);
		return sprite;
	}
}
