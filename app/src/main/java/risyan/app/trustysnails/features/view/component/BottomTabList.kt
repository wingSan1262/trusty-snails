package risyan.app.trustysnails.features.view.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.ImageUrl
import risyan.app.trustysnails.basecomponent.extractDomain
import risyan.app.trustysnails.basecomponent.recheckValidityAndTransform
import risyan.app.trustysnails.basecomponent.ui.component.SlideFromBottomContainer
import risyan.app.trustysnails.basecomponent.ui.component.SlideFromEndContainer
import risyan.app.trustysnails.features.model.TabModel
import kotlin.math.roundToInt

@Composable
fun BottomTabBar(
    tabItems: List<TabModel>,
    onTabSelected: (TabModel) -> Unit,
    onDelete: (TabModel) -> Unit,
    onAddTabSelected: () -> Unit,
    onHistorySelected: () -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .height(52.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item {
            SlideFromEndContainer(content = {
                IconButton(
                    onClick = { onHistorySelected() },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_history_24),
                        contentDescription = "History",
                        tint = Color.Gray
                    )
                }
            }, modifier = Modifier.fillMaxHeight())
        }

        item {
            SlideFromEndContainer(content = {
                IconButton(
                    onClick = { onAddTabSelected() },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_circle_24),
                        contentDescription = "Add Tab",
                        tint = Color.Gray
                    )
                }
            }, modifier = Modifier.fillMaxHeight())

        }

        itemsIndexed(
            tabItems,
            key = {index, item -> item.id}
        ) { index, tab ->
            SlideFromBottomContainer(content ={
                RoundedTabItem(
                    tab = tab,
                    onDelete = { onDelete(it) },
                    onTabSelected = { onTabSelected(it) },
                )
            })
        }
    }
}

@Composable
fun RoundedTabItem(
    tab: TabModel,
    onTabSelected: (TabModel) -> Unit,
    onDelete: (TabModel) -> Unit,
) {

    var offsetY by remember { mutableStateOf(0f) }
    var isSwipingToDelete by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(40.dp) }

    LaunchedEffect(size){
        if(size < 40.dp) size -= 2.dp
        if(size == 0.dp) onDelete(tab)
    }

    LaunchedEffect(key1 = offsetY){
        if(offsetY != 0f) {
            if(offsetY + 10 > 0) offsetY = 0f else offsetY += 10 }
        if(offsetY == 0f && isSwipingToDelete){ size -= 2.dp }
    }

    Box(
        modifier = Modifier
            .size(size)
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .background(
                color = if (tab.isSelected) Color.Gray else Color.Transparent,
                shape = CircleShape
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    offsetY = (change.position.y - change.previousPosition.y)
                    if (!isSwipingToDelete)
                        isSwipingToDelete = offsetY < -50
                }
                detectTapGestures { offset ->
                    if (!isSwipingToDelete) {
                        onTabSelected(tab)
                    }
                }
            }
        ,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable { onTabSelected(tab) }
                .background(
                    color = if (tab.isSelected) Color.Gray else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val imageFav = tab.url.extractDomain().recheckValidityAndTransform()+"/favicon.ico"
            ImageUrl( url = imageFav, Modifier.size(24.dp))
        }
    }
}
