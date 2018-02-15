package it.gruppoinfor.home2workapi.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2workapi.HomeToWorkClient
import java.io.Serializable
import java.util.*


class User : Serializable {

    @SerializedName("UserID")
    @Expose
    var id: Long = 0
    @SerializedName("Email")
    @Expose
    var email: String = ""
    @SerializedName("Token")
    @Expose
    var token: String = ""
    @SerializedName("Name")
    @Expose
    var name: String = ""
    @SerializedName("Surname")
    @Expose
    var surname: String = ""
    @SerializedName("HomeLatLng")
    @Expose
    var location: LatLng = LatLng()
    @SerializedName("HomeAddress")
    @Expose
    var address: Address = Address()
    @SerializedName("Company")
    @Expose
    var company: Company = Company()
    @SerializedName("Configured")
    @Expose
    var isConfigured: Boolean = false
    @SerializedName("Facebook")
    @Expose
    var facebook: String = ""
    @SerializedName("Twitter")
    @Expose
    var twitter: String = ""
    @SerializedName("Telegram")
    @Expose
    var telegram: String = ""
    @SerializedName("Regdate")
    @Expose
    var regdate: Date = Date()

    val avatarURL: String
        get() = "${HomeToWorkClient.AVATAR_BASE_URL}$id.jpg"

    private val formattedName: String
        get() = "$name $surname"

    override fun toString(): String {
        return formattedName
    }

    override fun equals(other: Any?): Boolean {
        if (other is User) {
            val user = other as User?
            return id == user!!.id
        }
        return false
    }
}
