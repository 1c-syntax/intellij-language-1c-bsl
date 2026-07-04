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

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BslLanguageServerConnectionProviderTest {

  @Test
  public void mergedJavaOptionsIsNullWhenUserOptionsBlank() {
    assertNull(BslLanguageServerConnectionProvider.mergedJavaOptions(null, null));
    assertNull(BslLanguageServerConnectionProvider.mergedJavaOptions("-Xmx1g", ""));
    assertNull(BslLanguageServerConnectionProvider.mergedJavaOptions("-Xmx1g", "   "));
  }

  @Test
  public void mergedJavaOptionsUsesUserOptionsWhenNoExisting() {
    assertEquals("-Xmx4g", BslLanguageServerConnectionProvider.mergedJavaOptions(null, "-Xmx4g"));
    assertEquals("-Xmx4g", BslLanguageServerConnectionProvider.mergedJavaOptions("", "  -Xmx4g  "));
    assertEquals("-Xmx4g", BslLanguageServerConnectionProvider.mergedJavaOptions("   ", "-Xmx4g"));
  }

  @Test
  public void mergedJavaOptionsAppendsUserOptionsToExisting() {
    assertEquals("-Xss2m -Xmx4g",
      BslLanguageServerConnectionProvider.mergedJavaOptions("-Xss2m", "-Xmx4g"));
  }

  @Test
  public void resolveConfigurationFileIsNullForBlankBaseOrName() {
    assertNull(BslLanguageServerConnectionProvider.resolveConfigurationFile(null, ".bsl-language-server.json"));
    assertNull(BslLanguageServerConnectionProvider.resolveConfigurationFile("/whatever", ""));
    assertNull(BslLanguageServerConnectionProvider.resolveConfigurationFile("/whatever", "   "));
  }

  @Test
  public void resolveConfigurationFileIsNullWhenFileMissing() throws IOException {
    var dir = Files.createTempDirectory("bsl-cfg-missing");
    try {
      assertNull(BslLanguageServerConnectionProvider.resolveConfigurationFile(
        dir.toString(), ".bsl-language-server.json"));
    } finally {
      Files.deleteIfExists(dir);
    }
  }

  @Test
  public void resolveConfigurationFileReturnsAbsolutePathWhenPresent() throws IOException {
    var dir = Files.createTempDirectory("bsl-cfg-present");
    var configFile = dir.resolve(".bsl-language-server.json");
    Files.writeString(configFile, "{}");
    try {
      assertEquals(configFile.toString(), BslLanguageServerConnectionProvider.resolveConfigurationFile(
        dir.toString(), ".bsl-language-server.json"));
    } finally {
      Files.deleteIfExists(configFile);
      Files.deleteIfExists(dir);
    }
  }
}
