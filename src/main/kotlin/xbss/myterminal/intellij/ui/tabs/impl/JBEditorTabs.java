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

import xbss.myterminal.intellij.openapi.Disposable;
import xbss.myterminal.intellij.openapi.util.SystemInfo;
import xbss.myterminal.intellij.openapi.util.registry.Registry;
import xbss.myterminal.intellij.ui.tabs.JBTabsPosition;
import xbss.myterminal.intellij.ui.tabs.TabInfo;
import xbss.myterminal.intellij.ui.tabs.impl.singleRow.SingleRowLayout;
import xbss.myterminal.intellij.ui.tabs.impl.table.TableLayout;
import xbss.myterminal.intellij.util.ui.DrawUtil;
import xbss.myterminal.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

/**
 * @author pegov
 */
public class JBEditorTabs extends JBTabsImpl {
  public static final String TABS_ALPHABETICAL_KEY = "tabs.alphabetical";
  protected JBEditorTabsPainter myDarkPainter = new DarculaEditorTabsPainter();
  protected JBEditorTabsPainter myDefaultPainter = new DefaultEditorTabsPainter();


  public JBEditorTabs(Disposable parent) {

  }

  @Override
  protected SingleRowLayout createSingleRowLayout() {
    return super.createSingleRowLayout();
  }

  @Override
  public boolean supportsCompression() {
    return true;
  }

  @Nullable
  public Rectangle getSelectedBounds() {
    TabLabel label = getSelectedLabel();
    return label != null ? label.getBounds() : null;
  }

  @Override
  public boolean isEditorTabs() {
    return true;
  }

  @Override
  protected void paintFirstGhost(Graphics2D g2d) {
  }

  @Override
  protected void paintLastGhost(Graphics2D g2d) {
  }

  @Override
  public boolean isGhostsAlwaysVisible() {
    return false;
  }

  @Override
  public boolean useSmallLabels() {
    return false;
//    return UISettings.getInstance().USE_SMALL_LABELS_ON_TABS;
  }

  @Override
  public boolean useBoldLabels() {
    return SystemInfo.isMac && Registry.is("ide.mac.boldEditorTabs");
  }

  @Override
  public boolean hasUnderline() {
    return isSingleRow();
  }



  protected void doPaintInactive(Graphics2D g2d,
                                 boolean leftGhostExists,
                                 TabLabel label,
                                 Rectangle effectiveBounds,
                                 boolean rightGhostExists, int row, int column) {
    Insets insets = getTabsBorder().getEffectiveBorder();

    int _x = effectiveBounds.x + insets.left;
    int _y = effectiveBounds.y + insets.top;
    int _width = effectiveBounds.width - insets.left - insets.right + (getTabsPosition() == JBTabsPosition.right ? 1 : 0);
    int _height = effectiveBounds.height - insets.top - insets.bottom;


    if ((!isSingleRow() /* for multiline */) || (isSingleRow() && isHorizontalTabs()))  {
      if (isSingleRow() && getPosition() == JBTabsPosition.bottom) {
        _y += getActiveTabUnderlineHeight();
      } else {
        if (isSingleRow()) {
          _height -= getActiveTabUnderlineHeight();
        } else {
          TabInfo info = label.getInfo();
          if (((TableLayout)getEffectiveLayout()).isLastRow(info)) {
            _height -= getActiveTabUnderlineHeight();
          }
        }
      }
    }

    final boolean vertical = getTabsPosition() == JBTabsPosition.left || getTabsPosition() == JBTabsPosition.right;
    final Color tabColor = label.getInfo().getTabColor();
    final Composite oldComposite = g2d.getComposite();
    //if (label != getSelectedLabel()) {
    //  g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
    //}
    getPainter().doPaintInactive(g2d, effectiveBounds, _x, _y, _width, _height, tabColor, row, column, vertical);
    //g2d.setComposite(oldComposite);
  }



  @Override
  public int getActiveTabUnderlineHeight() {
    return hasUnderline() ? super.getActiveTabUnderlineHeight() : 1;
  }

  protected JBEditorTabsPainter getPainter() {
    return DrawUtil.isUnderDarcula() ? myDarkPainter : myDefaultPainter;
  }

  public boolean isAlphabeticalMode() {
    return Registry.is(TABS_ALPHABETICAL_KEY);
  }

  public static void setAlphabeticalMode(boolean on) {
    //
  }

