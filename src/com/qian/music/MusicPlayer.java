package com.qian.music;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.qian.utils.FormatTime;

public class MusicPlayer extends Service {


	private MediaPlayer mediaPlayer;
	private SeekBar seekBar1;
	private Timer timer;
	private TimerTask task;
	private Button bt_start1,bt_stop1;
	private TextView tv1,tv_total1;
	private long currentPosition;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return new MyBinder();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			long miliseconds =  (Long) msg.obj;
			String time = FormatTime.formatTime(miliseconds);

			tv1.setText(time + "");//��Ҫ�������߳�
		//	System.out.println("�յ���Ϣ");  debug
		};
	};
	
	
	
	public void play( Button bt_start, Button bt_stop,
			EditText et_path, TextView tv, final TextView tv_total,final SeekBar seekBar) {
		
		if(mediaPlayer != null && mediaPlayer.isPlaying()) { //��ͣ
			bt_start.setText("����");
			mediaPlayer.pause();
		}else if(mediaPlayer != null && (!mediaPlayer.isPlaying())){//��������
			bt_start.setText("��ͣ");
			mediaPlayer.start();
		}else{//��һ�β���
			
				bt_start1 = bt_start;
				bt_stop1 = bt_stop;
				tv1= tv;
				seekBar1 = seekBar;
				tv_total1 = tv_total;
				seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int postion = seekBar.getProgress();
						mediaPlayer.seekTo(postion);
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						
					}
				});
				
				
				
				String path = et_path.getText().toString().trim();
				File file = new File(path);
				if(file.exists()) {
					try {
						mediaPlayer = new MediaPlayer();//����һ�����ֲ�����
						mediaPlayer.setDataSource(path);//���ò���Դ�ļ�   
						//�������ֲ�������ô�ж�   
						//if(path.startsWith("http://"))
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//����������������
						mediaPlayer.prepare();//׼������    ���ŵ��߼���c�������µ��߳�����ִ�С�
						mediaPlayer.start();//��ʼ����
						mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								mediaPlayer.seekTo(0);
								mediaPlayer.start();//��ʼ����
							}
						});
						int max = mediaPlayer.getDuration();
						
						seekBar.setMax(max);
						tv_total1.setText(FormatTime.formatTime(max));
						timer = new Timer();
						task = new TimerTask() {
							

							@Override
							public void run() {
								
								if(mediaPlayer != null) {
									int max = mediaPlayer.getDuration();
									
									//seekBar.setMax(max);
									//tv_total.setText(FormatTime.formatTime(max));
									
									
									currentPosition = mediaPlayer.getCurrentPosition();
									seekBar1.setProgress((int) currentPosition);
									
									
									Message msg = Message.obtain();//��new Message���� ��ʡ�ڴ�
									msg.obj = currentPosition;
									handler.sendMessage(msg);
								//	System.out.println(currentPosition);
								//	System.out.println("�����ˡ���������������");
								}
						
							}
						};
						
						timer.schedule(task, 0, 500);
						
						bt_stop.setEnabled(true);
						bt_start.setText("��ͣ");
						
					} catch (Exception e) {
						
						e.printStackTrace();
						Toast.makeText(getApplicationContext(), "����ʧ��", 0).show();
					} 
					
				} else {
					Toast.makeText(getApplicationContext(), "�ļ������ڣ������ļ�", 0).show();
				}
			
			}
		
				
	}
	
	
	@SuppressLint("ShowToast")
	public void stop(Button bt_start,Button bt_stop,SeekBar seekBar) {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			bt_start.setEnabled(true);			
			bt_stop.setEnabled(false);
			seekBar.setProgress(0);
			bt_start.setText("����");
		}
	}

	public void updateSeekBar(SeekBar seekBar, TextView tv, TextView tv_total,
			Button bt_start, Button bt_stop) {
		//seekBar1.getProgress()
		
		seekBar1 = seekBar;
		tv_total1 = tv_total;
		tv1 = tv;
		if(mediaPlayer!=null) {
			
			
			seekBar.setMax(mediaPlayer.getDuration());
			tv_total.setText(FormatTime.formatTime(mediaPlayer.getDuration()));
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					int postion = seekBar.getProgress();
					mediaPlayer.seekTo(postion);
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					
				}
			});
			
			
			if(mediaPlayer.isPlaying()) { //��ͣ
				bt_start.setText("��ͣ");
				//mediaPlayer.pause();
			}else if((!mediaPlayer.isPlaying())){//��������
				bt_start.setText("����");
				//mediaPlayer.start();
			}
			
			
			
			
			
			
			
			System.out.println("mediaPlayer!=null");
		}else{
			System.out.println("mediaPlayer==null");
		}
		
		
	}

	public class MyBinder extends Binder{//  Binder extends IBinder
		public void startPlay( Button bt_start, Button bt_stop,
				EditText et_path, TextView tv, TextView tv_total,
				SeekBar seekBar) {			
			play(bt_start,bt_stop,et_path,tv,tv_total,seekBar);
	
		}
		public void stopPlay(Button bt_start,Button bt_stop,SeekBar seekBar) {
			stop(bt_start,bt_stop,seekBar);
		}
		public void updateSeekBarPlay(SeekBar seekBar,TextView tv,TextView tv_total,Button bt_start, Button bt_stop) {
			updateSeekBar(seekBar, tv, tv_total, bt_start, bt_stop);
		}
		
		
		
	}

}
