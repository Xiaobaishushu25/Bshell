/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xbss.myterminal.intellij.ui.tabs.impl;

import xbss.myterminal.intellij.ui.ColorUtil;

import java.awt.*;

/**
 * @author Konstantin Bulenkov
 */
@SuppressWarnings("UseJBColor")
class DarculaEditorTabsPainter extends DefaultEditorTabsPainter {

  @Override
  protected Color getDefaultTabColor() {
    if (myDefaultTabColor != null) {
      return myDefaultTabColor;
    }
    return new Color(0x515658);
  }

  @Override
  protected Color getInactiveMaskColor() {
    return ColorUtil.withAlpha(new Color(0x262626), .5);
  }
}
