package com.facebook.stetho.inspector.elements.android;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewParent;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityNodeInfoWrapper {
  private AccessibilityNodeInfoCompat mNodeInfo;
  private View mView;
  private ViewParent mParent;

  public AccessibilityNodeInfoWrapper(AccessibilityNodeInfoCompat nodeInfo, View view) {
    mNodeInfo = nodeInfo;
    mView = view;
    mParent = view.getParent();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getIgnored() {
    int important = ViewCompat.getImportantForAccessibility(mView);

    if (important == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO ||
        important == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
      return true;
    }

    // Go all the way up the tree to make sure no parent has hidden its descendants
    ViewParent parent = mParent;
    while (parent instanceof View) {
      if (ViewCompat.getImportantForAccessibility((View) parent)
          == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
        return true;
      }
      parent = parent.getParent();
    }

    boolean isActionable = mView.isClickable() || mView.isLongClickable() || mView.isFocusable();
    boolean isTextViewWithText =
        mView instanceof TextView && ((TextView) mView).getText().length() > 0;
    boolean hasContentDescription =
        mView.getContentDescription() != null && mView.getContentDescription().length() > 0;
    boolean hasNodeProvider = ViewCompat.getAccessibilityNodeProvider(mView) != null;
    boolean hasAccessibilityDelegate = ViewCompat.hasAccessibilityDelegate(mView);

    return !isActionable
        && !isTextViewWithText
        && !hasContentDescription
        && !hasNodeProvider
        && !hasAccessibilityDelegate;
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getClickable() {
    return mNodeInfo.isClickable();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getLongClickable() {
    return mNodeInfo.isLongClickable();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getFocusable() {
    return mNodeInfo.isFocusable();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getFocused() {
    return mNodeInfo.isFocused();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getCheckable() {
    return mNodeInfo.isCheckable();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getChecked() {
    return mNodeInfo.isChecked();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public String getParentForAcessibility() {
    return String.valueOf(ViewCompat.getParentForAccessibility(mView));
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getVisibleToUser() {
    return mNodeInfo.isVisibleToUser();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public Boolean getAccessibilityFocused() {
    return mNodeInfo.isAccessibilityFocused();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public CharSequence getPackageName() {
    return mNodeInfo.getPackageName();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public CharSequence getClassName() {
    return mNodeInfo.getClassName();
  }

  @ViewDebug.ExportedProperty(category = "accessibility")
  public String getBounds() {
    Rect outBounds = new Rect();
    mNodeInfo.getBoundsInScreen(outBounds);
    return String.valueOf(outBounds);
  }


  @ViewDebug.ExportedProperty(category = "accessibility")
  public String getActions() {
    List<CharSequence> actionLabels = new ArrayList<>();

    for (AccessibilityNodeInfoCompat.AccessibilityActionCompat action : mNodeInfo.getActionList()) {
      switch (action.getId()) {
        case AccessibilityNodeInfoCompat.ACTION_FOCUS:
          actionLabels.add("focus");
          break;
        case AccessibilityNodeInfoCompat.ACTION_CLEAR_FOCUS:
          actionLabels.add("clear-focus");
          break;
        case AccessibilityNodeInfoCompat.ACTION_SELECT:
          actionLabels.add("select");
          break;
        case AccessibilityNodeInfoCompat.ACTION_CLEAR_SELECTION:
          actionLabels.add("clear-selection");
          break;
        case AccessibilityNodeInfoCompat.ACTION_CLICK:
          actionLabels.add("click");
          break;
        case AccessibilityNodeInfoCompat.ACTION_LONG_CLICK:
          actionLabels.add("long-click");
          break;
        case AccessibilityNodeInfoCompat.ACTION_ACCESSIBILITY_FOCUS:
          actionLabels.add("accessibility-focus");
          break;
        case AccessibilityNodeInfoCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS:
          actionLabels.add("clear-accessibility-focus");
          break;
        case AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY:
          actionLabels.add("next-at-movement-granularity");
          break;
        case AccessibilityNodeInfoCompat.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY:
          actionLabels.add("previous-at-movement-granularity");
          break;
        case AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT:
          actionLabels.add("next-html-element");
          break;
        case AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT:
          actionLabels.add("previous-html-element");
          break;
        case AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD:
          actionLabels.add("scroll-forward");
          break;
        case AccessibilityNodeInfoCompat.ACTION_SCROLL_BACKWARD:
          actionLabels.add("scroll-backward");
          break;
        case AccessibilityNodeInfoCompat.ACTION_CUT:
          actionLabels.add("cut");
          break;
        case AccessibilityNodeInfoCompat.ACTION_COPY:
          actionLabels.add("copy");
          break;
        case AccessibilityNodeInfoCompat.ACTION_PASTE:
          actionLabels.add("paste");
          break;
        case AccessibilityNodeInfoCompat.ACTION_SET_SELECTION:
          actionLabels.add("set-selection");
          break;
        default:
          if (action.getLabel() != null) {
            actionLabels.add(action.getLabel());
          } else {
            actionLabels.add("unknown");
          }
          break;
      }
    }
    return String.valueOf(actionLabels);
  }
}