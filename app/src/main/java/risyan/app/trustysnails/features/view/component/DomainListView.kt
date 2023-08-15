package risyan.app.trustysnails.features.view.component

import android.graphics.Path
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.checkLinkValidity
import risyan.app.trustysnails.basecomponent.recheckValidityAndTransform
import risyan.app.trustysnails.basecomponent.showToast
import risyan.app.trustysnails.basecomponent.ui.component.CommonEditText


@OptIn(ExperimentalMaterialApi::class)
fun DomainListInput(
    scope : LazyListScope,
    domainStateList: List<String> = listOf(),
    onDomainChanged: (List<String>) -> Unit
) {
    scope.item {

        val context = LocalContext.current
        Text(
            text = "Masukan list domain yang ingin kamu akses",
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {
            domainStateList.forEachIndexed { index,value ->
                SwipeToDismiss(
                    state = rememberDismissState { direction ->
                        if (direction == DismissValue.DismissedToStart) {
                            onDomainChanged(ArrayList(domainStateList).apply {removeAt(index)})
                            return@rememberDismissState true
                        }
                        return@rememberDismissState false
                    },
                    modifier = Modifier.padding(8.dp),
                    directions = setOf(DismissDirection.EndToStart),
                    dismissContent = {
                        CommonEditText(
                            startingText = value,
                            placeholder = "Enter domain",
                            onDone = {
                                if(it.isEmpty()) return@CommonEditText
                                it.recheckValidityAndTransform().let{ checkString ->
                                    if(checkString.checkLinkValidity())
                                        onDomainChanged(ArrayList(domainStateList).apply {
                                            this[index] = checkString
                                        })
                                    else
                                        context.showToast("Invalid add subdomain (ex:www) or tld (ex.com)")
                                }

                            }
                        )
                    },
                    background = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        onDomainChanged(
                            ArrayList(domainStateList).apply {add("")})
                    }
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "More next", fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Composable
fun Preview(){
    LazyColumn(content = {
        DomainListInput(
            this,
            listOf("awdinawid", "", "awdinawid", "awdinawid"),
            {}
        )
    })

}
