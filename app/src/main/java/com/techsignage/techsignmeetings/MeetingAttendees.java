package com.techsignage.techsignmeetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Adapters.AttendeesAdapter;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomMeetingsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Models.UserMeetingModel;
import com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface;
import com.techsignage.techsignmeetings.Network.ContentTypes;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Network.VolleyCallbackString;
import com.techsignage.techsignmeetings.Network.VolleyRequest;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MeetingAttendees extends CoreActivity {
    //ProgressDialog dialog;
    retrofitInterface retrofitInterface;
    private Subscription subscription;
    TextView tv_UnitName;
    TextView tv_NowDate;
    TextView tv_MeetingTitle;
    TextView tv_MeetingDate;
    TextView tv_MeetingOrganizer;
    RelativeLayout container1_lin;
    RelativeLayout container2_lin;
    RelativeLayout container3_lin;
    Button next_btn;
    Button prev_btn;
    Button back_btn;
    UserMeetingModel meetingModel = null;
    AttendeesAdapter adapter;
    RecyclerView activerequestslist;

    RelativeLayout progress_rel;
    Timer t;
    Timer tclose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_attendees);

        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null)
            {
                final String MEETING_ID = getIntent().getExtras().getString("MEETING_ID");
                final String UNIT_NAME = getIntent().getExtras().getString("UNIT_NAME");
                final String UNIT_ID = getIntent().getExtras().getString("UNIT_ID");
                //final String activityName = getIntent().getExtras().getString("activityName");
                //final Boolean showbtns_check = activityName.equals("MeetingsListActivity");
                final Boolean showbtns_check = true;
                //final Boolean Status = getIntent().getExtras().getBoolean("Status");
                //final Boolean StatusOut = getIntent().getExtras().getBoolean("StatusOut");

                container1_lin = (RelativeLayout)findViewById(R.id.container1_lin);
                container2_lin = (RelativeLayout)findViewById(R.id.container2_lin);
                container3_lin = (RelativeLayout)findViewById(R.id.container3_lin);
                tv_UnitName = (TextView)findViewById(R.id.tv_UnitName);
                tv_NowDate = (TextView)findViewById(R.id.tv_NowDate);
                tv_MeetingTitle = (TextView)findViewById(R.id.tv_MeetingTitle);
                tv_MeetingDate = (TextView)findViewById(R.id.tv_MeetingDate);
                tv_MeetingOrganizer = (TextView)findViewById(R.id.tv_MeetingOrganizer);
                next_btn = (Button) findViewById(R.id.next_btn);
                prev_btn = (Button)findViewById(R.id.prev_btn);
                back_btn = (Button)findViewById(R.id.back_btn);
                progress_rel = (RelativeLayout) findViewById(R.id.progress_rel);

                tv_UnitName.setText(UNIT_NAME);
                t = new Timer();
                t.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {
                                              runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      //if(!inMeeting)
                                                      //    startChecking();
                                                      tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));
                                                  }
                                              });

                                          }

                                      },
                        0,
                        30000);

//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        if(t != null)
//                            t.cancel();
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //finish();
//                                Intent intent = new Intent(MeetingAttendees.this, MainNewActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//
//                    }
//                }, 65000);

                setTimer();

                Globals.pageSize = 4;

//                next_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        if(Globals.skipCount < meetingModel.meeting.ATTENDEES.size() && (meetingModel.meeting.ATTENDEES.size() - (Globals.skipCount+Globals.pageSize)) > 0)
////                        {
////                            Globals.skipCount += Globals.pageSize;
////                        }
////
////                        setButtons();
////
////                        List<ATTENDEEModel> meetingModels = new ArrayList<ATTENDEEModel>();
////                        for (int i = 0; i < meetingModel.meeting.ATTENDEES.size(); i++)
////                        {
////                            if ((i+1) > Globals.skipCount)
////                            {
////                                if(meetingModels.size() < Globals.pageSize)
////                                {
////                                    meetingModels.add(meetingModel.meeting.ATTENDEES.get(i));
////                                }
////                            }
////                        }
//
//                        adapter = new AttendeesAdapter(MeetingAttendees.this);
//                        adapter.setLst(meetingModels);
//                        activerequestslist.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//
//
//                    }
//                });

