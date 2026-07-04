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

import com.intellij.openapi.components.State;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.xmlb.XmlSerializer;
import com.redhat.devtools.lsp4ij.settings.GlobalLanguageServerSettings;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Регрессионные тесты на issue #30. GitHub-токен хранится в {@code PasswordSafe} и не должен
 * попадать в сериализуемое состояние: иначе {@code XmlSerializer} трогает {@code PasswordSafe}
 * в {@code loadState} (медленная операция внутри non-cancelable read action — запрещена), а токен
 * рискует утечь в {@code intellij-bsl.xml} открытым текстом.
 */
public class LanguageServerSettingsStateTest extends BasePlatformTestCase {

  /**
   * Аксессоры токена не должны следовать JavaBean-именованию — иначе {@code XmlSerializer}
   * принимает {@code githubToken} за сериализуемое свойство состояния (корень issue #30).
   */
  public void testGithubTokenAccessorsAreNotJavaBeanProperties() {
    List<String> methodNames = Arrays.stream(LanguageServerSettingsState.class.getDeclaredMethods())
      .map(Method::getName)
      .toList();

    assertFalse("getGithubToken сделал бы githubToken сериализуемым свойством (issue #30)",
      methodNames.contains("getGithubToken"));
    assertFalse("setGithubToken сделал бы githubToken сериализуемым свойством (issue #30)",
      methodNames.contains("setGithubToken"));
    assertTrue(methodNames.contains("githubToken"));
    assertTrue(methodNames.contains("storeGithubToken"));
  }

  /**
   * Даже если токен сохранён в {@code PasswordSafe}, он не должен появляться в сериализованном
   * состоянии компонента — ни как значение, ни как свойство {@code githubToken}.
   */
  public void testGithubTokenNeverLeaksIntoSerializedState() throws Exception {
    var secret = "ghp_must_not_be_serialized";
    LanguageServerSettingsState.storeGithubToken(secret);
    try {
      var element = XmlSerializer.serialize(new LanguageServerSettingsState());
      var xml = element == null ? "" : JDOMUtil.write(element);
      assertFalse("токен утёк в сериализованное состояние (issue #30): " + xml, xml.contains(secret));
      assertFalse("свойство githubToken не должно сериализоваться (issue #30): " + xml,
        xml.contains("githubToken"));
    } finally {
      LanguageServerSettingsState.storeGithubToken(null);
    }
  }

  /**
   * Имя {@code @State} должно отличаться от lsp4ij: совпадающее имя даёт
   * «Conflicting component name» при инициализации сервисов (репорт А. Сосновый).
   */
  public void testStateNameDoesNotCollideWithLsp4ij() {
    var ourName = LanguageServerSettingsState.class.getAnnotation(State.class).name();
    var lsp4ijName = GlobalLanguageServerSettings.class.getAnnotation(State.class).name();
    assertFalse(
      "имя @State совпадает с lsp4ij → Conflicting component name",
      ourName.equals(lsp4ijName));
  }
}
