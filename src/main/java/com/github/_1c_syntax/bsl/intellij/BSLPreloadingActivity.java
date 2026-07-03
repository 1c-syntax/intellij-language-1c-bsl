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

import com.github._1c_syntax.bsl.intellij.files.BSLFileType;
import com.github._1c_syntax.bsl.intellij.files.OSFileType;
import com.github._1c_syntax.bsl.intellij.settings.LanguageServerSettingsState;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.ProcessBuilderServerDefinition;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.wso2.lsp4intellij.client.languageserver.serverdefinition.LanguageServerDefinition.SPLIT_CHAR;

public class BSLPreloadingActivity extends PreloadingActivity {

  @Override
  public void preload(@NotNull ProgressIndicator indicator) {

    LanguageServerSettingsState languageServerSettings = ApplicationManager.getApplication().getService(LanguageServerSettingsState.class);

    if (!languageServerSettings.enabled) {
      return;
    }

    Path languageServer;

    if (!languageServerSettings.path.equals("")) {
      languageServer = Paths.get(languageServerSettings.path).toAbsolutePath();
    } else {
      String pluginsPath = PathManager.getPluginsPath();

      File libDir = Paths.get(pluginsPath, "Language 1C (BSL)", "lib").toFile();
      File[] files = libDir.listFiles(
        (File dir, String name) -> name.startsWith("bsl-language-server-") && name.endsWith(".jar")
      );
      if (files == null || files.length == 0) {
        Notification notification = new Notification(
          "Language 1C (BSL)",
          "BSL Language server is not found",
          String.format("Check %s dir. Is plugin installed correctly?", libDir.getAbsolutePath()),
          NotificationType.ERROR
        );
        Notifications.Bus.notify(notification);
        return;
      }

      languageServer = files[0].toPath().toAbsolutePath();
    }

    List<String> args = new ArrayList<>();
    args.add("java");
    args.add("-jar");
    args.add(languageServer.toString());
    args.add("-c");
    args.add(".bsl-language-server.json");

    String extensions = BSLFileType.INSTANCE.getDefaultExtension() + SPLIT_CHAR + OSFileType.INSTANCE.getDefaultExtension();
    ProcessBuilder process = new ProcessBuilder(args);
    process.directory(new File(".").getAbsoluteFile());

    IntellijLanguageClient.addServerDefinition(
      new ProcessBuilderServerDefinition(extensions, Map.of("bsl", "bsl,os"), process)
    );
  }

}
