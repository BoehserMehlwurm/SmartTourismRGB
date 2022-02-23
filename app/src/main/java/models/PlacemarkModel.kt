package models

import android.net.Uri
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlacemarkModel(var id: Long = 0,
                          var title: String = "",
                          var description: String = "",
                          var image: Uri = Uri.EMPTY,
                          var lat : Double = 0.0,
                          var lng: Double = 0.0,
                          var zoom: Float = 0f,
                          var address: String = "") : Parcelable

@Parcelize
data class Locationsave(var lat: Double = 0.0,
                        var lng: Double = 0.0,
                        var zoom: Float = 0f,
                        var address: String= "",
                        var tag: Int = 0) : Parcelable

@Parcelize
data class Routing(
                    var routestartlatlng: LatLng? = null,
                    var routedestinationlatlng: LatLng? = null) : Parcelable
