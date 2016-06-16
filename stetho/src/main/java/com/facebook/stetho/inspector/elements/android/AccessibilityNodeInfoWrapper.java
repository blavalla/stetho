/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.stetho.inspector.elements.android;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

import com.facebook.stetho.common.android.AccessibilityUtil;

public final class AccessibilityNodeInfoWrapper {

  public AccessibilityNodeInfoWrapper() {
  }

  public static boolean getIgnored(AccessibilityNodeInfoCompat node, View view) {
    int important = ViewCompat.getImportantForAccessibility(view);
    if (important == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO ||
        important == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
      return true;
    }

    // Go all the way up the tree to make sure no parent has hidden its descendants
    ViewParent parent = view.getParent();
    while (parent instanceof View) {
      if (ViewCompat.getImportantForAccessibility((View) parent)
          == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
        return true;
      }
      parent = parent.getParent();
    }

    if (!node.isVisibleToUser()) {
      return true;
    }

    if (AccessibilityUtil.isAccessibilityFocusable(node, view)) {
      if (node.getChildCount() <= 0) {
        // Leaves that are accessibility focusable are never ignored, even if they don't have a
        // speakable description
        return false;
      } else if (AccessibilityUtil.isSpeakingNode(node, view)) {
        // Node is focusable and has something to speak
        return false;
      }

      // Node is focusable and has nothing to speak
      return true;
    }

    // If this node has no focusable ancestors, but it still has text,
    // then it should receive focus from navigation and be read aloud.
    if (!AccessibilityUtil.hasFocusableAncestor(node, view) && AccessibilityUtil.hasText(node)) {
      return false;
    }

    return true;
  }

  public static String getIgnoredReasons(AccessibilityNodeInfoCompat node, View view) {
    int important = ViewCompat.getImportantForAccessibility(view);

    if (important == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO) {
      return "View has importantForAccessibility set to 'NO'.";
    }

    if (important == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
      return "View has importantForAccessibility set to 'NO_HIDE_DESCENDANTS'.";
    }

    ViewParent parent = view.getParent();
    while (parent instanceof View) {
      if (ViewCompat.getImportantForAccessibility((View) parent)
              == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
        return "An ancestor View has importantForAccessibility set to 'NO_HIDE_DESCENDANTS'.";
      }
      parent = parent.getParent();
    }

    if (!node.isVisibleToUser()) {
      return "View is not visible.";
    }

    if (AccessibilityUtil.isAccessibilityFocusable(node, view)) {
      return "View is actionable, but has no description.";
    }

    if (AccessibilityUtil.hasText(node)) {
      return "View is not actionable, and an ancestor View has co-opted its description.";
    }

    return "View is not actionable and has no description.";
  }

  public static CharSequence getDescription(AccessibilityNodeInfoCompat node, View view) {
    CharSequence contentDescription = node.getContentDescription();
    CharSequence nodeText = node.getText();

    boolean hasNodeText = !TextUtils.isEmpty(nodeText);
    boolean isEditText = view instanceof EditText;

    // EditText's prioritize their own text content over a contentDescription
    if (!TextUtils.isEmpty(contentDescription) && (!isEditText || !hasNodeText)) {
      return contentDescription;
    }

    if (hasNodeText) {
      return nodeText;
    }

    // If there are child views and no contentDescription the text of all non-focusable children,
    // comma separated, becomes the description.
    if (view instanceof ViewGroup) {
      String concatChildDescription = "";
      String separator = ", ";
      ViewGroup viewGroup = (ViewGroup) view;

      for (int i = 0; i < viewGroup.getChildCount(); i++) {
        final View child = viewGroup.getChildAt(i);

        AccessibilityNodeInfoCompat childNodeInfo = AccessibilityNodeInfoCompat.obtain();
        ViewCompat.onInitializeAccessibilityNodeInfo(child, childNodeInfo);

        CharSequence childNodeDescription = null;
        if (AccessibilityUtil.isSpeakingNode(childNodeInfo, child)) {
          childNodeDescription = getDescription(childNodeInfo, child);
        }

        if (!TextUtils.isEmpty(childNodeDescription)) {
          if (concatChildDescription != "") {
            concatChildDescription += separator;
          }
          concatChildDescription += childNodeDescription;
        }
        childNodeInfo.recycle();
      }

      return !TextUtils.isEmpty(concatChildDescription) ? concatChildDescription : null;
    }

    return null;
  }

}
