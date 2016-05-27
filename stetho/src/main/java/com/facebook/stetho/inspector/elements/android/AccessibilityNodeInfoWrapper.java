package com.facebook.stetho.inspector.elements.android;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewParent;
import android.widget.TextView;

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
}
