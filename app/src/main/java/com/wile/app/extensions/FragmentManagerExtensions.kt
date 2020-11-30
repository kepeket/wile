package com.wile.app.extensions

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Convenient method to replace the container's Fragment, referenced by the given containerViewId,
 * by the given Fragment
 *
 * @param containerViewId Identifier of the container whose fragment(s) are to be replaced.
 * @param fragment The new fragment to place in the container. It won't be replaced if already in the back stack
 * @param tag Optional tag name for the fragment
 * @param shouldAddToBackStack whether the Fragment should be added to the back stack, default to false
 * @param backStackStateName optional name for the saved back stack state
 * @param transition which transition to use among recommended to use by [FragmentTransaction.setTransition],default [FragmentTransaction.TRANSIT_FRAGMENT_FADE]
 *
 */
fun FragmentManager.replaceFragment(
    @IdRes containerViewId: Int,
    fragment: Fragment,
    tag: String,
    shouldAddToBackStack: Boolean = false,
    backStackStateName: String? = null,
    allowMultipleInstance: Boolean = false,
    transition: Int = FragmentTransaction.TRANSIT_FRAGMENT_FADE
) {
    /* checking if fragments is already in the stack to prevent stacking multiple times the same fragment
     in edges cases; For example, when user click quickly several time */
    if (allowMultipleInstance || findFragmentByTag(tag) == null) {
        beginTransaction()
            .replace(containerViewId, fragment, tag)
            .setTransition(transition)
            .apply {
                if (shouldAddToBackStack) {
                    addToBackStack(backStackStateName)
                }
            }
            .commit()
    }
}