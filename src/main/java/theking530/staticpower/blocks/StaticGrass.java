package theking530.staticpower.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class StaticGrass extends StaticPowerBlock {

	public StaticGrass(String name) {
		super(name, Block.Properties.create(Material.EARTH).harvestTool(ToolType.SHOVEL).harvestLevel(0).hardnessAndResistance(0.6f).sound(SoundType.GROUND).setLightLevel((state) -> 8)
				.tickRandomly());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		tooltip.add(new StringTextComponent("Take a Leap."));
		tooltip.add(new StringTextComponent("Take Flight."));
	}

	@Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			livingEntity.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 20, 2));
		}
	}
}