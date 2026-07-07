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
import com.github._1c_syntax.utils.downloader.DownloadProgressListener;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.server.CannotStartProcessException;
import com.redhat.devtools.lsp4ij.server.ProcessStreamConnectionProvider;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Запускает процесс BSL Language Server для LSP4IJ.
 *
 * <p>Источник исполняемого файла определяется настройками: если задан путь к внешнему
 * {@code bsl-language-server.jar} — запускается {@code java -jar}; иначе native-image сборка
 * сервера скачивается с GitHub-релизов через {@link BslLanguageServerDownloader}. Дополнительные
 * опции JVM пробрасываются через переменную окружения {@code _JAVA_OPTIONS} в обоих случаях.
 */
public class BslLanguageServerConnectionProvider extends ProcessStreamConnectionProvider {

  private static final Logger LOG = Logger.getInstance(BslLanguageServerConnectionProvider.class);

  private static final String JAVA_OPTIONS_ENV = "_JAVA_OPTIONS";
  private static final String GITHUB_TOKEN_ENV = "LANGUAGE_1C_BSL_GITHUB_TOKEN";
  private static final String PROGRESS_TITLE = "Preparing BSL Language Server";
  private static final String PROGRESS_DOWNLOADING = "Downloading BSL Language Server";

  private final Project project;

  public BslLanguageServerConnectionProvider(Project project) {
    this.project = project;
  }

  @Override
  public void start() throws CannotStartProcessException {
    var settings = LanguageServerSettingsState.getInstance();
    try {
      setCommands(resolveCommandsWithProgress(settings));
    } catch (IOException e) {
      var failure = new CannotStartProcessException("Failed to prepare BSL Language Server: " + e.getMessage());
      failure.initCause(e);
      throw failure;
    }

    var basePath = project.getBasePath();
    if (basePath != null) {
      setWorkingDirectory(basePath);
    }

    var javaOptions = mergedJavaOptions(System.getenv(JAVA_OPTIONS_ENV), settings.javaOpts);
    if (javaOptions != null) {
      setUserEnvironmentVariables(Map.of(JAVA_OPTIONS_ENV, javaOptions));
    }

    super.start();
  }

  List<String> resolveCommands(LanguageServerSettingsState settings) throws IOException {
    return resolveCommands(settings, DownloadProgressListener.NONE);
  }

  private List<String> resolveCommands(LanguageServerSettingsState settings,
                                       DownloadProgressListener progressListener) throws IOException {
    var commands = new ArrayList<String>();

    if (Boolean.TRUE.equals(settings.externalJar)) {
      var javaPath = settings.javaPath.isBlank() ? "java" : settings.javaPath;
      commands.add(javaPath);
      commands.add("-jar");
      commands.add(Path.of(settings.path).toAbsolutePath().toString());
    } else {
      commands.add(resolveDownloadedBinary(settings, progressListener).toString());
    }

    var configurationFile = resolveConfigurationFile(settings);
    if (configurationFile != null) {
      commands.add("-c");
      commands.add(configurationFile);
    }

    return commands;
  }

  /**
   * Готовит команду запуска под индикатором прогресса, чтобы скачивание сервера отображалось
   * прогресс-баром, а не бесконечным «крутящимся» индикатором. Если вызов уже выполняется под
   * индикатором прогресса, используется он; иначе запускается фоновая задача. Метод блокирует
   * поток до готовности команды — как и раньше, старт сервера ждёт скачивания.
   */
  private List<String> resolveCommandsWithProgress(LanguageServerSettingsState settings) throws IOException {
    var progressManager = ProgressManager.getInstance();
    var currentIndicator = progressManager.getProgressIndicator();
    if (currentIndicator != null) {
      return resolveCommands(settings, progressListener(currentIndicator));
    }

    var result = new CompletableFuture<List<String>>();
    progressManager.run(new Task.Backgroundable(project, PROGRESS_TITLE, true) {
      @Override
      public void run(ProgressIndicator indicator) {
        try {
          result.complete(resolveCommands(settings, progressListener(indicator)));
        } catch (Throwable t) {
          result.completeExceptionally(t);
        }
      }
    });

    return awaitCommands(result);
  }

  private static List<String> awaitCommands(CompletableFuture<List<String>> result) throws IOException {
    try {
      return result.get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("BSL Language Server preparation was interrupted", e);
    } catch (ExecutionException e) {
      var cause = e.getCause();
      if (cause instanceof IOException io) {
        throw io;
      }
      if (cause instanceof RuntimeException runtime) {
        throw runtime;
      }
      if (cause instanceof Error error) {
        throw error;
      }
      throw new IOException("Failed to prepare BSL Language Server", cause);
    }
  }

