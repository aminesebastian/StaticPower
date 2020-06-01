package theking530.staticpower.tileentities.components;

import java.util.Optional;

import net.minecraft.tileentity.TileEntity;
import theking530.staticpower.tileentities.TileEntityBase;

public class ComponentUtilities {
	public static <T extends AbstractTileEntityComponent> Optional<T> getComponent(Class<T> componentClass, TileEntity tileEntity) {
		return getComponent(componentClass, null, tileEntity);
	}

	public static <T extends AbstractTileEntityComponent> Optional<T> getComponent(Class<T> componentClass, String componentName, TileEntity tileEntity) {
		// If the tile entity isn't of the right type, return empty.
		if (!(tileEntity instanceof TileEntityBase)) {
			return Optional.empty();
		}

		// If the tile entity does not have the side configuration component, return
		// empty.
		TileEntityBase baseTileEntity = (TileEntityBase) tileEntity;
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
