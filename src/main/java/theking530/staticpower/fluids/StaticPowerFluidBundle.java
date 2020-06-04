package theking530.staticpower.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.items.StaticPowerFluidBucket;
import theking530.staticpower.utilities.Reference;

public class StaticPowerFluidBundle {
	public Tag<Fluid> Tag;
	public StaticPowerFluidBucket Bucket;
	public StaticPowerFluidBlock FluidBlock;
	public AbstractStaticPowerFluid.Source Fluid;
	public AbstractStaticPowerFluid.Flowing FlowingFluid;

	public StaticPowerFluidBundle(String name) {
		String stillTexture = "blocks/fluids/" + name + "_still";
		String flowingTexture = "blocks/fluids/" + name + "_flowing";
		Tag = new FluidTags.Wrapper(new ResourceLocation(Reference.MOD_ID, name));
		Bucket = new StaticPowerFluidBucket("bucket_" + name, () -> Fluid);
		FluidBlock = new StaticPowerFluidBlock(name, () -> Fluid, Block.Properties.create(Material.WATER));
		Fluid = new AbstractStaticPowerFluid.Source(name, () -> Bucket, () -> FluidBlock, () -> Fluid, () -> FlowingFluid, stillTexture, flowingTexture, Tag);
		FlowingFluid = new AbstractStaticPowerFluid.Flowing(name, () -> Bucket, () -> FluidBlock, () -> Fluid, () -> FlowingFluid, stillTexture, flowingTexture, Tag);
	}
}
