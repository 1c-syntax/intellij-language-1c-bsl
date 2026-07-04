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

import org.junit.Test;
import org.kohsuke.github.extras.HttpClientGitHubConnector;

import java.net.http.HttpClient;

import static org.junit.Assert.assertNotNull;

/**
 * Регрессионный тест на issue #29. {@code github-api} — многорелизный JAR: рабочая реализация
 * {@code HttpClientGitHubConnector} лежит в {@code META-INF/versions/11}, а в корне — Java 8-
 * заглушка без конструктора {@code (HttpClient)}. Classloader плагинов IntelliJ игнорирует
 * {@code META-INF/versions/**} ([IDEA-220300]), поэтому грузилась заглушка и загрузчик BSL LS падал
 * с {@code NoSuchMethodError}. Сборка «расплющивает» jar (поднимает классы из
 * {@code META-INF/versions/11} в корень); тест проверяет, что рабочая реализация действительно
 * доступна из корня — как через рефлексию, так и через фактическое создание коннектора.
 */
public class GithubApiFlattenTest {

  @Test
  public void httpClientConstructorIsAvailable() throws Exception {
    // именно этого конструктора не хватало в issue #29 (грузилась Java 8-заглушка без него)
    assertNotNull(HttpClientGitHubConnector.class.getConstructor(HttpClient.class));
  }

  @Test
  public void httpClientConnectorIsConstructible() {
    // ровно этот вызов падал с NoSuchMethodError в issue #29
    // (BslLanguageServerDownloader.latestRelease → new HttpClientGitHubConnector(httpClient))
    assertNotNull(new HttpClientGitHubConnector(HttpClient.newHttpClient()));
  }
}
