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
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Платформенные тесты сборки команды запуска BSL Language Server. Обращение к GitHub за
 * скачиванием сервера подменяется заглушкой {@link StubDownloader}, поэтому тесты не ходят в сеть.
 */
public class BslLanguageServerConnectionProviderPlatformTest extends BasePlatformTestCase {

  public void testExternalJarBuildsJavaJarCommand() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.TRUE;
    settings.javaPath = "java";
    settings.path = "server.jar";
    settings.configurationFile = "";

    var commands = newProvider(new StubDownloader(Path.of("/stub/bsl"), Optional.empty()))
      .resolveCommands(settings);

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

    var commands = newProvider(new StubDownloader(Path.of("/stub/bsl"), Optional.empty()))
      .resolveCommands(settings);

    assertEquals("/opt/jdk/bin/java", commands.get(0));
  }

  public void testDownloadedBinaryUsesDownloaderResultAndPrereleaseChannel() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.TRUE;
    settings.prerelease = Boolean.TRUE;
    settings.configurationFile = "";

    var stub = new StubDownloader(Path.of("/stub/bsl-language-server"), Optional.empty());
    var commands = newProvider(stub).resolveCommands(settings);

    assertEquals(Path.of("/stub/bsl-language-server").toString(), commands.get(0));
    assertEquals(BslLanguageServerReleaseChannel.PRERELEASE, stub.requestedChannel);
  }

  public void testStableChannelWhenPrereleaseDisabled() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.TRUE;
    settings.prerelease = Boolean.FALSE;
    settings.configurationFile = "";

    var stub = new StubDownloader(Path.of("/stub/bsl"), Optional.empty());
    newProvider(stub).resolveCommands(settings);

    assertEquals(BslLanguageServerReleaseChannel.STABLE, stub.requestedChannel);
  }

  public void testInstalledBinaryUsedWhenDownloadDisabled() throws IOException {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.FALSE;
    settings.configurationFile = "";

    var installed = Path.of("/installed/bsl-language-server");
    var commands = newProvider(new StubDownloader(Path.of("/unused"), Optional.of(installed)))
      .resolveCommands(settings);

    assertEquals(installed.toString(), commands.get(0));
  }

  public void testThrowsWhenDownloadDisabledAndNothingInstalled() {
    var settings = new LanguageServerSettingsState();
    settings.externalJar = Boolean.FALSE;
    settings.downloadServer = Boolean.FALSE;
    settings.configurationFile = "";

    try {
      newProvider(new StubDownloader(Path.of("/unused"), Optional.empty())).resolveCommands(settings);
      fail("Expected IOException when nothing is installed and downloading is disabled");
    } catch (IOException expected) {
      // ожидаемо
    }
  }

  private BslLanguageServerConnectionProvider newProvider(BslLanguageServerDownloader downloader) {
    return new BslLanguageServerConnectionProvider(getProject()) {
      @Override
      BslLanguageServerDownloader createDownloader(Path installDir, @Nullable String token) {
        return downloader;
      }
    };
  }

  /**
   * Заглушка загрузчика: не обращается к GitHub, возвращает заранее заданный бинарь и запоминает
   * запрошенный канал релизов.
   */
  private static final class StubDownloader extends BslLanguageServerDownloader {

    private final Path binary;
    private final Optional<Path> installed;
    private BslLanguageServerReleaseChannel requestedChannel;

    StubDownloader(Path binary, Optional<Path> installed) {
      super(Path.of("."), java.net.http.HttpClient.newHttpClient(), null);
      this.binary = binary;
      this.installed = installed;
    }

    @Override
    public Path downloadIfNeeded(BslLanguageServerReleaseChannel channel,
                                 DownloadProgressListener progressListener) {
      requestedChannel = channel;
      return binary;
    }

    @Override
    public Optional<Path> installedBinary() {
      return installed;
    }
  }
}
