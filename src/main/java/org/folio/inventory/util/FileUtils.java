package org.folio.inventory.util;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class FileUtils {

  public static String readFile(String filePath) {
    log.info("Reading file: {}", filePath);

    final URL resource = FileUtils.class.getClassLoader().getResource(filePath);

    if (resource == null) {
      throw new IllegalArgumentException("Failed to access the resource: " + filePath);
    }

    try (Stream<String> lines = Files.lines(Paths.get(resource.toURI()))) {
      return lines.collect(Collectors.joining(System.lineSeparator()));
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to read contents of the file: " + filePath, e);
    }
  }
}
