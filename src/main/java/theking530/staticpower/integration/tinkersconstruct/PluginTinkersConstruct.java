package theking530.staticpower.integration.tinkersconstruct;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import theking530.staticpower.assists.utilities.EnumTextFormatting;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.integration.ICompatibilityPlugin;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.ItemMaterials;

public class PluginTinkersConstruct implements ICompatibilityPlugin {

	private boolean registered;
	
	@Override
	public boolean isRegistered() {
		return registered;
	}

	@Override
	public boolean shouldRegister() {
		return Loader.isModLoaded("tconstruct");
	}

	@Override
	public void register() {
		if(isRegistered()) {
			return;
		}
		registered = true;
		initializeTinkersMaterials();
		initializeTinkersFluids();		
	}

	@Override
	public String getPluginName() {
		return "Tinkers' Construct";
	}
	
	public void initializeTinkersMaterials() {
		createTinkersMaterial(250, ItemMaterials.ingotStatic, "Static Metal", 2, 700, 800, 4, 0.8f, EnumTextFormatting.GREEN.toString(), 255 << 24 | 51 << 16 | 255 << 8 | 53, 180, 1.0f, 2.0f, 0.8f);
		createTinkersMaterial(251, ItemMaterials.ingotEnergized, "Energized Metal", 3, 1100, 1300, 8, 1.0f, EnumTextFormatting.AQUA.toString(), 255 << 24 | 51 << 16 | 255 << 8 | 255, 80, 1.2f, 2.0f, 0.9f);
		createTinkersMaterial(252, ItemMaterials.ingotLumum, "Lumum Metal", 5, 2000, 1500, 10, 1.3f, EnumTextFormatting.YELLOW.toString(), 255 << 24 | 255 << 16 | 255 << 8 | 90, 40, 1.5f, 2.0f, 1.0f);
	}
	public void createTinkersMaterial(int ID, ItemStack item, String name, int harvestLevel, int durability, int miningSpeed, int attack, float handlerModifier, String textColor, int color,
			int drawSpeed, float projectileSpeed, float projectileMass, float projectileFragility) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Id", ID); // Unique material ID. Reseved IDs: 0-40 Tinker, 41-45 Iguana Tinker Tweaks, 100-200 ExtraTiC
		tag.setString("Name", name); // Unique material name
		tag.setInteger("HarvestLevel", harvestLevel); // diamond level
		tag.setInteger("Durability", durability);
		tag.setInteger("MiningSpeed", miningSpeed);
		tag.setInteger("Attack", attack); // optional
		tag.setFloat("HandleModifier", handlerModifier);
		//tag.setInteger("Reinforced", 0); // optional
		//tag.setFloat("Stonebound", 0); // optional, cannot be used if jagged
		//tag.setFloat("Jagged", 0); // optional, cannot be used if stonebound
		tag.setString("Style", textColor); // optional, color of the material text
		tag.setInteger("Color", color); // argb

		tag.setInteger("Bow_DrawSpeed", drawSpeed); // the higher the longer it takes to draw the bow
		tag.setFloat("Bow_ProjectileSpeed", projectileSpeed); // the higher the faster the projectile goes
		tag.setFloat("Projectile_Mass", projectileMass);
		tag.setFloat("Projectile_Fragility", projectileFragility); // This is a multiplier to the shafts break-chance

		FMLInterModComms.sendMessage("tconstruct", "addMaterial", tag);	
		
		NBTTagCompound repairTag = new NBTTagCompound();
		repairTag.setInteger("MaterialId", ID);
		repairTag.setInteger("Value", 2); // 1 material ever 2 value. See PartMapping IMC
		NBTTagCompound repairItem = new NBTTagCompound();
		item.writeToNBT(repairItem); // seared brick item
		repairTag.setTag("Item", repairItem);

		FMLInterModComms.sendMessage("tconstruct", "addMaterialItem", repairTag);
		
		NBTTagCompound partTag = new NBTTagCompound();
		partTag.setInteger("MaterialId", ID); // output material id
		NBTTagCompound partItem = new NBTTagCompound();
		item.writeToNBT(partItem); // seared brick block
		partTag.setTag("Item", partItem);

		// 1 value = 1 shard. So 1 blocks like stone usually have value 2.
		// Seared Brick is the shard, the block consists of 4 bricks, therefore value 4
		partTag.setInteger("Value", 2);
		FMLInterModComms.sendMessage("tconstruct", "addPartBuilderMaterial", partTag);
		
	}
	
	public void initializeTinkersFluids(){
		createTinkersFluid(250, ItemMaterials.ingotStatic, ModBlocks.StaticBlock, new FluidStack(ModFluids.StaticFluid, 144), 700);
		createTinkersFluid(250, new ItemStack(Item.getItemFromBlock(ModBlocks.StaticBlock)), ModBlocks.StaticBlock, new FluidStack(ModFluids.StaticFluid, 1296), 700);
		createTinkersFluid(250, ItemMaterials.nuggetStatic, ModBlocks.LumumBlock, new FluidStack(ModFluids.LumumFluid, 6), 1100);
		
		createTinkersFluid(251, ItemMaterials.ingotEnergized, ModBlocks.EnergizedBlock, new FluidStack(ModFluids.EnergizedFluid, 144), 900);
		createTinkersFluid(251, new ItemStack(Item.getItemFromBlock(ModBlocks.EnergizedBlock)), ModBlocks.EnergizedBlock, new FluidStack(ModFluids.EnergizedFluid, 1296), 900);
		createTinkersFluid(251, ItemMaterials.nuggetEnergized, ModBlocks.LumumBlock, new FluidStack(ModFluids.LumumFluid, 6), 1100);
		
		createTinkersFluid(252, ItemMaterials.ingotLumum, ModBlocks.LumumBlock, new FluidStack(ModFluids.LumumFluid, 144), 1100);
		createTinkersFluid(252, new ItemStack(Item.getItemFromBlock(ModBlocks.LumumBlock)), ModBlocks.LumumBlock, new FluidStack(ModFluids.LumumFluid, 1296), 1100);
		createTinkersFluid(252, ItemMaterials.nuggetLumum, ModBlocks.LumumBlock, new FluidStack(ModFluids.LumumFluid, 6), 1100);
		
	}
	public void createTinkersFluid(int ID, ItemStack meltingItem, Block displayItem, FluidStack fluid, int temperature) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound item = new NBTTagCompound();
		meltingItem.writeToNBT(item);
		tag.setTag("Item", item);
		item = new NBTTagCompound();
		(new ItemStack(Item.getItemFromBlock(displayItem))).writeToNBT(item); 
		tag.setTag("Block", item);
		fluid.writeToNBT(tag);
		tag.setInteger("Temperature", temperature);
		FMLInterModComms.sendMessage("tconstruct", "addSmelteryMelting", tag);
		
		NBTTagCompound fluidTag = new NBTTagCompound();
		fluid.writeToNBT(fluidTag); // this also works, amount doesn't matter
		fluidTag.setInteger("MaterialId", ID); // output material id
		FMLInterModComms.sendMessage("tconstruct", "addPartCastingMaterial", fluidTag);

	}
}
