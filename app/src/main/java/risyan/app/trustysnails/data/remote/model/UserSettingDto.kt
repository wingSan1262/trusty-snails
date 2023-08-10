package risyan.app.trustysnails.data.remote.model

data class UserSettingDto (

    @SerializedName("browsing_mode"       ) var browsingMode      : String?           = null,
    @SerializedName("one_by_one_list"     ) var oneByOneList      : ArrayList<String> = arrayListOf(),
    @SerializedName("clean_filter_list"   ) var cleanFilterList   : ArrayList<String> = arrayListOf(),
    @SerializedName("hard_single_browser" ) var hardSingleBrowser : Boolean?          = null

)