package fr.broeglin.tools.properties2json;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import java.nio.file.Path;

public class MainParameters {

  @Parameter(names = {"-Source"}, description = "Source directory", converter = PathConverter.class, required = false)
  Path source;

  @Parameter(names = {"-Target"}, description = "Target directory", converter = PathConverter.class, required = false)
  Path target;

  @Parameter(names = {
    "-Exclude"}, description = "Exclusion pattern (java pattern)")
  String exclude = "^$";


}