//                prev_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        if(Globals.skipCount > 0)
////                            Globals.skipCount -= Globals.pageSize;
////
////                        setButtons();
////
////                        List<ATTENDEEModel> meetingModels = new ArrayList<ATTENDEEModel>();
////                        for (int i = 0; i < meetingModel.meeting.ATTENDEES.size(); i++)
////                        {
////                            if ((i+1) > Globals.skipCount)
////                            {
////                                if(meetingModels.size() < Globals.pageSize)
////                                {
////                                    meetingModels.add(meetingModel.meeting.ATTENDEES.get(i));
////                                }
////                            }
////                        }
//
//                        adapter = new AttendeesAdapter(MeetingAttendees.this);
//                        adapter.setLst(meetingModels);
//                        activerequestslist.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//
//
//                    }
//                });

                back_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                //tv_NowDate.setText(Globals.timeStamp);
                tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));
                //dialog = Utilities.showDialog(MeetingAttendees.this);
                progress_rel.setVisibility(View.VISIBLE);

                VolleyRequest request = new VolleyRequest();
                request.getString(new VolleyCallbackString() {
                                      @Override
                                      public void onSuccess(String result) {
                                          try {
                                              if (result != null) {
                                                  JSONObject object = new JSONObject(result);
                                                  final String token = object.getString("access_token");
                                                  Utilities.setSharedValue("token", token, getApplicationContext());
                                                  Utilities.setSharedValue("username", "Admin", MeetingAttendees.this);
                                                  retrofitInterface = Utilities.liveAPI(token);

                                                  final UnitModel model = new UnitModel();
                                                  model.UNIT_ID = UNIT_ID;
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
                                                                  progress_rel.setVisibility(View.GONE);

                                                                  for (UserMeetingModel userMeetingModel : serviceResponse.RoomMeetings.MeetingsAll)
                                                                  {
                                                                      if (userMeetingModel.meeting.MEETING_ID.equals(MEETING_ID))
                                                                      {
                                                                          meetingModel = userMeetingModel;
                                                                          break;
                                                                      }
                                                                  }

                                                                  adapter = new AttendeesAdapter(MeetingAttendees.this, meetingModel.meeting.CanCheckin, meetingModel.meeting.CanCheckOut, showbtns_check) ;
//                                                                  List<ATTENDEEModel> meetingModels = new ArrayList<ATTENDEEModel>();
//                                                                  for (int i = 0; i < meetingModel.meeting.ATTENDEES.size(); i++)
//                                                                  {
//                                                                      if ((i+1) > Globals.skipCount)
//                                                                      {
//                                                                          if(meetingModels.size() < Globals.pageSize)
//                                                                          {
//                                                                              meetingModels.add(meetingModel.meeting.ATTENDEES.get(i));
//                                                                          }
//                                                                      }
//                                                                  }
//
//                                                                  setButtons();

                                                                  tv_UnitName.setText(serviceResponse.RoomMeetings.Room.UNIT_NAME);
                                                                  tv_MeetingTitle.setText(meetingModel.meeting.MEETING_TITLE);
                                                                  tv_MeetingOrganizer.setText(String.format("%s %s", meetingModel.user.FIRST_NAME,
                                                                          meetingModel.user.LAST_NAME));
                                                                  //tv_MeetingDate.setText(serviceResponse.RoomMeetingsResponse.Meetings.get(0).meeting.START_DATETIME);

                                                                  try
                                                                  {
                                                                      Date startdate = Globals.format.parse(meetingModel.meeting.START_DATETIME);
                                                                      Date enddate = Globals.format.parse(meetingModel.meeting.END_DATETIME);
                                                                      String MeetingDate = String.format("%s | %s - %s", Globals.format3.format(startdate), Globals.format1.format(startdate), Globals.format1.format(enddate));
                                                                      tv_MeetingDate.setText(MeetingDate);
                                                                  }
                                                                  catch(Exception ex)
                                                                  {

                                                                  }

                                                                  activerequestslist = (RecyclerView)findViewById(R.id.attendees_list);
                                                                  assert activerequestslist != null;
                                                                  adapter = new AttendeesAdapter(MeetingAttendees.this, meetingModel.meeting.CanCheckin, meetingModel.meeting.CanCheckOut, showbtns_check);
                                                                  //adapter.setLst(meetingModels);
                                                                  adapter.setLst(meetingModel.meeting.ATTENDEES);
                                                                  activerequestslist.setAdapter(adapter);

                                                                  LinearLayoutManager llm = new LinearLayoutManager(MeetingAttendees.this);
                                                                  //llm.setOrientation(LinearLayoutManager.HORIZONTAL);
                                                                  activerequestslist.setLayoutManager(llm);
                                                              }
                                                          });


                                              } else {

                                              }
                                          } catch (Exception ex) {
                                              progress_rel.setVisibility(View.GONE);
                                          }
                                      }

                                      @Override
                                      public void onError(String result) {
                                          progress_rel.setVisibility(View.GONE);
                                      }
                                  }, MeetingAttendees.this, getApplicationContext(), Globals.tokenUrl, "",
                        String.format("grant_type=password&username=%s&password=%s", "Admin", "P@ssw0rd"), ContentTypes.FormEncoded.toString());


            }
        }

        final WebView webview = (WebView)findViewById(R.id.webview_main);
        if (webview != null)
        {
            try
            {
                webview.setWebViewClient(new WebViewClient(){

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url){
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                        super.onReceivedError(view, request, error);
                    }
                });
                webview.getSettings().setJavaScriptEnabled(true);
                webview.loadUrl(String.format("http://197.45.191.5:760/main.html?poi=", meetingModel.unit.WF_POI.ROOM_NO));
            }
            catch(Exception ex)
            {

            }

        }

    }

    public void setTimer()
    {
        if (tclose != null)
        {
            //Toast.makeText(getApplicationContext(), "aloh", Toast.LENGTH_SHORT).show();
            tclose.cancel();
        }
        tclose = new Timer();
        tclose.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //finish();
                        //Intent intent = new Intent(MeetingAttendees.this, MainNewActivity.class);
                        Intent intent = new Intent(MeetingAttendees.this, AllListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }, 180000);
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


//    void setButtons()
//    {
//        if(Globals.skipCount < meetingModel.meeting.ATTENDEES.size() && (meetingModel.meeting.ATTENDEES.size() - (Globals.skipCount+Globals.pageSize)) > 0)
//        {
//            next_btn.setEnabled(true);
//        }
//        else
//            next_btn.setEnabled(false);
//
//        if(Globals.skipCount > 0)
//        {
//            prev_btn.setEnabled(true);
//        }
//        else
//            prev_btn.setEnabled(false);
//    }

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
        if (tclose != null)
            tclose.cancel();
    }
}
