package theking530.staticpower.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnergizedGrass extends StaticPowerBlock {

	public EnergizedGrass(String name) {
		super(name, Block.Properties.of(Material.DIRT).strength(0.6f).sound(SoundType.GRAVEL)
				.lightLevel((state) -> 8).randomTicks());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
			boolean isShowingAdvanced) {
		tooltip.add(new TextComponent("Walk On Top."));
		tooltip.add(new TextComponent("I Dare You."));
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 1));
		}
	}
}
