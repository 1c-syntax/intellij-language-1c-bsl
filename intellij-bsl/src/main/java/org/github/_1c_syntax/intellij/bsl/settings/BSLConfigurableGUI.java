/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018
 * Alexey Sosnoviy <labotamy@yandex.ru>, Nikita Gryzlov <nixel2007@gmail.com>
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

import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class BSLConfigurableGUI {
  private JRadioButton diagnosticLanguageRu;
  private JRadioButton diagnosticLanguageEn;
  private JPanel rootPanel;

  private LanguageServerSettingsState state = LanguageServerSettingsState.getInstance();

  public JPanel getRootPanel() {
    return rootPanel;
  }

  public boolean isModified() {
    return state.diagnosticLanguage != getDiagnosticLanguage();
  }

  public void apply() {
    state.diagnosticLanguage = getDiagnosticLanguage();
  }

  public void reset() {
    setDiagnosticLanguage();
  }

  private DiagnosticLanguage getDiagnosticLanguage() {
    DiagnosticLanguage diagnosticLanguage;
    if (diagnosticLanguageRu.isSelected()) {
      diagnosticLanguage = DiagnosticLanguage.RU;
    } else {
      diagnosticLanguage = DiagnosticLanguage.EN;
    }

    return diagnosticLanguage;
  }

  private void setDiagnosticLanguage() {
    diagnosticLanguageEn.setSelected(state.diagnosticLanguage == DiagnosticLanguage.EN);
    diagnosticLanguageRu.setSelected(state.diagnosticLanguage == DiagnosticLanguage.RU);
  }
}
