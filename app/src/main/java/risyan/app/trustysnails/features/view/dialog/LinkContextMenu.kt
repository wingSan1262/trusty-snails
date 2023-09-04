package risyan.app.trustysnails.features.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import risyan.app.trustysnails.features.view.model.ContextMenuModel

@Composable
fun LinkContextMenu(
    onDismiss : ()->Unit,
    linkContent : String,
    onDownload : (link : String)->Unit = {},
    onOpenWithBrowser: (link : String)->Unit = {},
){
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Url: $linkContent") },
        buttons = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Button(onClick = {onDownload(linkContent); onDismiss()}) {
                    Text("Save Link")
                }
//                Button(onClick = {
//                    onOpenWithBrowser(linkContent);
//                    onDismiss()}) {
//                    Text("Open in browser")
//                }
                Button(onClick = {onDismiss()}) {
                    Text("Close")
                }
            }
        }
    )
}