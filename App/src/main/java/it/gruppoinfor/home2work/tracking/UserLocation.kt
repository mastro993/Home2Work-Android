package it.gruppoinfor.home2work.tracking


import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import it.gruppoinfor.home2work.location.LatLng
import it.gruppoinfor.home2work.utils.LatLngConverter

@Entity
data class UserLocation(var userId: Long = 0,
                        @Convert(converter = LatLngConverter::class, dbType = String::class) var latLng: LatLng = LatLng(),
                        var timestamp: Long = 0) {

    @Id
    var id: Long = 0

}