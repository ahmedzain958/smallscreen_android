package com.techsignage.techsignmeetings;

import android.annotation.SuppressLint;
import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.google.gson.Gson;
import com.techsignage.techsignmeetings.Activities.CoreActivityNew;
import com.techsignage.techsignmeetings.Applications.TechApp;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface;
import com.techsignage.techsignmeetings.Models.MeetingModel;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomMeetingsResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Network.ContentTypes;
import com.techsignage.techsignmeetings.Network.VolleyCallbackString;
import com.techsignage.techsignmeetings.Network.VolleyRequest;
import com.techsignage.techsignmeetings.Tasks.LiscenceTask;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainNewActivity extends CoreActivityNew {

    @InjectView(R.id.progress_rel)
    RelativeLayout progress_rel;

    @InjectView(R.id.tv_MeetingName)
    TextView tv_MeetingName;

    @InjectView(R.id.tv_MeetingDate)
    TextView tv_MeetingDate;

    @InjectView(R.id.tv_nextMeeting)
    TextView tv_nextMeeting;

    @InjectView(R.id.tv_nextMeetingDate)
    TextView tv_nextMeetingDate;

    @InjectView(R.id.tv_NowDate)
    TextView tv_NowDate;

    @InjectView(R.id.tv_UnitName)
    TextView tv_UnitName;

    private Handler handler = new Handler();
    private int value;
    private int gpio_num;
    private Subscription subscription;
    Timer t;
    MeetingModel firstMeeting;
    MeetingModel secondMeeting;

    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface retrofitInterface;
    @Inject
    Gson gson;

    private Runnable task = new Runnable() {
        @SuppressLint("DefaultLocale")
        public void run() {
            // TODO Auto-generated method stub

            String cmd = String.format("echo %d > /sys/class/gpio/gpio%s/value\n", value, gpio_num);
            try {
                Process exeEcho = Runtime.getRuntime().exec("sh");
                exeEcho.getOutputStream().write(cmd.getBytes());
                exeEcho.getOutputStream().flush();
            } catch (IOException e) {
                showMessage("Excute exception: " + e.getMessage());
            }


        }

        private void showMessage(String string) {
            // TODO Auto-generated method stub

        }
    };
    private SmdtManager smdtManager;
    //TextView tv_nextMeetingDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        //getWindow().setBackgroundDrawableResource(R.drawable.mainpage_bggreen);

        //tv_nextMeetingDate = (TextView) findViewById(R.id.tv_nextMeetingDate);

        ButterKnife.inject(this);

        if (Utilities.checkConfigurationFile(this)) return;

        try
        {
            new LiscenceTask(this).execute(Utilities.getSharedValue("licensekey", getApplicationContext()));
        }
        catch(Exception ex)
        {

        }

        if (Utilities.getSharedValue("licensed", this).equals(""))
        {
            Intent intent = new Intent(MainNewActivity.this, LicenseNewActivity.class);
            intent.putExtra("activityName", "MainActivity");
            startActivity(intent);
            finish();
        }

        setGreenOn();

        smdtManager = SmdtManager.create(this);

        if(smdtManager != null)
        {
            smdtManager.smdtSetExtrnalGpioValue (1,false);
            smdtManager.smdtSetExtrnalGpioValue (2,false);
            smdtManager.smdtSetExtrnalGpioValue (1,true);
        }

        //dialog = Utilities.showDialog(MainActivity.this);
        getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
        try
        {
            if (!(Utilities.getSharedValue("licensed", this).equals("")))
            {
                progress_rel.setVisibility(View.VISIBLE);
                callWithToken();
            }
        }
        catch (Exception ex)
        {

        }
    }

    private void callWithToken() {
        if(t != null)
            t.cancel();

//        if (Utilities.getSharedValue("token", MainNewActivity.this).equals(""))
//        {
//            VolleyRequest request = new VolleyRequest();
//            request.getString(new VolleyCallbackString() {
//                                  @Override
//                                  public void onSuccess(String result) {
//                                      try {
//                                          if (result != null) {
//                                              JSONObject object = new JSONObject(result);
//                                              final String token = object.getString("access_token");
//                                              Utilities.setSharedValue("token", token, getApplicationContext());
//                                              Utilities.setSharedValue("username", "Admin", MainNewActivity.this);
//                                              loadMeetings();
//                                          }
//                                      } catch (Exception ex) {
//                                          progress_rel.setVisibility(View.GONE);
//
//                                      }
//                                  }
//
//
//                                  @Override
//                                  public void onError(String result) {
//                                      //sweetAlertDialog.hide();
//                                      progress_rel.setVisibility(View.GONE);
//
//                                  }
//                              }, MainNewActivity.this, getApplicationContext(), Globals.tokenUrl, "",
//                    String.format("grant_type=password&username=%s&password=%s", "Admin", "P@ssw0rd"), ContentTypes.FormEncoded.toString());
//        }
//        else
//        {
//            loadMeetings();
//        }
        VolleyRequest request = new VolleyRequest();
        request.getString(new VolleyCallbackString() {
                              @Override
                              public void onSuccess(String result) {
                                  try {
                                      if (result != null) {
                                          JSONObject object = new JSONObject(result);
                                          final String token = object.getString("access_token");
                                          Utilities.setSharedValue("token", token, getApplicationContext());
                                          Utilities.setSharedValue("username", "Admin", MainNewActivity.this);
                                          loadMeetings();
                                      }
                                  } catch (Exception ex) {
                                      progress_rel.setVisibility(View.GONE);

                                  }
                              }


                              @Override
                              public void onError(String result) {
                                  //sweetAlertDialog.hide();
                                  progress_rel.setVisibility(View.GONE);

                              }
                          }, MainNewActivity.this, getApplicationContext(), Globals.tokenUrl, "",
                String.format("grant_type=password&username=%s&password=%s", "Admin", "P@ssw0rd"), ContentTypes.FormEncoded.toString());

    }

    private void loadMeetings() {
        ((TechApp)getApplication()).setNetComponent();
        ((TechApp) getApplication()).getNetComponent().inject(MainNewActivity.this);
        Observable<RoomsResponse> allRooms = retrofitInterface.allrooms();
        subscription = allRooms.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<RoomsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(MainNewActivity.this,
                                    MainNewActivity.this.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                        } else if (error instanceof ServerError) {
                            //TODO
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(MainNewActivity.this,
                                    MainNewActivity.this.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                        }

                    }

                    @Override
                    public void onNext(final RoomsResponse roomsResponse) {
                        String ss = "";
                        //settingsDialog.setSpinner(roomsResponse.Rooms);
                    }
                });


        //tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              if (!(Utilities.getSharedValue("licensed", MainNewActivity.this).equals("")))
                                                  startChecking();
                                          }
                                      });

                                  }

                              },
                0,
                30000);
    }

    //start the async call to get the meetings
    void startChecking()
    {
        UnitModel model = new UnitModel();
        model.UNIT_ID = Globals.unitId;
        Observable<RoomMeetingsResponse> data = retrofitInterface.roomreservations(model);

        subscription = data
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RoomMeetingsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(RoomMeetingsResponse serviceResponse) {
                        //sweetAlertDialog.hide();
                        progress_rel.setVisibility(View.GONE);
                        OngoingReactAsync(serviceResponse);
                    }
                });
    }

    private void OngoingReactAsync(RoomMeetingsResponse serviceResponse) {
        Globals.loggedUnit = serviceResponse.RoomMeetings.Room;

//        if (isRed)
//            setRedOff();
//        else
//            setGreenOff();
//        setGreenOn();

        tv_MeetingDate.setText("");
        tv_MeetingName.setText(getResources().getString(R.string.firstmeeting_title));
        tv_nextMeetingDate.setText("");
        tv_nextMeeting.setText("");
        //startmeeting_btn.setEnabled(false);
        tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));


        tv_UnitName.setText(serviceResponse.RoomMeetings.Room.UNIT_NAME);
        long chkfirst_diff = 0;
        if(serviceResponse.RoomMeetings.Meetings.size() > 0)
        {
            firstMeeting = serviceResponse.RoomMeetings.Meetings.get(0).meeting;
            try
            {
                Date startdate = Globals.format.parse(firstMeeting.START_DATETIME);
                Date enddate = Globals.format.parse(firstMeeting.END_DATETIME);
                chkfirst_diff = getDifference(new Date(), startdate);
                if(chkfirst_diff <= 1)
                {
                    //startmeeting_btn.setEnabled(true);
                    String MeetingDate = String.format("%s - %s", Globals.format1.format(startdate), Globals.format1.format(enddate));
                    tv_MeetingDate.setText(MeetingDate);
                    tv_MeetingName.setText(firstMeeting.MEETING_TITLE);

                    if (firstMeeting.ACTUAL_START_DATETIME != null)
                    {
                        setRedOn();

                        //container2_lin.setBackgroundColor(Color.RED);
                        //startmeeting_btn.setText(R.string.endmeeting);
                        setChinaColor(3);
                    }
                    else
                    {
                        setRedOff();

                        //startmeeting_btn.setText(R.string.startmeeting);
                        //container2_lin.setBackgroundColor(Color.WHITE);

                        if (chkfirst_diff < -4)
                            tv_MeetingDate.setText(String.format(("Delayed\n%s"), MeetingDate));
                        setChinaColor(2);
                    }
                }
                else {

                    setRedOff();

                    //container2_lin.setBackgroundColor(getResources().getColor(R.color.green));
                    String MeetingDate2 = String.format("%s - %s", Globals.format1.format(startdate), Globals.format1.format(enddate));
                    tv_nextMeetingDate.setText(MeetingDate2);
                    tv_nextMeeting.setText(firstMeeting.MEETING_TITLE);

                    setChinaColor(2);
                }
            }
            catch(Exception ex)
            {

            }
        }
        if(serviceResponse.RoomMeetings.Meetings.size() > 1) {
            try
            {
                secondMeeting = serviceResponse.RoomMeetings.Meetings.get(1).meeting;

                Date startdate2 = Globals.format.parse(secondMeeting.START_DATETIME);
                Date enddate2 = Globals.format.parse(secondMeeting.END_DATETIME);
                String MeetingDate2 = String.format("%s - %s", Globals.format1.format(startdate2), Globals.format1.format(enddate2));

                if(chkfirst_diff <= 1) {
                    tv_nextMeeting.setText(secondMeeting.MEETING_TITLE);
                    tv_nextMeetingDate.setText(MeetingDate2);
                }
            }
            catch (Exception ex)
            {

            }
        }
        if(serviceResponse.RoomMeetings.Meetings.size() == 0)
        {
            //container2_lin.setBackgroundColor(getResources().getColor(R.color.green));
            setChinaColor(2);
            setRedOff();
        }
    }

    //set colors for china devices
    private void setChinaColor(int color) {
        //2 for green, 1 for blue, 3 for red
        if(smdtManager != null)
        {   smdtManager.smdtSetExtrnalGpioValue (1,false);
            smdtManager.smdtSetExtrnalGpioValue (2,false);
            smdtManager.smdtSetExtrnalGpioValue (3,false);

            smdtManager.smdtSetExtrnalGpioValue (color,true);
        }
    }


    //get difference between two dates
    public long getDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        return elapsedMinutes;
    }

    private void setRedOn()
    {
        value = 0;
        gpio_num = 42;
        handler.post(task);
        //isRed = true;
    }

    private void setRedOff() {
        value = 1;
        gpio_num = 42;
        handler.post(task);
    }

    private void setGreenOn() {
        value = 0;
        gpio_num = 43;
        handler.post(task);
        //isRed = false;
    }

    private void setGreenOff() {
        value = 1;
        gpio_num = 43;
        handler.post(task);
    }
}
