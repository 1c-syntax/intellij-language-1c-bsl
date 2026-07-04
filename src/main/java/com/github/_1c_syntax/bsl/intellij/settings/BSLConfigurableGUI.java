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

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BSLConfigurableGUI {
  private JPanel rootPanel;
  private JCheckBox enabled;
  private JCheckBox useExternalJar;
  private JTextField path;
  private JTextField javaOpts;

  public JPanel getRootPanel() {
    return rootPanel;
  }

  public JCheckBox getUseExternalJar() {
    return useExternalJar;
  }

  public JTextField getJavaOpts() {
    return javaOpts;
  }

  public JCheckBox getEnabled() {
    return enabled;
  }

  public JTextField getPath() {
    return path;
  }
}
