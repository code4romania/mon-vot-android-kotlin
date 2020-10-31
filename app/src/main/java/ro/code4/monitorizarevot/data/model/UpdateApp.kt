package ro.code4.monitorizarevot.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.parceler.Parcel

/*
    CREATED BY @PEDROFSN IN 31/10/20 16:29
*/

@Parcelize
data class UpdateApp(
    val needUpdate : Boolean,
    val forceUpdate : Boolean
) : Parcelable