package api


//import com.google.gson.annotations.SerializedName
import models.Place

/**
 * Data class encapsulating a response from the nearby search call to the Places API.
 */
data class NearbyPlacesResponse(
    @SerializedName("results") val results: List<Place>
)

annotation class SerializedName(val value: String)
