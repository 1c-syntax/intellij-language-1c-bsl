package org.bslplugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.bslplugin.BSLFileType;
import org.bslplugin.BSLLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BSLFile extends PsiFileBase {

  public BSLFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, BSLLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return BSLFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "BSL File";
  }

  @Nullable
  @Override
  public Icon getIcon(int flags) {
    return super.getIcon(flags);
  }
}
