package risyan.app.trustysnails.features.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.size.Size
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.*
import risyan.app.trustysnails.basecomponent.ui.theme.BLUE_002989
import risyan.app.trustysnails.data.remote.model.BrowsingMode
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.features.view.component.DomainListInput
import risyan.app.trustysnails.features.view.navigator.Screen
import risyan.app.trustysnails.features.viewmodel.UserViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.SettingScreen(
    userViewModel: UserViewModel,
    finish: ()->Unit
){
    composable(route = Screen.SETTING_SCREEN){

        val saveDataStatus = userViewModel.setSettingData.observeAsState()
        val getUserSettingData = userViewModel.getSettingData.observeAsState()
        var userSetting : UserSettingModel? by remember { mutableStateOf(
            userViewModel.getSettingData.value?.getBareContent()
        ) }
        var isLoading by remember {
            mutableStateOf(false)
        }

        userSetting?.let {
            SettingContent(
                userSetting = userSetting!!,
                isLoading = isLoading,
                saveSetting = {
                    isLoading = true
                    userViewModel.setSetting(it)
                })
        }

        EventEffect(saveDataStatus, {
            userViewModel.getSetting();finish()
        }){ data, owner ->
            isLoading = false; owner .showToast(data.exception.message.toString())}

        EventEffect(getUserSettingData,{
            userSetting = it.body; isLoading = false
        }){data, owner ->
            userSetting = UserSettingModel()
            isLoading = false; owner.showToast(data.exception.message.toString())
        }
    }
}

@Composable
@Preview
fun SettingPreview(){
    SettingContent(UserSettingModel(cleanFilterList = arrayListOf("awdawd", "awdawdad")), false){
    }
}

@Composable
fun SettingContent(
    userSetting : UserSettingModel,
    isLoading : Boolean,
    saveSetting : (UserSettingModel)->Unit
){
    val context = LocalContext.current
    var userSettingState by remember {
        mutableStateOf(userSetting)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Screen title
        item {

            Text(
                text = "Pilih Mode",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )

            GifDisplay(
                gifResource = if(userSettingState.browsingMode == BrowsingMode.CLEAN_MODE)
                    R.drawable.clean_mode else R.drawable.one_by_one,
                size = Size(108, 108),
                modifier = Modifier.size(108.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                CardButton(
                    title = "Clean Filter",
                    description = "Semua web terblock kecuali yang kamu list dibawah",
                    isSelected = userSettingState.browsingMode == BrowsingMode.CLEAN_MODE // Change this to true/false based on your logic
                ){
                    userSettingState = userSettingState.copy(browsingMode = BrowsingMode.CLEAN_MODE)
                }

                CardButton(
                    title = "One By One",
                    description = "Hanya web yang kamu list yang akan terblock",
                    isSelected = userSettingState.browsingMode == BrowsingMode.ONE_BY_ONE  // Change this to true/false based on your logic
                ){  userSettingState = userSettingState.copy(browsingMode = BrowsingMode.ONE_BY_ONE)}
            }
        }

        DomainListInput(
            this,
            userSettingState.browsingMode,
            domainStateList = if(userSettingState.browsingMode == BrowsingMode.CLEAN_MODE) userSettingState.cleanFilterList
                else userSettingState.oneByOneList,
        ) // Pass your list of domains here
        {
            userSettingState = if (userSettingState.browsingMode == BrowsingMode.CLEAN_MODE)
                userSettingState.copy(cleanFilterList = ArrayList(it))
            else userSettingState.copy(oneByOneList = ArrayList(it))
        }

        item {
            Button(
                onClick = {
                    saveSetting(userSettingState)
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = BLUE_002989),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                enabled = !isLoading
            ) {
                Row(
                    Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Simpan", color = Color.White, fontSize = 18.sp)
                    if(isLoading){
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            Modifier
                                .size(18.dp)
                                .padding(top = 1.dp),
                            color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CardButton(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick : ()->Unit
) {
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .width(160.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        border = BorderStroke(2.dp, if (isSelected) Blue else Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = description,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
