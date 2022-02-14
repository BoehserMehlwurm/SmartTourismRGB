package models

import android.net.Uri
import android.os.Parcelable
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.ar.sceneform.math.Vector3
import com.google.maps.android.data.Geometry
import com.google.maps.android.ktx.utils.sphericalHeading
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import timber.log.Timber
import timber.log.Timber.i
import kotlin.math.*
import kotlin.text.Typography.degree

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


