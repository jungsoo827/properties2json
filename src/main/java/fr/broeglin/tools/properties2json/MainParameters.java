package fr.broeglin.tools.properties2json;

import java.nio.file.Path;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;

public class MainParameters {
  @Parameter(names = { "-Source" }, description = "Source directory", converter = PathConverter.class, required = true)
  Path source;

  @Parameter(names = { "-Target" }, description = "Target directory", converter = PathConverter.class, required = true)
  Path target;

  @Parameter(names = {
      "-Exclude" }, description = "Exclusion pattern (java pattern)")
  String exclude = "^$";
}
