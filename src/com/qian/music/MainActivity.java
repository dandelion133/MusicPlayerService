package com.qian.music;



import java.util.Timer;
import java.util.TimerTask;

import com.qian.music.MusicPlayer.MyBinder;
import com.qian.utils.FormatTime;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText et_path;
	private MediaPlayer mediaPlayer;
	private Button bt_pause,bt_start,bt_stop;
	private SeekBar seekBar1;
	private Timer timer;
	private TimerTask task;
	
	
	private MyConn conn;
	private MyBinder myBinder;
	
	private TextView tv,tv_total;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		et_path = (EditText) findViewById(R.id.et);	
		bt_start = (Button) findViewById(R.id.bt_start);
		bt_stop = (Button) findViewById(R.id.bt_stop);		
		tv = (TextView) findViewById(R.id.tv);
		tv_total = (TextView) findViewById(R.id.tv_total);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		/*服务长期后台运行，又想调用服务的方法：
		1.start方式开启服务（保证服务长期后台运行）
		2.bind方式绑定服务（保证调用服务的方法）
		3.unbind解除绑定服务
		4.stopService停止服务。*/
		Intent intent = new Intent(this, MusicPlayer.class);
		startService(intent);
		conn = new MyConn();
		boolean isSuccess = bindService(intent, conn, BIND_AUTO_CREATE);
		if(isSuccess) {
			Toast.makeText(getApplicationContext(), "服务被成功绑定了", 0).show();
		}else {
			Toast.makeText(getApplicationContext(), "服务没有成功绑定", 0).show();
		}
		
	}
	
	public void start(View v) {
		
		
		myBinder.startPlay(bt_start, bt_stop, et_path,tv,tv_total,seekBar1);
		
		
	}
private class MyConn implements ServiceConnection {

		
		/*
		 * @Override
			public IBinder onBind(Intent intent) {
				return new MiddlePerson();
			}
			//此处得到IBinder service 建立链接后上面方法返回的MiddlePerson对象
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			Toast.makeText(getApplicationContext(), "得到中间人", 0).show();
			myBinder = (MyBinder)service;//得到中间人
			myBinder.updateSeekBarPlay(seekBar1, tv, tv_total, bt_start, bt_stop);
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	public void stop(View v) {
		myBinder.stopPlay(bt_start, bt_stop, seekBar1);
	}
	public void replay(View v) {
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
		
		
	}
	
	
	
	

}
