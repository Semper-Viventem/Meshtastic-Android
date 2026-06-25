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
package org.meshtastic.feature.map

import org.meshtastic.core.model.Node
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Behavioral tests for the "Max hops away" map filter: given the selected max-hops value, which nodes
 * remain on the map. These assert the observable filtering outcome (not any specific enum/API shape).
 */
@Suppress("MagicNumber")
class MaxHopsFilterTest {

    private val now = 1_000_000L

    private fun state(
        onlyFavorites: Boolean = false,
        lastHeard: LastHeardFilter = LastHeardFilter.Any,
        maxHops: MaxHopsFilter = MaxHopsFilter.Any,
    ) = BaseMapViewModel.MapFilterState(
        onlyFavorites = onlyFavorites,
        showWaypoints = true,
        showPrecisionCircle = true,
        lastHeardFilter = lastHeard,
        lastHeardTrackFilter = LastHeardFilter.Any,
        maxHopsFilter = maxHops,
    )

    private fun node(num: Int, hopsAway: Int = -1, isFavorite: Boolean = false, lastHeard: Int = now.toInt()) =
        Node(num = num, hopsAway = hopsAway, isFavorite = isFavorite, lastHeard = lastHeard)

    @Test
    fun any_keepsAllNodes() {
        val nodes = listOf(node(1, hopsAway = 0), node(2, hopsAway = 3), node(3, hopsAway = -1))
        assertEquals(nodes, nodes.applyMapFilters(state(maxHops = MaxHopsFilter.Any), ourNodeNum = null, now))
    }

    @Test
    fun direct_keepsOnlyDirectlyConnectedNodes() {
        val direct = node(1, hopsAway = 0)
        val oneHop = node(2, hopsAway = 1)
        val unknown = node(3, hopsAway = -1)
        val result =
            listOf(direct, oneHop, unknown)
                .applyMapFilters(state(maxHops = MaxHopsFilter.Direct), ourNodeNum = null, now)
        assertEquals(listOf(direct), result)
    }

    @Test
    fun twoHops_keepsZeroThroughTwoHops() {
        val nodes = (0..4).map { node(num = it + 1, hopsAway = it) }
        val result = nodes.applyMapFilters(state(maxHops = MaxHopsFilter.TwoHops), ourNodeNum = null, now)
        assertEquals(listOf(0, 1, 2), result.map { it.hopsAway })
    }

    @Test
    fun ownNode_isAlwaysKept_evenWhenOutOfHopRange() {
        val ourNode = node(num = 42, hopsAway = -1)
        val result = listOf(ourNode).applyMapFilters(state(maxHops = MaxHopsFilter.Direct), ourNodeNum = 42, now)
        assertEquals(listOf(ourNode), result)
    }

    @Test
    fun direct_excludesUnknownHopNodes() {
        assertFalse(node(1, hopsAway = -1).matchesMapFilters(state(maxHops = MaxHopsFilter.Direct), null, now))
        assertTrue(node(1, hopsAway = 0).matchesMapFilters(state(maxHops = MaxHopsFilter.Direct), null, now))
    }

    @Test
    fun composesWithOnlyFavoritesFilter() {
        val favDirect = node(1, hopsAway = 0, isFavorite = true)
        val nonFavDirect = node(2, hopsAway = 0, isFavorite = false)
        val favFar = node(3, hopsAway = 4, isFavorite = true)
        val result =
            listOf(favDirect, nonFavDirect, favFar)
                .applyMapFilters(state(onlyFavorites = true, maxHops = MaxHopsFilter.Direct), ourNodeNum = null, now)
        assertEquals(listOf(favDirect), result)
    }

    @Test
    fun composesWithLastHeardFilter() {
        val recentDirect = node(1, hopsAway = 0, lastHeard = now.toInt())
        val staleDirect = node(2, hopsAway = 0, lastHeard = (now - 100_000L).toInt())
        val result =
            listOf(recentDirect, staleDirect)
                .applyMapFilters(
                    state(lastHeard = LastHeardFilter.OneHour, maxHops = MaxHopsFilter.Direct),
                    ourNodeNum = null,
                    now,
                )
        assertEquals(listOf(recentDirect), result)
    }
}
