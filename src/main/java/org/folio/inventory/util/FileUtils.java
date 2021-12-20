package org.folio.inventory.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class FileUtils {

  public static String readFile(String filePath) {
    log.info("Reading file: {}", filePath);

    try (InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath)) {
      if (inputStream == null) {
        throw new IllegalArgumentException("Failed to read resource: " + filePath);
      }

      StringBuilder stringBuilder = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
         stringBuilder.append(line).append(System.lineSeparator());
      }

      return stringBuilder.toString();
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to read contents of the file: " + filePath, e);
    }
  }

}
