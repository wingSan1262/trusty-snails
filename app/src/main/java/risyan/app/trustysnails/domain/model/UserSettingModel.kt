package risyan.app.trustysnails.domain.model

import com.google.gson.annotations.SerializedName
import risyan.app.trustysnails.basecomponent.extractDomain
import risyan.app.trustysnails.data.remote.model.BrowsingMode
import risyan.app.trustysnails.data.remote.model.UserSettingDto

data class UserSettingModel(
    @SerializedName("browsing_mode") var browsingMode: BrowsingMode = BrowsingMode.CLEAN_MODE,
    @SerializedName("one_by_one_list") var oneByOneList: ArrayList<String> = arrayListOf(),
    @SerializedName("clean_filter_list") var cleanFilterList: ArrayList<String> = arrayListOf(),
    @SerializedName("hard_single_browser") var hardSingleBrowser: Boolean = false
) {
    fun toSettingRequest(): UserSettingDto {
        return UserSettingDto(
            browsingMode.value,
            ArrayList(oneByOneList),
            ArrayList(cleanFilterList),
            hardSingleBrowser
        )
    }

    fun validityTransform(){
        cleanFilterList = ArrayList(cleanFilterList.filter { !it.isEmpty() })
        oneByOneList = ArrayList(oneByOneList.filter { !it.isEmpty() })
    }
}

fun List<String>.getOneByOneIsSafe(link : String): Boolean {
    val targetDomain = link.extractDomain()
    forEach {
        if(
            it.contains(targetDomain, true) ||
            targetDomain.contains(it, true))
            return true
    }
    return false
}

fun List<String>.getCleanIsSafe(link : String): Boolean {
    val targetDomain = link.extractDomain()
    forEach {
        if(
            it.contains(targetDomain, true) ||
            targetDomain.contains(it, true))
            return false
    }
    return true
}