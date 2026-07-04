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

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import org.jspecify.annotations.Nullable;

import java.awt.Component;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Отправка отчётов об ошибках плагина в issue tracker на GitHub.
 *
 * <p>Открывает в браузере форму создания issue с предзаполненными заголовком, стектрейсом,
 * комментарием пользователя и версиями плагина/IDE.
 */
public class BslErrorReportSubmitter extends ErrorReportSubmitter {

  private static final String NEW_ISSUE_URL =
    "https://github.com/1c-syntax/intellij-language-1c-bsl/issues/new";

  // GitHub обрезает слишком длинные URL, поэтому ограничиваем размер стектрейса в теле issue.
  private static final int MAX_STACKTRACE_LENGTH = 6000;
  private static final int MAX_TITLE_LENGTH = 120;

  @Override
  public String getReportActionText() {
    return "Report to 1c-syntax issue tracker";
  }

  @Override
  public boolean submit(IdeaLoggingEvent[] events,
                        @Nullable String additionalInfo,
                        Component parentComponent,
                        Consumer<? super SubmittedReportInfo> consumer) {
    var event = events.length > 0 ? events[0] : null;
    var throwableText = event == null ? "" : event.getThrowableText();

    var title = throwableText.lines().findFirst().orElse("Unhandled exception");
    var url = NEW_ISSUE_URL
      + "?labels=bug"
      + "&title=" + encode(truncate(title, MAX_TITLE_LENGTH))
      + "&body=" + encode(buildBody(additionalInfo, throwableText));

    BrowserUtil.browse(url);

    ApplicationManager.getApplication().invokeLater(() ->
      consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE)));
    return true;
  }

  private String buildBody(@Nullable String additionalInfo, String throwableText) {
    var descriptor = getPluginDescriptor();
    var pluginVersion = descriptor == null ? "unknown" : descriptor.getVersion();
    var appInfo = ApplicationInfo.getInstance();

    return "### Environment\n"
      + "- Plugin: Language 1C (BSL) " + pluginVersion + "\n"
      + "- IDE: " + appInfo.getFullApplicationName() + " (build " + appInfo.getBuild().asString() + ")\n"
      + "- OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n"
      + "- Java: " + System.getProperty("java.version") + "\n\n"
      + "### Additional information\n"
      + (additionalInfo == null || additionalInfo.isBlank() ? "_None provided._" : additionalInfo) + "\n\n"
      + "### Stacktrace\n"
      + "```\n" + truncate(throwableText, MAX_STACKTRACE_LENGTH) + "\n```\n";
  }

  static String truncate(String value, int maxLength) {
    return value.length() <= maxLength ? value : value.substring(0, maxLength) + "\n… (truncated)";
  }

  static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
