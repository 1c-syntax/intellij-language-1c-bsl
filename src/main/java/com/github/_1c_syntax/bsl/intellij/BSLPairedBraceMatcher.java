/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018-2019
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>
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
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adapter.lexer.PsiElementTypeFactory;
import org.antlr.jetbrains.adapter.lexer.TokenIElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BSLPairedBraceMatcher implements PairedBraceMatcher {

  @NotNull
  @Override
  public BracePair[] getPairs() {
    PsiElementTypeFactory psiElementTypeFactory = BSLSyntaxHighlighter.getPsiElementTypeFactory();
    List<TokenIElementType> tokenTypes = psiElementTypeFactory.getTokenIElementTypes();

    return new BracePair[]{
      new BracePair(tokenTypes.get(BSLLexer.LPAREN), tokenTypes.get(BSLLexer.RPAREN), true),
      new BracePair(tokenTypes.get(BSLLexer.LBRACK), tokenTypes.get(BSLLexer.RBRACK), false),
      new BracePair(tokenTypes.get(BSLLexer.IF_KEYWORD), tokenTypes.get(BSLLexer.ENDIF_KEYWORD), true),
      new BracePair(tokenTypes.get(BSLLexer.WHILE_KEYWORD), tokenTypes.get(BSLLexer.ENDDO_KEYWORD), true),
      new BracePair(tokenTypes.get(BSLLexer.FOR_KEYWORD), tokenTypes.get(BSLLexer.ENDDO_KEYWORD), true),
      new BracePair(tokenTypes.get(BSLLexer.TRY_KEYWORD), tokenTypes.get(BSLLexer.ENDTRY_KEYWORD), true),
    };
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lBraceType, @Nullable IElementType contextType) {
    return true;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return 0;
  }
}
