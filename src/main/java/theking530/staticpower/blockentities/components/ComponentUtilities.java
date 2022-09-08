package theking530.staticpower.blockentities.components;

import java.util.Optional;

import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticpower.blockentities.BlockEntityBase;

public class ComponentUtilities {
	public static <T extends AbstractTileEntityComponent> Optional<T> getComponent(Class<T> componentClass, BlockEntity tileEntity) {
		return getComponent(componentClass, null, tileEntity);
	}

	public static <T extends AbstractTileEntityComponent> Optional<T> getComponent(Class<T> componentClass, String componentName, BlockEntity tileEntity) {
		// If the tile entity isn't of the right type, return empty.
		if (!(tileEntity instanceof BlockEntityBase)) {
			return Optional.empty();
		}

		// If the tile entity does not have the side configuration component, return
		// empty.
		BlockEntityBase baseTileEntity = (BlockEntityBase) tileEntity;
		if (!baseTileEntity.hasComponentOfType(componentClass)) {
			return Optional.empty();
		}

		// Return the side configuration component.
		if (componentName == null) {
			return Optional.of(baseTileEntity.getComponent(componentClass));
		} else {
			return Optional.of(baseTileEntity.getComponent(componentClass, componentName));
		}
	}
}
