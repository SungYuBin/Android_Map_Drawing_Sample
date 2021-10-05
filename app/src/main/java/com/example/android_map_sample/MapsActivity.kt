package com.example.android_map_sample

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.line_dialog.*
import kotlinx.android.synthetic.main.plane_dialog.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnMarkerClickListener {

    public var isPointButton: Boolean = false
    public var isLineButton: Boolean = false
    public var isPlaneButton: Boolean = false


    private lateinit var mMap: GoogleMap
    private lateinit var lineDialog: LineDialog;
    private lateinit var planeDialog: PlaneDialog;

    //line 수정할값모음
    private var lastLineColor: String = "#000000"; //마지막으로 설정한 컬러값
    private var lastThick: Float = 5F;
    private var selectPolyline: Polyline? = null;

    private var beforeLineColor: String = "";
    private var deforeThick: Float = 5F;


    //plane수정할 값 모음
    private var lastPlanColor: String = "#FFFFFFFF"
    private var beforePlaneColor: String = "#FFFFFFFF"
    private var selectPolygon: Polygon? = null;


    //symbolt수정할 값 모음
    lateinit var symbolActivity: SymbolActivity;
    lateinit var pointIntent: Intent;
    private lateinit var symbolMarker: Marker;


    var lineList: MutableList<Line> = ArrayList()//Line 내역들을 저장합니다
    var line: Line? = null; // Line

    var plane: Plane? = null

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10


    // var REQUIRED_PERMISSION : String[] = {ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};


    // 위치 권한이 있는지 확인하는 메서드
    fun checkPermissionForLocation(context: Context): Boolean {
        Log.d(TAG, "checkPermissionForLocation()")
        // Android 6.0 Marshmallow 이상에서는 지리 확보(위치) 권한에 추가 런타임 권한이 필요합니다.
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : O")
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                Log.d(TAG, "checkPermissionForLocation() 권한 상태 : X")
                ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult()")
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 클릭")
                startLocationUpdates()
                // View Button 활성화 상태 변경
//          btnStartupdate.isEnabled = false
//          btnStopUpdates.isEnabled = true
            } else {
                Log.d(TAG, "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    protected fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()")

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "startLocationUpdates() 두 위치 권한중 하나라도 없는 경우 ")
            return
        }
        Log.d(TAG, "startLocationUpdates() 위치 권한이 하나라도 존재하는 경우")
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청합니다.
        try {
            mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        } catch (e: Exception) {
            var i: Int = 2;
        }

    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d(TAG, "onLocationResult()")
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        Log.d(TAG, "onLocationChanged()")
        mLastLocation = location

        gpsvaluetext.setText(location.latitude.toString())
//        val date: Date = Calendar.getInstance().time
//        val simpleDateFormat = SimpleDateFormat("hh:mm:ss a")

