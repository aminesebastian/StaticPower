package theking530.staticpower.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.StaticPowerTiers;

public class StaticPowerAdditionalModels {
	public static final HashSet<ResourceLocation> MODELS = new HashSet<ResourceLocation>();

	public record CableModelSet(ResourceLocation straight, ResourceLocation extension, ResourceLocation attachment) {
	}

	@SuppressWarnings("unchecked")
	private static final Tuple<ResourceLocation, String>[] CABLE_TIERS = new Tuple[] { new Tuple<ResourceLocation, String>(StaticPowerTiers.BASIC, "basic"),
			new Tuple<ResourceLocation, String>(StaticPowerTiers.ADVANCED, "advanced"), new Tuple<ResourceLocation, String>(StaticPowerTiers.STATIC, "static"),
			new Tuple<ResourceLocation, String>(StaticPowerTiers.ENERGIZED, "energized"), new Tuple<ResourceLocation, String>(StaticPowerTiers.LUMUM, "lumum"),
			new Tuple<ResourceLocation, String>(StaticPowerTiers.CREATIVE, "creative") };

	public static final Map<ResourceLocation, CableModelSet> POWER_CABLES = new HashMap<>();
	public static final Map<ResourceLocation, CableModelSet> INSULATED_POWER_CABLES = new HashMap<>();
	public static final Map<ResourceLocation, CableModelSet> INDUSTRIAL_POWER_CABLES = new HashMap<>();
	public static final Map<ResourceLocation, CableModelSet> FLUID_CABLES = new HashMap<>();
	public static final Map<ResourceLocation, CableModelSet> INDUSTRIAL_FLUID_CABLES = new HashMap<>();
	public static final Map<ResourceLocation, CableModelSet> ITEM_CABLES = new HashMap<>();

	static {
		for (Tuple<ResourceLocation, String> tier : CABLE_TIERS) {
			POWER_CABLES.put(tier.getA(), registerCable("block/cable_power_" + tier.getB()));
			INSULATED_POWER_CABLES.put(tier.getA(), registerCable("block/cable_insulated_power_" + tier.getB()));
			INDUSTRIAL_POWER_CABLES.put(tier.getA(), registerCable("block/cable_industrial_power_" + tier.getB()));
			FLUID_CABLES.put(tier.getA(), registerCable("block/cable_fluid_" + tier.getB()));
			INDUSTRIAL_FLUID_CABLES.put(tier.getA(), registerCable("block/cable_industrial_fluid_" + tier.getB()));
			ITEM_CABLES.put(tier.getA(), registerCable("block/cable_item_" + tier.getB()));
		}
	}

	public static final CableModelSet CABLE_HEAT_COPPER = registerCable("block/cable_heat_copper");
	public static final CableModelSet CABLE_HEAT_GOLD = registerCable("block/cable_heat_gold");
	public static final CableModelSet CABLE_HEAT_ALUMINUM = registerCable("block/cable_heat_aluminum");

	public static final CableModelSet CABLE_DIGISTORE = registerCable("block/cable_digistore");
	public static final CableModelSet CABLE_SCAFFOLD = registerCable("block/cable_scaffold");

	public static final ResourceLocation CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT = registerModel("block/redstone_cable_attachment_input");
	public static final ResourceLocation CABLE_REDSTONE_BASIC_ATTACHMENT_OUTPUT = registerModel("block/redstone_cable_attachment_output");
	public static final ResourceLocation CABLE_REDSTONE_BASIC_ATTACHMENT_INPUT_OUTPUT = registerModel("block/redstone_cable_attachment_input_output");
	public static final ResourceLocation CABLE_REDSTONE_BASIC_NAKED_STRAIGHT = registerModel("block/cable_redstone_basic_naked_straight");
	public static final ResourceLocation CABLE_REDSTONE_BASIC_NAKED_EXTENSION = registerModel("block/cable_redstone_basic_naked_extension");

	public static final ResourceLocation CABLE_BUNDLED_REDSTONE_STRAIGHT = registerModel("block/cable_bundled_redstone_straight");
	public static final ResourceLocation CABLE_BUNDLED_REDSTONE_EXTENSION = registerModel("block/cable_bundled_redstone_extension");

	public static final ResourceLocation CABLE_BASIC_EXTRACTOR_ATTACHMENT = registerModel("item/cable_attachment_basic_extractor");
	public static final ResourceLocation CABLE_ADVANCED_EXTRACTOR_ATTACHMENT = registerModel("item/cable_attachment_advanced_extractor");
	public static final ResourceLocation CABLE_STATIC_EXTRACTOR_ATTACHMENT = registerModel("item/cable_attachment_static_extractor");
	public static final ResourceLocation CABLE_ENERGIZED_EXTRACTOR_ATTACHMENT = registerModel("item/cable_attachment_energized_extractor");
	public static final ResourceLocation CABLE_LUMUM_EXTRACTOR_ATTACHMENT = registerModel("item/cable_attachment_lumum_extractor");

