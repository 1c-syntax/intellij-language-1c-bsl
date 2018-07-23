package org.bslplugin;

import com.github.gtache.lsp.client.languageserver.serverdefinition.ArtifactLanguageServerDefinition;
import com.github.gtache.lsp.client.languageserver.serverdefinition.LanguageServerDefinition;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

public class BSLPreloadingActivity extends PreloadingActivity {

  @Override
  public void preload(@NotNull ProgressIndicator indicator) {
    LanguageServerDefinition.register(
      new ArtifactLanguageServerDefinition(
        "bsl",
        "org.bslplugin",
        "BSLLSPLauncher",
        ArrayUtils.EMPTY_STRING_ARRAY
      )
    );
  }

}
