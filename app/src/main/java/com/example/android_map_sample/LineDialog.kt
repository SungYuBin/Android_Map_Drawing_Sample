package com.example.android_map_sample

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.line_dialog.*
import java.lang.Exception

class LineDialog(_context: Context) {
    //
    var context: Context = _context;

    val dialog: Dialog = Dialog(_context)
    var isApplyMode = false;

    //  var iscancle = false;
    var mode = "ADD"

    fun ShowDialog(pos: LatLng?, lastLineColor: String, thick: Float) {
        dialog.setContentView(R.layout.line_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        dialog.setCancelable(false)

        if (pos != null) {
            //초기값을 설정해주어야합니다
            dialog.line_latitude_text.setText(pos.latitude.toString())
            dialog.line_longitude_text.setText(pos.longitude.toString())
            dialog.line_color_text.setText(lastLineColor)
            dialog.line_color_text.setTextColor(Color.parseColor(lastLineColor))
            dialog.line_thickness_text.setText(thick.toString())

        }
        else{
            dialog.line_color_text.setText(lastLineColor)
            dialog.line_color_text.setTextColor(Color.parseColor(lastLineColor))
            dialog.line_thickness_text.setText(thick.toString())
        }

        if (!isApplyMode) {

            dialog.button_line_delete.visibility = View.GONE //삭제버튼을 숨깁니다
            dialog.button_line_apply.visibility = View.GONE //적용버튼을 숨깁니다

        }


        dialog.show()


        //글자 변하는 이벤트
        dialog.line_color_text.doAfterTextChanged {
            try {
                dialog.line_color_text.setTextColor(Color.parseColor(dialog.line_color_text.text.toString()))
            } catch (e: Exception) {
                dialog.line_color_text.setTextColor(Color.parseColor("#000000"))
            }

        }

        dialog.button_line_add.setOnClickListener(View.OnClickListener {
            //기능: 위치를 추가해서 선을 늘리는 작업을 합니다

            mode = "ADD"
            Dismiss()
        })

        dialog.button_line_apply.setOnClickListener(View.OnClickListener {
            //선을 클릭해서 색상을 변경하는
            mode = "APPLY"
            Dismiss()
        })

        dialog.button_line_delete.setOnClickListener {
            //선을 지우는 작업

            mode = "DELETE"
            Dismiss()
        }

        dialog.button_line_cancle.setOnClickListener {
            mode = "CANCLE"
            //   iscancle = true
            Dismiss()
        }

    }

    fun Dismiss() //끕니다
    {
        dialog.dismiss()
    }


}