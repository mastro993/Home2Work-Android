package it.gruppoinfor.home2workapi.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2workapi.HomeToWorkClient
import java.io.Serializable
import java.util.*


open class User : Serializable {

    @SerializedName("Id")
    @Expose
    var id: Long = 0
    @SerializedName("Email")
    @Expose
    var email: String = ""
    @SerializedName("Name")
    @Expose
    var name: String = ""
    @SerializedName("Surname")
    @Expose
    var surname: String = ""
    @SerializedName("Location")
    @Expose
    var location: LatLng = LatLng()
    @SerializedName("Address")
    @Expose
    var address: Address = Address()
    @SerializedName("Company")
    @Expose
    var company: Company = Company()
    @SerializedName("Regdate")
    @Expose
    var regdate: Date = Date()

    val avatarURL: String
        get() = "${HomeToWorkClient.AVATAR_BASE_URL}$id.jpg"

    override fun toString(): String {
        return "$name $surname"
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


}
