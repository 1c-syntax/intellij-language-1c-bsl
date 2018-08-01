package org.github._1c_syntax.intellij.bsl.psi;

import com.intellij.psi.tree.IElementType;
import org.github._1c_syntax.intellij.bsl.BSLLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BSLTokenType extends IElementType {
  public BSLTokenType(@NotNull @NonNls String debugName) {
    super(debugName, BSLLanguage.INSTANCE);
  }


  @Override
  public String toString() {
    return "BSLTokenType." + super.toString();
  }
}
