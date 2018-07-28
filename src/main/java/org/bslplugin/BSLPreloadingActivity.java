package org.bslplugin;

import com.github.gtache.lsp.client.languageserver.serverdefinition.ExeLanguageServerDefinition;
import com.github.gtache.lsp.client.languageserver.serverdefinition.LanguageServerDefinition;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BSLPreloadingActivity extends PreloadingActivity {

  @Override
  public void preload(@NotNull ProgressIndicator indicator) {

    String pluginsPath = PathManager.getPluginsPath();
    Path languageServer = Paths.get(pluginsPath, "Language 1C (BSL)", "lib", "languageserver-1.0.jar");

    List<String> args = new ArrayList<>();
    args.add("-jar");
    args.add(languageServer.toString());
    args.add("-debug");
    LanguageServerDefinition.register(
      new ExeLanguageServerDefinition("bsl", "java", args.toArray(new String[0]))
    );
  }

}
