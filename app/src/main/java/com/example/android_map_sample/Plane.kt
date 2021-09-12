package com.example.android_map_sample

import com.google.android.gms.maps.model.LatLng

class Plane {
    var list : MutableList<LatLng> = ArrayList()//여기에 위치값을 저장합니다
    fun AddPlane(latLng: LatLng){
        list.add(latLng)
    }

    fun PlaneSize() :Int{
        return list.size
    }

    fun PlaneClear(){
       list.clear()
    }

}