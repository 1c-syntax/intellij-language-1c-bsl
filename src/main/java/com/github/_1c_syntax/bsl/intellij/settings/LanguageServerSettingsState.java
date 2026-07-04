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
package com.github._1c_syntax.bsl.intellij.settings;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
  name = "LanguageServerSettingsState",
  storages = @Storage("intellij-bsl.xml")
)
public class LanguageServerSettingsState implements PersistentStateComponent<LanguageServerSettingsState> {

  /**
   * Запускать ли BSL Language Server.
   */
  public Boolean enabled = Boolean.TRUE;

  /**
   * Использовать внешний {@code bsl-language-server.jar} ({@link #path}) вместо скачивания сервера.
   */
  public Boolean externalJar = Boolean.FALSE;

  /**
   * Путь к внешнему {@code bsl-language-server.jar} (используется при {@link #externalJar}).
   * Запуск через {@link #javaPath}.
   */
  public String path = "";

  /**
   * Скачивать сервер с GitHub-релизов, если внешний jar не задан.
   */
  public Boolean downloadServer = Boolean.TRUE;

  /**
   * Использовать канал pre-release при скачивании сервера.
   */
  public Boolean prerelease = Boolean.FALSE;

  /**
   * Каталог установки скачанного сервера. Пусто — служебный каталог плагина.
   */
  public String installDir = "";

  /**
   * Путь к исполняемому файлу Java для запуска внешнего jar.
   */
  public String javaPath = "java";

  /**
   * Дополнительные опции JVM, пробрасываются в {@code _JAVA_OPTIONS}
   * (и для внешнего jar, и для native-image сборки).
   */
  public String javaOpts = "-Xmx4g";

  /**
   * Имя файла конфигурации BSL Language Server относительно корня проекта.
   */
  public String configurationFile = ".bsl-language-server.json";

  public static LanguageServerSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(LanguageServerSettingsState.class);
  }

  /**
   * GitHub OAuth-токен для обхода лимитов API при скачивании (необязателен).
   * Хранится в {@link PasswordSafe}, а не в открытом виде в настройках.
   */
  @Nullable
  public static String getGithubToken() {
    return PasswordSafe.getInstance().getPassword(githubTokenCredentialAttributes());
  }

  public static void setGithubToken(@Nullable String token) {
    PasswordSafe.getInstance().setPassword(githubTokenCredentialAttributes(), token);
  }

  private static CredentialAttributes githubTokenCredentialAttributes() {
    return new CredentialAttributes(
      CredentialAttributesKt.generateServiceName("BSL Language Server", "githubToken"));
  }

  @Nullable
  @Override
  public LanguageServerSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull LanguageServerSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}

