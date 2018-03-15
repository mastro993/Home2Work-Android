package it.gruppoinfor.home2work.utils


import io.objectbox.converter.PropertyConverter
import it.gruppoinfor.home2work.location.LatLng

class LatLngConverter : PropertyConverter<LatLng, String> {

    override fun convertToEntityProperty(databaseValue: String): LatLng {

        val strings = databaseValue.split(",")
        val lat = strings[0].toDouble()
        val lng = strings[1].toDouble()

        return LatLng(lat, lng)
    }

    override fun convertToDatabaseValue(entityProperty: LatLng): String {

        return "%.8f,%.8f".format(entityProperty.lat, entityProperty.lng)
    }

}
