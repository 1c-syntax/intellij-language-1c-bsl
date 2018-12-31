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
package org.github._1c_syntax.intellij.bsl.files;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.github._1c_syntax.intellij.bsl.BSLIcons;
import org.github._1c_syntax.intellij.bsl.BSLLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public final class BSLFileType extends LanguageFileType {

  public static final BSLFileType INSTANCE = new BSLFileType();

  private BSLFileType() {
    super(BSLLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "BSL File";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "1C (BSL) language file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "bsl";
  }

  @Override
  public Icon getIcon() {
    return BSLIcons.BSL_FILE;
  }
}