  private static DownloadProgressListener progressListener(ProgressIndicator indicator) {
    // Загрузчик уведомляет о каждом прочитанном блоке (16 КБ) — для архива в десятки МБ это
    // тысячи вызовов. Отмену проверяем на каждом (дёшево и отзывчиво), но индикатор перерисовываем
    // не чаще, чем меняется целый процент (или, при неизвестном размере, целый мегабайт).
    var lastReported = new long[]{-1};
    return (bytesRead, totalBytes) -> {
      indicator.checkCanceled();
      var tick = totalBytes > 0 ? bytesRead * 100 / totalBytes : bytesRead / (1024 * 1024);
      if (tick == lastReported[0]) {
        return;
      }
      lastReported[0] = tick;
      indicator.setText(PROGRESS_DOWNLOADING);
      if (totalBytes > 0) {
        indicator.setIndeterminate(false);
        indicator.setFraction((double) bytesRead / (double) totalBytes);
        indicator.setText2(formatMegabytes(bytesRead) + " / " + formatMegabytes(totalBytes));
      } else {
        indicator.setIndeterminate(true);
        indicator.setText2(formatMegabytes(bytesRead));
      }
    };
  }

  private static String formatMegabytes(long bytes) {
    return String.format(Locale.ROOT, "%.1f MB", bytes / (1024.0 * 1024.0));
  }

  private Path resolveDownloadedBinary(LanguageServerSettingsState settings,
                                       DownloadProgressListener progressListener) throws IOException {
    var installDir = settings.installDir.isBlank()
      ? Path.of(PathManager.getSystemPath(), "bsl-language-server")
      : Path.of(settings.installDir);

    var downloader = createDownloader(installDir, resolveToken());

    if (Boolean.TRUE.equals(settings.downloadServer)) {
      var channel = Boolean.TRUE.equals(settings.prerelease)
        ? BslLanguageServerReleaseChannel.PRERELEASE
        : BslLanguageServerReleaseChannel.STABLE;
      return downloader.downloadIfNeeded(channel, progressListener);
    }

    return downloader.installedBinary()
      .orElseThrow(() -> new IOException(
        "BSL Language Server is not installed and downloading is disabled. "
          + "Set the external jar path or enable downloading in the settings."));
  }

  /**
   * Точка расширения для тестов: создаёт загрузчик сервера. Переопределяется в тестах,
   * чтобы подменить обращение к GitHub на заглушку.
   */
  BslLanguageServerDownloader createDownloader(Path installDir, @Nullable String token) {
    return new BslLanguageServerDownloader(installDir, token);
  }

  private @Nullable String resolveConfigurationFile(LanguageServerSettingsState settings) {
    return resolveConfigurationFile(project.getBasePath(), settings.configurationFile);
  }

  /**
   * Возвращает абсолютный путь к файлу конфигурации сервера относительно корня проекта,
   * либо {@code null}, если корень неизвестен, имя не задано или файл отсутствует.
   */
  static @Nullable String resolveConfigurationFile(@Nullable String basePath, String configurationFile) {
    if (basePath == null || configurationFile.isBlank()) {
      return null;
    }
    var path = Path.of(basePath, configurationFile);
    if (Files.exists(path)) {
      return path.toString();
    }
    LOG.warn("Configured BSL Language Server config file not found: " + path);
    return null;
  }

  private static String resolveToken() {
    var token = LanguageServerSettingsState.githubToken();
    if (token != null && !token.isBlank()) {
      return token;
    }
    var envToken = System.getenv(GITHUB_TOKEN_ENV);
    return envToken == null || envToken.isBlank() ? null : envToken;
  }

  /**
   * Объединяет уже заданное значение {@code _JAVA_OPTIONS} с пользовательскими опциями.
   *
   * @param existing текущее значение {@code _JAVA_OPTIONS} (обычно из окружения), может быть {@code null}
   * @param javaOpts пользовательские опции из настроек
   * @return итоговое значение или {@code null}, если пользовательские опции пусты
   */
  static @Nullable String mergedJavaOptions(@Nullable String existing, @Nullable String javaOpts) {
    var options = javaOpts == null ? "" : javaOpts.strip();
    if (options.isEmpty()) {
      return null;
    }
    return existing == null || existing.isBlank() ? options : existing + " " + options;
  }
}
