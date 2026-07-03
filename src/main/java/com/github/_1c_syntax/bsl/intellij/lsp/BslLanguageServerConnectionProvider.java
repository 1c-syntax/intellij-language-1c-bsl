/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018-2026
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
package com.github._1c_syntax.bsl.intellij.lsp;

import com.github._1c_syntax.bsl.intellij.settings.LanguageServerSettingsState;
import com.github._1c_syntax.utils.downloader.BslLanguageServerDownloader;
import com.github._1c_syntax.utils.downloader.BslLanguageServerReleaseChannel;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.server.CannotStartProcessException;
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Запускает процесс BSL Language Server для LSP4IJ.
 *
 * <p>Источник исполняемого файла определяется настройками: если задан путь к внешнему
 * {@code bsl-language-server.jar} — запускается {@code java -jar}; иначе native-image сборка
 * сервера скачивается с GitHub-релизов через {@link BslLanguageServerDownloader}. Дополнительные
 * опции JVM пробрасываются через переменную окружения {@code _JAVA_OPTIONS} в обоих случаях.
 */
public class BslLanguageServerConnectionProvider extends ProcessStreamConnectionProvider {

  private static final String JAVA_OPTIONS_ENV = "_JAVA_OPTIONS";
  private static final String GITHUB_TOKEN_ENV = "LANGUAGE_1C_BSL_GITHUB_TOKEN";

  private final Project project;

  public BslLanguageServerConnectionProvider(Project project) {
    this.project = project;
  }

  @Override
  public void start() throws CannotStartProcessException {
    var settings = LanguageServerSettingsState.getInstance();
    try {
      setCommands(resolveCommands(settings));
    } catch (IOException e) {
      var failure = new CannotStartProcessException("Failed to prepare BSL Language Server: " + e.getMessage());
      failure.initCause(e);
      throw failure;
    }

    var basePath = project.getBasePath();
    if (basePath != null) {
      setWorkingDirectory(basePath);
    }

    var javaOptions = mergedJavaOptions(settings.javaOpts);
    if (javaOptions != null) {
      setUserEnvironmentVariables(Map.of(JAVA_OPTIONS_ENV, javaOptions));
    }

    super.start();
  }

  private List<String> resolveCommands(LanguageServerSettingsState settings) throws IOException {
    var commands = new ArrayList<String>();

    if (!settings.path.isBlank()) {
      var javaPath = settings.javaPath.isBlank() ? "java" : settings.javaPath;
      commands.add(javaPath);
      commands.add("-jar");
      commands.add(Path.of(settings.path).toAbsolutePath().toString());
    } else {
      commands.add(resolveDownloadedBinary(settings).toString());
    }

    var configurationFile = resolveConfigurationFile(settings);
    if (configurationFile != null) {
      commands.add("-c");
      commands.add(configurationFile);
    }

    return commands;
  }

  private Path resolveDownloadedBinary(LanguageServerSettingsState settings) throws IOException {
    var installDir = settings.installDir.isBlank()
      ? Path.of(PathManager.getSystemPath(), "bsl-language-server")
      : Path.of(settings.installDir);

    var downloader = new BslLanguageServerDownloader(installDir, resolveToken(settings));

    if (Boolean.TRUE.equals(settings.downloadServer)) {
      var channel = Boolean.TRUE.equals(settings.prerelease)
        ? BslLanguageServerReleaseChannel.PRERELEASE
        : BslLanguageServerReleaseChannel.STABLE;
      return downloader.downloadIfNeeded(channel);
    }

    return downloader.installedBinary()
      .orElseThrow(() -> new IOException(
        "BSL Language Server is not installed and downloading is disabled. "
          + "Set the external jar path or enable downloading in the settings."));
  }

  private String resolveConfigurationFile(LanguageServerSettingsState settings) {
    var basePath = project.getBasePath();
    if (basePath == null || settings.configurationFile.isBlank()) {
      return null;
    }
    var configurationFile = Path.of(basePath, settings.configurationFile);
    return Files.exists(configurationFile) ? configurationFile.toString() : null;
  }

  private static String resolveToken(LanguageServerSettingsState settings) {
    if (!settings.githubToken.isBlank()) {
      return settings.githubToken;
    }
    var envToken = System.getenv(GITHUB_TOKEN_ENV);
    return envToken == null || envToken.isBlank() ? null : envToken;
  }

  private static String mergedJavaOptions(String javaOpts) {
    var options = javaOpts == null ? "" : javaOpts.strip();
    if (options.isEmpty()) {
      return null;
    }
    var existing = System.getenv(JAVA_OPTIONS_ENV);
    return existing == null || existing.isBlank() ? options : existing + " " + options;
  }
}
