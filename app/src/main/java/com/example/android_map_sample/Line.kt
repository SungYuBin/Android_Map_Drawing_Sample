package com.example.android_map_sample

import com.google.android.gms.maps.model.LatLng

class Line {
     var list : MutableList<LatLng> = ArrayList()//여기에 위치값을 저장합니다
    fun AddLine(latLng: LatLng){
        list.add(latLng)
    }

    fun LineSize() :Int{
        return list.size
    }

    fun LineClear(){
        list.clear()
    }



}