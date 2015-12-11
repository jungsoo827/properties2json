package fr.broeglin.tools.properties2json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;

public class FileVisitorTest {
  FileVisitor visitor = new FileVisitor(null, null);
  Properties props;

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

  @Test
  public void should_convert_empty_to_json() {
    assertThat(visitor.convertToJson(props), equalTo("{}"));
  }

  @Test
  public void should_convert_one_property_to_json() {
    props.put("a.b.c", "1");

    assertThat(visitor.convertToJson(props), equalTo("{\n  \"a.b.c\": \"1\"\n}"));
  }

  @Test
  public void should_convert_two_property_to_json() {
    props.put("a.b.c", "1");
    props.put("c.d.e", "2");

    assertThat(visitor.convertToJson(props), equalTo("{\n  \"c.d.e\": \"2\",\n  \"a.b.c\": \"1\"\n}"));
  }

  @Test
  public void should_rename_ending_with_properties() {
    assertThat(visitor.computeNewFileName(Paths.get("test.properties")), equalTo("test.json"));
  }

  @Test
  public void should_not_rename_if_not_ending_with_properties() {
    assertThat(visitor.computeNewFileName(Paths.get("test.properties1")), equalTo("test.properties1"));
  }

  @Test
  public void should_compute_relative_parent_as_empty() {
    FileVisitor visitor = new FileVisitor(Paths.get("source_dir"), null);

    assertThat(visitor.computeRelativeSourceParent(Paths.get("source_dir/a")), 
        equalTo(Paths.get("")));
  }

  @Test
  public void should_compute_relative_parent_as_a() {
    FileVisitor visitor = new FileVisitor(Paths.get("source_dir"), null);

    assertThat(visitor.computeRelativeSourceParent(Paths.get("source_dir/a/b.txt")), 
        equalTo(Paths.get("a")));

    visitor = new FileVisitor(Paths.get("/source_dir"), null);

    assertThat(visitor.computeRelativeSourceParent(Paths.get("/source_dir/a/b.txt")), 
        equalTo(Paths.get("a")));
  }

  // plumbing
  @Before
  public void init() {
    this.props = new Properties();
  }
}
