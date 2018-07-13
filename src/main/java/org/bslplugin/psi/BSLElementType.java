package org.bslplugin.psi;

import com.intellij.psi.tree.IElementType;
import org.bslplugin.BSLLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BSLElementType extends IElementType {
  public BSLElementType(@NotNull @NonNls String debugName) {
    super(debugName, BSLLanguage.INSTANCE);
  }
}
