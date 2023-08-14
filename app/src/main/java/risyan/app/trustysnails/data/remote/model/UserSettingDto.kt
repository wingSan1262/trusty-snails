package risyan.app.trustysnails.data.remote.model

import com.google.gson.annotations.SerializedName
import risyan.app.trustysnails.domain.model.UserSettingModel

data class UserSettingDto(
    @SerializedName("browsing_mode") var browsingMode: String? = null,
    @SerializedName("one_by_one_list") var oneByOneList: ArrayList<String> = arrayListOf(),
    @SerializedName("clean_filter_list") var cleanFilterList: ArrayList<String> = arrayListOf(),
    @SerializedName("hard_single_browser") var hardSingleBrowser: Boolean? = null
) {
    fun toUserSettingModel(): UserSettingModel {
        return UserSettingModel(
            BrowsingMode.fromValue(browsingMode ?: ""),
            ArrayList(oneByOneList),
            ArrayList(cleanFilterList),
            hardSingleBrowser ?: false
        )
    }
}

enum class BrowsingMode(val value: String) {
    CLEAN_MODE("clean_filter"),
    ONE_BY_ONE("one_by_one");

    companion object {
        fun fromValue(string: String): BrowsingMode {
            return values().find { it.value == string } ?: ONE_BY_ONE
        }
    }
}