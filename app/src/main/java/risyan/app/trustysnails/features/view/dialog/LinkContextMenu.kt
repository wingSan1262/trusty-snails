package risyan.app.trustysnails.features.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import risyan.app.trustysnails.basecomponent.extractDomain
import risyan.app.trustysnails.features.view.model.ContextMenuModel

@Composable
fun LinkContextMenu(
    onDismiss : ()->Unit,
    downloadLink : String = "" ,
    webLink : String = "" ,
    onDownload : (link : String)->Unit = {},
    onOpenLinkNewTab  : (link : String)->Unit = {}
){
    val title =  (downloadLink + webLink).extractDomain()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text( title, maxLines = 3, fontWeight = FontWeight.Bold) },
        text = { Text(" $downloadLink$webLink ", maxLines = 3) },
        buttons = {
            Column(
                Modifier.padding(start = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.Start
            ){
                if(downloadLink.isNotEmpty())
                    Button(
                        colors = ButtonDefaults.buttonColors(Color(0xFF1946AE)),
                        onClick = {onDownload(downloadLink); onDismiss()},
                    ) {
                        Text("Save Link", color = Color(0XFFFFFFFF))
                    }
                Button(
                    colors = ButtonDefaults.buttonColors(Color(0xFF1946AE)),
                    onClick = {onOpenLinkNewTab(webLink+downloadLink); onDismiss()}) {
                    Text("Open in new tab",  color = Color(0XFFFFFFFF))
                }

                Button(
                    colors = ButtonDefaults.buttonColors(Color(0xFF1946AE)),
                    onClick = {onDismiss()}) {
                    Text("Close",  color = Color(0XFFFFFFFF))
                }
            }
        }
    )
}