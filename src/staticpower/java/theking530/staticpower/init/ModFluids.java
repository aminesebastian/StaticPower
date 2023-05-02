package theking530.staticpower.init;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.fluid.StaticPowerFluidBuilder;
import theking530.staticcore.fluid.StaticPowerFluidBundle;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.materials.MaterialBundle;

public class ModFluids {
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister
			.create(ForgeRegistries.Keys.FLUID_TYPES, StaticPower.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS,
			StaticPower.MOD_ID);
	public static final List<StaticPowerFluidBundle> FLUID_BUNDLES = new ArrayList<>();
	public static final FluidStack WILDCARD = new FluidStack(Fluids.EMPTY, 1);

	public static final StaticPowerFluidBundle StaticFluid = registerFluid(
			create("fluid_static", new SDColor(0.2f, 0.9f, 0.2f)).addProperties(builder -> {
				builder.lightLevel(15);
			}));
	public static final StaticPowerFluidBundle EnergizedFluid = registerFluid(
			create("fluid_energized", new SDColor(0.1f, 0.6f, 0.9f)).addProperties(builder -> {
				builder.lightLevel(15);
			}));
	public static final StaticPowerFluidBundle LumumFluid = registerFluid(
			create("fluid_lumum", new SDColor(0.5f, 0.1f, 0.9f)).addProperties(builder -> {
				builder.lightLevel(15);
			}));

	public static final StaticPowerFluidBundle Ethanol = registerFluid(
			create("ethanol", new SDColor(0.4f, 0.75f, 0.9f)).addProperties(builder -> {
				builder.viscosity(500);
			}));
	public static final StaticPowerFluidBundle Mash = registerFluid(
			create("mash", new SDColor(0.3f, 0.2f, 0.2f)).addProperties(builder -> {
				builder.viscosity(4000);
			}));
	public static final StaticPowerFluidBundle EvaporatedMash = registerFluid(
			create("evaporated_mash", new SDColor(0.4f, 0.3f, 0.3f)).addProperties(builder -> {
				builder.density(-500).viscosity(500);
			}));
	public static final StaticPowerFluidBundle LiquidExperience = registerFluid(
			create("experience", new SDColor(0.6f, 0.8f, 0.3f)));
	public static final StaticPowerFluidBundle Steam = registerFluid(
			create("steam", new SDColor(0.8f, 0.8f, 0.8f)).addProperties(builder -> {
				builder.density(-500).viscosity(500);
			}));
	public static final StaticPowerFluidBundle TreeOil = registerFluid(
			create("oil_tree", new SDColor(0.6f, 0.6f, 0.4f)).addProperties(builder -> {
				builder.viscosity(500);
			}));
	public static final StaticPowerFluidBundle TreeSap = registerFluid(
			create("tree_sap", new SDColor(0.3f, 0.25f, 0.1f)).addProperties(builder -> {
				builder.viscosity(6000);
			}));
	public static final StaticPowerFluidBundle InfernalTreeSap = registerFluid(
			create("infernal_tree_sap", new SDColor(0.5f, 0.25f, 0.1f)).addProperties(builder -> {
				builder.viscosity(5000);
			}));
	public static final StaticPowerFluidBundle SeedOil = registerFluid(
			create("oil_seed", new SDColor(0.6f, 0.55f, 0.4f)).addProperties(builder -> {
				builder.viscosity(500);
			}));

