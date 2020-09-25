package theking530.staticcore.utilities;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class CustomJsonLoader {
	public static final Logger LOGGER = LogManager.getLogger(CustomJsonLoader.class);
	private static final Gson GSON_INSTANCE = new Gson();

	public static <T> T loadResource(IResourceManager manager, ResourceLocation location, Class<T> outputType) {
		try {
			// Get the resource.
			IResource res = manager.getResource(location);

			// Attempt to open and parse the resource.
			try (InputStream inputstream = res.getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));) {
				T parsedResource = GSON_INSTANCE.fromJson(reader, outputType);
				inputstream.close();
				reader.close();
				return parsedResource;
			} catch (Exception e) {
				throw e;
			} finally {
				IOUtils.closeQuietly((Closeable) res);
			}
		} catch (Exception e) {
			LOGGER.error(String.format("An error occured when attempting to load file: %1$s.", location), e);
		}
		return null;
	}
}
