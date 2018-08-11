package org.github._1c_syntax.intellij.bsl;

import com.intellij.lexer.FlexAdapter;
import org.github._1c_syntax.parser.BSLLexer;

import java.io.Reader;

public class BSLLexerAdapter extends FlexAdapter {
  public BSLLexerAdapter() {
    super(new BSLLexer((Reader) null));
  }
}
