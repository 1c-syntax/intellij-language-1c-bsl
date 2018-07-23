package org.bslplugin;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.bslplugin.psi.BSLTypes;
import org.jetbrains.annotations.NotNull;

public class BSLCompletionContributor extends CompletionContributor {
  public BSLCompletionContributor() {
    extend(CompletionType.BASIC,
      PlatformPatterns.psiElement(BSLTypes.IDENTIFIER).withLanguage(BSLLanguage.INSTANCE),
      new CompletionProvider<CompletionParameters>() {
        public void addCompletions(@NotNull CompletionParameters parameters,
                                   ProcessingContext context,
                                   @NotNull CompletionResultSet resultSet) {
          resultSet.addElement(LookupElementBuilder.create("Hello"));
        }
      }
    );
  }
}