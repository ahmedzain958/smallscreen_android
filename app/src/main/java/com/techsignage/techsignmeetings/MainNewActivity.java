package com.techsignage.techsignmeetings;

import android.annotation.SuppressLint;
import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
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

    private Handler handler = new Handler();
    private int value;
    private int gpio_num;
    private Subscription subscription;
    Timer t;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        //getWindow().setBackgroundDrawableResource(R.drawable.mainpage_bggreen);

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

        if (Utilities.getSharedValue("token", MainNewActivity.this).equals(""))
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
        else
        {
            loadMeetings();
        }

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
                        //OngoingReactAsync(serviceResponse);
                    }
                });
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