	public static final ResourceLocation CABLE_BASIC_FILTER_ATTACHMENT = registerModel("item/cable_attachment_basic_filter");
	public static final ResourceLocation CABLE_ADVANCED_FILTER_ATTACHMENT = registerModel("item/cable_attachment_advanced_filter");
	public static final ResourceLocation CABLE_STATIC_FILTER_ATTACHMENT = registerModel("item/cable_attachment_static_filter");
	public static final ResourceLocation CABLE_ENERGIZED_FILTER_ATTACHMENT = registerModel("item/cable_attachment_energized_filter");
	public static final ResourceLocation CABLE_LUMUM_FILTER_ATTACHMENT = registerModel("item/cable_attachment_lumum_filter");

	public static final ResourceLocation CABLE_BASIC_RETRIEVER_ATTACHMENT = registerModel("item/cable_attachment_basic_retriever");
	public static final ResourceLocation CABLE_ADVANCED_RETRIEVER_ATTACHMENT = registerModel("item/cable_attachment_advanced_retriever");
	public static final ResourceLocation CABLE_STATIC_RETRIEVER_ATTACHMENT = registerModel("item/cable_attachment_static_retriever");
	public static final ResourceLocation CABLE_ENERGIZED_RETRIEVER_ATTACHMENT = registerModel("item/cable_attachment_energized_retriever");
	public static final ResourceLocation CABLE_LUMUM_RETRIEVER_ATTACHMENT = registerModel("item/cable_attachment_lumum_retriever");

	public static final ResourceLocation CABLE_DIGISTORE_TERMINAL_ATTACHMENT_ON = registerModel("item/cable_attachment_digistore_terminal");
	public static final ResourceLocation CABLE_DIGISTORE_TERMINAL_ATTACHMENT = registerModel("item/cable_attachment_digistore_terminal_off");

	public static final ResourceLocation CABLE_DIGISTORE_CRAFTING_TERMINAL_ATTACHMENT_ON = registerModel("item/cable_attachment_digistore_crafting_terminal");
	public static final ResourceLocation CABLE_DIGISTORE_CRAFTING_TERMINAL_ATTACHMENT = registerModel("item/cable_attachment_digistore_crafting_terminal_off");

	public static final ResourceLocation CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT_ON = registerModel("item/cable_attachment_digistore_pattern_encoder");
	public static final ResourceLocation CABLE_DIGISTORE_PATTERN_ENCODER_ATTACHMENT = registerModel("item/cable_attachment_digistore_pattern_encoder_off");

	public static final ResourceLocation CABLE_DIGISTORE_SCREEN_ATTACHMENT_ON = registerModel("item/cable_attachment_digistore_screen");
	public static final ResourceLocation CABLE_DIGISTORE_SCREEN_ATTACHMENT = registerModel("item/cable_attachment_digistore_screen_off");

	public static final ResourceLocation CABLE_DIGISTORE_LIGHT_ATTACHMENT_ON = registerModel("item/cable_attachment_digistore_light");
	public static final ResourceLocation CABLE_DIGISTORE_LIGHT_ATTACHMENT = registerModel("item/cable_attachment_digistore_light_off");

	public static final ResourceLocation CABLE_DIGISTORE_EXPORTER_ATTACHMENT = registerModel("item/cable_attachment_digistore_exporter");
	public static final ResourceLocation CABLE_DIGISTORE_IMPORTER_ATTACHMENT = registerModel("item/cable_attachment_digistore_importer");
	public static final ResourceLocation CABLE_DIGISTORE_IO_BUS_ATTACHMENT = registerModel("item/cable_attachment_digistore_io_bus");
	public static final ResourceLocation CABLE_DIGISTORE_REGULATOR_ATTACHMENT = registerModel("item/cable_attachment_digistore_regulator");
	public static final ResourceLocation CABLE_DIGISTORE_CRAFTING_INTERFACE_ATTACHMENT = registerModel("item/cable_attachment_digistore_crafting_interface");

	public static final ResourceLocation SPRINKLER = registerModel("item/cable_attachment_sprinkler");
	public static final ResourceLocation DRAIN = registerModel("item/cable_attachment_drain");

