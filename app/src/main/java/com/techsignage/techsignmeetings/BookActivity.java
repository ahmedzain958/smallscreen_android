package com.techsignage.techsignmeetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Adapters.BookingAdapter;
import com.techsignage.techsignmeetings.Adapters.RoomsAdapter;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.KeyboardUtils;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.ServiceResponses.AuthResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.CreateMeetingResponse;
import com.techsignage.techsignmeetings.Models.HourModel;
import com.techsignage.techsignmeetings.Models.MeetingModel;
import com.techsignage.techsignmeetings.Models.Interfaces.hourCallback;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BookActivity extends CoreActivity {

    @InjectView(R.id.tv_MeetingDate)
    TextView tv_MeetingDate;

    @InjectView(R.id.tv_NowDate)
    TextView tv_NowDate;

    @InjectView(R.id.date_picker)
    DatePicker date_picker;

    Calendar selectedDate;

    @InjectView(R.id.tv_UnitName)
    TextView tv_UnitName;

//    @InjectView(R.id.tv_leftbtn)
//    TextView tv_leftbtn;
//
//    @InjectView(R.id.tv_rightBtn)
//    TextView tv_rightBtn;

    @InjectView(R.id.next_btn)
    TextView next_btn;

    @InjectView(R.id.tv_MeetingTitle)
    TextView tv_MeetingTitle;

    @InjectView(R.id.lin2)
    LinearLayout lin2;

    @InjectView(R.id.progress_rel)
    RelativeLayout progress_rel;

//    @InjectView(R.id.rooms_spinner)
    Spinner rooms_spinner;

    LinearLayoutManager llm;

    //ProgressDialog dialog;
    //SweetAlertDialog sweetAlertDialog;
    com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface retrofitInterface;

    @InjectView(R.id.back_btn)
    Button back_btn;

    Subscription subscription;
    HourModel hourModel;
    String firstHour;
    String lastHour;

    final int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    final int flags2 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.inject(this);
        progress_rel.setVisibility(View.GONE);
        rooms_spinner = (Spinner)findViewById(R.id.rooms_spinner);

        tv_MeetingTitle.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                Log.d("keyboard", "keyboard visible: "+isVisible);
                //Toast.makeText(LoginActivity.this, "keyboard visible: "+isVisible, Toast.LENGTH_SHORT).show();
                getWindow().getDecorView().setSystemUiVisibility(flags2);
//                if (isVisible)
//                {
//                    //getWindow().getDecorView().setSystemUiVisibility(flags);
//                }
//                else
//                {
//                    //getWindow().getDecorView().setSystemUiVisibility(flags2);
//                }
            }
        });

        //final String activityName = getIntent().getExtras().getString("activityName");
        //final Boolean show_spinner = activityName.equals("AllListActivity");

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.book_list);
        final hourCallback callback = new hourCallback() {
            @Override
            public void selectedItem(HourModel selectedModel, final List<HourModel> lst) {
                if (selectedModel.IsEnabled && !selectedModel.IsBooked)
                {
                    hourModel = selectedModel;
                    for (int i=0; i < lst.size(); i++)
                    {
                        if(i == 0)
                            //selectedModel.StartHour = lst.get(i).StartHour;
                        firstHour = lst.get(i).StartHour;

                        if (i == lst.size()-1)
                            //selectedModel.EndHour = lst.get(i).EndHour;
                        lastHour = lst.get(i).EndHour;
                    }
                    //selectedModel.CombinedHours = String.format("%s | %s", selectedModel.StartHour, selectedModel.EndHour);
                    //String ss = "";
                    tv_MeetingDate.setText(
                            String.format("%s %s", new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime())
                                    , String.format("%s | %s", firstHour, lastHour))
                    );
                }
                if (!selectedModel.IsSelected && lst.size() == 0)
                {
                    tv_MeetingDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()));
                }


            }
        };
        if (rooms_spinner != null)
        {
            rooms_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MeetingModel meetingModel = new MeetingModel();
                    meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
                    meetingModel.UNIT_ID = ((UnitModel)rooms_spinner.getSelectedItem()).UNIT_ID;
                    structCalendar(callback, mRecyclerView, meetingModel);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else
        {
            tv_UnitName.setText(Globals.loggedUnit.UNIT_NAME);
        }

        BookingAdapter adapter = new BookingAdapter(BookActivity.this, callback);
        for (HourModel hourModel : Globals.hours)
        {
            hourModel.IsSelected = false;
        }
        adapter.setLst(Globals.hours);
        mRecyclerView.setAdapter(adapter);

        llm = new LinearLayoutManager(BookActivity.this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(llm);

        final String token = Utilities.getSharedValue("token", this);
        retrofitInterface = Utilities.liveAPI(token);

        selectedDate = Calendar.getInstance();
        tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));
        tv_MeetingDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()));

        Observable<RoomsResponse> allRooms = retrofitInterface.allrooms();
        subscription = allRooms.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<RoomsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final RoomsResponse roomsResponse) {
                        RoomsAdapter roomsAdapter = new RoomsAdapter(BookActivity.this, R.layout.spinner_item, roomsResponse.Rooms);
                        rooms_spinner.setAdapter(roomsAdapter);

                        if (rooms_spinner != null)
                        {
                            MeetingModel meetingModel = new MeetingModel();
                            meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
                            meetingModel.UNIT_ID = roomsResponse.Rooms.get(0).UNIT_ID;
                            structCalendar(callback, mRecyclerView, meetingModel);
                        }
                    }
                });


        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        date_picker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tv_MeetingDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()));

                MeetingModel meetingModel = new MeetingModel();
                meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
                meetingModel.UNIT_ID = Globals.unitId;
                structCalendar(callback, mRecyclerView, meetingModel);

            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                for (int i = 0; i < Globals.hours.size(); i++)
                {
                    RecyclerView.ViewHolder v = mRecyclerView.findViewHolderForAdapterPosition(i);
                    if(v != null)
                    {
                        RelativeLayout rel1 = (RelativeLayout)v.itemView.findViewById(R.id.rel1);
                        TextView tv_container = (TextView)rel1.findViewById(R.id.tv_container);
                        tv_container.setBackgroundResource(R.drawable.white_bg);

                        if (Globals.hours.get(i).IsSelected != null)
                        {
                            if (Globals.hours.get(i).IsSelected)
                            {
                                tv_container.setBackgroundResource(R.drawable.right_pic);
                            }
                        }

                        if (Globals.hours.get(i).IsEnabled != null)
                        {
                            if (!Globals.hours.get(i).IsEnabled)
                            {
                                tv_container.setBackgroundResource(R.drawable.expired);
                            }
                        }

                        if (Globals.hours.get(i).IsBooked)
                        {
                            tv_container.setBackgroundResource(R.drawable.booked_pic);
                        }
                    }
                }
            }
        });



