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

import com.github._1c_syntax.utils.downloader.DownloadProgressListener;
import com.intellij.openapi.progress.ProgressIndicator;

import java.util.Locale;

/**
 * Отражает прогресс скачивания релиза BSL Language Server на индикаторе IntelliJ: при известном
 * размере — долю и «прочитано / всего» в мегабайтах, при неизвестном — неопределённый индикатор.
 * Отмену задачи проверяет на каждом блоке, а перерисовку троттлит до целого процента (или, при
 * неизвестном размере, до целого мегабайта), чтобы не дёргать UI на каждый прочитанный блок.
 */
final class ProgressIndicatorDownloadListener implements DownloadProgressListener {

  private static final String PROGRESS_DOWNLOADING = "Downloading BSL Language Server";

  private final ProgressIndicator indicator;

  // Последняя показанная «засечка» (процент либо мегабайт); -1 — ещё ничего не рисовали.
  private long lastReported = -1;

  ProgressIndicatorDownloadListener(ProgressIndicator indicator) {
    this.indicator = indicator;
  }

  @Override
  public void onProgress(long bytesRead, long totalBytes) {
    // Отмену проверяем на каждом блоке (дёшево и отзывчиво), даже если перерисовку пропустим.
    indicator.checkCanceled();
    var tick = totalBytes > 0 ? bytesRead * 100 / totalBytes : bytesRead / (1024 * 1024);
    if (tick == lastReported) {
      return;
    }
    lastReported = tick;
    indicator.setText(PROGRESS_DOWNLOADING);
    if (totalBytes > 0) {
      indicator.setIndeterminate(false);
      indicator.setFraction((double) bytesRead / (double) totalBytes);
      indicator.setText2(formatMegabytes(bytesRead) + " / " + formatMegabytes(totalBytes));
    } else {
      indicator.setIndeterminate(true);
      indicator.setText2(formatMegabytes(bytesRead));
    }
  }

  private static String formatMegabytes(long bytes) {
    return String.format(Locale.ROOT, "%.1f MB", bytes / (1024.0 * 1024.0));
  }
}
