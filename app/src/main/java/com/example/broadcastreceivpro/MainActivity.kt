package com.example.broadcastreceivpro

import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.broadcastreceivpro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 1. 브로드 캐스터 리시버 임시객체 만들어서 바로 배터리 정보를 획득한다.
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)


        // 2. 배터리 값이 바뀌는 정보를 바로 갖고옴
        val intent = registerReceiver(null, intentFilter)
        val extra_status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        Log.e("MainActivity", "extra_status: ${extra_status}")

        // 충전 상태의 정보를 체크하는데 USB 충전중 , AC 충전중 여부를 확인
        when (extra_status) {

            // ++ BatteryManager.BATTERY_STATUS_CHARGING  start
            BatteryManager.BATTERY_STATUS_CHARGING -> {
                when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                    //ac
                    BatteryManager.BATTERY_PLUGGED_AC -> {
                        binding.ivBattery.setImageBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.power_ac))
                        binding.tvInfo.text = "PLUGGED_AC"
                    }
                    BatteryManager.BATTERY_PLUGGED_USB -> {
                        binding.ivBattery.setImageBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.usb))
                        binding.tvInfo.text = "PLUGGED_USB"
                    }
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                        binding.ivBattery.setImageBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.wireless))
                        binding.tvInfo.text = "PLUGGED_WIRELESS"
                    }
                    else -> {
                        binding.ivBattery.setImageBitmap(
                            BitmapFactory.decodeResource(resources, R.drawable.battery_full_24))
                        binding.tvInfo.text = "PULL_CHARGING"
                    }
                }
            } // ++ BatteryManager.BATTERY_STATUS_CHARGING  end

            //No 충전중
            else -> {
                binding.ivBattery.setImageResource(R.drawable.battery_unknown_24)
                binding.tvInfo.text = "!NO CHARGING!"
            }
        }
        //배터리의 잔여량을 계산하여 보여줌(공통적인 부분)
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        // 여기는 왜 !! 가 뜨는 지 모르겠음. 나중에 꼭 확인해보기.
        val percent = (level!! / scale!!.toFloat()) * 100
        binding.tvPercent.text = "${percent}%"


        // 이벤트 처리 (내가 만든 MyReceiver을 불러서 Notification 알림 발생) :부가적인 정보 배터리 정보를 알려줌

        binding.btnCallReceiver.setOnClickListener {
            val intent =Intent(this, MyReceiver::class.java)
            intent.putExtra("batteryPercent", binding.tvPercent.text)
            sendBroadcast(intent)
        }
    }//onCreate end

}
