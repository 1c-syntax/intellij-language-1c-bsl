/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018
 * Alexey Sosnoviy <labotamy@yandex.ru>, Nikita Gryzlov <nixel2007@gmail.com>
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * IntelliJ Language 1C (BSL) Plugin is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * IntelliJ Language 1C (BSL) Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with IntelliJ Language 1C (BSL) Plugin.
 */
package org.github._1c_syntax.intellij.bsl;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.jetbrains.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.jetbrains.adaptor.lexer.TokenIElementType;
import org.github._1c_syntax.parser.BSLLexer;
import org.github._1c_syntax.parser.BSLParser;
import org.jetbrains.annotations.NotNull;

public class BSLSyntaxHighlighter extends SyntaxHighlighterBase {

  private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

  private static final TextAttributesKey COMMENT =
          TextAttributesKey.createTextAttributesKey("BSL_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  private static final TextAttributesKey BAD_CHARACTER =
          TextAttributesKey.createTextAttributesKey("BSL_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
  private static final TextAttributesKey KEYWORDS =
          TextAttributesKey.createTextAttributesKey("BSL_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

  private static final TextAttributesKey STRING =
          TextAttributesKey.createTextAttributesKey("BSL_STRING", DefaultLanguageHighlighterColors.STRING);

  private static final TextAttributesKey DATETIME =
          TextAttributesKey.createTextAttributesKey("BSL_DATETIME", DefaultLanguageHighlighterColors.STRING);

  private static final TextAttributesKey LITERAL_CONSTANT =
          TextAttributesKey.createTextAttributesKey("BSL_LITERAL_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);

  private static final TextAttributesKey COMPILER_DIRECTIVE =
          TextAttributesKey.createTextAttributesKey("BSL_COMPILER_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);

  private static final TextAttributesKey USING_DIRECTIVE =
          TextAttributesKey.createTextAttributesKey("BSL_USING_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);

  private static final TextAttributesKey PREPROCESSOR_DIRECTIVE =
          TextAttributesKey.createTextAttributesKey("BSL_PREPROCESSOR_DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);

  static {
    PSIElementTypeFactory.defineLanguageIElementTypes(BSLLanguage.INSTANCE,
      BSLParser.tokenNames,
      BSLParser.ruleNames);
  }

  private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
  private static final TextAttributesKey[] COMPILER_DIRECTIVE_KEYS = new TextAttributesKey[]{COMPILER_DIRECTIVE};
  private static final TextAttributesKey[] USING_DIRECTIVE_KEYS = new TextAttributesKey[]{USING_DIRECTIVE};
  private static final TextAttributesKey[] PREPROCESSOR_DIRECTIVE_KEYS = new TextAttributesKey[]{PREPROCESSOR_DIRECTIVE};


  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    BSLLexer lexer = new BSLLexer(null);
    return new ANTLRLexerAdaptor(BSLLanguage.INSTANCE, lexer);
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    if ( !(tokenType instanceof TokenIElementType) ) {
      return EMPTY_KEYS;
    }
    TokenIElementType myType = (TokenIElementType)tokenType;
    int ttype = myType.getANTLRTokenType();
    TextAttributesKey attrKey;

    switch ( ttype ) {
      case BSLLexer.PROCEDURE_KEYWORD:
      case BSLLexer.FUNCTION_KEYWORD:
      case BSLLexer.ENDPROCEDURE_KEYWORD:
      case BSLLexer.ENDFUNCTION_KEYWORD:
      case BSLLexer.EXPORT_KEYWORD:
      case BSLLexer.VAL_KEYWORD:
      case BSLLexer.ENDIF_KEYWORD:
      case BSLLexer.ENDDO_KEYWORD:
      case BSLLexer.IF_KEYWORD:
      case BSLLexer.ELSEIF_KEYWORD:
      case BSLLexer.ELSE_KEYWORD:
      case BSLLexer.THEN_KEYWORD:
      case BSLLexer.WHILE_KEYWORD:
      case BSLLexer.DO_KEYWORD:
      case BSLLexer.FOR_KEYWORD:
      case BSLLexer.TO_KEYWORD:
      case BSLLexer.EACH_KEYWORD:
      case BSLLexer.FROM_KEYWORD:
      case BSLLexer.TRY_KEYWORD:
      case BSLLexer.EXCEPT_KEYWORD:
      case BSLLexer.ENDTRY_KEYWORD:
      case BSLLexer.RETURN_KEYWORD:
      case BSLLexer.CONTINUE_KEYWORD:
      case BSLLexer.RAISE_KEYWORD:
      case BSLLexer.VAR_KEYWORD:
      case BSLLexer.NOT_KEYWORD:
      case BSLLexer.OR_KEYWORD:
      case BSLLexer.AND_KEYWORD:
      case BSLLexer.NEW_KEYWORD:
      case BSLLexer.GOTO_KEYWORD:
      case BSLLexer.BREAK_KEYWORD:
      case BSLLexer.EXECUTE_KEYWORD:
        attrKey = KEYWORDS;
        break;
      case BSLLexer.TRUE :
      case BSLLexer.FALSE :
      case BSLLexer.UNDEFINED :
      case BSLLexer.NULL :
        attrKey = LITERAL_CONSTANT;
        break;
      case BSLLexer.STRING :
      case BSLLexer.STRINGSTART :
      case BSLLexer.STRINGPART :
      case BSLLexer.STRINGTAIL :
        attrKey = STRING;
        break;
      case BSLLexer.DATETIME :
        attrKey = DATETIME;
        break;
      case BSLLexer.LINE_COMMENT :
        attrKey = COMMENT;
        break;
      default :
        return EMPTY_KEYS;
    }
    return new TextAttributesKey[] {attrKey};

//    } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
//      return BAD_CHAR_KEYS;
//    } else if (tokenType.equals(BSLTypes.COMPILER_DIRECTIVE)) {
//      return COMPILER_DIRECTIVE_KEYS;
//    } else if (tokenType.equals(BSLTypes.USING)) {
//      return USING_DIRECTIVE_KEYS;
//    } else if (tokenType.equals(BSLTypes.PREPROCESSOR)) {
//      return PREPROCESSOR_DIRECTIVE_KEYS;
//    } else {
//      return new TextAttributesKey[0];
//    }
  }
}
