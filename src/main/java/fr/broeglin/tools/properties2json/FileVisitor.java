package fr.broeglin.tools.properties2json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class FileVisitor extends SimpleFileVisitor<Path> {

  private Path target;

  public FileVisitor(Path target) {
    this.target = target;
  }

  @Override
  public FileVisitResult visitFile(Path source, BasicFileAttributes attrs)
      throws IOException {
    logPath(source);

    Path parent = source.getParent();

    Path targetParent = Files.createDirectories(target.resolve(parent));

    if (isPropertyFile(source.getFileName())) {
      Path targetJson = targetParent.resolve(source.getFileName().toString().replaceFirst(".properties$", ".json"));
      
      convertProperties(source, targetJson);
    } else {
      Files.copy(source, targetParent.resolve(source.getFileName()));
    }

    return FileVisitResult.CONTINUE;
  }

  private void convertProperties(Path source, Path target) {
    Properties props = new Properties();
    try (InputStream is = Files.newInputStream(source)) {
      props.load(is);
    } catch (IOException e) {
      throw new RuntimeException("IO Error occured while reading '" + source + "'", e);
    }

    JsonObject json = new JsonObject();
    props.forEach((name, value) -> {
      json.addProperty((String) name, (String) value);
    });

    try (OutputStream os = Files.newOutputStream(target);
        OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      osw.write(gson.toJson(json));

    } catch (IOException e) {
      throw new RuntimeException("IO Error occured while writing '" + target + "'", e);
    }
  }

  boolean isPropertyFile(Path fileName) {
    return fileName.toString().endsWith(".properties");
  }

  private void logPath(Path path) {
    System.out.println(path);
  }
}
