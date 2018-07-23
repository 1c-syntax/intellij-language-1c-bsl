package org.bslplugin;

import org.bslplugin.lsp.server.BSLLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

public class BSLLSPLauncher {
  private LanguageServer server;

  public BSLLSPLauncher() {

    server = new BSLLanguageServer();
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
