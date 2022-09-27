package theking530.staticpower.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.utilities.SDColor;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.fluid.StaticPowerFluidBundle.StaticPowerFluidBuilder;

public class ModFluids {
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, StaticPower.MOD_ID);
	public static final FluidStack WILDCARD = new FluidStack(Fluids.EMPTY, 1);
	
	public static final StaticPowerFluidBundle StaticFluid = new StaticPowerFluidBuilder("fluid_static", new SDColor(0.2f, 0.9f, 0.2f)).addAutoBucket().addAttributes(builder -> {
		builder.luminosity(15);
	}).build();
	public static final StaticPowerFluidBundle EnergizedFluid = new StaticPowerFluidBuilder("fluid_energized", new SDColor(0.1f, 0.6f, 0.9f)).addAutoBucket()
			.addAttributes(builder -> {
				builder.luminosity(15);
			}).build();
	public static final StaticPowerFluidBundle LumumFluid = new StaticPowerFluidBuilder("fluid_lumum", new SDColor(0.5f, 0.1f, 0.9f)).addAutoBucket().addAttributes(builder -> {
		builder.luminosity(15);
	}).build();

	public static final StaticPowerFluidBundle Ethanol = new StaticPowerFluidBuilder("ethanol", new SDColor(0.4f, 0.75f, 0.9f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(500);
	}).build();
	public static final StaticPowerFluidBundle Mash = new StaticPowerFluidBuilder("mash", new SDColor(0.3f, 0.2f, 0.2f)).addAttributes(builder -> {
		builder.viscosity(2000);
	}).addAutoBucket().build();
	public static final StaticPowerFluidBundle EvaporatedMash = new StaticPowerFluidBuilder("evaporated_mash", new SDColor(0.4f, 0.3f, 0.3f)).addAutoBucket()
			.addAttributes(builder -> {
				builder.gaseous().density(-500).viscosity(500);
			}).build();
	public static final StaticPowerFluidBundle LiquidExperience = new StaticPowerFluidBuilder("experience", new SDColor(0.6f, 0.8f, 0.3f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle Steam = new StaticPowerFluidBuilder("steam", new SDColor(0.8f, 0.8f, 0.8f)).addAutoBucket().addAttributes(builder -> {
		builder.gaseous().density(-500).viscosity(500);
	}).build();
	public static final StaticPowerFluidBundle TreeOil = new StaticPowerFluidBuilder("oil_tree", new SDColor(0.6f, 0.6f, 0.4f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(500);
	}).build();
	public static final StaticPowerFluidBundle TreeSap = new StaticPowerFluidBuilder("tree_sap", new SDColor(0.3f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(2000);
	}).build();
	public static final StaticPowerFluidBundle InfernalTreeSap = new StaticPowerFluidBuilder("infernal_tree_sap", new SDColor(0.5f, 0.25f, 0.1f)).addAttributes(builder -> {
		builder.viscosity(2000);
	}).addAutoBucket().build();
	public static final StaticPowerFluidBundle SeedOil = new StaticPowerFluidBuilder("oil_seed", new SDColor(0.6f, 0.55f, 0.4f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(500);
	}).build();

	public static final StaticPowerFluidBundle AppleJuice = new StaticPowerFluidBuilder("juice_apple", new SDColor(0.5f, 0.45f, 0.3f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle BerryJuice = new StaticPowerFluidBuilder("juice_berry", new SDColor(0.7f, 0.2f, 0.3f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle PumpkinJuice = new StaticPowerFluidBuilder("juice_pumpkin", new SDColor(0.7f, 0.4f, 0.25f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle CarrotJuice = new StaticPowerFluidBuilder("juice_carrot", new SDColor(0.9f, 0.6f, 0.2f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle WatermelonJuice = new StaticPowerFluidBuilder("juice_melon", new SDColor(0.9f, 0.2f, 0.3f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle BeetJuice = new StaticPowerFluidBuilder("juice_beetroot", new SDColor(0.45f, 0.05f, 0.175f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle Fertilizer = new StaticPowerFluidBuilder("fertilizer", new SDColor(0.2f, 0.25f, 0.6f)).addAutoBucket().build();
	public static final StaticPowerFluidBundle Honey = new StaticPowerFluidBuilder("honey", new SDColor(0.9f, 0.7f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(2000).density(64).sound(SoundEvents.HONEY_BLOCK_STEP);
	}).build();
	public static final StaticPowerFluidBundle Latex = new StaticPowerFluidBuilder("latex", new SDColor(1.0f, 1.0f, 1.0f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1500).density(32).sound(SoundEvents.HONEY_BLOCK_STEP);
	}).build();
	public static final StaticPowerFluidBundle Coolant = new StaticPowerFluidBuilder("coolant", new SDColor(0.7f, 0.8f, 1.0f))
			.addAutoBucket(true, StaticPowerSprites.DYE_BUCKET_FLUID_MASK).addAttributes(builder -> {
				builder.viscosity(500).density(32);
			}).build();

	public static final StaticPowerFluidBundle MoltenIron = new StaticPowerFluidBuilder("molten_iron", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1500).density(32).temperature(2800).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenGold = new StaticPowerFluidBuilder("molten_gold", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1500).density(32).temperature(1064).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenCopper = new StaticPowerFluidBuilder("molten_copper", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1500).density(32).temperature(1084).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenTin = new StaticPowerFluidBuilder("molten_tin", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1500).density(32).temperature(450).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenZinc = new StaticPowerFluidBuilder("molten_zinc", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1500).density(32).temperature(420).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenMagnesium = new StaticPowerFluidBuilder("molten_magnesium", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket()
			.addAttributes(builder -> {
				builder.viscosity(1250).density(32).temperature(1202).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();
	public static final StaticPowerFluidBundle MoltenTungsten = new StaticPowerFluidBuilder("molten_tungsten", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket()
			.addAttributes(builder -> {
				builder.viscosity(1000).density(32).temperature(3400).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();
	public static final StaticPowerFluidBundle MoltenSilver = new StaticPowerFluidBuilder("molten_silver", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1000).density(32).temperature(961).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenLead = new StaticPowerFluidBuilder("molten_lead", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(750).density(32).temperature(328).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenAluminum = new StaticPowerFluidBuilder("molten_aluminum", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket()
			.addAttributes(builder -> {
				builder.viscosity(500).density(32).temperature(660).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();
	public static final StaticPowerFluidBundle MoltenPlatinum = new StaticPowerFluidBuilder("molten_platinum", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket()
			.addAttributes(builder -> {
				builder.viscosity(1500).density(32).temperature(1770).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();
	public static final StaticPowerFluidBundle MoltenBrass = new StaticPowerFluidBuilder("molten_brass", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(750).density(32).temperature(1710).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();
	public static final StaticPowerFluidBundle MoltenBronze = new StaticPowerFluidBuilder("molten_bronze", new SDColor(1.0f, 0.25f, 0.1f)).addAutoBucket().addAttributes(builder -> {
		builder.viscosity(1500).density(32).temperature(950).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
	}).build();

	public static final StaticPowerFluidBundle CrudeOil = new StaticPowerFluidBuilder("oil_crude", new SDColor(0.1f, 0.1f, 0.1f))
			.addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
				builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();
	public static final StaticPowerFluidBundle LightOil = new StaticPowerFluidBuilder("oil_light", new SDColor(0.25f, 0.225f, 0.15f))
			.addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
				builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();
	public static final StaticPowerFluidBundle HeavyOil = new StaticPowerFluidBuilder("oil_heavy", new SDColor(0.35f, 0.225f, 0.15f))
			.addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
				builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();
	public static final StaticPowerFluidBundle Fuel = new StaticPowerFluidBuilder("fuel", new SDColor(0.35f, 0.375f, 0.15f))
			.addAutoBucket(true, StaticPowerSprites.OIL_BUCKET_FLUID_MASK).addAttributes(builder -> {
				builder.viscosity(1500).density(32).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
			}).build();

	public static final Map<MinecraftColor, StaticPowerFluidBundle> ColoredConrete = new HashMap<>();
	public static final Map<MinecraftColor, StaticPowerFluidBundle> Dyes = new HashMap<>();

	static {
		// Create all colors of concrete.
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle = new StaticPowerFluidBuilder("concrete_" + color.getId(), color.getColor()).setTextureName("concrete")
					.addAutoBucket(true, StaticPowerSprites.CONCRETE_BUCKET_FLUID_MASK).addAttributes(builder -> {
						builder.viscosity(2500).density(64).sound(SoundEvents.HONEY_BLOCK_STEP).color(color.getColor().fromFloatToEightBit().encodeInInteger());
					}).build();
			ColoredConrete.put(color, bundle);
		}

		// Create all colors of dye.
		for (MinecraftColor color : MinecraftColor.values()) {
			StaticPowerFluidBundle bundle = new StaticPowerFluidBuilder("dye_" + color.getId(), color.getColor()).setTextureName("dye")
					.addAutoBucket(true, StaticPowerSprites.DYE_BUCKET_FLUID_MASK).addAttributes(builder -> {
						builder.sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).color(color.getColor().fromFloatToEightBit().encodeInInteger());
					}).build();
			Dyes.put(color, bundle);
		}
	}

	public static void init(IEventBus eventBus) {
		FLUIDS.register(eventBus);
	}
}
