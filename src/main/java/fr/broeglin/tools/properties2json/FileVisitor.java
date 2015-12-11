package fr.broeglin.tools.properties2json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class FileVisitor extends SimpleFileVisitor<Path> {

  private Path source;
  private Path target;
  private Gson gson;

  public FileVisitor(Path source, Path target) {
    this.source = source;
    this.target = target;
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  @Override
  public FileVisitResult visitFile(Path sourceFile, BasicFileAttributes attrs)
      throws IOException {
    Path relativeParent = computeRelativeSourceParent(sourceFile);
    Path targetParent = Files.createDirectories(target.resolve(relativeParent));
    Path fileName = sourceFile.getFileName();

    if (isPropertyFile(fileName)) {
      Path targetJson = targetParent.resolve(computeNewFileName(fileName));

      convertProperties(sourceFile, targetJson);
    } else {
      Path targetFile = targetParent.resolve(sourceFile.getFileName());
      
      System.out.format("Copying    '%s' to '%s'...\n", sourceFile, targetFile);
      Files.copy(sourceFile, targetFile);
    }

    return FileVisitResult.CONTINUE;
  }

  Path computeRelativeSourceParent(Path sourceFile) {
    Path relativeSourceFile = source.relativize(sourceFile);
    Path relativeParent = relativeSourceFile.getParent();
    
    return relativeParent == null ? Paths.get("") : relativeParent;
  }

  String computeNewFileName(Path fileName) {
    return fileName.toString().replaceFirst(".properties$", ".json");
  }

  private void convertProperties(Path source, Path target) {
    System.out.format("Converting '%s' to '%s'...\n", source, target);
    Properties props = new Properties();
    try (InputStream is = Files.newInputStream(source)) {
      props.load(is);
    } catch (IOException e) {
      throw new RuntimeException("IO Error occured while reading '" + source + "'", e);
    }

    String jsonData = convertToJson(props);

    try (OutputStream os = Files.newOutputStream(target);
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
      osw.write(jsonData);

    } catch (IOException e) {
      throw new RuntimeException("IO Error occured while writing '" + target + "'", e);
    }
  }

  String convertToJson(Properties props) {
    JsonObject json = new JsonObject();
    props.forEach((name, value) -> {
      json.addProperty((String) name, (String) value);
    });
    String jsonData = gson.toJson(json);
    return jsonData;
  }

  boolean isPropertyFile(Path fileName) {
    return fileName.toString().endsWith(".properties");
  }

  private void logPath(Path path) {
    System.out.println(path);
  }
}
