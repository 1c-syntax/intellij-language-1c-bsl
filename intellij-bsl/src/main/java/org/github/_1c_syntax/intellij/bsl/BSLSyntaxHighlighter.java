package org.github._1c_syntax.intellij.bsl;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.github._1c_syntax.parser.psi.BSLTypes;
import org.jetbrains.annotations.NotNull;

public class BSLSyntaxHighlighter extends SyntaxHighlighterBase {
  public static final TextAttributesKey COMMENT =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey BAD_CHARACTER =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
  public static final TextAttributesKey KEYWORDS =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

  public static final TextAttributesKey STRING =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_STRING", DefaultLanguageHighlighterColors.STRING);

  public static final TextAttributesKey DATETIME =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_DATETIME", DefaultLanguageHighlighterColors.STRING);

  public static final TextAttributesKey LITERAL_CONSTANT =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_LITERAL_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);

  public static final TextAttributesKey COMPILER_DIRECTIVE =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_COMPILER_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);

  public static final TextAttributesKey USING_DIRECTIVE =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_USING_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);

  public static final TextAttributesKey PREPROCESSOR_DIRECTIVE =
          TextAttributesKey.createTextAttributesKey("ONESCRIPT_PREPROCESSOR_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);

  private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
  private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
  private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORDS};
  private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
  private static final TextAttributesKey[] DATETIME_KEYS = new TextAttributesKey[]{DATETIME};
  private static final TextAttributesKey[] LITERAL_CONSTANT_KEYS = new TextAttributesKey[]{LITERAL_CONSTANT};
  private static final TextAttributesKey[] COMPILER_DIRECTIVE_KEYS = new TextAttributesKey[]{COMPILER_DIRECTIVE};
  private static final TextAttributesKey[] USING_DIRECTIVE_KEYS = new TextAttributesKey[]{USING_DIRECTIVE};
  private static final TextAttributesKey[] PREPROCESSOR_DIRECTIVE_KEYS = new TextAttributesKey[]{PREPROCESSOR_DIRECTIVE};


  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return new BSLLexerAdapter();
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    if (tokenType.equals(BSLTypes.COMMENT)) {
      return COMMENT_KEYS;
    } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
      return BAD_CHAR_KEYS;
    } else if (tokenType.equals(BSLTypes.STRING)
            || tokenType.equals(BSLTypes.STRINGSTART)
            || tokenType.equals(BSLTypes.STRINGPART)
            || tokenType.equals(BSLTypes.STRINGTAIL)) {
      return STRING_KEYS;
    } else if (tokenType.equals(BSLTypes.DATETIME)) {
      return DATETIME_KEYS;
    } else if (tokenType.equals(BSLTypes.COMPILER_DIRECTIVE)) {
      return COMPILER_DIRECTIVE_KEYS;
    } else if (tokenType.equals(BSLTypes.USING)) {
      return USING_DIRECTIVE_KEYS;
    } else if (tokenType.equals(BSLTypes.PREPROCESSOR)) {
      return PREPROCESSOR_DIRECTIVE_KEYS;
    } else if (tokenType.equals(BSLTypes.BOOLEAN_TRUE)
            || tokenType.equals(BSLTypes.BOOLEAN_FALSE)
            || tokenType.equals(BSLTypes.UNDEFINED)
            || tokenType.equals(BSLTypes.NULL)) {
      return LITERAL_CONSTANT_KEYS;
    } else if (tokenType.toString().endsWith("_KEYWORD")) {
      return KEYWORD_KEYS;
    } else {
      return new TextAttributesKey[0];
    }
  }
}