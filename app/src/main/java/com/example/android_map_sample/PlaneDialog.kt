package com.example.android_map_sample

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.line_dialog.*
import kotlinx.android.synthetic.main.plane_dialog.*
import java.lang.Exception

class PlaneDialog(_context: Context) {

 //   var context :Context = _context;
    var dialog: Dialog = Dialog(_context)
    var mode ="ADD"
    var isApplyMode = false;

    fun ShowDialog(pos:LatLng?,planColor:String){

        dialog.setContentView(R.layout.plane_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        dialog.setCancelable(false)


        if(pos!=null){
            dialog.plane_latitude_text.setText(pos.latitude.toString())
            dialog.plane_longitude_text.setText(pos.longitude.toString())
            dialog.plane_color.setText(planColor)
        }
        else{
            dialog.plane_color.setText(planColor)
        }

        //글자 변하는 이벤트
        dialog.plane_color.doAfterTextChanged {
            try {
                dialog.plane_color.setTextColor(Color.parseColor(dialog.plane_color.text.toString()))
            } catch (e: Exception) {
                dialog.plane_color.setTextColor(Color.parseColor("#000000"))
            }

        }

        if (!isApplyMode) {

            dialog.button_plane_delete.visibility = View.GONE //삭제버튼을 숨깁니다
            dialog.button_plane_apply.visibility = View.GONE //적용버튼을 숨깁니다

        }

        dialog.show()

        dialog.button_plane_add.setOnClickListener(View.OnClickListener {
            //기능: 위치를 추가해서 선을 늘리는 작업을 합니다
            mode ="ADD"
            Dismiss()
        })

        dialog.button_plane_apply.setOnClickListener(View.OnClickListener {
            //선을 클릭해서 색상을 변경하는
            mode= "APPLY"
            Dismiss()
        })

        dialog.button_plane_cancle.setOnClickListener{

            mode ="CANCLE"

            Dismiss()
        }

        dialog.button_plane_delete.setOnClickListener(View.OnClickListener {
            //선을 지우는 작업
            mode= "DELETE"
            Dismiss()

        })

    }

    fun Dismiss() //끕니다
    {
        dialog.dismiss()
    }



}