  @Override
  protected void doPaintBackground(Graphics2D g2d, Rectangle clip) {
    List<TabInfo> visibleInfos = getVisibleInfos();
    final boolean vertical = getTabsPosition() == JBTabsPosition.left || getTabsPosition() == JBTabsPosition.right;

    Insets insets = getTabsBorder().getEffectiveBorder();

    int minOffset = vertical ? getHeight(): getWidth();
    int maxOffset = 0;
    int maxLength = 0;

    for (int i = visibleInfos.size() - 1; i >= 0; i--) {
      TabInfo visibleInfo = visibleInfos.get(i);
      TabLabel tabLabel = myInfo2Label.get(visibleInfo);
      Rectangle r = tabLabel.getBounds();
      if (r.width == 0 || r.height == 0) continue;
      minOffset = Math.min(vertical ? r.y : r.x, minOffset);
      maxOffset = Math.max(vertical ? r.y + r.height : r.x + r.width, maxOffset);
      maxLength = vertical ? r.width : r.height;
    }

    minOffset--;
    maxOffset++;

    Rectangle r2 = new Rectangle(0, 0, getWidth(), getHeight());

    Rectangle beforeTabs;
    Rectangle afterTabs;
    if (vertical) {
      beforeTabs = new Rectangle(insets.left, insets.top, getWidth(), minOffset - insets.top);
      afterTabs = new Rectangle(insets.left, maxOffset, getWidth(),
                                r2.height - maxOffset - insets.top - insets.bottom);
    } else {
      int y = r2.y + insets.top;
      int height = maxLength - insets.top - insets.bottom;
      if (getTabsPosition() == JBTabsPosition.bottom) {
        y = r2.height - height - insets.top + getActiveTabUnderlineHeight();
      } else {
        height++;
        height -= getActiveTabUnderlineHeight();
      }
      y--;

      afterTabs = new Rectangle(maxOffset, y, r2.width - maxOffset - insets.left - insets.right, height);
      beforeTabs = new Rectangle(0, y, minOffset, height);
    }

    getPainter().doPaintBackground(g2d, clip, vertical, afterTabs);
    g2d.setPaint(getEmptySpaceColor());
    g2d.fill(beforeTabs);
    g2d.fill(afterTabs);
  }

  protected Color getEmptySpaceColor() {
    return getPainter().getEmptySpaceColor();
  }

  protected void paintSelectionAndBorder(Graphics2D g2d) {
    if (getSelectedInfo() == null || isHideTabs()) return;

    TabLabel label = getSelectedLabel();
    Rectangle r = label.getBounds();

    ShapeInfo selectedShape = _computeSelectedLabelShape();

    Insets insets = getTabsBorder().getEffectiveBorder();

    Color tabColor = label.getInfo().getTabColor();
    final boolean isHorizontalTabs = isHorizontalTabs();

    getPainter().paintSelectionAndBorder(g2d, r, selectedShape, insets, tabColor, isHorizontalTabs);
  }

  @Override
  public Color getBackground() {
    return getPainter().getBackgroundColor();
  }

  public Color getForeground() {
    return UIUtil.getLabelForeground();
  }

  protected ShapeInfo _computeSelectedLabelShape() {
    final ShapeInfo shape = new ShapeInfo();

    shape.path = getEffectiveLayout().createShapeTransform(getSize());
    shape.insets = shape.path.transformInsets(getLayoutInsets());
    shape.labelPath = shape.path.createTransform(getSelectedLabel().getBounds());

    shape.labelBottomY = shape.labelPath.getMaxY() - shape.labelPath.deltaY(getActiveTabUnderlineHeight() - 1);
    boolean isTop = getPosition() == JBTabsPosition.top;
    boolean isBottom = getPosition() == JBTabsPosition.bottom;
    shape.labelTopY =
      shape.labelPath.getY() + (isTop ? shape.labelPath.deltaY(1) : isBottom ? shape.labelPath.deltaY(-1) : 0) ;
    shape.labelLeftX = shape.labelPath.getX() + (isTop || isBottom ? 0 : shape.labelPath.deltaX(1));
    shape.labelRightX = shape.labelPath.getMaxX() /*- shape.labelPath.deltaX(1)*/;

    int leftX = shape.insets.left + (isTop || isBottom ? 0 : shape.labelPath.deltaX(1));

    shape.path.moveTo(leftX, shape.labelBottomY);
    shape.path.lineTo(shape.labelLeftX, shape.labelBottomY);
    shape.path.lineTo(shape.labelLeftX, shape.labelTopY);
    shape.path.lineTo(shape.labelRightX, shape.labelTopY);
    shape.path.lineTo(shape.labelRightX, shape.labelBottomY);

    int lastX = shape.path.getWidth() - shape.path.deltaX(shape.insets.right);

    shape.path.lineTo(lastX, shape.labelBottomY);
    shape.path.lineTo(lastX, shape.labelBottomY + shape.labelPath.deltaY(getActiveTabUnderlineHeight() - 1));
    shape.path.lineTo(leftX, shape.labelBottomY + shape.labelPath.deltaY(getActiveTabUnderlineHeight() - 1));

    shape.path.closePath();
    shape.fillPath = shape.path.copy();

    return shape;
  }
}
