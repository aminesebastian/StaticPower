package theking530.staticpower.tileentities.components.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

/**
 * Utility class to help serialize objects to and from NBT using the annotations
 * {@link UpdateSerailize} and {@link SaveSerialize}.
 * 
 * @author amine
 *
 */
public class SerializationUtilities {
	public static final Logger LOGGER = LogManager.getLogger(SerializationUtilities.class);
	public static final Map<Class<?>, List<Field>> FIELD_MAP = new HashMap<Class<?>, List<Field>>();

	/**
	 * Gets all the {@link UpdateSerialize} fields on the provided object.
	 * 
	 * @return The list of update serializeable fields.
	 */
	public static List<Field> getUpdateSerializeableFields(Object obj) {
		if (!FIELD_MAP.containsKey(obj.getClass())) {
			// Allocate output list.
			List<Field> updateFields = new ArrayList<Field>();

			// Iterate through all the fields.
			for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
				for (Field field : c.getDeclaredFields()) {
					if (field.isAnnotationPresent(UpdateSerialize.class)) {
						updateFields.add(field);
					}
				}
			}

			FIELD_MAP.put(obj.getClass(), updateFields);
			return updateFields;
		} else {
			return FIELD_MAP.get(obj);
		}
	}

	/**
	 * Gets all the {@link SaveSerialize} fields on the provided object.
	 * 
	 * @return The list of save serializeable fields.
	 */
	public static List<Field> getSaveSerializeableFields(Object obj) {
		// Allocate output list.
		List<Field> saveFields = new ArrayList<Field>();

		// Iterate through all the fields.
		for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
			for (Field field : obj.getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(SaveSerialize.class) || field.isAnnotationPresent(UpdateSerialize.class)) {
					saveFields.add(field);
				}
			}
		}

		// Return the save serializeable fields.
		return saveFields;
	}

	/**
	 * Serializes the provided list of fields using the values in the provided
	 * object to the provided nbt.
	 * 
	 * @param nbt
	 * @param fields
	 * @param object
	 */
	public static void serializeFieldsToNbt(CompoundNBT nbt, List<Field> fields, Object object) {
		// Iterate through all the fields.
		for (Field field : fields) {
			// Check if the field is accessible.
			boolean isPrivate = !field.isAccessible();
			try {
				// Mark it accessible if not.
				if (isPrivate) {
					field.setAccessible(true);
				}
				// Get the type of the field and then serialize it if possible.
				Class<?> t = field.getType();
				if (t.isEnum()) {
					Method m = t.getMethod("ordinal");
					int value = (int) m.invoke(field.get(object));
					nbt.putByte(field.getName(), (byte) value);
				} else if (t == boolean.class) {
					nbt.putBoolean(field.getName(), field.getBoolean(object));
				} else if (t == int.class) {
					nbt.putInt(field.getName(), field.getInt(object));
				} else if (t == float.class) {
					nbt.putFloat(field.getName(), field.getFloat(object));
				} else if (t == double.class) {
					nbt.putDouble(field.getName(), field.getDouble(object));
				} else if (t == long.class) {
					nbt.putLong(field.getName(), field.getLong(object));
				} else if (t == String.class) {
					nbt.putString(field.getName(), (String) field.get(object));
				} else if (t == BlockPos.class) {
					BlockPos pos = (BlockPos) field.get(object);
					nbt.putLong(field.getName(), pos.toLong());
				} else if (t == FluidStack.class) {
					FluidStack fluid = (FluidStack) field.get(object);
					CompoundNBT tag = new CompoundNBT();
					fluid.writeToNBT(tag);
					nbt.put(field.getName(), tag);
				} else if (INBTSerializable.class.isAssignableFrom(t)) {
					@SuppressWarnings("unchecked")
					INBTSerializable<CompoundNBT> serializeable = (INBTSerializable<CompoundNBT>) field.get(object);
					CompoundNBT serialized = serializeable.serializeNBT();
					nbt.put(field.getName(), serialized);
				} else {
					LOGGER.warn(String.format("Encountered serializeable field %1$s with unsupported type: %2$s.", field.getName(), t));
				}
			} catch (Exception e) {
				LOGGER.error(String.format("An error occured when attempting to serialize field: %1$s from object: %2$s to NBT.", field.getName(), object), e);
			} finally {
				// Reset the private state if needed.
				if (isPrivate) {
					field.setAccessible(false);
				}
			}
		}
	}

	/**
	 * Deserializes the provided list of fields into the provided object from the
	 * provided nbt.
	 * 
	 * @param nbt
	 * @param fields
	 * @param object
	 */
	public static void deserializeFieldsToNbt(CompoundNBT nbt, List<Field> fields, Object object) {
		// Iterate through all the fields.
		for (Field field : fields) {
			// Skip any fields that we have not had serialized yet.
			if (!nbt.contains(field.getName())) {
				continue;
			}

			// Check if the field is accessible.
			boolean isPrivate = !field.isAccessible();
			try {
				// Mark it accessible if not.
				if (isPrivate) {
					field.setAccessible(true);
				}

				// Get the type of the field and then serialize it if possible.
				Class<?> t = field.getType();
				if (t.isEnum()) {
					Method m = t.getMethod("values");
					Object[] values = (Object[]) m.invoke(field.get(object));
					Object result = values[nbt.getByte(field.getName())];
					field.set(object, result);
				} else if (t == boolean.class) {
					field.set(object, nbt.getBoolean(field.getName()));
				} else if (t == int.class) {
					field.set(object, nbt.getInt(field.getName()));
				} else if (t == float.class) {
					field.set(object, nbt.getFloat(field.getName()));
				} else if (t == double.class) {
					field.set(object, nbt.getDouble(field.getName()));
				} else if (t == long.class) {
					field.set(object, nbt.getLong(field.getName()));
				} else if (t == String.class) {
					field.set(object, nbt.getString(field.getName()));
				} else if (t == BlockPos.class) {
					BlockPos pos = BlockPos.fromLong(nbt.getLong(field.getName()));
					field.set(object, pos);
				} else if (INBTSerializable.class.isAssignableFrom(t)) {
					@SuppressWarnings("unchecked")
					INBTSerializable<CompoundNBT> serializeable = (INBTSerializable<CompoundNBT>) field.get(object);
					serializeable.deserializeNBT(nbt.getCompound(field.getName()));
				} else if (t == FluidStack.class) {
					FluidStack stack = FluidStack.loadFluidStackFromNBT(nbt.getCompound(field.getName()));
					field.set(object, stack);
				} else {
					LOGGER.warn(String.format("Encountered deserializeable field %1$s with unsupported type: %2$s.", field.getName(), t));
				}
			} catch (Exception e) {
				LOGGER.error(String.format("An error occured when attempting to deserialize field: %1$s to object: %2$s from NBT.", field.getName(), object), e);
			} finally {
				// Reset the private state if needed.
				if (isPrivate) {
					field.setAccessible(false);
				}
			}
		}
	}
}
