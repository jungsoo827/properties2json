package fr.broeglin.tools.properties2json;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MainTest {

  @Test(expected = ArgumentParsingException.class)
  public void should_fail_if_no_source() throws Exception {
    Main.parseArguments("-Target", "test");
  }

  @Test(expected = ArgumentParsingException.class)
  public void should_fail_if_no_target() throws Exception {
    Main.parseArguments("-Source", "test");
  }

  @Test(expected = ArgumentParsingException.class)
  public void should_fail_if_source_not_a_dir() throws Exception {
    Main.parseArguments("-Source", source.newFile().toString(), "-Target", target.getRoot().toString());
  }

  @Test(expected = ArgumentParsingException.class)
  public void should_fail_if_target_not_a_dir() throws Exception {
    Main.parseArguments("-Source", source.getRoot().toString(), "-Target", target.newFile().toString());
  }

  @Test(expected = ArgumentParsingException.class)
  public void should_fail_if_source_does_not_exist() throws Exception {
    Main.parseArguments("-Source", "does_not_exist", "-Target", target.getRoot().toString());
  }

  @Test(expected = ArgumentParsingException.class)
  public void should_fail_if_target_does_not_exist() throws Exception {
    Main.parseArguments("-Source", source.getRoot().toString(), "-Target", "does_not_exist");
  }

  public void should_parse_if_both_directories() throws Exception {
    Main.parseArguments("-Source", source.getRoot().toString(), "-Target", target.getRoot().toString());    
  }
  
  // plumbing
  @Rule
  public TemporaryFolder source = new TemporaryFolder();

  @Rule
  public TemporaryFolder target = new TemporaryFolder();
}
