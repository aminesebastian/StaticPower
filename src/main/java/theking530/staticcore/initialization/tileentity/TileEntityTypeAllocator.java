package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

/**
 * Pre-registers a {@link TileEntity} for initialization through the registry
 * event. Returns an instance of the {@link TileEntityType} for this
 * {@link TileEntity} and {@link Block} list combination. The registry name for
 * the tile entity type will be the registry name of the first provided block.
 */
public class TileEntityTypeAllocator<T extends TileEntity> {
	protected static final Logger LOGGER = LogManager.getLogger("StaticCore");
	@OnlyIn(Dist.CLIENT)
	public Function<TileEntityRendererDispatcher, ? extends TileEntityRenderer<T>> rendererFactory;

	public final Function<TileEntityTypeAllocator<T>, T> factory;
	public final Block[] blocks;
	protected TileEntityType<T> type;
	private boolean registered;

	public TileEntityTypeAllocator(Function<TileEntityTypeAllocator<T>, T> factory, Block... blocks) {
		this.factory = factory;
		this.blocks = blocks;
		this.registered = false;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean requiresTileEntitySpecialRenderer() {
		return rendererFactory != null;
	}

	@OnlyIn(Dist.CLIENT)
	public TileEntityTypeAllocator<T> setTileEntitySpecialRenderer(Function<TileEntityRendererDispatcher, ? extends TileEntityRenderer<T>> rendererFactory) {
		this.rendererFactory = rendererFactory;
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public Function<TileEntityRendererDispatcher, ? extends TileEntityRenderer<T>> getTileEntitySpecialRenderer() {
		return rendererFactory;
	}

	public TileEntityType<T> getType() {
		return type;
	}

	public void register(RegistryEvent.Register<TileEntityType<?>> event) {
		if (registered) {
			throw new RuntimeException("Attempted to register an already registered TileEntityTypeAllocator!");
		} else {
			TileEntityInitializer<T> initializer = new TileEntityInitializer<T>(this);
			type = TileEntityType.Builder.create(initializer, blocks).build(null);
			type.setRegistryName(blocks[0].getRegistryName());
			event.getRegistry().register(type);
			initializer.setType(type);
			registered = true;
		}
	}

	public TileEntity create() {
		return type.create();
	}
}
