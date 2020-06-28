package theking530.common.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class CustomJsonLoader {
	public static final Logger LOGGER = LogManager.getLogger(CustomJsonLoader.class);

	public static <T> List<T> loadAllInPath(Class<?> mainModClass, String relativePath, Class<T> outputType) {
		List<T> output = new LinkedList<T>();
		FileSystem filesystem = null;
		URL url = mainModClass.getResource(relativePath);

		try {
			if (url != null) {
				URI uri = url.toURI();
				Path path = null;

				if ("file".equals(uri.getScheme())) {
					path = Paths.get(mainModClass.getResource(relativePath).toURI());
				} else {
					filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
					path = filesystem.getPath(relativePath);
				}
				Gson gson = new Gson();
				Stream<Path> paths = Files.walk(path);

				paths.filter(Files::isRegularFile).forEach((filePath) -> {
					try {
						File fileToParse = filePath.toFile();
						FileReader reader = new FileReader(fileToParse);
						BufferedReader bufferedReader = new BufferedReader(reader);
						output.add(gson.fromJson(bufferedReader, outputType));
					} catch (FileNotFoundException e) {
						LOGGER.error(String.format("An error occured when attempting to parse file: %1$s as class: %2$s.", filePath, outputType), e);
					}
				});

			}
			return output;
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to load files for ModClass: %1$s from relative path: %2$s.", mainModClass, relativePath), e);
		}
	}
}
