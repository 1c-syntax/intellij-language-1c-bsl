/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018-2019
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Gryzlov <nixel2007@gmail.com>
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

import com.github.lsp4intellij.IntellijLanguageClient;
import com.github.lsp4intellij.client.languageserver.serverdefinition.ExeLanguageServerDefinition;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import org.github._1c_syntax.intellij.bsl.files.BSLFileType;
import org.github._1c_syntax.intellij.bsl.files.OSFileType;
import org.github._1c_syntax.intellij.bsl.settings.LanguageServerSettingsState;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.github.lsp4intellij.client.languageserver.serverdefinition.LanguageServerDefinition.SPLIT_CHAR;

public class BSLPreloadingActivity extends PreloadingActivity {

  @Override
  public void preload(@NotNull ProgressIndicator indicator) {

    LanguageServerSettingsState languageServerSettings = ServiceManager.getService(LanguageServerSettingsState.class);

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
    args.add("-jar");
    args.add(languageServer.toString());

    args.add("--diagnosticLanguage");
    args.add(languageServerSettings.diagnosticLanguage.getLanguageCode());

    String extensions = BSLFileType.INSTANCE.getDefaultExtension() + SPLIT_CHAR + OSFileType.INSTANCE.getDefaultExtension();
    IntellijLanguageClient.addServerDefinition(
      new ExeLanguageServerDefinition(extensions, "java", args.toArray(new String[0]))
    );
  }

}
