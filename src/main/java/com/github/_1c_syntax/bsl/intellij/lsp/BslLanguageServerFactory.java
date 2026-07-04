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
import com.intellij.openapi.project.Project;
import com.redhat.devtools.lsp4ij.LanguageServerEnablementSupport;
import com.redhat.devtools.lsp4ij.LanguageServerFactory;
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider;

/**
 * Фабрика BSL Language Server для LSP4IJ. Запуск сервера включается/выключается настройкой плагина
 * {@link LanguageServerSettingsState#enabled}.
 */
public class BslLanguageServerFactory implements LanguageServerFactory, LanguageServerEnablementSupport {

  @Override
  public StreamConnectionProvider createConnectionProvider(Project project) {
    return new BslLanguageServerConnectionProvider(project);
  }

  @Override
  public boolean isEnabled(Project project) {
    return Boolean.TRUE.equals(LanguageServerSettingsState.getInstance().enabled);
  }

  @Override
  public void setEnabled(boolean enabled, Project project) {
    LanguageServerSettingsState.getInstance().enabled = enabled;
  }
}
