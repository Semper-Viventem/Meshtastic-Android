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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.meshtastic.core.resources.Res
import org.meshtastic.core.resources.max_hops_filter_label
import org.meshtastic.feature.map.MaxHopsFilter
import kotlin.math.roundToInt

/** Slider label for a [MaxHopsFilter]: its worded label if any (e.g. "0 (direct connect)", "Any"),
 *  otherwise the raw hop count ("1".."5"). */
@Composable
fun MaxHopsFilter.displayLabel(): String = label?.let { stringResource(it) } ?: maxHops.toString()

/**
 * "Max hops away" map filter — a slider over [MaxHopsFilter] options in slider order
 * (0 (direct connect), 1..5, Any). Stateless (hoisted [selected] + [onSelectedChange]) so it is
 * shared by both map flavors and verifiable with Compose UI tests.
 */
@Composable
fun MaxHopsFilterControl(
    selected: MaxHopsFilter,
    onSelectedChange: (MaxHopsFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = MaxHopsFilter.entries
    val selectedIndex = options.indexOf(selected)
    var sliderPosition by remember(selectedIndex) { mutableFloatStateOf(selectedIndex.toFloat()) }
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(Res.string.max_hops_filter_label, selected.displayLabel()),
            style = MaterialTheme.typography.labelLarge,
        )
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = {
                onSelectedChange(options[sliderPosition.roundToInt().coerceIn(0, options.size - 1)])
            },
            valueRange = 0f..(options.size - 1).toFloat(),
            steps = options.size - 2,
        )
    }
}