	public static final StaticPowerFluidBundle AppleJuice = registerFluid(
			create("juice_apple", new SDColor(0.5f, 0.45f, 0.3f)));
	public static final StaticPowerFluidBundle BerryJuice = registerFluid(
			create("juice_berry", new SDColor(0.7f, 0.2f, 0.3f)));
	public static final StaticPowerFluidBundle PumpkinJuice = registerFluid(
			create("juice_pumpkin", new SDColor(0.7f, 0.4f, 0.25f)));
	public static final StaticPowerFluidBundle CarrotJuice = registerFluid(
			create("juice_carrot", new SDColor(0.9f, 0.6f, 0.2f)));
	public static final StaticPowerFluidBundle WatermelonJuice = registerFluid(
			create("juice_melon", new SDColor(0.9f, 0.2f, 0.3f)));
	public static final StaticPowerFluidBundle BeetJuice = registerFluid(
			create("juice_beetroot", new SDColor(0.45f, 0.05f, 0.175f)));
	public static final StaticPowerFluidBundle Fertilizer = registerFluid(
			create("fertilizer", new SDColor(0.2f, 0.25f, 0.6f)));
	public static final StaticPowerFluidBundle Honey = registerFluid(
			create("honey", new SDColor(0.9f, 0.7f, 0.1f)).addProperties(builder -> {
				builder.viscosity(6000).density(64).sound(SoundActions.BUCKET_FILL, SoundEvents.HONEY_BLOCK_STEP);
			}));
	public static final StaticPowerFluidBundle Latex = registerFluid(
			create("latex", new SDColor(1.0f, 1.0f, 1.0f)).addProperties(builder -> {
				builder.viscosity(2000).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.HONEY_BLOCK_STEP);
			}));
	public static final StaticPowerFluidBundle Coolant = registerFluid(
			create("coolant", new SDColor(0.7f, 0.8f, 1.0f)).addProperties(builder -> {
				builder.viscosity(500).density(32);
			}));

	public static final StaticPowerFluidBundle CrudeOil = registerFluid(
			create("oil_crude", new SDColor(0.1f, 0.1f, 0.1f)).addProperties(builder -> {
				builder.viscosity(6000).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
						.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
			}));
	public static final StaticPowerFluidBundle LightOil = registerFluid(
			create("oil_light", new SDColor(0.25f, 0.225f, 0.15f)).addProperties(builder -> {
				builder.viscosity(1500).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
						.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
			}));
	public static final StaticPowerFluidBundle HeavyOil = registerFluid(
			create("oil_heavy", new SDColor(0.35f, 0.225f, 0.15f)).addProperties(builder -> {
				builder.viscosity(5000).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
						.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
			}));
	public static final StaticPowerFluidBundle Fuel = registerFluid(
			create("fuel", new SDColor(0.35f, 0.375f, 0.15f)).addProperties(builder -> {
				builder.viscosity(2000).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
						.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
			}));

	public static final Map<MinecraftColor, StaticPowerFluidBundle> CONCRETE = new HashMap<>();
	public static final Map<MinecraftColor, StaticPowerFluidBundle> DYES = new HashMap<>();

	static {
		// Create all colors of concrete.
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle = registerFluid(create("concrete_" + color.getId(), color.getColor())
					.setTextureName("concrete").addProperties(builder -> {
						builder.viscosity(8000).density(64).sound(SoundActions.BUCKET_EMPTY,
								SoundEvents.HONEY_BLOCK_STEP);
					}).setTint(color.getColor().fromFloatToEightBit().encodeInInteger()));
			CONCRETE.put(color, bundle);
		}

		// Create all colors of dye.
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle = registerFluid(
					create("dye_" + color.getId(), color.getColor()).setTextureName("dye").addProperties(builder -> {
						builder.sound(SoundActions.BUCKET_EMPTY, SoundEvents.HONEY_BLOCK_STEP);
					}).setTint(color.getColor().fromFloatToEightBit().encodeInInteger()));
			DYES.put(color, bundle);
		}
	}

	public static StaticPowerFluidBundle registerFluid(StaticPowerFluidBuilder builder) {
		StaticPowerFluidBundle bundle = builder.build(FLUID_TYPES, FLUIDS, ModBlocks.BLOCKS, ModItems.ITEMS);
		FLUID_BUNDLES.add(bundle);
		return bundle;
	}

	public static void init(IEventBus eventBus) {
		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			bundle.generateFluids();
		}
		FLUID_TYPES.register(eventBus);
		FLUIDS.register(eventBus);
	}

	public static StaticPowerFluidBuilder create(String name, SDColor fogColor) {
		return new StaticPowerFluidBuilder(StaticPower.MOD_ID, ModCreativeTabs.GENERAL, name, fogColor);
	}
}
