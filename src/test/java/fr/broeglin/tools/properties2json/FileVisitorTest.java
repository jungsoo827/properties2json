package fr.broeglin.tools.properties2json;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class FileVisitorTest {
  FileVisitor visitor = new FileVisitor(null);

  @Test
  public void isPropertyFile_should_return_match_dot_properties() throws Exception {
    Path tmp = Files.createTempFile("test", ".properties");

    assertThat(visitor.isPropertyFile(tmp), is(true));
  }

  @Test
  public void isPropertyFile_should_not_match_if_not_dot_properties() throws Exception {
    Path tmp = Files.createTempFile("test", ".foo");

    assertThat(visitor.isPropertyFile(tmp), is(false));
  }
}
