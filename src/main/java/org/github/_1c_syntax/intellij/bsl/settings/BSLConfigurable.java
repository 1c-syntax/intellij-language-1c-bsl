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
package org.github._1c_syntax.intellij.bsl.settings;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class BSLConfigurable implements Configurable {

  private BSLConfigurableGUI form;
  private LanguageServerSettingsState state = LanguageServerSettingsState.getInstance();

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "1C (BSL)";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return getForm().getRootPanel();
  }

  @Override
  public void disposeUIResources() {
    form = null;
  }

  @Override
  public boolean isModified() {
    return state.enabled != getEnabled() || state.diagnosticLanguage != getDiagnosticLanguage();
  }

  @Override
  public void apply() {
    state.enabled = getEnabled();
    state.diagnosticLanguage = getDiagnosticLanguage();
  }

  @Override
  public void reset() {
    setEnabled();
    setDiagnosticLanguage();
  }

  private BSLConfigurableGUI getForm() {
    if (form == null) {
      form = new BSLConfigurableGUI();
    }
    return form;
  }

  private DiagnosticLanguage getDiagnosticLanguage() {
    DiagnosticLanguage diagnosticLanguage;
    if (getForm().getDiagnosticLanguageRu().isSelected()) {
      diagnosticLanguage = DiagnosticLanguage.RU;
    } else {
      diagnosticLanguage = DiagnosticLanguage.EN;
    }

    return diagnosticLanguage;
  }

  private boolean getEnabled() {
    return getForm().getEnabled().isSelected();
  }

  private void setDiagnosticLanguage() {
    getForm().getDiagnosticLanguageEn().setSelected(state.diagnosticLanguage == DiagnosticLanguage.EN);
    getForm().getDiagnosticLanguageRu().setSelected(state.diagnosticLanguage == DiagnosticLanguage.RU);
  }

  private void setEnabled() {
    getForm().getEnabled().setSelected(state.enabled);
  }
}
