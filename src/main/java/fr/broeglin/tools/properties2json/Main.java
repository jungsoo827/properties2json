package fr.broeglin.tools.properties2json;

import java.io.IOException;
import java.nio.file.Files;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {
  public static void main(String[] args) {
    MainParameters params = null;
    try {
      params = parseArguments(args);
    } catch (ArgumentParsingException e) {
      System.err.println(e.getMessage());
      System.exit(e.getReturnCode());
    }

    System.err.println("Source: " + params.source);
    System.err.println("Target: " + params.target);

    try {
      Files.walkFileTree(params.source, new FileVisitor(params.source, params.target));
    } catch (IOException e) {
      System.err.println("IO error while walking converting");
      e.printStackTrace();
      System.exit(-3);
    }
  }

  static MainParameters parseArguments(String... args) throws ArgumentParsingException {
    MainParameters params = new MainParameters();

    try {
      new JCommander(params, args);
    } catch (ParameterException e) {
      throw new ArgumentParsingException(-1, e.getMessage());
    }


    if (!params.source.toFile().isDirectory()) {
      throw new ArgumentParsingException(-2,
          String.format("Source '%s' should be an existing directory\n", params.source));
    }
    if (!params.target.toFile().isDirectory()) {
      throw new ArgumentParsingException(-3,
          String.format("Target '%s' should be an existing directory\n", params.target));
    }
    return params;
  }
}
