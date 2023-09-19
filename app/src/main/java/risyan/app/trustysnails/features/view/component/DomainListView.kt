@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class)

package risyan.app.trustysnails.features.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.checkLinkValidity
import risyan.app.trustysnails.basecomponent.isTimeValid
import risyan.app.trustysnails.basecomponent.recheckValidityAndTransform
import risyan.app.trustysnails.basecomponent.showToast
import risyan.app.trustysnails.basecomponent.ui.component.SlideFromEndContainer
import risyan.app.trustysnails.basecomponent.ui.component.UrlNavigatingEditText
import risyan.app.trustysnails.data.remote.model.BrowsingMode


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
fun DomainListInput(
    scope: LazyListScope,
    browsingMode: BrowsingMode,
    domainStateList: List<String> = listOf(),
    onDomainChanged: (List<String>) -> Unit
) {
    scope.item {

        val context = LocalContext.current
        val keyboard = LocalSoftwareKeyboardController.current

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
                SlideFromEndContainer(content = {
                    SwipeToDismiss(
                        state = rememberDismissState { direction ->
                            if (direction == DismissValue.DismissedToStart) {
                                if(browsingMode == BrowsingMode.ONE_BY_ONE && !isTimeValid()){
                                    context.showToast("Penghapusan tersedia pada 15 menit pertama " +
                                            "setiap jam dengan kelipatan 3 (mis: 12, 21, dll).")
                                    return@rememberDismissState false
                                }
                                onDomainChanged(ArrayList(domainStateList).apply {removeAt(index)})
                                return@rememberDismissState true
                            }
                            return@rememberDismissState false
                        },
                        modifier = Modifier.padding(8.dp),
                        directions = setOf(DismissDirection.EndToStart),
                        dismissContent = {
                            UrlNavigatingEditText(
                                valueText = value,
                                placeholder = "Enter domain",
                                onNewLink = {
                                    if(it.isEmpty()) return@UrlNavigatingEditText
                                    it.recheckValidityAndTransform().let{ checkString ->
                                        if(checkString.checkLinkValidity())
                                            onDomainChanged(ArrayList(domainStateList).apply {
                                                this[index] = checkString
                                            })
                                        else
                                            context.showToast("Invalid add subdomain (ex:www) or tld (ex.com)")
                                    }
                                    keyboard?.hide()
                                },
                                isUseRefresh = false
                            )
                        },
                        background = {}
                    )
                })
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
                        if (browsingMode == BrowsingMode.CLEAN_MODE && !isTimeValid()) {
                            context.showToast(
                                "Penyimpanan tersedia pada 15 menit pertama " +
                                        "setiap jam dengan kelipatan 3 (mis: 12, 21, dll)."
                            )
                            return@clickable
                        }
                        onDomainChanged(
                            ArrayList(domainStateList).apply { add("") })
                    }
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "More next", fontWeight = FontWeight.Bold)
        }
    }
}
