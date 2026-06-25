/*
 * Copyright (c) 2026 Meshtastic LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.meshtastic.feature.map.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import org.meshtastic.feature.map.MaxHopsFilter
import kotlin.test.Test

/**
 * Compose UI tests for the "Max hops away" map-filter slider. Verifies the control renders the spec's
 * option labels ("0 (direct connect)", a numeric hop count, and the default "Any"). The composable
 * resolves these from string resources; the assertions match the spec's literal (default-locale) text,
 * so they validate the UI feature rather than any internal API. (getString() can't be called outside a
 * composable in this Robolectric host test, so we match literals.)
 */
@OptIn(ExperimentalTestApi::class)
class MaxHopsFilterControlTest {

    @Test
    fun defaultAny_showsAnyLabel() = runComposeUiTest {
        setContent { MaxHopsFilterControl(selected = MaxHopsFilter.Any, onSelectedChange = {}) }
        // Label reads "Max hops away: Any" — the default, no-filtering option.
        onNodeWithText("Any", substring = true).assertIsDisplayed()
    }

    @Test
    fun directConnect_showsDirectConnectLabel() = runComposeUiTest {
        setContent { MaxHopsFilterControl(selected = MaxHopsFilter.Direct, onSelectedChange = {}) }
        // "0 (direct connect)" per the spec.
        onNodeWithText("0 (direct connect)", substring = true).assertIsDisplayed()
    }

    @Test
    fun numericHop_showsHopCount() = runComposeUiTest {
        setContent { MaxHopsFilterControl(selected = MaxHopsFilter.ThreeHops, onSelectedChange = {}) }
        // Numeric hop options render as their hop count.
        onNodeWithText("3", substring = true).assertIsDisplayed()
    }
}
