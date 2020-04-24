/*
 * This file is a part of IntelliJ Language 1C (BSL) Plugin.
 *
 * Copyright © 2018-2020
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
package com.github._1c_syntax.bsl.intellij;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public class BSLCommenter implements Commenter {

  @Nullable
  @Override
  public String getLineCommentPrefix() {
    return "//";
  }

  @Nullable
  @Override
  public String getBlockCommentPrefix() {
    return null;
  }

  @Nullable
  @Override
  public String getBlockCommentSuffix() {
    return null;
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentPrefix() {
    return "//";
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentSuffix() {
    return null;
  }
}