//        tv_leftbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // llm.scrollToPosition(Globals.hours.size());
//                //mRecyclerView.getLayoutManager().scrollToPosition(llm.findFirstVisibleItemPosition() - 1);
//                mRecyclerView.smoothScrollToPosition(llm.findFirstVisibleItemPosition() - 3);
//            }
//        });
//
//        tv_rightBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // llm.scrollToPosition(Globals.hours.size());
//                //mRecyclerView.getLayoutManager().scrollToPosition(llm.findLastVisibleItemPosition() + 1);
//                mRecyclerView.smoothScrollToPosition(llm.findLastVisibleItemPosition() + 3);
//            }
//        });

        //final LinearLayout layout = (LinearLayout)findViewById(R.id.lin2);
//        ViewTreeObserver vto = lin2.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    lin2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                } else {
//                    lin2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//                int height = lin2.getMeasuredHeight();
//                int width  = lin2.getMeasuredWidth();
//
////                if (height>100)
////                {
////                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,100);
////                    lin2.setLayoutParams(parms);
////
////                    //lin2.getLayoutParams().height = 100;
////                    //lin2.requestLayout();
////                }
//            }
//        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_MeetingTitle.setError(null);
                final String title = tv_MeetingTitle.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    tv_MeetingTitle.setError(getString(R.string.error_field_required));
                    tv_MeetingTitle.requestFocus();
                    return;
                }

                if (hourModel == null)
                {
                    Toast.makeText(BookActivity.this, R.string.selectrange, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rooms_spinner != null)
                {
                    if (rooms_spinner.getSelectedItem() == null)
                    {
                        Toast.makeText(BookActivity.this, "Choose Room", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                progress_rel.setVisibility(View.VISIBLE);

                MeetingModel meetingModel = new MeetingModel();
                meetingModel.UNIT_ID = Globals.unitId;
                if (rooms_spinner != null)
                    meetingModel.UNIT_ID = ((UnitModel)rooms_spinner.getSelectedItem()).UNIT_ID;
                meetingModel.CREATE_USER = Globals.loggedUser.USER_ID;
                meetingModel.MEETING_TITLE = title;
                meetingModel.RECURRENCE_TYPE = "Single";
                //meetingModel.MEETING_TITLE =
                meetingModel.START_DATETIME = String.format("%s %s", new SimpleDateFormat("yyyy/MM/dd").format(selectedDate.getTime()),
                        firstHour);
                meetingModel.END_DATETIME = String.format("%s %s", new SimpleDateFormat("yyyy/MM/dd").format(selectedDate.getTime()),
                        lastHour);
                meetingModel.MEETING_STATUS_ID = "F6A33569-A955-48C2-934A-574F8589E7DC";
                Gson gson = new Gson();
                String j = gson.toJson(meetingModel);
                Observable<CreateMeetingResponse> call = retrofitInterface.savemeeting(meetingModel);
                call.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CreateMeetingResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                progress_rel.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(CreateMeetingResponse authResponse) {
                                progress_rel.setVisibility(View.GONE);

                                Toast.makeText(BookActivity.this, authResponse.Message, Toast.LENGTH_SHORT).show();
                                Intent intent = null;
                                if (rooms_spinner == null)
                                {
                                    intent = new Intent(BookActivity.this, MainActivity.class);
                                }
                                else
                                {
                                    intent = new Intent(BookActivity.this, AllListActivity.class);
                                }
                                //Intent intent = new Intent(BookActivity.this, AllListActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void structCalendar(final hourCallback callback, final RecyclerView mRecyclerView
    , MeetingModel meetingModel) {
        //dialog = Utilities.showDialog(BookActivity.this);
//        MeetingModel meetingModel = new MeetingModel();
//        meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
//        meetingModel.UNIT_ID = Globals.unitId;
        Observable<AuthResponse> call = retrofitInterface.roomblocks(meetingModel);
        call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AuthResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progress_rel.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(AuthResponse authResponse) {
//                                if (dialog.isShowing())
//                                {
//                                    dialog.hide();
//                                }
                        Globals.hours = authResponse.authElements.hours;
                        for (HourModel hourModel : Globals.hours)
                        {
                            hourModel.IsSelected = false;
                        }
                        BookingAdapter adapter = new BookingAdapter(BookActivity.this, callback);
                        adapter.setLst(authResponse.authElements.hours);
                        mRecyclerView.setAdapter(adapter);
                    }
                });
    }
}