//      txtTime.text = "Updated at : " + simpleDateFormat.format(date) // 갱신된 날짜
//      txtLat.text = "LATITUDE : " + mLastLocation.latitude // 갱신 된 위도
//      txtLong.text = "LONGITUDE : " + mLastLocation.longitude // 갱신 된 경도
    }

    // 위치 업데이터를 제거 하는 메서드
    private fun stoplocationUpdates() {
        Log.d(TAG, "stoplocationUpdates()")
        // 지정된 위치 결과 리스너에 대한 모든 위치 업데이트를 제거
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLocationRequest = LocationRequest.create().apply {
            interval = 2000 // 업데이트 간격 단위(밀리초)
            fastestInterval = 1000 // 가장 빠른 업데이트 간격 단위(밀리초)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 정확성
            maxWaitTime = 2000 // 위치 갱신 요청 최대 대기 시간 (밀리초)
        }

        checkPermissionForLocation(this)


        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //다이얼로그를 초기화합니다
        lineDialog = LineDialog(this)
        planeDialog = PlaneDialog(this)


        //라인다이얼로그가 중단되었을때 발생하는 이벤트
        lineDialog.dialog.setOnDismissListener {
            var mode = lineDialog.mode
            if (mode == "CANCLE") {
                selectPolyline!!.color = Color.parseColor(beforeLineColor) //기존의색상
                selectPolyline!!.width = deforeThick;
                //Toast.makeText(this, "취소했습니다", Toast.LENGTH_SHORT).show()
            } else {

                var latitude = lineDialog.dialog.line_latitude_text.text.toString()
                var lat = latitude.toDoubleOrNull()
                var longitude = lineDialog.dialog.line_longitude_text.text.toString()
                var log = longitude.toDoubleOrNull()

                var linecolor = lineDialog.dialog.line_color_text.text.toString()
                var thick = lineDialog.dialog.line_thickness_text.text.toString() //두께 값을 조정합니다
                val thickint: Float? = thick.toFloatOrNull()
                if (mode == "ADD") {
                    if (log != null && lat != null) {
                        val pos: LatLng = LatLng(lat!!, log!!)
//                        val circleOptions = CircleOptions()
//                                .center(pos)
//                                .radius(100000.0)
//                                .fillColor(Color.parseColor("#88000000"))
//                                .
//                        mMap.addCircle(circleOptions)

                        if (line!!.LineSize() == 0) {
                            line!!.AddLine(pos)
                            lastLineColor = linecolor //마지막으로 지정했던 색상을 해줍니다

                        } else {

                            var polylineOption: PolylineOptions = PolylineOptions()
                            polylineOption.clickable(true)

                            try {
                                polylineOption.color(Color.parseColor(linecolor))
                            } catch (e: Exception) {
                                polylineOption.color(Color.parseColor("#000000"))
                            }


                            if (thickint != null) {
                                polylineOption.width(thickint)
                            } else {
                                polylineOption.width(5F)
                            }

                            line!!.AddLine(pos)
                            polylineOption.addAll(line!!.list)
                            mMap.addPolyline(polylineOption)
                            mMap.setOnPolylineClickListener(this)

                            lastLineColor = linecolor //마지막으로 지정했던 색상을 해줍니다
                            lastThick = thickint!!
                        }
                    }
                } else if (mode == "DELETE") {

                    if (selectPolyline != null) {
                        line!!.LineClear()
                        selectPolyline!!.remove();
                    }

                } else if (mode == "APPLY") {

                    try {
                        selectPolyline?.color = Color.parseColor(linecolor)
                        lastLineColor = java.lang.String.format("#%06X", 0xFFFFFF and selectPolyline!!.color.toInt())
                    } catch (e: Exception) {
                        selectPolyline?.color = Color.parseColor("#000000")
                        Toast.makeText(this, "오류가 있습니다", Toast.LENGTH_SHORT).show()
                    }
                    //색상값을 변경해줍니다

                    if (thickint != null) {
                        selectPolyline?.width = thickint
                    } else {
                        selectPolyline?.width = 5F
                    }
                } else {
                    selectPolyline!!.color = Color.parseColor(beforeLineColor) //기존의색상
                    selectPolyline!!.width = deforeThick;
                }
            }

        }

        planeDialog.dialog.setOnDismissListener {

            var mode = planeDialog.mode
            if (mode == "CANCLE") {

                //취소 : 변경되었던 사항을 되돌립니다

            } else {
                var latitude = planeDialog.dialog.plane_latitude_text.text.toString()
                var lat = latitude.toDoubleOrNull()
                var longitude = planeDialog.dialog.plane_longitude_text.text.toString()
                var log = longitude.toDoubleOrNull()

                var color = planeDialog.dialog.plane_color.text.toString()
                if (mode == "ADD") {
                    if (log != null && lat != null) {
                        val pos: LatLng = LatLng(lat!!, log!!)
                        if (plane!!.PlaneSize() == 0) {
                            plane!!.AddPlane(pos)

                        } else {

                            val polygonOptions = PolygonOptions()
                            polygonOptions.clickable(true)
                            plane!!.AddPlane(pos)

                            polygonOptions.addAll(plane!!.list)

                            try {
                                polygonOptions.fillColor(Color.parseColor(color))
                                lastPlanColor = color;
                            } catch (e: Exception) {
                                polygonOptions.fillColor(Color.parseColor(beforePlaneColor))
                                lastPlanColor = "#FFFFFFF"
                            }

                            mMap.addPolygon(polygonOptions)
                            mMap.setOnPolygonClickListener(this)
                        }
                    }
                } else if (mode == "DELETE") {

                    plane!!.PlaneClear()
                    selectPolygon!!.remove()

                } else if (mode == "APPLY") {
                    try {
                        selectPolygon!!.fillColor = Color.parseColor(color)
                    } catch (e: Exception) {
                        selectPolygon!!.fillColor = Color.parseColor(beforePlaneColor)
                    }
                } else {

                }
            }
        }

//


    }


    override fun onResume() { //액티비티가 다시 들어올때 여기로 들어옵니다
        super.onResume()
    }

    override fun onPolygonClick(p0: Polygon) {
        if (isPlaneButton) {
            planeDialog.isApplyMode = true
            selectPolygon = p0;
//            if(selectPolygon==null) //처음선택한경우
//            {
//                selectPolygon = p0;
//            }
//
//            else{ //처음선택한게 아니라면
//
//
//                selectPolygon
//
//            }
            beforePlaneColor = java.lang.String.format("#%08X", 0xFFFFFFFF and selectPolygon!!.fillColor.toLong())
            planeDialog.ShowDialog(null, beforePlaneColor)

            planeDialog.isApplyMode = false
        }
    }

    //폴리라인을 클릭한경우 :이부분 다시 코드짜볼생각을하자
    override fun onPolylineClick(p0: Polyline) {

        if (isLineButton) {
            lineDialog.isApplyMode = true; //수정모드 on

            if (selectPolyline == null) {
                selectPolyline = p0
//
//                beforeLineColor = java.lang.String.format("#%06X", 0xFFFFFF and selectPolyline!!.color.toInt())
//                deforeThick = selectPolyline!!.width.toFloat()
            } else {

                if (selectPolyline != p0) //다른값을 선택했다면 기존의값을 저장해두고 그값을 그대로...
                {
                    selectPolyline!!.color = Color.parseColor(beforeLineColor)
                    selectPolyline = p0
//
//                    beforeLineColor = java.lang.String.format("#%06X", 0xFFFFFF and selectPolyline!!.color.toInt())
//                    deforeThick = selectPolyline!!.width.toFloat()
                } else  //같은값을 선택했던경우
                {
//                    beforeLineColor = java.lang.String.format("#%06X", 0xFFFFFF and selectPolyline!!.color.toInt())
//                    deforeThick = selectPolyline!!.width.toFloat()
                }

            }

            beforeLineColor = java.lang.String.format("#%06X", 0xFFFFFF and selectPolyline!!.color.toInt())
            deforeThick = selectPolyline!!.width.toFloat()


            //val selectColor = java.lang.String.format("#%06X", 0xFFFFFF and selectPolyline!!.color.toInt())
            //  lineDialog.ShowDialog(null, selectColor, lastThick) //다이얼로그를 띄웁니다
            lineDialog.ShowDialog(null, beforeLineColor, lastThick) //다이얼로그를 띄웁니다


            selectPolyline?.color = Color.parseColor("#CEEC0D")//하이라이트 처리합니다
            lineDialog.isApplyMode = false; //수정모드 off
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 101) {
            val mode = data?.getStringExtra("mode")
            if (mode == "ADD") {
                val latitude: Double? = data?.getStringExtra("latitude")?.toDoubleOrNull()
                if (latitude == null) {
                    Toast.makeText(this, "위도값이 잘못되었습니다", Toast.LENGTH_SHORT).show()
                    SetPointButtonUI() //ui를 변경합니다
                    return;
                }

                val longitude: Double? = data?.getStringExtra("longitude")?.toDoubleOrNull()
                if (longitude == null) {
                    Toast.makeText(this, "경도값이 잘못되었습니다", Toast.LENGTH_SHORT).show()
                    SetPointButtonUI() //ui를 변경합니다
                    return;
                }

                val rotation: Float? = data?.getStringExtra("rotation")?.toFloatOrNull()
                if (rotation == null) {
                    // Toast.makeText(this, "회전값이 비어있습니다", Toast.LENGTH_SHORT).show()
                }

                //모든값이 잘 들어가있다면 심볼을 추가합니다
                AddSymbol(latitude, longitude, rotation) //심볼을 추가합니다
            } else if (mode == "Delete") {
                symbolMarker.remove()
                SetPointButtonUI()
            } else if (mode == "APPLY") {

                val latitude: Double? = data?.getStringExtra("latitude")?.toDoubleOrNull()
                val longitude: Double? = data?.getStringExtra("longitude")?.toDoubleOrNull()
                if (latitude != null && longitude != null) {

                    val newpos: LatLng = LatLng(latitude, longitude)
                    symbolMarker.position = newpos
                }


                val rotation: Float? = data?.getStringExtra("rotation")?.toFloatOrNull()
                if (rotation != null) {
                    symbolMarker.rotation = rotation
                }

                SetPointButtonUI()
            } else if (mode == "CANCLE") {
                SetPointButtonUI()
            }
        }

    }

    //심볼을 읽어와서 클릭합니다
    fun AddSymbol(lat: Double, log: Double, rot: Float?) {
        val pos: LatLng = LatLng(lat, log)
        try {
            val markerOptions: MarkerOptions = MarkerOptions()
            markerOptions.position(pos)
            markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.ic__561238_navigation_icon))
