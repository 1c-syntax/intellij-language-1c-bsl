package org.github._1c_syntax.intellij.bsl.lsp.server;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;

import java.io.*;

public class BSLLSPLauncher {

  public static void main(String[] args) throws FileNotFoundException {

    InputStream in = System.in;
    OutputStream out = System.out;

    LanguageServer server = new BSLLanguageServer();
    PrintWriter tracer = new PrintWriter(new File("D:\\git\\1c-syntax\\idea-language-1c-bsl\\intellij-bsl\\build\\idea-sandbox\\system\\log\\tracer.log"));
    Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, in, out, false, tracer);

    LanguageClient client = launcher.getRemoteProxy();
    ((LanguageClientAware) server).connect(client);

    launcher.startListening();
  }
}
