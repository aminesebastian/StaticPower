package theking530.staticpower.items.cableattachments;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.tileentities.cables.AbstractCableBlock;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.CableUtilities;

public abstract class AbstractCableAttachment extends StaticPowerItem {

	public AbstractCableAttachment(String name) {
		super(name);
	}

	@Override
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		if (world.getBlockState(pos).getBlock() instanceof AbstractCableBlock) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
			if (cableComponent != null) {
				AbstractCableBlock block = (AbstractCableBlock) world.getBlockState(pos).getBlock();
				Direction hoveredDirection = block.CableBounds.getHoveredAttachmentDirection(pos, player);
				if (hoveredDirection != null && cableComponent.attachAttachment(item, hoveredDirection)) {
					if (!world.isRemote) {
						item.setCount(item.getCount() - 1);
					} else {
						world.playSound(player, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
					}
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	public void onRemovedFromCable(ItemStack attachment, AbstractCableProviderComponent cableComponent) {

	}

	public abstract ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent);
}
