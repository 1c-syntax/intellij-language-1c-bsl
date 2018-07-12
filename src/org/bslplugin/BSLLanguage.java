package org.bslplugin;

import com.intellij.lang.Language;

public class BSLLanguage extends Language {

  public static final BSLLanguage INSTANCE = new BSLLanguage();

  private BSLLanguage() {
    super("BSL");
  }
}
