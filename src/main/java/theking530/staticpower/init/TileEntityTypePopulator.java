package theking530.staticpower.init;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

@Retention(RetentionPolicy.RUNTIME)
public @interface TileEntityTypePopulator {
	public static class TileEntityTypeAllocator {
		public final Function<TileEntityType<? extends TileEntity>, ? extends TileEntity> factory;
		public final Block[] blocks;
		public TileEntityType<? extends TileEntity> type;

		public TileEntityTypeAllocator(Function<TileEntityType<? extends TileEntity>, ? extends TileEntity> factory, Block... blocks) {
			this.factory = factory;
			this.blocks = blocks;
		}
	}
}
