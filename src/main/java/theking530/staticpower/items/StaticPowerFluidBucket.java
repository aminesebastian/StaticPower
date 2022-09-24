package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.rendering.items.DynamicBucketItemModel;

public class StaticPowerFluidBucket extends BucketItem implements ICustomModelSupplier {
	private final boolean useDynamicModel;
	private final ResourceLocation fluidMaskSprite;

	public StaticPowerFluidBucket(boolean useDynamicModel, ResourceLocation fluidMaskSprite, RegistryObject<? extends Fluid> supplier) {
		super(supplier, new Properties().stacksTo(1).tab(StaticPower.CREATIVE_TAB));
		this.useDynamicModel = useDynamicModel;
		this.fluidMaskSprite = fluidMaskSprite;
	}

	public StaticPowerFluidBucket(RegistryObject<? extends Fluid> supplier) {
		this(false, null, supplier);
	}

	@Override
	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
		FluidBucketWrapper output = new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
		output.fill(new FluidStack(getFluid(), FluidAttributes.BUCKET_VOLUME), FluidAction.EXECUTE);
		return output;
	}

	public boolean requiresDynamicModel() {
		return useDynamicModel;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return useDynamicModel;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBaseModelOverride(ModelBakeEvent event) {
		return event.getModelRegistry().get(new ModelResourceLocation(new ResourceLocation("minecraft", "bucket"), "inventory"));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new DynamicBucketItemModel(existingModel, fluidMaskSprite);
	}
}
