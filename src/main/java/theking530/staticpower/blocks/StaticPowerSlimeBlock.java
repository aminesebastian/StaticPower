package theking530.staticpower.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StaticPowerSlimeBlock extends StaticPowerBlock {

	public StaticPowerSlimeBlock(String name, Block.Properties properties) {
		super(name, properties);
	}

	/**
	 * Block's chance to react to a living entity falling on it.
	 */
	@Override
	public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
		if (entityIn.isSuppressingBounce()) {
			super.fallOn(worldIn, state, pos, entityIn, fallDistance);
		} else {
			entityIn.causeFallDamage(fallDistance, 0.0f, DamageSource.FALL);
		}

	}

	/**
	 * Called when an Entity lands on this Block. This method *must* update motionY
	 * because the entity will not do that on its own
	 */
	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
		if (entityIn.isSuppressingBounce()) {
			super.updateEntityAfterFallOn(worldIn, entityIn);
		} else {
			this.bounceUp(entityIn);
		}

	}

	private void bounceUp(Entity p_226946_1_) {
		Vec3 Vector3d = p_226946_1_.getDeltaMovement();
		if (Vector3d.y < 0.0D) {
			double d0 = p_226946_1_ instanceof LivingEntity ? 1.0D : 0.8D;
			p_226946_1_.setDeltaMovement(Vector3d.x, -Vector3d.y * d0, Vector3d.z);
		}

	}

	/**
	 * Called when the given entity walks on this Block
	 */
	@Override
	public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
		double d0 = Math.abs(entityIn.getDeltaMovement().y);
		if (d0 < 0.1D && !entityIn.isSteppingCarefully()) {
			double d1 = 0.4D + d0 * 0.2D;
			entityIn.setDeltaMovement(entityIn.getDeltaMovement().multiply(d1, 1.0D, d1));
		}

		super.stepOn(worldIn, pos, state, entityIn);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.translucent();
	}
}