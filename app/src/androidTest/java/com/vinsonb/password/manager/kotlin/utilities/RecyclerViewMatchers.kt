package com.vinsonb.password.manager.kotlin.utilities

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object RecyclerViewMatchers {
    fun withPositionMatchesText(position: Int, itemMatcher: Matcher<View>): Matcher<View> =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("expected: has item at position $position")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(item: View?): Boolean {
                if (item !is RecyclerView) return false
                val viewHolder = item.findViewHolderForAdapterPosition(position)
                if (viewHolder !is RecyclerView.ViewHolder) return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }

    fun withAdaptedDataLength(length: Int): Matcher<View> =
        object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("expected length: $length")
            }

            override fun matchesSafely(item: View?): Boolean {
                if (item !is RecyclerView) return false
                val adapterCount = item.adapter?.itemCount ?: return false
                return adapterCount == length
            }
        }
}