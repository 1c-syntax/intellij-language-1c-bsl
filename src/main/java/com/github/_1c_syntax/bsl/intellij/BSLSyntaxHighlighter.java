/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018-2025
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com>
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
package com.github._1c_syntax.bsl.intellij;

import com.github._1c_syntax.bsl.parser.BSLLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerState;
import org.antlr.intellij.adaptor.lexer.TokenIElementType;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
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

  private static final TextAttributesKey NUMBER =
    TextAttributesKey.createTextAttributesKey("BSL_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

  private static final TextAttributesKey DOT =
    TextAttributesKey.createTextAttributesKey("BSL_DOT", DefaultLanguageHighlighterColors.DOT);

  private static final TextAttributesKey SEMICOLON =
    TextAttributesKey.createTextAttributesKey("BSL_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);

  private static final TextAttributesKey COMMA =
    TextAttributesKey.createTextAttributesKey("BSL_COMMA", DefaultLanguageHighlighterColors.COMMA);

  private static final TextAttributesKey PARENTHESES =
    TextAttributesKey.createTextAttributesKey("BSL_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);

  private static final TextAttributesKey BRACKETS =
    TextAttributesKey.createTextAttributesKey("BSL_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);

  private static final TextAttributesKey LITERAL_CONSTANT =
    TextAttributesKey.createTextAttributesKey("BSL_LITERAL_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);

  private static final TextAttributesKey COMPILER_DIRECTIVE =
    TextAttributesKey.createTextAttributesKey("BSL_COMPILER_DIRECTIVE", DefaultLanguageHighlighterColors.METADATA);

  private static final TextAttributesKey ANNOTATIONS =
    TextAttributesKey.createTextAttributesKey("BSL_ANNOTATIONS", DefaultLanguageHighlighterColors.METADATA);

  private static final TextAttributesKey PREPROCESSOR_INSTRUCTION =
    TextAttributesKey.createTextAttributesKey("BSL_PREPROCESSOR_INSTRUCTION", DefaultLanguageHighlighterColors.METADATA);

  private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    BSLLexer lexer = new BSLLexer(CharStreams.fromString(""));
    return new ANTLRLexerAdaptor(BSLLanguage.INSTANCE, lexer) {
      @Override
      protected void applyLexerState(CharStream input, ANTLRLexerState state) {
        lexer.setInputStream(input);
        state.apply(lexer);
      }
    };
  }

  @NotNull
  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    if (!(tokenType instanceof TokenIElementType myType)) {
      return EMPTY_KEYS;
    }
    int antlrTokenType = myType.getANTLRTokenType();
    TextAttributesKey attrKey;

    switch (antlrTokenType) {
      case BSLLexer.PROCEDURE_KEYWORD,
           BSLLexer.FUNCTION_KEYWORD,
           BSLLexer.ENDPROCEDURE_KEYWORD,
           BSLLexer.ENDFUNCTION_KEYWORD,
           BSLLexer.EXPORT_KEYWORD,
           BSLLexer.VAL_KEYWORD,
           BSLLexer.ENDIF_KEYWORD,
           BSLLexer.ENDDO_KEYWORD,
           BSLLexer.IF_KEYWORD,
           BSLLexer.ELSIF_KEYWORD,
           BSLLexer.ELSE_KEYWORD,
           BSLLexer.THEN_KEYWORD,
           BSLLexer.WHILE_KEYWORD,
           BSLLexer.DO_KEYWORD,
           BSLLexer.FOR_KEYWORD,
           BSLLexer.TO_KEYWORD,
           BSLLexer.EACH_KEYWORD,
           BSLLexer.IN_KEYWORD,
           BSLLexer.TRY_KEYWORD,
           BSLLexer.EXCEPT_KEYWORD,
           BSLLexer.ENDTRY_KEYWORD,
           BSLLexer.RETURN_KEYWORD,
           BSLLexer.CONTINUE_KEYWORD,
           BSLLexer.RAISE_KEYWORD,
           BSLLexer.VAR_KEYWORD,
           BSLLexer.NOT_KEYWORD,
           BSLLexer.OR_KEYWORD,
           BSLLexer.AND_KEYWORD,
           BSLLexer.NEW_KEYWORD,
           BSLLexer.GOTO_KEYWORD,
           BSLLexer.BREAK_KEYWORD,
           BSLLexer.EXECUTE_KEYWORD,
           BSLLexer.ADDHANDLER_KEYWORD,
           BSLLexer.REMOVEHANDLER_KEYWORD,
           BSLLexer.ASYNC_KEYWORD,
           BSLLexer.AWAIT_KEYWORD -> attrKey = KEYWORDS;
      case BSLLexer.TRUE, BSLLexer.FALSE, BSLLexer.UNDEFINED, BSLLexer.NULL -> attrKey = LITERAL_CONSTANT;
      case BSLLexer.DECIMAL, BSLLexer.FLOAT -> attrKey = NUMBER;
      case BSLLexer.STRING, BSLLexer.STRINGSTART, BSLLexer.STRINGPART, BSLLexer.STRINGTAIL, BSLLexer.PREPROC_STRING ->
        attrKey = STRING;
      case BSLLexer.DATETIME -> attrKey = DATETIME;
      case BSLLexer.LINE_COMMENT -> attrKey = COMMENT;
      case BSLLexer.HASH,
           BSLLexer.PREPROC_USE_KEYWORD,
           BSLLexer.PREPROC_REGION,
           BSLLexer.PREPROC_END_REGION,
           BSLLexer.PREPROC_AND_KEYWORD,
           BSLLexer.PREPROC_OR_KEYWORD,
           BSLLexer.PREPROC_NOT_KEYWORD,
           BSLLexer.PREPROC_IF_KEYWORD,
           BSLLexer.PREPROC_THEN_KEYWORD,
           BSLLexer.PREPROC_ELSIF_KEYWORD,
           BSLLexer.PREPROC_ELSE_KEYWORD,
           BSLLexer.PREPROC_ENDIF_KEYWORD,
           BSLLexer.PREPROC_EXCLAMATION_MARK,
           BSLLexer.PREPROC_LPAREN,
           BSLLexer.PREPROC_RPAREN,
           BSLLexer.PREPROC_MOBILEAPPCLIENT_SYMBOL,
           BSLLexer.PREPROC_MOBILEAPPSERVER_SYMBOL,
           BSLLexer.PREPROC_MOBILECLIENT_SYMBOL,
           BSLLexer.PREPROC_THICKCLIENTORDINARYAPPLICATION_SYMBOL,
           BSLLexer.PREPROC_THICKCLIENTMANAGEDAPPLICATION_SYMBOL,
           BSLLexer.PREPROC_EXTERNALCONNECTION_SYMBOL,
           BSLLexer.PREPROC_THINCLIENT_SYMBOL,
           BSLLexer.PREPROC_WEBCLIENT_SYMBOL,
           BSLLexer.PREPROC_ATCLIENT_SYMBOL,
           BSLLexer.PREPROC_CLIENT_SYMBOL,
           BSLLexer.PREPROC_ATSERVER_SYMBOL,
           BSLLexer.PREPROC_SERVER_SYMBOL,
           BSLLexer.PREPROC_INSERT,
           BSLLexer.PREPROC_ENDINSERT,
           BSLLexer.PREPROC_DELETE,
           BSLLexer.PREPROC_DELETE_ANY,
           BSLLexer.PREPROC_ENDDELETE,
           BSLLexer.PREPROC_IDENTIFIER,
           BSLLexer.PREPROC_LINUX,
           BSLLexer.PREPROC_WINDOWS,
           BSLLexer.PREPROC_MACOS,
           BSLLexer.PREPROC_ANY,
           BSLLexer.PREPROC_MOBILE_STANDALONE_SERVER,
           BSLLexer.PREPROC_NATIVE -> attrKey = PREPROCESSOR_INSTRUCTION;
      case BSLLexer.AMPERSAND,
           BSLLexer.ANNOTATION_AFTER_SYMBOL,
           BSLLexer.ANNOTATION_AROUND_SYMBOL,
           BSLLexer.ANNOTATION_ATCLIENT_SYMBOL,
           BSLLexer.ANNOTATION_ATCLIENTATSERVER_SYMBOL,
           BSLLexer.ANNOTATION_ATCLIENTATSERVERNOCONTEXT_SYMBOL,
           BSLLexer.ANNOTATION_ATSERVER_SYMBOL,
           BSLLexer.ANNOTATION_ATSERVERNOCONTEXT_SYMBOL,
           BSLLexer.ANNOTATION_BEFORE_SYMBOL,
           BSLLexer.ANNOTATION_CHANGEANDVALIDATE_SYMBOL,
           BSLLexer.ANNOTATION_CUSTOM_SYMBOL,
           BSLLexer.ANNOTATION_UNKNOWN -> attrKey = ANNOTATIONS;
      case BSLLexer.DOT -> attrKey = DOT;
      case BSLLexer.SEMICOLON -> attrKey = SEMICOLON;
      case BSLLexer.COMMA -> attrKey = COMMA;
      case BSLLexer.LPAREN, BSLLexer.RPAREN -> attrKey = PARENTHESES;
      case BSLLexer.LBRACK, BSLLexer.RBRACK -> attrKey = BRACKETS;
      default -> {
        return EMPTY_KEYS;
      }
    }
    return new TextAttributesKey[]{attrKey};
  }
}
