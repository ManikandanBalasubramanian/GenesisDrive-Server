package com.gd;

import java.io.File;

public class JNI {
  static {
    File lib = new File(System.mapLibraryName("gdrive")); // No I18N
    System.load(lib.getAbsolutePath());
  }

  public native byte[] getPassphrase(String id);
}
