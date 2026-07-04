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
package com.github._1c_syntax.bsl.intellij.textmate;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Поставляет встроенный TextMate-бандл с грамматикой BSL/SDBL, обеспечивающий подсветку
 * файлов {@code .bsl}/{@code .os} без регистрации собственного {@code FileType}.
 *
 * <p>TextMate требует бандл в виде каталога на файловой системе, а ресурсы плагина лежат в jar,
 * поэтому бандл распаковывается в служебный каталог IDE при инициализации.
 */
public class BslTextMateBundleProvider implements TextMateBundleProvider {

  private static final Logger LOG = Logger.getInstance(BslTextMateBundleProvider.class);

  private static final String BUNDLE_NAME = "1C (BSL)";
  private static final String PLUGIN_ID = "io.github.1c-syntax.language-1c-bsl";
  private static final String RESOURCE_ROOT = "/textmate/1c-bsl";
  private static final List<String> BUNDLE_FILES = List.of(
    "package.json",
    "language-configuration.json",
    "syntaxes/1c.tmLanguage.json",
    "syntaxes/1c-query.tmLanguage.json"
  );

  @Override
  public List<PluginBundle> getBundles() {
    try {
      var bundleDir = extractBundle();
      return List.of(new PluginBundle(BUNDLE_NAME, bundleDir));
    } catch (IOException e) {
      LOG.warn("Failed to prepare the BSL TextMate bundle", e);
      return List.of();
    }
  }

  private Path extractBundle() throws IOException {
    // Каталог версионируется версией плагина: при обновлении плагина создаётся новый каталог,
    // а уже распакованные файлы текущей версии повторно не копируются.
    var targetDir = Path.of(PathManager.getSystemPath(), "textmate-bundles", "1c-bsl", pluginVersion());
    for (var relativePath : BUNDLE_FILES) {
      var target = targetDir.resolve(relativePath);
      if (Files.exists(target)) {
        continue;
      }
      Files.createDirectories(target.getParent());
      try (InputStream input = BslTextMateBundleProvider.class.getResourceAsStream(RESOURCE_ROOT + "/" + relativePath)) {
        if (input == null) {
          throw new IOException("TextMate bundle resource not found: " + relativePath);
        }
        Files.copy(input, target, REPLACE_EXISTING);
      }
    }
    return targetDir;
  }

  private static String pluginVersion() {
    var descriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
    if (descriptor != null && descriptor.getVersion() != null) {
      return descriptor.getVersion();
    }
    return "dev";
  }
}