	public static final ResourceLocation BASIC_DIGISTORE_CARD = registerModel("block/digistore_cards/digistore_card_basic");
	public static final ResourceLocation ADVANCVED_DIGISTORE_CARD = registerModel("block/digistore_cards/digistore_card_advanced");
	public static final ResourceLocation STATIC_DIGISTORE_CARD = registerModel("block/digistore_cards/digistore_card_static");
	public static final ResourceLocation ENERGIZED_DIGISTORE_CARD = registerModel("block/digistore_cards/digistore_card_energized");
	public static final ResourceLocation LUMUM_DIGISTORE_CARD = registerModel("block/digistore_cards/digistore_card_lumum");
	public static final ResourceLocation CREATIVE_DIGISTORE_CARD = registerModel("block/digistore_cards/digistore_card_creative");

	public static final ResourceLocation BASIC_DIGISTORE_SINGULAR_CARD = registerModel("block/digistore_cards/digistore_card_singular_basic");
	public static final ResourceLocation ADVANCVED_DIGISTORE_SINGULAR_CARD = registerModel("block/digistore_cards/digistore_card_singular_advanced");
	public static final ResourceLocation STATIC_DIGISTORE_SINGULAR_CARD = registerModel("block/digistore_cards/digistore_card_singular_static");
	public static final ResourceLocation ENERGIZED_DIGISTORE_SINGULAR_CARD = registerModel("block/digistore_cards/digistore_card_singular_energized");
	public static final ResourceLocation LUMUM_DIGISTORE_SINGULAR_CARD = registerModel("block/digistore_cards/digistore_card_singular_lumum");
	public static final ResourceLocation CREATIVE_DIGISTORE_SINGULAR_CARD = registerModel("block/digistore_cards/digistore_card_singular_creative");
	public static final ResourceLocation DIGISTORE_SINGULAR_CARD_BAR = registerModel("block/digistore_cards/digistore_singular_card_bar");
	public static final ResourceLocation DIGISTORE_SINGULAR_CARD_BAR_FULL = registerModel("block/digistore_cards/digistore_singular_card_bar_full");

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

	public static final ResourceLocation TURBINE_BLADES_WOOD = registerModel("block/turbine_blades/turbine_blades_wood");
	public static final ResourceLocation TURBINE_BLADES_BASIC = registerModel("block/turbine_blades/turbine_blades_basic");
	public static final ResourceLocation TURBINE_BLADES_ADVANCED = registerModel("block/turbine_blades/turbine_blades_advanced");
	public static final ResourceLocation TURBINE_BLADES_STATIC = registerModel("block/turbine_blades/turbine_blades_static");
	public static final ResourceLocation TURBINE_BLADES_ENERGIZED = registerModel("block/turbine_blades/turbine_blades_energized");
	public static final ResourceLocation TURBINE_BLADES_LUMUM = registerModel("block/turbine_blades/turbine_blades_lumum");
	public static final ResourceLocation TURBINE_BLADES_CREATIVE = registerModel("block/turbine_blades/turbine_blades_creative");

	public static final Map<String, ResourceLocation[]> CABLE_REDSTONE_BASIC;

	static {
		CABLE_REDSTONE_BASIC = new HashMap<>();
		for (int i = 0; i < 16; i++) {
			ChatFormatting formatting = ChatFormatting.values()[i];
			String name = formatting.name().toLowerCase();
			CABLE_REDSTONE_BASIC.put(name, new ResourceLocation[] { registerModel("block/cable_redstone_basic_" + name + "_straight"),
					registerModel("block/cable_redstone_basic_" + name + "_extension") });
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerModels(ModelEvent.RegisterAdditional event) {
		for (ResourceLocation model : MODELS) {
			event.register(model);
			StaticPower.LOGGER.trace(String.format("Loading additional model: %1$s.", model.toString()));
		}
	}

	private static CableModelSet registerCable(String cablePath) {
		ResourceLocation straightModel = registerModel(cablePath + "_straight");
		ResourceLocation extensionModel = registerModel(cablePath + "_extension");
		ResourceLocation attachmentModel = registerModel(cablePath + "_attachment");
		return new CableModelSet(straightModel, extensionModel, attachmentModel);
	}

	/**
	 * This executes on both the client on the server. On the client, it's purpose
	 * is to register a model for loading. On the server, it's purpose is to
	 * silently assign the resource location back to the static variable that called
	 * it.
	 * 
	 * @param path
	 * @return
	 */
	private static ResourceLocation registerModel(String path) {
		ResourceLocation sprite = new ResourceLocation(StaticPower.MOD_ID, path);
		MODELS.add(sprite);
		return sprite;
	}
}
