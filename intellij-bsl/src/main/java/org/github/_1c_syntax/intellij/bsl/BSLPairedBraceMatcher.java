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

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.antlr.jetbrains.adapter.lexer.PsiElementTypeFactory;
import org.antlr.jetbrains.adapter.lexer.TokenIElementType;
import org.github._1c_syntax.parser.BSLLexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BSLPairedBraceMatcher implements PairedBraceMatcher {

  @NotNull
  @Override
  public BracePair[] getPairs() {
    PsiElementTypeFactory psiElementTypeFactory = BSLSyntaxHighlighter.getPsiElementTypeFactory();
    List<TokenIElementType> tokenIElementTypes = psiElementTypeFactory.getTokenIElementTypes();

    TokenIElementType lParen = tokenIElementTypes.get(BSLLexer.LPAREN);
    TokenIElementType rParen = tokenIElementTypes.get(BSLLexer.RPAREN);

    TokenIElementType lBracket = tokenIElementTypes.get(BSLLexer.LBRACK);
    TokenIElementType rBracket = tokenIElementTypes.get(BSLLexer.RBRACK);

    return new BracePair[]{
      new BracePair(lParen, rParen, true),
      new BracePair(lBracket, rBracket, false),
    };
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
    return false;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return 0;
  }
}
