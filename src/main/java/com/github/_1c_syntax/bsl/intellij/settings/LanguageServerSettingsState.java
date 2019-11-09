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
package com.github._1c_syntax.bsl.intellij.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
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

  public Boolean enabled = Boolean.TRUE;
  public DiagnosticLanguage diagnosticLanguage = DiagnosticLanguage.EN;
  public String path = "";

  public static LanguageServerSettingsState getInstance() {
    return ServiceManager.getService(LanguageServerSettingsState.class);
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

