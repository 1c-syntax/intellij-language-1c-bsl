package org.github._1c_syntax.intellij.bsl;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class BSLFileType extends LanguageFileType {

  public static final BSLFileType INSTANCE = new BSLFileType();

  private BSLFileType() {
    super(BSLLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "BSL File";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "1C (BSL) language file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "bsl";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return BSLIcons.FILE;
  }
}
