/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018-2021
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

import com.github._1c_syntax.bsl.intellij.psi.BSLFile;
import com.github._1c_syntax.bsl.parser.BSLLexer;
import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.CaseChangingCharStream;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerAdaptor;
import org.antlr.intellij.adaptor.lexer.ANTLRLexerState;
import org.antlr.intellij.adaptor.lexer.PSIElementTypeFactory;
import org.antlr.intellij.adaptor.parser.ANTLRParserAdaptor;
import org.antlr.intellij.adaptor.psi.ANTLRPsiNode;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

public class BSLParserDefinition implements ParserDefinition {

  static {
    PSIElementTypeFactory.defineLanguageIElementTypes(BSLLanguage.INSTANCE,
      BSLParser.tokenNames,
      BSLParser.ruleNames);
  }

  private static final IFileElementType FILE =
    new IFileElementType(BSLLanguage.INSTANCE);

  private static final TokenSet COMMENTS =
    PSIElementTypeFactory.createTokenSet(BSLLanguage.INSTANCE, BSLLexer.LINE_COMMENT);

  private static final TokenSet WHITESPACE =
    PSIElementTypeFactory.createTokenSet(
      BSLLanguage.INSTANCE,
      BSLLexer.WHITE_SPACE,
      BSLLexer.PREPROC_WHITE_SPACE,
      BSLLexer.PREPROC_NEWLINE,
      BSLLexer.ANNOTATION_WHITE_SPACE
    );

  private static final TokenSet STRINGS =
    PSIElementTypeFactory.createTokenSet(
      BSLLanguage.INSTANCE,
      BSLLexer.STRING,
      BSLLexer.STRINGPART,
      BSLLexer.STRINGSTART,
      BSLLexer.STRINGTAIL
    );

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    BSLLexer lexer = new BSLLexer(null);
    return new ANTLRLexerAdaptor(BSLLanguage.INSTANCE, lexer) {
      @Override
      protected void applyLexerState(CharStream input, ANTLRLexerState state) {
        var inputStream = new CaseChangingCharStream(input, true);
        lexer.setInputStream(inputStream);
        state.apply(lexer);
      }
    };
  }

  @NotNull
  @Override
  public PsiParser createParser(final Project project) {
    final BSLParser parser = new BSLParser(null);
    return new ANTLRParserAdaptor(BSLLanguage.INSTANCE, parser) {
      @Override
      protected ParseTree parse(Parser parser, IElementType root) {
        // start rule depends on root passed in; sometimes we want to create an ID node etc...
        if ( root instanceof IFileElementType ) {
          return ((BSLParser) parser).file();
        }
        // let's hope it's an ID as needed by "rename function"
        return ((BSLParser) parser).complexIdentifier();
      }
    };
  }

  /** "Tokens of those types are automatically skipped by PsiBuilder." */
  @Override
  @NotNull
  public TokenSet getWhitespaceTokens() {
    return WHITESPACE;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return STRINGS;
  }

  @Override
  public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }

  /** What is the IFileElementType of the root parse tree node? It
   *  is called from {@link #createFile(FileViewProvider)} at least.
   */
  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return FILE;
  }

  /** Create the root of your PSI tree (a PsiFile).
   *
   *  From IntelliJ IDEA Architectural Overview:
   *  "A PSI (Program Structure Interface) file is the root of a structure
   *  representing the contents of a file as a hierarchy of elements
   *  in a particular programming language."
   *
   *  PsiFile is to be distinguished from a FileASTNode, which is a parse
   *  tree node that eventually becomes a PsiFile. From PsiFile, we can get
   *  it back via: {@link PsiFile#getNode}.
   */
  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new BSLFile(viewProvider);
  }

  /** Convert from *NON-LEAF* parse node (AST they call it)
   *  to PSI node. Leaves are created in the AST factory.
   *  Rename re-factoring can cause this to be
   *  called on a TokenIElementType since we want to rename ID nodes.
   *  In that case, this method is called to create the root node
   *  but with ID type. Kind of strange, but we can simply create a
   *  ASTWrapperPsiElement to make everything work correctly.
   *
   *  RuleIElementType.  Ah! It's that ID is the root
   *  IElementType requested to parse, which means that the root
   *  node returned from parsetree->PSI conversion.  But, it
   *  must be a CompositeElement! The adaptor calls
   *  rootMarker.done(root) to finish off the PSI conversion.
   *  See {@link ANTLRParserAdaptor#parse(IElementType root,
   *  PsiBuilder)}
   *
   *  If you don't care to distinguish PSI nodes by type, it is
   *  sufficient to create a {@link ANTLRPsiNode} around
   *  the parse tree node
   */
  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return new ANTLRPsiNode(node);
  }
}
