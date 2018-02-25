package it.gruppoinfor.home2workapi.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.stfalcon.chatkit.commons.models.IUser
import it.gruppoinfor.home2workapi.HomeToWorkClient
import java.io.Serializable
import java.util.*


class User : Serializable, IUser {

    @SerializedName("UserId")
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
    var name_: String = ""
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
    @SerializedName("Regdate")
    @Expose
    var regdate: Date = Date()

    val avatarURL: String
        get() = "${HomeToWorkClient.AVATAR_BASE_URL}$id.jpg"

    private val formattedName: String
        get() = "$name_ $surname"

    override fun toString(): String {
        return formattedName
    }

    override fun equals(other: Any?): Boolean {
        if (other is User) {
            return id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    // Metodi interfaccia Chat Kit

    override fun getId(): String {
        return id.toString()
    }

    override fun getName(): String {
        return formattedName
    }

    override fun getAvatar(): String {
        return avatarURL
    }


}
