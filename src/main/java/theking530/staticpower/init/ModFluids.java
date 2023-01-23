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
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.fluid.StaticPowerFluidBuilder;
import theking530.staticpower.fluid.StaticPowerFluidBundle;

public class ModFluids {
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, StaticPower.MOD_ID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, StaticPower.MOD_ID);
	public static final List<StaticPowerFluidBundle> FLUID_BUNDLES = new ArrayList<>();
	public static final FluidStack WILDCARD = new FluidStack(Fluids.EMPTY, 1);

	public static final StaticPowerFluidBundle StaticFluid = registerFluid(new StaticPowerFluidBuilder("fluid_static", new SDColor(0.2f, 0.9f, 0.2f)).addProperties(builder -> {
		builder.lightLevel(15);
	}));
	public static final StaticPowerFluidBundle EnergizedFluid = registerFluid(
			new StaticPowerFluidBuilder("fluid_energized", new SDColor(0.1f, 0.6f, 0.9f)).addProperties(builder -> {
				builder.lightLevel(15);
			}));
	public static final StaticPowerFluidBundle LumumFluid = registerFluid(new StaticPowerFluidBuilder("fluid_lumum", new SDColor(0.5f, 0.1f, 0.9f)).addProperties(builder -> {
		builder.lightLevel(15);
	}));

	public static final StaticPowerFluidBundle Ethanol = registerFluid(new StaticPowerFluidBuilder("ethanol", new SDColor(0.4f, 0.75f, 0.9f)).addProperties(builder -> {
		builder.viscosity(500);
	}));
	public static final StaticPowerFluidBundle Mash = registerFluid(new StaticPowerFluidBuilder("mash", new SDColor(0.3f, 0.2f, 0.2f)).addProperties(builder -> {
		builder.viscosity(2000);
	}));
	public static final StaticPowerFluidBundle EvaporatedMash = registerFluid(
			new StaticPowerFluidBuilder("evaporated_mash", new SDColor(0.4f, 0.3f, 0.3f)).addProperties(builder -> {
				builder.density(-500).viscosity(500);
			}));
	public static final StaticPowerFluidBundle LiquidExperience = registerFluid(new StaticPowerFluidBuilder("experience", new SDColor(0.6f, 0.8f, 0.3f)));
	public static final StaticPowerFluidBundle Steam = registerFluid(new StaticPowerFluidBuilder("steam", new SDColor(0.8f, 0.8f, 0.8f)).addProperties(builder -> {
		builder.density(-500).viscosity(500);
	}));
	public static final StaticPowerFluidBundle TreeOil = registerFluid(new StaticPowerFluidBuilder("oil_tree", new SDColor(0.6f, 0.6f, 0.4f)).addProperties(builder -> {
		builder.viscosity(500);
	}));
	public static final StaticPowerFluidBundle TreeSap = registerFluid(new StaticPowerFluidBuilder("tree_sap", new SDColor(0.3f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(2000);
	}));
	public static final StaticPowerFluidBundle InfernalTreeSap = registerFluid(
			new StaticPowerFluidBuilder("infernal_tree_sap", new SDColor(0.5f, 0.25f, 0.1f)).addProperties(builder -> {
				builder.viscosity(2000);
			}));
	public static final StaticPowerFluidBundle SeedOil = registerFluid(new StaticPowerFluidBuilder("oil_seed", new SDColor(0.6f, 0.55f, 0.4f)).addProperties(builder -> {
		builder.viscosity(500);
	}));

	public static final StaticPowerFluidBundle AppleJuice = registerFluid(new StaticPowerFluidBuilder("juice_apple", new SDColor(0.5f, 0.45f, 0.3f)));
	public static final StaticPowerFluidBundle BerryJuice = registerFluid(new StaticPowerFluidBuilder("juice_berry", new SDColor(0.7f, 0.2f, 0.3f)));
	public static final StaticPowerFluidBundle PumpkinJuice = registerFluid(new StaticPowerFluidBuilder("juice_pumpkin", new SDColor(0.7f, 0.4f, 0.25f)));
	public static final StaticPowerFluidBundle CarrotJuice = registerFluid(new StaticPowerFluidBuilder("juice_carrot", new SDColor(0.9f, 0.6f, 0.2f)));
	public static final StaticPowerFluidBundle WatermelonJuice = registerFluid(new StaticPowerFluidBuilder("juice_melon", new SDColor(0.9f, 0.2f, 0.3f)));
	public static final StaticPowerFluidBundle BeetJuice = registerFluid(new StaticPowerFluidBuilder("juice_beetroot", new SDColor(0.45f, 0.05f, 0.175f)));
	public static final StaticPowerFluidBundle Fertilizer = registerFluid(new StaticPowerFluidBuilder("fertilizer", new SDColor(0.2f, 0.25f, 0.6f)));
	public static final StaticPowerFluidBundle Honey = registerFluid(new StaticPowerFluidBuilder("honey", new SDColor(0.9f, 0.7f, 0.1f)).addProperties(builder -> {
		builder.viscosity(2000).density(64).sound(SoundActions.BUCKET_FILL, SoundEvents.HONEY_BLOCK_STEP);
	}));
	public static final StaticPowerFluidBundle Latex = registerFluid(new StaticPowerFluidBuilder("latex", new SDColor(1.0f, 1.0f, 1.0f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.HONEY_BLOCK_STEP);
	}));
	public static final StaticPowerFluidBundle Coolant = registerFluid(new StaticPowerFluidBuilder("coolant", new SDColor(0.7f, 0.8f, 1.0f)).addProperties(builder -> {
		builder.viscosity(500).density(32);
	}));

	public static final StaticPowerFluidBundle MoltenIron = registerFluid(new StaticPowerFluidBuilder("molten_iron", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).temperature(2800).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenGold = registerFluid(new StaticPowerFluidBuilder("molten_gold", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).temperature(1064).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenCopper = registerFluid(new StaticPowerFluidBuilder("molten_copper", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).temperature(1084).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenTin = registerFluid(new StaticPowerFluidBuilder("molten_tin", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).temperature(450).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenZinc = registerFluid(new StaticPowerFluidBuilder("molten_zinc", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).temperature(420).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenMagnesium = registerFluid(
			new StaticPowerFluidBuilder("molten_magnesium", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
				builder.viscosity(1250).density(32).temperature(1202).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
						SoundEvents.BUCKET_EMPTY_LAVA);
			}));
	public static final StaticPowerFluidBundle MoltenTungsten = registerFluid(
			new StaticPowerFluidBuilder("molten_tungsten", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
				builder.viscosity(1000).density(32).temperature(3400).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
						SoundEvents.BUCKET_EMPTY_LAVA);
			}));
	public static final StaticPowerFluidBundle MoltenSilver = registerFluid(new StaticPowerFluidBuilder("molten_silver", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1000).density(32).temperature(961).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenLead = registerFluid(new StaticPowerFluidBuilder("molten_lead", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(750).density(32).temperature(328).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenAluminum = registerFluid(
			new StaticPowerFluidBuilder("molten_aluminum", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
				builder.viscosity(500).density(32).temperature(660).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
						SoundEvents.BUCKET_EMPTY_LAVA);
			}));
	public static final StaticPowerFluidBundle MoltenPlatinum = registerFluid(
			new StaticPowerFluidBuilder("molten_platinum", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
				builder.viscosity(1500).density(32).temperature(1770).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
						SoundEvents.BUCKET_EMPTY_LAVA);
			}));
	public static final StaticPowerFluidBundle MoltenBrass = registerFluid(new StaticPowerFluidBuilder("molten_brass", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(750).density(32).temperature(1710).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle MoltenBronze = registerFluid(new StaticPowerFluidBuilder("molten_bronze", new SDColor(1.0f, 0.25f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).temperature(950).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY,
				SoundEvents.BUCKET_EMPTY_LAVA);
	}));

	public static final StaticPowerFluidBundle CrudeOil = registerFluid(new StaticPowerFluidBuilder("oil_crude", new SDColor(0.1f, 0.1f, 0.1f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle LightOil = registerFluid(new StaticPowerFluidBuilder("oil_light", new SDColor(0.25f, 0.225f, 0.15f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle HeavyOil = registerFluid(new StaticPowerFluidBuilder("oil_heavy", new SDColor(0.35f, 0.225f, 0.15f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
	}));
	public static final StaticPowerFluidBundle Fuel = registerFluid(new StaticPowerFluidBuilder("fuel", new SDColor(0.35f, 0.375f, 0.15f)).addProperties(builder -> {
		builder.viscosity(1500).density(32).sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA).sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA);
	}));

	public static final Map<MinecraftColor, StaticPowerFluidBundle> CONCRETE = new HashMap<>();
	public static final Map<MinecraftColor, StaticPowerFluidBundle> DYES = new HashMap<>();

	static {
		// Create all colors of concrete.
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle = registerFluid(
					new StaticPowerFluidBuilder("concrete_" + color.getId(), color.getColor()).setTextureName("concrete").addProperties(builder -> {
						builder.viscosity(2500).density(64).sound(SoundActions.BUCKET_EMPTY, SoundEvents.HONEY_BLOCK_STEP);
					}).setTint(color.getColor().fromFloatToEightBit().encodeInInteger()));
			CONCRETE.put(color, bundle);
		}

		// Create all colors of dye.
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle = registerFluid(new StaticPowerFluidBuilder("dye_" + color.getId(), color.getColor()).setTextureName("dye").addProperties(builder -> {
				builder.sound(SoundActions.BUCKET_EMPTY, SoundEvents.HONEY_BLOCK_STEP);
			}).setTint(color.getColor().fromFloatToEightBit().encodeInInteger()));
			DYES.put(color, bundle);
		}
	}

	private static StaticPowerFluidBundle registerFluid(StaticPowerFluidBuilder builder) {
		StaticPowerFluidBundle bundle = builder.build();
		FLUID_BUNDLES.add(bundle);
		return bundle;
	}

	public static void init(IEventBus eventBus) {
		FLUID_TYPES.register(eventBus);
		FLUIDS.register(eventBus);
	}
}
