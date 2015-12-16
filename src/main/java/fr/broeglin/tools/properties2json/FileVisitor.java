package fr.broeglin.tools.properties2json;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class FileVisitor extends SimpleFileVisitor<Path> {
  private static final Pattern NOT_OK_CONTENT_PATTERN = Pattern.compile("^\\s*[{\\[\"].*",
      Pattern.DOTALL | Pattern.MULTILINE);

  private Path source;
  private Path target;
  private Pattern excludePattern;
  private Gson gson;

  public FileVisitor(Path source, Path target, String excludeString) {
    this.source = source;
    this.target = target;
    this.excludePattern = Pattern.compile(excludeString);
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  @Override
  public FileVisitResult visitFile(Path sourceFile, BasicFileAttributes attrs)
      throws IOException {
    Path relativeParent = computeRelativeSourceParent(sourceFile);
    Path targetParent = Files.createDirectories(target.resolve(relativeParent));

    if (isPropertyFile(sourceFile)) {
      Path targetJson = targetParent.resolve(computeNewFileName(sourceFile.getFileName()));

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
    return "__" + fileName.toString() + ".json";
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

  boolean isPropertyFile(Path file) throws IOException {
    String fileNameString = file.getFileName().toString();

    return fileNameString.endsWith(".properties")
        && !excludePattern.matcher(fileNameString).matches()
        && contentIsOk(file);
  }

  /**
   * Heuristic method that reads 1024 bytes and looks for either "{", "[" or '"'
   * as the first non space character in those.
   * 
   * @param fileName
   * @return true if the characters are not found.
   * @throws IOException
   */
  private boolean contentIsOk(Path fileName) throws IOException {
    try {
      byte[] buf = new byte[1024];
      try (RandomAccessFile raf = new RandomAccessFile(fileName.toFile(), "r")) {
        try {
          raf.readFully(buf);
        } catch (EOFException e) {
          // OK to ignore, we just want the min number of bytes
          // between the file length and the buffer size
        }
      }
      String content = new String(buf, "UTF-8");

      return !NOT_OK_CONTENT_PATTERN.matcher(content).matches();
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Got encoding exception (should not happen on JVM!)", e);
    }
  }
}
