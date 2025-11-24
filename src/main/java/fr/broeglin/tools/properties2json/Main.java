package fr.broeglin.tools.properties2json;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Main {


  public static void main(String[] args) {
    MainParameters params = null;
    try {
      params = parseArguments(args);
    } catch (ArgumentParsingException e) {
      System.err.println(e.getMessage());
      System.exit(e.getReturnCode());
    }

//    System.out.println("Keystore: " + params.keystore);
//    System.out.println("Source: " + params.source);
//    System.out.println("Target: " + params.target);
//    System.out.println("Exclude: " + params.exclude);
//    System.out.println("PrivateKey entered: " + (params.privateKey != null));
//    System.out.println("KeystorePassword entered: " + (params.keystorePassword != null));
//    System.out.println("KeyAlias: " + params.keyAlias);

      if (params.source == null || params.target == null) {
        System.err.println("Source and Target must be entered");
        System.exit(-3);
      }
      try {
        Files.walkFileTree(params.source, new FileVisitor(params.source, params.target, params.exclude));
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

    if (params.source != null && !params.source.toFile().isDirectory()) {
      throw new ArgumentParsingException(-2,
        String.format("Source '%s' should be an existing directory\n", params.source));
    }
    if (params.target != null && !params.target.toFile().isDirectory()) {
      throw new ArgumentParsingException(-3,
        String.format("Target '%s' should be an existing directory\n", params.target));
    }
    return params;
  }
}