//            mMap.addMarker(MarkerOptions()
//                    .position(pos)
//                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic__561238_navigation_icon))
//                    .rotation(rot!!))

            if (rot != null) {
                markerOptions.rotation(rot!!)
            }
            mMap.addMarker(markerOptions)

        } catch (e: Exception) {

            Toast.makeText(this, "결국 죽고말았습니다 음음", Toast.LENGTH_SHORT).show()
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos)) //화면을 이 위치로 변경합니다
        mMap.setOnMarkerClickListener(this)

    }

    //vector 이미지를 bitmap으로 변경합니다
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        symbolActivity.isApplyMode = true
        symbolMarker = p0
        Toast.makeText(this, "점을 클릭했습니다", Toast.LENGTH_SHORT).show()
        startActivityForResult(pointIntent, 101)

        symbolActivity.isApplyMode = false
        return true
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //  mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);

    }

    //맵을 선택했을때의 이벤트
    override fun onMapClick(pos: LatLng) {

        if (isLineButton) { //선을 그립니다
            lineDialog.ShowDialog(pos, lastLineColor, lastThick)
        }

        if (isPlaneButton) //면을 그립니다
        {
            planeDialog.ShowDialog(pos, lastPlanColor)
        }
    }


    fun SetPointButtonUI() {
        isPointButton = !isPointButton
        if (isPointButton) {
            point_button.setBackgroundColor(Color.parseColor("#99003399"))

            //var intent = Intent(this, SymbolActivity::class.java) //팝업을 띄웁니다
            symbolActivity = SymbolActivity()
            pointIntent = Intent(this, symbolActivity::class.java) //팝업을 띄웁니다
            startActivityForResult(pointIntent, 101)
        } else {
            point_button.setBackgroundColor(Color.parseColor("#003399"))
        }


        if (isLineButton) {
            line_button.setBackgroundColor(Color.parseColor("#003399"))
            isLineButton = !isLineButton
        }
        if (isPlaneButton) {
            plane_button.setBackgroundColor(Color.parseColor("#003399"))
            isPlaneButton = !isPlaneButton
        }
    }

    fun SetLineButtonUI() {
        isLineButton = !isLineButton
        if (isLineButton) {
            line_button.setBackgroundColor(Color.parseColor("#99003399"))

        } else {
            line_button.setBackgroundColor(Color.parseColor("#003399"))
            lineDialog.Dismiss()
        }

        if (isPointButton) //모든종류의 버튼을 색상 off 해야합니다
        {
            point_button.setBackgroundColor(Color.parseColor("#003399"))
            isPointButton = !isPointButton

        }
        if (isPlaneButton) {
            plane_button.setBackgroundColor(Color.parseColor("#003399"))
            planeDialog.Dismiss()
            isPlaneButton = !isPlaneButton
        }

    }

    fun SetPlaneButtonUI() {
        isPlaneButton = !isPlaneButton
        if (isPlaneButton) {
            plane_button.setBackgroundColor(Color.parseColor("#99003399"))


        } else {
            plane_button.setBackgroundColor(Color.parseColor("#003399"))
            planeDialog.Dismiss()

        }


        if (isPointButton) //모든종류의 버튼을 색상 off 해야합니다
        {
            point_button.setBackgroundColor(Color.parseColor("#003399"))
            isPointButton = !isPointButton
        }
        if (isLineButton) {
            line_button.setBackgroundColor(Color.parseColor("#003399"))
            isLineButton = !isLineButton
            lineDialog.Dismiss()
        }

    }

    fun OnClickPointButton(view: View) {
        SetPointButtonUI()
    }

    fun OnClickLineButton(view: View) {
        line = Line()
        // lineDialog.i
        lastLineColor = "#000000"
        lastThick = 5F

        SetLineButtonUI();
    }

    fun OnClickPlaneButton(view: View) {
        plane = Plane()


        SetPlaneButtonUI();
    }
}

