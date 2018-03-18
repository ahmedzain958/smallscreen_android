package com.techsignage.techsignmeetings;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Adapters.MeetingsAllAdapter;
import com.techsignage.techsignmeetings.Dialogs.NotAuthorizedDialog;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomMeetingsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Models.UserMeetingModel;
import com.techsignage.techsignmeetings.Network.ContentTypes;
import com.techsignage.techsignmeetings.Network.IConnector;
import com.techsignage.techsignmeetings.Network.VolleyCallbackString;
import com.techsignage.techsignmeetings.Network.VolleyRequest;
import com.techsignage.techsignmeetings.Tasks.LiscenceTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AllListActivity extends CoreActivity {


    @BindView(R.id.tv_NowDate)
    TextView tv_NowDate;

    @BindView(R.id.next_btn)
    Button next_btn;

    @BindView(R.id.prev_btn)
    Button prev_btn;

    @BindView(R.id.back_btn)
    Button back_btn;

    @BindView(R.id.meetings_list)
    RecyclerView activerequestslist;

    @BindView(R.id.container1_lin)
    RelativeLayout container1_lin;

    @BindView(R.id.container2_lin)
    RelativeLayout container2_lin;

    @BindView(R.id.container3_lin)
    RelativeLayout container3_lin;

    @BindView(R.id.book_btn)
    Button book_btn;

    @BindView(R.id.progress_rel)
    RelativeLayout progress_rel;

    Timer t;
    IConnector connector;
    MeetingsAllAdapter adapter;
    List<UserMeetingModel> Meetings;
    retrofitInterface retrofitInterface;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_list);
        if (checkConfigurationFile()) return;
        new LiscenceTask(this).execute(Utilities.getSharedValue("licensekey", getApplicationContext()));

        if (Utilities.getSharedValue("licensed", this).equals(""))
        {
            Intent intent = new Intent(AllListActivity.this, LicenseNewActivity.class);
            intent.putExtra("activityName", "AllListActivity");
            startActivity(intent);
            finish();
        }

        ButterKnife.bind(this);

        VolleyRequest request = new VolleyRequest();
        request.getString(new VolleyCallbackString() {
                              @Override
                              public void onSuccess(String result) {
                                  try
                                  {
                                      JSONObject object = new JSONObject(result);
                                      final String token = object.getString("access_token");
                                      Utilities.setSharedValue("token", token, getApplicationContext());
                                      Utilities.setSharedValue("username", "Admin", AllListActivity.this);

                                      progress_rel.setVisibility(View.GONE);
                                      retrofitInterface = Utilities.liveAPI(token);
                                      tv_NowDate = (TextView)findViewById(R.id.tv_NowDate);
                                      //tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy").format(new Date()));
                                      tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));

                                      t = new Timer();
                                      t.scheduleAtFixedRate(new TimerTask() {

                                                                @Override
                                                                public void run() {
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            getMeetingsList();
                                                                        }
                                                                    });

                                                                }

                                                            },
                                              0,
                                              30000);
                                  }
                                  catch (Exception ex)
                                  {

                                  }
                              }

                              @Override
                              public void onError(String result) {

                              }
                          }, AllListActivity.this, getApplicationContext(), Globals.tokenUrl, "",
                String.format("grant_type=password&username=%s&password=%s", "Admin", "P@ssw0rd"), ContentTypes.FormEncoded.toString());

        //Globals.pageSize = 5;

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Globals.skipCount < Meetings.size() && (Meetings.size() - (Globals.skipCount+pageSize)) > 0)
                {
                    Globals.skipCount += pageSize;
                }

                setButtons();

                List<UserMeetingModel> meetingModels = new ArrayList<UserMeetingModel>();
                for (int i = 0; i < Meetings.size(); i++)
                {
                    if ((i+1) > Globals.skipCount)
                    {
                        if(meetingModels.size() < pageSize)
                        {
                            meetingModels.add(Meetings.get(i));
                        }
                    }
                }

                adapter = new MeetingsAllAdapter(AllListActivity.this, R.layout.meeting_itemall);
                adapter.setLst(meetingModels);
                activerequestslist.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });

        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Globals.skipCount > 0)
                    Globals.skipCount -= pageSize;

                setButtons();

                List<UserMeetingModel> meetingModels = new ArrayList<UserMeetingModel>();
                for (int i = 0; i < Meetings.size(); i++)
                {
                    if ((i+1) > Globals.skipCount)
                    {
                        if(meetingModels.size() < pageSize)
                        {
                            meetingModels.add(Meetings.get(i));
                        }
                    }
                }

                adapter = new MeetingsAllAdapter(AllListActivity.this, R.layout.meeting_itemall);
                adapter.setLst(meetingModels);
                activerequestslist.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }
        });

        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllListActivity.this, LoginActivity.class);
                //intent.putExtra("activityName", getClass().getSimpleName());
                intent.putExtra("activityName", "AllListActivity");
                AllListActivity.this.startActivity(intent);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        connector = new IConnector() {
            @Override
            public void getConnectionStatus(Boolean currentStatus) {
                if (currentStatus)
                {
                    try
                    {
                        getMeetingsList();
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    void setButtons()
    {
        if(Globals.skipCount < Meetings.size() && (Meetings.size() - (Globals.skipCount+pageSize)) > 0)
        {
            next_btn.setEnabled(true);
        }
        else
            next_btn.setEnabled(false);

        if(Globals.skipCount > 0)
        {
            prev_btn.setEnabled(true);
        }
        else
            prev_btn.setEnabled(false);
    }

    double pageSize;
    void getMeetingsList()
    {
        Observable<RoomMeetingsResponse> data = retrofitInterface.allroomreservations();

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
                        //tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy").format(new Date()));
                        tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));
                        next_btn.setEnabled(true);
                        prev_btn.setEnabled(true);
                        //Meetings = serviceResponse.RoomMeetingsResponse.Meetings;
                        Meetings = serviceResponse.RoomMeetings.MeetingsAll;
                        assert activerequestslist != null;
                        adapter = new MeetingsAllAdapter(AllListActivity.this, R.layout.meeting_itemall) ;

                        //tv_UnitName.setText(serviceResponse.RoomMeetings.Room.UNIT_NAME);

                        LinearLayoutManager llm = new LinearLayoutManager(AllListActivity.this);
                        activerequestslist.setLayoutManager(llm);






                        //First Step to get the actual height of the recycler view
                        List<UserMeetingModel> meetingModels = serviceResponse.RoomMeetings.MeetingsAll;
                        adapter.setLst(meetingModels);
                        activerequestslist.setAdapter(adapter);
                        activerequestslist.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        int height = activerequestslist.getMeasuredHeight();
                        pageSize = Math.ceil((double) height / (double)(adapter.getItemHeight()));


                        meetingModels = new ArrayList<>();
                        for (int i = 0; i < Meetings.size(); i++)
                        {
                            if ((i+1) > Globals.skipCount)
                            {
                                if(meetingModels.size() < pageSize)
                                {
                                    meetingModels.add(Meetings.get(i));
                                }
                            }
                        }
                        adapter.setLst(meetingModels);
                        //adapter.notifyAll();
                        //adapter.notifyDataSetChanged();
                        activerequestslist.invalidate();
                        setButtons();
                    }
                });
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

    void setLandscape()
    {
        container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .19f));
        container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .64f));
        container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .17f));
    }

    void setPortrait()
    {
        container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
        container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .70f));
        container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.networkStateReceiver.connector = null;
        if(t != null)
            t.cancel();
    }
}
