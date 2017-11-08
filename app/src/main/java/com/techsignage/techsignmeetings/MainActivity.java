package com.techsignage.techsignmeetings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.google.gson.Gson;
import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Adapters.RoomsAdapter;
import com.techsignage.techsignmeetings.Applications.TechApp;
import com.techsignage.techsignmeetings.Dialogs.NotAuthorizedDialog;
import com.techsignage.techsignmeetings.Dialogs.SettingsDialog;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.Injection.NetComponent;
import com.techsignage.techsignmeetings.Models.MeetingModel;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomMeetingsResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface;
import com.techsignage.techsignmeetings.Network.ContentTypes;
import com.techsignage.techsignmeetings.Network.HttpRequestWrapper;
import com.techsignage.techsignmeetings.Network.IConnector;
import com.techsignage.techsignmeetings.Network.VolleyCallbackString;
import com.techsignage.techsignmeetings.Network.VolleyRequest;
import com.techsignage.techsignmeetings.Tasks.LiscenceTask;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends CoreActivity {

    private Subscription subscription;

    @InjectView(R.id.container1_lin)
    RelativeLayout container1_lin;

    @InjectView(R.id.container2_lin)
    RelativeLayout container2_lin;

    @InjectView(R.id.container3_lin)
    RelativeLayout container3_lin;

    @InjectView(R.id.tv_UnitName)
    TextView tv_UnitName;

    @InjectView(R.id.tv_NowDate)
    TextView tv_NowDate;

    @InjectView(R.id.startmeeting_btn)
    Button startmeeting_btn;

    @InjectView(R.id.meetingslist_btn)
    Button meetingslist_btn;

    @InjectView(R.id.tv_MeetingName)
    TextView tv_MeetingName;

    @InjectView(R.id.tv_MeetingDate)
    TextView tv_MeetingDate;

    @InjectView(R.id.tv_nextMeeting)
    TextView tv_nextMeeting;

    @InjectView(R.id.tv_nextMeetingDate)
    TextView tv_nextMeetingDate;

    @InjectView(R.id.book_btn)
    Button book_btn;

    @InjectView(R.id.progress_rel)
    RelativeLayout progress_rel;

    //ProgressDialog dialog;
    //SweetAlertDialog sweetAlertDialog;
    MeetingModel firstMeeting;
    MeetingModel secondMeeting;
    IConnector connector;
    Timer t;
    //SettingsDialog settingsDialog;

    private Handler handler = new Handler();
    private int value;
    private int gpio_num;
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

    //@Inject
    //OkHttpClient mOkHttpClient;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    retrofitInterface retrofitInterface;
    @Inject
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "onCreateAct", Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);

        if (checkConfigurationFile()) return;

        //new LiscenceTask(this).execute("AGKA0S0H00AXNGZ78XM35J1I4M");
        new LiscenceTask(this).execute(Utilities.getSharedValue("licensekey", getApplicationContext()));

        if (Utilities.getSharedValue("licensed", this).equals(""))
        {
            Intent intent = new Intent(MainActivity.this, LicenseNewActivity.class);
            startActivity(intent);
            finish();
        }

        smdtManager = SmdtManager.create(this);

        if(smdtManager != null)
        {
            smdtManager.smdtSetExtrnalGpioValue (1,false);
            smdtManager.smdtSetExtrnalGpioValue (2,false);
            smdtManager.smdtSetExtrnalGpioValue (1,true);
        }

        meetingslist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MeetingsListActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        //dialog = Utilities.showDialog(MainActivity.this);
        getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
        try
        {
            if (!(Utilities.getSharedValue("licensed", this).equals("")))
            {
                progress_rel.setVisibility(View.VISIBLE);
                //sweetAlertDialog = Utilities.showProgressPrettyDialog(this, getResources().getString(R.string.processing));
                //sweetAlertDialog.show();
                callWithToken();
            }
        }
        catch (Exception ex)
        {

        }

        startmeeting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
                if(smdtManager != null)
                {
                    smdtManager.smdtSetExtrnalGpioValue (1,false);
                    smdtManager.smdtSetExtrnalGpioValue (2,false);
                    smdtManager.smdtSetExtrnalGpioValue (3,false);
                }

                progress_rel.setVisibility(View.VISIBLE);
                //sweetAlertDialog = Utilities.showProgressPrettyDialog(MainActivity.this, getResources().getString(R.string.processing));
                //sweetAlertDialog.show();
                MeetingModel meetingModel = new MeetingModel();
                meetingModel.MEETING_ID = firstMeeting.MEETING_ID;
                meetingModel.UNIT_ID = Globals.unitId;

                if (startmeeting_btn.getText().toString().equals(getResources().getString(R.string.startmeeting)))
                {
                    meetingModel.IsStarting = 1;
                }
                else
                {
                    meetingModel.IsStarting = 0;
                }

                Observable<RoomMeetingsResponse> callforstart = retrofitInterface.startmeeting(meetingModel);
                subscription = callforstart.observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Subscriber<RoomMeetingsResponse>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onNext(final RoomMeetingsResponse serviceResponse) {

                                    OngoingReactAsync(serviceResponse);

                                    getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);

                                    //sweetAlertDialog.hide();
                                    progress_rel.setVisibility(View.GONE);
                                }
                            });
            }
        });

        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        connector = new IConnector() {
            @Override
            public void getConnectionStatus(Boolean currentStatus) {
                if (currentStatus)
                {
                    try
                    {
                        if (!(Utilities.getSharedValue("licensed", MainActivity.this).equals("")))
                            callWithToken();
                    }
                    catch (Exception ex)
                    {

                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please connect to the Internet", Toast.LENGTH_LONG).show();
                }
            }
        };

        super.networkStateReceiver.setConnector(connector);
    }

    private boolean checkConfigurationFile() {
        //File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = new File(dir, Globals.filename + ".txt");
        if (!file.exists())
        {
            Log.v("file", "non existing");
            NotAuthorizedDialog dialog = new NotAuthorizedDialog();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "NotAuth_Dialog");
            return true;
        }
        else
        {
            try
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    //text.append('\n');
                }
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                Globals.unitId = jsonObject.get("UNIT_ID").toString();
                Globals.coreUrl = jsonObject.get("IP").toString();
                Globals.tokenUrl = String.format("%s/token", jsonObject.get("IP").toString());
            }
            catch (Exception ex)
            {

            }
        }
        return false;
    }

    private void callWithToken() {
        if(t != null)
            t.cancel();

        if (Utilities.getSharedValue("token", MainActivity.this).equals(""))
        {
            VolleyRequest request = new VolleyRequest();
            request.getString(new VolleyCallbackString() {
                                  @Override
                                  public void onSuccess(String result) {
                                      try {
                                          if (result != null) {
                                              JSONObject object = new JSONObject(result);
                                              final String token = object.getString("access_token");
                                              Utilities.setSharedValue("token", token, getApplicationContext());
                                              Utilities.setSharedValue("username", "Admin", MainActivity.this);
                                              loadMeetings();
                                          }
                                      } catch (Exception ex) {
                                          //sweetAlertDialog.hide();
                                          progress_rel.setVisibility(View.GONE);

                                      }
                                  }


                                  @Override
                                  public void onError(String result) {
                                      //sweetAlertDialog.hide();
                                      progress_rel.setVisibility(View.GONE);

                                  }
                              }, MainActivity.this, getApplicationContext(), Globals.tokenUrl, "",
                    String.format("grant_type=password&username=%s&password=%s", "Admin", "P@ssw0rd"), ContentTypes.FormEncoded.toString());
        }
        else
        {
            loadMeetings();
        }

    }

    private void loadMeetings() {
        ((TechApp)getApplication()).setNetComponent();
        ((TechApp) getApplication()).getNetComponent().inject(MainActivity.this);
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
                            Toast.makeText(MainActivity.this,
                                    MainActivity.this.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                        } else if (error instanceof ServerError) {
                            //TODO
                        } else if (error instanceof NetworkError) {
                            //TODO
                            Toast.makeText(MainActivity.this,
                                    MainActivity.this.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                        }

                    }

                    @Override
                    public void onNext(final RoomsResponse roomsResponse) {
                        //String ss = "";
                        //settingsDialog.setSpinner(roomsResponse.Rooms);
                    }
                });


        tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));

        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              if (!(Utilities.getSharedValue("licensed", MainActivity.this).equals("")))
                                                  startChecking();
                                          }
                                      });

                                  }

                              },
                0,
                30000);
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

    @Override
    protected void onResume() {
        super.onResume();
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

    //react android response is being processed here
    private void OngoingReactAsync(RoomMeetingsResponse serviceResponse) {
        Globals.loggedUnit = serviceResponse.RoomMeetings.Room;

        tv_MeetingDate.setText("");
        tv_MeetingName.setText(getResources().getString(R.string.firstmeeting_title));
        tv_nextMeetingDate.setText("");
        tv_nextMeeting.setText("");
        startmeeting_btn.setEnabled(false);
        //inMeeting = false;
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
                    startmeeting_btn.setEnabled(true);
                    String MeetingDate = String.format("%s - %s", Globals.format1.format(startdate), Globals.format1.format(enddate));
                    tv_MeetingDate.setText(MeetingDate);
                    tv_MeetingName.setText(firstMeeting.MEETING_TITLE);
                    //inMeeting = false;

                    if (firstMeeting.ACTUAL_START_DATETIME != null)
                    {
                        value = 1;
                        gpio_num = 42;
                        handler.post(task);

                        value = 1;
                        gpio_num = 43;
                        handler.post(task);

                        value = 0;
                        gpio_num = 42;
                        handler.post(task);

                        container2_lin.setBackgroundColor(Color.RED);
                        startmeeting_btn.setText(R.string.endmeeting);
                        setChinaColor(3);

                    }
                    else {
                        value = 1;
                        gpio_num = 42;
                        handler.post(task);

                        value = 1;
                        gpio_num = 43;
                        handler.post(task);

                        startmeeting_btn.setText(R.string.startmeeting);
                        container2_lin.setBackgroundColor(Color.WHITE);

                        if (chkfirst_diff < -4)
                            tv_MeetingDate.setText(String.format(("Delayed\n%s"), MeetingDate));
                        setChinaColor(2);
                    }
                }
                else {
                    value = 1;
                    gpio_num = 43;
                    handler.post(task);

                    value = 1;
                    gpio_num = 42;
                    handler.post(task);

                    value = 0;
                    gpio_num = 43;
                    handler.post(task);

                    //inMeeting = false;
                    container2_lin.setBackgroundColor(getResources().getColor(R.color.green));
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
            value = 1;
            gpio_num = 42;
            handler.post(task);

            value = 1;
            gpio_num = 43;
            handler.post(task);

            value = 0;
            gpio_num = 43;
            handler.post(task);

            container2_lin.setBackgroundColor(getResources().getColor(R.color.green));
            setChinaColor(2);

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

    void setLandscape()
    {
        container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .18f));
        container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .64f));
        container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .18f));
    }

    void setPortrait()
    {
        container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
        container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .70f));
        container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            setLandscape();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            setPortrait();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.networkStateReceiver.connector = null;
        if(t != null)
            t.cancel();
    }
}
