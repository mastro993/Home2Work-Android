package it.gruppoinfor.home2workapi.share

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import it.gruppoinfor.home2workapi.user.User
import java.io.Serializable
import java.sql.Timestamp
import java.util.*

class Share : Serializable {

    @SerializedName("Id")
    @Expose
    var id: Long = 0
    @SerializedName("Host")
    @Expose
    var host: User? = null
    @SerializedName("Status")
    @Expose
    var status: Status? = null
    @SerializedName("Date")
    @Expose
    var date: Timestamp = Timestamp(0)
    @SerializedName("Type")
    @Expose
    var type: Type? = null
    @SerializedName("Guests")
    @Expose
    var guests = ArrayList<Guest>()

    enum class Status constructor(val value: Int) {

        @SerializedName("0")
        CREATED(0),
        @SerializedName("1")
        COMPLETED(1),
        @SerializedName("2")
        CANCELED(2)
    }

    enum class Type constructor(val value: Int) {
        @SerializedName("0")
        DRIVER(0),

        @SerializedName("1")
        GUEST(1)
    }
}
