package theking530.staticpower.initialization;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import theking530.staticpower.Registry;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerRotatePillarBlock;
import theking530.staticpower.blocks.crops.BaseSimplePlant;
import theking530.staticpower.blocks.decorative.Lamp;
import theking530.staticpower.blocks.decorative.StaticPowerGlassBlock;

public class ModBlocks {
	// Decorative
	public static Lamp StaticLamp;
	public static Lamp EnergizedLamp;
	public static Lamp LumumLamp;
	public static StaticPowerGlassBlock ObsidianGlass;

	// Plants
	public static BaseSimplePlant StaticPlant;
	public static BaseSimplePlant EnergizedPlant;
	public static BaseSimplePlant LumumPlant;

	// Wood
	public static StaticPowerRotatePillarBlock StaticWood;
	public static StaticPowerRotatePillarBlock EnergizedWood;
	public static StaticPowerRotatePillarBlock LumumWood;

	// Planks
	public static StaticPowerBlock StaticPlanks;
	public static StaticPowerBlock EnergizedPlanks;
	public static StaticPowerBlock LumumPlanks;
	
	public static void init() {
		// Decorative
		Registry.preRegisterBlock(StaticLamp = new Lamp("lamp_static"));
		Registry.preRegisterBlock(EnergizedLamp = new Lamp("lamp_energized"));
		Registry.preRegisterBlock(LumumLamp = new Lamp("lamp_lumum"));
		Registry.preRegisterBlock(ObsidianGlass = new StaticPowerGlassBlock("glass_obsidian"));

		// Plants
		Registry.preRegisterBlock(StaticPlant = new BaseSimplePlant("plant_static"));
		Registry.preRegisterBlock(EnergizedPlant = new BaseSimplePlant("plant_energized"));
		Registry.preRegisterBlock(LumumPlant = new BaseSimplePlant("plant_lumum"));
		
		// Wood
		Registry.preRegisterBlock(StaticWood = new StaticPowerRotatePillarBlock("wood_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(EnergizedWood = new StaticPowerRotatePillarBlock("wood_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(LumumWood = new StaticPowerRotatePillarBlock("wood_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));;
		
		// Planks
		Registry.preRegisterBlock(StaticPlanks = new StaticPowerBlock("planks_static", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(EnergizedPlanks = new StaticPowerBlock("planks_energized", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
		Registry.preRegisterBlock(LumumPlanks = new StaticPowerBlock("planks_lumum", Block.Properties.create(Material.WOOD).harvestTool(ToolType.AXE).sound(SoundType.WOOD)));
	}
}
