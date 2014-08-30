package com.shutup.location;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
public class MainActivity extends Activity {

	private MyHandler mHandler;
	//baidu api
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener ;
	 LocationClientOption option=null;
	 BDLocation location=null;
	 //my own ui 
	 private  TextView location_info=null;
	 private TextView weather_info=null;
	private EditText et;
	private Button btn_start;
	private Button btn_stop;
	private Button btn_query;
	// for weather query
	private static String base_query="http://api.map.baidu.com/telematics/v3/weather?location=";
	private String str_location="吕梁";
	private static String second_nd_base_query="&output=json&ak=";
	private static String ak="weather_info key";
	private RequestQueue queue;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		setFinishOnTouchOutside(false);
		
		mHandler =new MyHandler(Looper.myLooper());
		myListener = new MyLocationListener(mHandler);
		
	    mLocationClient = new LocationClient(getApplicationContext()); 
	    mLocationClient.registerLocationListener( myListener );  
	    option = new LocationClientOption();
	    option.setLocationMode(LocationMode.Hight_Accuracy);
	    option.setCoorType("bd09ll");
	    option.setScanSpan(5000);
	    option.setIsNeedAddress(true);
	    option.setNeedDeviceDirect(true);
	    mLocationClient.setLocOption(option);
	    
	    btn_start=(Button) findViewById(R.id.btn_start);
	    btn_stop=(Button) findViewById(R.id.btn_stop);
	    btn_query=(Button) findViewById(R.id.btn_query);
	    btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLocationClient.start();
			}
		});
	    btn_stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mLocationClient.stop();
				location=null;
				location_info.setText("");
				weather_info.setText("");
			}
		});
	    btn_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String temp=et.getText().toString();
				Log.d("TAG", temp+"you can not get the hint");
				//you can not use str=="" to see if it is null
				if(!temp.equalsIgnoreCase(""))
				{
					query_weather(temp);
				}
				else
				{
					if(location!=null)
					{
						if(location.getCity()!=null)
						{
						query_weather(location.getCity());
						Log.d("TAG", "location is not null");
						}
						query_weather(str_location);
					}
					else
					{
						query_weather(str_location);
					}
				}
			}
		});
	    btn_query.requestFocus();
	    //for info_pad
	    location_info =(TextView) findViewById(R.id.info_pad);
	    //for loc_str user input
	    et=(EditText) findViewById(R.id.loc_str);
	    //for weather info
	    weather_info =(TextView) findViewById(R.id.weather_info);
	}
	private void query_weather(String loc_str)
	{
		  //for volley
		// to setup the url
	    StringBuffer sb=new StringBuffer(base_query);
	    //it is needed to encode the chinese parameter
	    if(loc_str=="")
	    {
		    try {
				sb.append(URLEncoder.encode(str_location,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }else
	    {
	    	try {
				sb.append(URLEncoder.encode(loc_str,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    sb.append(second_nd_base_query);
	    sb.append(ak);
	    
	    //for json request
	    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(sb.toString(), null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				// TODO Auto-generated method stub
				Log.d("TAG1", arg0.toString());
				weather_info.setText(parseWeatherJson(arg0));
			}
		
	    },
	            new Response.ErrorListener() {  
	                @Override  
	                public void onErrorResponse(VolleyError error) {  
	                    Log.e("TAG", error.getMessage(), error);  
	                }  
	            }); 
	    Log.d("TAG",sb.toString());
	    queue=Volley.newRequestQueue(this);
	    queue.add(jsonObjectRequest);
	}
	
	//parse the location packet
	private String parseBDLocation(BDLocation location)
	{
		StringBuffer sb = new StringBuffer(256);
		sb.append("\ntime : ");
		sb.append(location.getTime());
		sb.append("\ncode : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		sb.append("\ncity : ");
		sb.append(location.getCity());
		sb.append("\ncity code: ");
		sb.append(location.getCityCode());
		
		sb.append("\nspeed : ");
		sb.append(location.getSpeed());
		sb.append("\nsatellite : ");
		sb.append(location.getSatelliteNumber());
		sb.append("\naddr : ");
		sb.append(location.getAddrStr());
		sb.append("\ndir : ");
		sb.append(location.getDirection());
		return sb.toString();
	}
	//parse the weather data
	private String parseWeatherJson(JSONObject json)
	{
		StringBuffer sb=new StringBuffer(256);
		try {
			sb.append("\ndate : ");
			sb.append(json.getString("date"));
			JSONArray temp=json.getJSONArray("results");
			for(int i=0;i<temp.length();i++)
			{
				JSONObject t=temp.getJSONObject(i);
				sb.append("\ncity : ");
				sb.append(t.getString("currentCity"));
				sb.append("\npm2.5 : ");
				sb.append(t.getString("pm25"));
				JSONArray ta=t.getJSONArray("index");
				//for fast debug, so i just write code simple
				JSONArray tb=t.getJSONArray("weather_data");
				sb.append("\nweather : ");
				sb.append(tb.getJSONObject(0).getString("date"));
				sb.append("\n");
				for(int j=0;j<ta.length();j++)
				{
					sb.append("\n"+ta.getJSONObject(j).getString("tipt")+" :\n");
					sb.append(ta.getJSONObject(j).getString("des"));
					
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	//
	public   class MyHandler extends Handler{

		public MyHandler(Looper myLooper) {
			// TODO Auto-generated constructor stub
		}
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0)
			{
				location=(BDLocation)msg.obj;
				location_info.setText(parseBDLocation(location));
			}
		}
	}
}
