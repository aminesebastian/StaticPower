package theking530.staticpower.cables.digistore;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.cablenetwork.CableBoundsCache;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.DigistoreLight;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;

public class BlockDigistoreNetworkWire extends AbstractCableBlock {

	public BlockDigistoreNetworkWire() {
		super(new CableBoundsCache(1.5D, new Vector3D(4.0f, 4.0f, 1.0f)), 1.75f);

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		ResourceLocation extensionModel = StaticPowerAdditionalModels.CABLE_DIGISTORE_EXTENSION;
		ResourceLocation straightModel = StaticPowerAdditionalModels.CABLE_DIGISTORE_STRAIGHT;
		ResourceLocation attachmentModel = StaticPowerAdditionalModels.CABLE_DIGISTORE_ATTACHMENT;

		return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutoutMipped();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityDigistoreWire.TYPE.create(pos, state);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		AbstractCableProviderComponent cable = CableUtilities.getCableWrapperComponent(world, pos);
		if (cable instanceof DigistoreCableProviderComponent) {
			DigistoreCableProviderComponent digistoreCable = (DigistoreCableProviderComponent) cable;
			if (digistoreCable.hasAttachmentOfType(DigistoreLight.class) && digistoreCable.isManagerPresent()) {
				return 15;
			}
		}
		return super.getLightEmission(state, world, pos);
	}
}
