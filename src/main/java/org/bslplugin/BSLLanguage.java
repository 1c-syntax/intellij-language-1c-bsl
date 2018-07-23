package org.bslplugin;

import com.intellij.lang.Language;
import org.bslplugin.lsp.server.BSLLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

public class BSLLanguage extends Language {

  public static final BSLLanguage INSTANCE = new BSLLanguage();

  private BSLLanguage() {
    super("BSL");

    LanguageServer server = new BSLLanguageServer();
    Launcher<LanguageClient> launcher =
            LSPLauncher.createServerLauncher(
              server,
              System.in,
              System.out
            );

    LanguageClient client = launcher.getRemoteProxy();
    ((LanguageClientAware)server).connect(client);

    launcher.startListening();
  }
}
