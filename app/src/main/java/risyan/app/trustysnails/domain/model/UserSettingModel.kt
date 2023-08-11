package risyan.app.trustysnails.domain.model

import com.google.gson.annotations.SerializedName
import risyan.app.trustysnails.data.remote.model.UserSettingDto

data class UserSettingModel(
    @SerializedName("browsing_mode") var browsingMode: String? = null,
    @SerializedName("one_by_one_list") var oneByOneList: ArrayList<String> = arrayListOf(),
    @SerializedName("clean_filter_list") var cleanFilterList: ArrayList<String> = arrayListOf(),
    @SerializedName("hard_single_browser") var hardSingleBrowser: Boolean? = null
) {
    fun toSettingRequest(): UserSettingDto {
        return UserSettingDto(
            browsingMode,
            ArrayList(oneByOneList),
            ArrayList(cleanFilterList),
            hardSingleBrowser
        )
    }
}