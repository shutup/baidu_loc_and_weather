package com.shutup.location;

import android.os.Message;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.shutup.location.MainActivity.MyHandler;

public class MyLocationListener implements BDLocationListener {

	private MyHandler mHandler;
	public MyLocationListener(MyHandler mHandler) {
		// TODO Auto-generated constructor stub
		this.mHandler=mHandler;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		// TODO Auto-generated method stub
		if (location == null)
            return ;
		Message msg=mHandler.obtainMessage();
		msg.obj=location;
		msg.what=0;
		mHandler.sendMessage(msg);
		
	}

		
}
