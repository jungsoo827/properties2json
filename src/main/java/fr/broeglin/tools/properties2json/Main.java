package fr.broeglin.tools.properties2json;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Main {

  public static void createKeyStore(Path keystoreFilePath, String keyAlias, String keystorePassword, String privateKey) {

    // Your secret key value (use ONE of these)
    String base64KeyValue = null;
//    String base64KeyValue = "your-base64-encoded-key-here"; // e.g., "vE/v8/U1nZ2sA/sPjB+0gA=="
//    String hexKeyValue = null; // e.g., "bc4ffbf3f5359d9d9b00fb0f8c1fb480"
    String hexKeyValue = privateKey; // e.g., "bc4ffbf3f5359d9d9b00fb0f8c1fb480"

    // Details for the new keystore entry
    String keyAlgorithm = "AES"; // The algorithm of your key (e.g., "AES", "HmacSHA256")

    // Details for the new keystore file
    char[] keystorePasswordBytes = keystorePassword.toCharArray();
    // ------------------------

    try {
      // --- 2. Get the raw key bytes from your string ---
      byte[] keyBytes;
      if (base64KeyValue != null) {
        keyBytes = Base64.getDecoder().decode(base64KeyValue);
      } else if (hexKeyValue != null) {
        keyBytes = hexToBytes(hexKeyValue);
      } else {
        System.out.println("Error: No key value provided.");
        return;
      }

      // 3. Create a SecretKey object from the raw bytes
      SecretKey secretKey = new SecretKeySpec(keyBytes, keyAlgorithm);

      // 4. Get a KeyStore instance (PKCS12 is the modern standard)
      KeyStore keyStore = KeyStore.getInstance("PKCS12");

      // 5. Initialize a new, empty KeyStore
      // To create a new keystore, call load() with null streams.
      keyStore.load(null, keystorePasswordBytes);

      // 6. Create the ProtectionParameter for the new entry
      // We'll use the same password as the keystore
      KeyStore.ProtectionParameter entryPassword =
        new KeyStore.PasswordProtection(keystorePasswordBytes);

      // 7. Create the SecretKeyEntry
      KeyStore.SecretKeyEntry secretKeyEntry =
        new KeyStore.SecretKeyEntry(secretKey);

      // 8. Add the entry to the KeyStore
      keyStore.setEntry(keyAlias, secretKeyEntry, entryPassword);

      // 9. Save the new KeyStore to a file
      try (OutputStream fos = Files.newOutputStream(keystoreFilePath)) {
        keyStore.store(fos, keystorePasswordBytes);
      }

      System.out.println("Successfully created '" + keystoreFilePath + "'");
      System.out.println("Added secret key with alias: " + keyAlias);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static byte[] hexToBytes(String hex) {
    int len = hex.length();
    if (len % 2 != 0) {
      throw new IllegalArgumentException("Hex string must have an even number of characters.");
    }
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
        + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }

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

    if (params.keystore != null && params.privateKey != null) {
      createKeyStore(params.keystore, params.keyAlias, params.keystorePassword, params.privateKey);
    } else {
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
