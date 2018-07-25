package org.bslplugin;

import com.github.gtache.lsp.client.languageserver.serverdefinition.ArtifactLanguageServerDefinition;
import com.github.gtache.lsp.client.languageserver.serverdefinition.LanguageServerDefinition;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;

public class BSLPreloadingActivity extends PreloadingActivity {

  @Override
  public void preload(@NotNull ProgressIndicator indicator) {
    LanguageServerDefinition.register(
      new ArtifactLanguageServerDefinition("bsl", "org.bslplugin:languageserver:1.0", "org.bslplugin.lsp.server.BSLLSPLauncher", new String[]{})
    );
  }

}
