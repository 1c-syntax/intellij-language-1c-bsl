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
package com.github._1c_syntax.bsl.intellij;

import com.intellij.ide.FileIconProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jspecify.annotations.Nullable;

import javax.swing.Icon;

/**
 * Иконки для файлов {@code .bsl}/{@code .os}.
 *
 * <p>Иконки выдаются провайдером, а не через регистрацию {@code FileType}, — регистрация своего
 * {@code FileType} перехватила бы файлы и отключила TextMate-подсветку, которую использует плагин.
 */
public class BslFileIconProvider implements FileIconProvider {

  @Override
  public @Nullable Icon getIcon(VirtualFile file, int flags, @Nullable Project project) {
    var extension = file.getExtension();
    if (extension == null) {
      return null;
    }
    return switch (extension.toLowerCase()) {
      case "bsl" -> BSLIcons.BSL_FILE;
      case "os" -> BSLIcons.OS_FILE;
      default -> null;
    };
  }
}
