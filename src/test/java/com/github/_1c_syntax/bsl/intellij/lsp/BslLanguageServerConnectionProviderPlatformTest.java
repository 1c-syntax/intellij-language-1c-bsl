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
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Платформенные тесты сборки команды запуска BSL Language Server. Проверяется только обвязка
 * провайдера; сам загрузчик — замоканная зависимость (его собственные тесты живут в модуле utils),
 * поэтому тесты не ходят в сеть.
 */
public class BslLanguageServerConnectionProviderPlatformTest extends BasePlatformTestCase {

  public void testExternalJarBuildsJavaJarCommand() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.TRUE;
    settings.javaPath = "java";
    settings.path = "server.jar";
    settings.configurationFile = "";

    var commands = newProvider(mock(BslLanguageServerDownloader.class)).resolveCommands(settings);

    assertEquals(3, commands.size());
    assertEquals("java", commands.get(0));
    assertEquals("-jar", commands.get(1));
    assertEquals(Path.of("server.jar").toAbsolutePath().toString(), commands.get(2));
  }

  public void testExternalJarUsesConfiguredJavaPath() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.TRUE;
    settings.javaPath = "/opt/jdk/bin/java";
    settings.path = "server.jar";
    settings.configurationFile = "";

    var commands = newProvider(mock(BslLanguageServerDownloader.class)).resolveCommands(settings);

    assertEquals("/opt/jdk/bin/java", commands.get(0));
  }

  public void testDownloadedBinaryUsesDownloaderResultAndPrereleaseChannel() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.TRUE;
    settings.prerelease = Boolean.TRUE;
    settings.configurationFile = "";

    var downloader = mock(BslLanguageServerDownloader.class);
    when(downloader.downloadIfNeeded(any(), any())).thenReturn(Path.of("/stub/bsl-language-server"));
    var commands = newProvider(downloader).resolveCommands(settings);

    assertEquals(Path.of("/stub/bsl-language-server").toString(), commands.get(0));
    verify(downloader).downloadIfNeeded(eq(BslLanguageServerReleaseChannel.PRERELEASE), any());
  }

  public void testStableChannelWhenPrereleaseDisabled() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.TRUE;
    settings.prerelease = Boolean.FALSE;
    settings.configurationFile = "";

    var downloader = mock(BslLanguageServerDownloader.class);
    when(downloader.downloadIfNeeded(any(), any())).thenReturn(Path.of("/stub/bsl"));
    newProvider(downloader).resolveCommands(settings);

    verify(downloader).downloadIfNeeded(eq(BslLanguageServerReleaseChannel.STABLE), any());
  }

  public void testInstalledBinaryUsedWhenDownloadDisabled() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.FALSE;
    settings.configurationFile = "";

    var installed = Path.of("/installed/bsl-language-server");
    var downloader = mock(BslLanguageServerDownloader.class);
    when(downloader.installedBinary()).thenReturn(Optional.of(installed));
    var commands = newProvider(downloader).resolveCommands(settings);

    assertEquals(installed.toString(), commands.get(0));
  }

  public void testThrowsWhenDownloadDisabledAndNothingInstalled() {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.FALSE;
    settings.configurationFile = "";

    var downloader = mock(BslLanguageServerDownloader.class);
    when(downloader.installedBinary()).thenReturn(Optional.empty());
    try {
      newProvider(downloader).resolveCommands(settings);
      fail("Expected IOException when nothing is installed and downloading is disabled");
    } catch (IOException expected) {
      // ожидаемо
    }
  }

  public void testProgressListenerReportsFractionAndThrottlesWithinPercent() {
    var total = 200L * 1024 * 1024; // 200 МБ, 1% == 2 МБ
    var indicator = mock(ProgressIndicator.class);
    var listener = BslLanguageServerConnectionProvider.progressListener(indicator);

    listener.onProgress(0, total);                 // 0% — первая перерисовка
    listener.onProgress(1024L * 1024, total);      // всё ещё 0% — троттлится
    listener.onProgress(2L * 1024 * 1024, total);  // 1% — новая перерисовка

    // Три уведомления, но индикатор перерисован только на смене процента — дважды.
    verify(indicator, times(2)).setFraction(anyDouble());
    verify(indicator).setFraction(0.0);
    verify(indicator).setIndeterminate(false);
    verify(indicator).setText2("0.0 MB / 200.0 MB");
    verify(indicator).setText2("2.0 MB / 200.0 MB");
    // Отмену проверяем на каждом блоке, даже на пропущенном троттлингом.
    verify(indicator, times(3)).checkCanceled();
  }

  public void testProgressListenerIsIndeterminateWhenTotalUnknown() {
    var indicator = mock(ProgressIndicator.class);
    var listener = BslLanguageServerConnectionProvider.progressListener(indicator);

    listener.onProgress(512L * 1024, -1);   // 0 МБ — первая перерисовка
    listener.onProgress(768L * 1024, -1);   // всё ещё 0 МБ — троттлится
    listener.onProgress(1024L * 1024, -1);  // 1 МБ — новая перерисовка

    // Размер неизвестен: доля не выставляется, индикатор — неопределённый.
    verify(indicator, never()).setFraction(anyDouble());
    verify(indicator, times(2)).setIndeterminate(true);
    verify(indicator).setText2("0.5 MB");
    verify(indicator).setText2("1.0 MB");
    verify(indicator, times(3)).checkCanceled();
  }

  public void testProgressListenerPropagatesCancellation() {
    var indicator = mock(ProgressIndicator.class);
    doThrow(new ProcessCanceledException()).when(indicator).checkCanceled();
    var listener = BslLanguageServerConnectionProvider.progressListener(indicator);

    try {
      listener.onProgress(1024, 4096);
      fail("Expected ProcessCanceledException to abort the download");
    } catch (ProcessCanceledException expected) {
      // отмена индикатора должна прерывать загрузку
    }
    verify(indicator, never()).setFraction(anyDouble());
  }

  private BslLanguageServerConnectionProvider newProvider(BslLanguageServerDownloader downloader) {
    return new BslLanguageServerConnectionProvider(getProject()) {
      @Override
      BslLanguageServerDownloader createDownloader(Path installDir, @Nullable String token) {
        return downloader;
      }
    };
  }
}
