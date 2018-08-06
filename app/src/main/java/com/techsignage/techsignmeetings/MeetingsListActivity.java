package com.techsignage.techsignmeetings;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Adapters.MeetingsAdapter;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomMeetingsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Models.UserMeetingModel;
import com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface;
import com.techsignage.techsignmeetings.Network.ContentTypes;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Network.IConnector;
import com.techsignage.techsignmeetings.Network.VolleyCallbackString;
import com.techsignage.techsignmeetings.Network.VolleyRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import butterknife.ButterKnife;
import butterknife.BindView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.techsignage.techsignmeetings.Helpers.Utilities.hideNavBar;

public class MeetingsListActivity extends CoreActivity {

    @BindView(R.id.tv_UnitName)
    TextView tv_UnitName;

    @BindView(R.id.tv_NowDate)
    TextView tv_NowDate;

    /* @BindView(R.id.next_btn)
     Button next_btn;*/
    @BindView(R.id.book_btn)
    Button book_btn;

    /*@BindView(R.id.prev_btn)
    Button prev_btn;*/

    @BindView(R.id.back_btn)
    Button back_btn;

    @BindView(R.id.meetings_list)
    RecyclerView activerequestslist;

    /*@BindView(R.id.container1_lin)
  RelativeLayout container1_lin;

 /*ndView(R.id.container2_lin)
  RelativeLayout container2_lin;

  @BindView(R.id.container3_lin)
  RelativeLayout container3_lin;
*/
    @BindView(R.id.progress_rel)
    RelativeLayout progress_rel;

    Timer t;
    Timer tclose;
    IConnector connector;
    MeetingsAdapter adapter;
    List<UserMeetingModel> Meetings;
    retrofitInterface retrofitInterface;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetings_list);
        ButterKnife.bind(this);
        hideNavBar(this);
        VolleyRequest request = new VolleyRequest();
        request.getString(new VolleyCallbackString() {
                              @Override
                              public void onSuccess(String result) {
                                  try {
                                      progress_rel.setVisibility(View.GONE);
                                      JSONObject object = new JSONObject(result);
                                      final String token = object.getString("access_token");

                                      retrofitInterface = Utilities.liveAPI(token);
                                      tv_UnitName = (TextView) findViewById(R.id.tv_UnitName);
                                      tv_NowDate = (TextView) findViewById(R.id.tv_NowDate);
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

//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            if(t != null)
//                                t.cancel();
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //finish();
//                                    Intent intent = new Intent(MeetingsListActivity.this, MainNewActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            });
//
//                        }
//                    }, 65000);

//                    tclose = new Timer();
//                    tclose.schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //finish();
//                                    Intent intent = new Intent(MeetingsListActivity.this, MainNewActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            });
//
//                        }
//                    }, 180000);
                                  } catch (Exception ex) {

                                  }
                              }

                              @Override
                              public void onError(String result) {

                              }
                          }, MeetingsListActivity.this, getApplicationContext(), Globals.tokenUrl, "",
                String.format("grant_type=password&username=%s&password=%s", "Admin", "P@ssw0rd"), ContentTypes.FormEncoded.toString());

        Globals.pageSize = 5;
        book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetingsListActivity.this, LoginActivity.class);
                intent.putExtra("activityName", getClass().getSimpleName());
                MeetingsListActivity.this.startActivity(intent);
            }
        });
       /* next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*if (Globals.skipCount < Meetings.size() && (Meetings.size() - (Globals.skipCount + Globals.pageSize)) > 0) {
                    Globals.skipCount += Globals.pageSize;
                }

                setButtons();

                List<UserMeetingModel> meetingModels = new ArrayList<UserMeetingModel>();
                for (int i = 0; i < Meetings.size(); i++) {
                    if ((i + 1) > Globals.skipCount) {
                        if (meetingModels.size() < Globals.pageSize) {
                            meetingModels.add(Meetings.get(i));
                        }
                    }
                }*//*

                adapter = new MeetingsAdapter(MeetingsListActivity.this, R.layout.meeting_item);
                adapter.setLst(Meetings);
                activerequestslist.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });

        prev_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Globals.skipCount > 0)
                    Globals.skipCount -= Globals.pageSize;

//                setButtons();

                List<UserMeetingModel> meetingModels = new ArrayList<UserMeetingModel>();
                for (int i = 0; i < Meetings.size(); i++) {
                    if ((i + 1) > Globals.skipCount) {
                        if (meetingModels.size() < Globals.pageSize) {
                            meetingModels.add(Meetings.get(i));
                        }
                    }
                }

                adapter = new MeetingsAdapter(MeetingsListActivity.this, R.layout.meeting_item);
                adapter.setLst(meetingModels);
                activerequestslist.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }
        });*/

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        connector = new IConnector() {
            @Override
            public void getConnectionStatus(Boolean currentStatus) {
                if (currentStatus) {
                    try {
                        getMeetingsList();
                    } catch (Exception ex) {

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please connect to the Internet", Toast.LENGTH_LONG).show();
                }
            }
        };

        super.networkStateReceiver.setConnector(connector);


        setTimer();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
//        float x = event.getX();
//        float y = event.getY();
//        Toast.makeText(Example.this, "x=" + x + " y="+ y,
//                Toast.LENGTH_SHORT).show();
        setTimer();

        return false;
    }


    private void setTimer() {
        if (tclose != null) {
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
                        Intent intent = new Intent(MeetingsListActivity.this, MainNewActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }, 180000);
    }

   /* void setButtons() {
        if (Globals.skipCount < Meetings.size() && (Meetings.size() - (Globals.skipCount + Globals.pageSize)) > 0) {
            next_btn.setEnabled(true);
        } else
            next_btn.setEnabled(false);

        if (Globals.skipCount > 0) {
            prev_btn.setEnabled(true);
        } else
            prev_btn.setEnabled(false);
    }*/

    void getMeetingsList() {
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
                        tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));
                    /*    next_btn.setEnabled(true);
                        prev_btn.setEnabled(true);*/
                        //Meetings = serviceResponse.RoomMeetingsResponse.Meetings;
                        Meetings = serviceResponse.RoomMeetings.MeetingsAll;
//                        setButtons();
                        assert activerequestslist != null;
                        adapter = new MeetingsAdapter(MeetingsListActivity.this, R.layout.meeting_item);
                       /* List<UserMeetingModel> meetingModels = new ArrayList<UserMeetingModel>();
                        for (int i = 0; i < Meetings.size(); i++) {
                            if ((i + 1) > Globals.skipCount) {
                                if (meetingModels.size() < Globals.pageSize) {
                                    meetingModels.add(Meetings.get(i));
                                }
                            }
                        }*/
                        adapter.setLst(Meetings);
                        activerequestslist.setAdapter(adapter);
                        tv_UnitName.setText(serviceResponse.RoomMeetings.Room.UNIT_NAME);

                        LinearLayoutManager llm = new LinearLayoutManager(MeetingsListActivity.this);
                        activerequestslist.setLayoutManager(llm);
                    }
                });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

       /* if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            setLandscape();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            setPortrait();
        }*/
    }

    /*  void setLandscape() {
          container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .19f));
          container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .64f));
          container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .17f));
      }

      void setPortrait() {
          container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
          container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .70f));
          container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
      }
  */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        super.networkStateReceiver.connector = null;
        if (t != null)
            t.cancel();
        if (tclose != null)
            tclose.cancel();
    }
}
