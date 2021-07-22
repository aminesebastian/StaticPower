package theking530.staticpower.cables.redstone;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.CableBoundsCache;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.client.rendering.blocks.CableBakedModel;

public class BlockRedstoneCable extends AbstractCableBlock {
	private final String color;

	public BlockRedstoneCable(String name) {
		super(name, new CableBoundsCache(1.5D, new Vector3D(8.0f, 1.0f, 1.0f)));

		// String the color from the last section of the registry name.
		this.color = name.substring(name.indexOf("_", name.indexOf("_", name.indexOf("_") + 1) + 1) + 1);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, @Nullable IBakedModel existingModel, ModelBakeEvent event) {
		if (color.equals("naked")) {
			IBakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_STRAIGHT);
			IBakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC_NAKED_EXTENSION);
			IBakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTOND_BASIC_ATTACHMENT);
			return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
		} else {
			IBakedModel straightModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[0]);
			IBakedModel extensionModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTONE_BASIC.get(color)[1]);
			IBakedModel attachmentModel = event.getModelRegistry().get(StaticPowerAdditionalModels.CABLE_REDSTOND_BASIC_ATTACHMENT);
			return new CableBakedModel(existingModel, extensionModel, straightModel, attachmentModel);
		}
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		switch (color) {
		case "black":
			return TileEntityRedstoneCable.TYPE_BASIC_BLACK.create();
		case "dark_blue":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_BLUE.create();
		case "dark_green":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_GREEN.create();
		case "dark_aqua":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_AQUA.create();
		case "dark_red":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_RED.create();
		case "dark_purple":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_PURPLE.create();
		case "gold":
			return TileEntityRedstoneCable.TYPE_BASIC_GOLD.create();
		case "gray":
			return TileEntityRedstoneCable.TYPE_BASIC_GRAY.create();
		case "dark_gray":
			return TileEntityRedstoneCable.TYPE_BASIC_DARK_GRAY.create();
		case "blue":
			return TileEntityRedstoneCable.TYPE_BASIC_BLUE.create();
		case "green":
			return TileEntityRedstoneCable.TYPE_BASIC_GREEN.create();
		case "aqua":
			return TileEntityRedstoneCable.TYPE_BASIC_AQUA.create();
		case "red":
			return TileEntityRedstoneCable.TYPE_BASIC_RED.create();
		case "light_purple":
			return TileEntityRedstoneCable.TYPE_BASIC_LIGHT_PURPLE.create();
		case "yellow":
			return TileEntityRedstoneCable.TYPE_BASIC_YELLOW.create();
		case "white":
			return TileEntityRedstoneCable.TYPE_BASIC_WHITE.create();
		default:
			return TileEntityRedstoneCable.TYPE_BASIC_NAKED.create();
		}
	}
}
