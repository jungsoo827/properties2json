package fr.broeglin.tools.properties2json;

public class ArgumentParsingException extends Exception {

  private static final long serialVersionUID = 1L;
  private int returnCode;
  
  public int getReturnCode() {
    return returnCode;
  }

  public ArgumentParsingException(int returnCode, String msg) {
    super(msg);
    this.returnCode = returnCode;
  }
}
