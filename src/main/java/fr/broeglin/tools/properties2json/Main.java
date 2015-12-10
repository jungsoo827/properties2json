package fr.broeglin.tools.properties2json;

import java.io.IOException;
import java.nio.file.Files;

import com.beust.jcommander.JCommander;

public class Main {
  public static void main(String[] args) {
    MainParameters params = new MainParameters();

    new JCommander(params, args);

    System.err.println("Source: " + params.source);
    System.err.println("Target: " + params.target);

    if (!params.source.toFile().isDirectory()) {
      System.err.format("Source '%s' should be an existing directory\n", params.source);
      System.exit(-1);
    }
    if (!params.target.toFile().isDirectory()) {
      System.err.format("Target '%s' should be an existing directory\n", params.target);
      System.exit(-2);
    }

    try {
      Files.walkFileTree(params.source, new FileVisitor(params.target));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-3);
    }
  }
}
