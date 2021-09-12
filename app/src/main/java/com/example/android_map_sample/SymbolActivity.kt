package com.example.android_map_sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_symbol.*


class SymbolActivity : AppCompatActivity() {

    var isApplyMode: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_create_symbol)

        if (!isApplyMode) {
            button_symbol_apply.visibility = View.GONE //적용
            button_symbol_delete.visibility = View.GONE //삭제
        }
        else{
            button_symbol_apply.visibility = View.VISIBLE //적용
            button_symbol_delete.visibility = View.VISIBLE //삭제
        }
    }

    fun OnClickOkButton(view: View) {
        val latitudestr: String = latitude_text.text.toString() //위도
        val longitudestr: String = longitude_text.text.toString() //경도

        if (latitudestr == "" || longitudestr == "") {
            finish()
        }

        val rotationstr: String = rotation_text.text.toString() //회전값

        val intent = Intent()
        intent.putExtra("mode", "ADD")
        intent.putExtra("latitude", latitudestr)
        intent.putExtra("longitude", longitudestr)
        intent.putExtra("rotation", rotationstr)


        setResult(RESULT_OK, intent)

        finish()
    }

    fun OnClickCancleButton(view: View) {


        val intent = Intent()
        intent.putExtra("mode", "CANCLE")
//        intent.putExtra("latitude",latitudestr)
//        intent.putExtra("longitude",longitudestr)
//        intent.putExtra("rotation",rotationstr)


        setResult(RESULT_OK, intent)

        finish()
    }

    fun OnClickDeleteButton(view: View) {

//        val latitudestr:String= latitude_text.text.toString() //위도
//        val longitudestr:String = longitude_text.text.toString() //경도
//
//        if(latitudestr==""||longitudestr=="")
//        {
//            finish()
//        }
//
//        val rotationstr:String = rotation_text.text.toString() //회전값
//
//        val intent = Intent()
//        intent.putExtra("mode","ADD")
//        intent.putExtra("latitude",latitudestr)
//        intent.putExtra("longitude",longitudestr)
//        intent.putExtra("rotation",rotationstr)
//
//
//        setResult(RESULT_OK, intent)
//

        finish()
    }

    fun OnClickApplyButton(view: View) {

        val latitudestr: String = latitude_text.text.toString() //위도
        val longitudestr: String = longitude_text.text.toString() //경도

        if (latitudestr == "" || longitudestr == "") {
            finish()
        }

        val rotationstr: String = rotation_text.text.toString() //회전값

        val intent = Intent()
        intent.putExtra("mode", "APPLY")
        intent.putExtra("latitude", latitudestr)
        intent.putExtra("longitude", longitudestr)
        intent.putExtra("rotation", rotationstr)


        setResult(RESULT_OK, intent)


        finish()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}