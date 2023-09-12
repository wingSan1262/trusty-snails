package risyan.app.trustysnails.basecomponent.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import risyan.app.trustysnails.basecomponent.ImageUrl
import risyan.app.trustysnails.basecomponent.extractDomain
import risyan.app.trustysnails.basecomponent.recheckValidityAndTransform
import risyan.app.trustysnails.data.remote.model.HistoryItem
import risyan.app.trustysnails.data.remote.model.convertToUserFriendlyFormat
import risyan.app.trustysnails.data.remote.model.toDateFormat

@Composable
fun HistoryItemView(
    historyItem: HistoryItem,
    onItemClick: (HistoryItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(historyItem) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageUrl(url = historyItem.url.extractDomain().recheckValidityAndTransform()+"/favicon.ico")
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = historyItem.title,
                maxLines = 3,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )
            Text(text = historyItem.url, maxLines = 3)
            Text(
                text = historyItem.timestamp.convertToUserFriendlyFormat(),
                color = Color.Gray
            )
        }
    }
}