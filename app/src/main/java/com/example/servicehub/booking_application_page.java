package com.example.servicehub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class booking_application_page extends AppCompatActivity {

    private ImageView iv_messageBtn, iv_notificationBtn, iv_homeBtn, iv_accountBtn,
            iv_moreBtn;
    private ImageView iv_pickAddress;
    private EditText et_addInfo;
    private TextInputEditText et_phoneNum;
    private TextView tv_address, tv_back, et_date, et_time;
    private CardView cardView12, cardView13, cardView14, cardView15, cardView16;
    private Button btn_next1,btn_next2, btn_next3, btn_next4, btn_back2, btn_back3, btn_back4, btn_back5, btn_submit;
    private Geocoder geocoder;

    private AutoCompleteTextView auto_complete_txt,auto_aircon_type,auto_brand,auto_unit_type;
    private ArrayAdapter<CharSequence> adapterPropertyItems, adapterBrand, adapterAirconType, adapterUnitType;
    private String[] propertyItems = {"Condo | Apartment", "House | Townhouse", "Small business | Store", "Office building", "Warehouse | Storage"};
    private String[] brand = {"Home","Camel","Carrier","Coldfront","Condura","Daikin","Everest","Fujidenzo","GE","Gree","Haier","Hanabishi","Hisense",
            "Hitachi","Kelvinator","Koppel","LG","Mabe","Midea","Mitsubishi","National","Panasonic","Samsung","Sanyo","Sharp","TCL","Union","Xtreme",
            "York","Other","I don't know"};
    private String[] airconType = {"Window","Split","Tower","Cassette","Suspended","Concealed","U-shaped Window"};
    private String[] unitType = {"Inverter","Non-Inverter","I don't know"};
    private String latLng, latString, longString, propertyType, acBrand, acType, acUnitType, projectIdFromIntent;
    private int hour, minute, year, month, day;
    private final SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_application_page);
        projectIdFromIntent = getIntent().getStringExtra("projectIdFromIntent");

        setRef();
        initPlaces();
        dropDownMenuTextView();
        clickListeners();
        bottomNavTaskbar();
    }

    private void initPlaces() {

        //Initialize places
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        //Set edittext no focusable
        tv_address.setFocusable(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && resultCode == RESULT_OK){
            com.google.android.libraries.places.api.model.Place place = Autocomplete.getPlaceFromIntent(data);

            List<Address> address = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                address = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);

                latString = String.valueOf(address.get(0).getLatitude());
                longString = String.valueOf(address.get(0).getLongitude());
                String latLngText = latString + "," + longString;
                String addressText =  place.getAddress().toString();


                latLng = latLngText;
                tv_address.setText(addressText);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
        }

    }

    private void clickListeners() {

        iv_pickAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(booking_application_page.this);
                builderSingle.setIcon(R.drawable.logo);
                builderSingle.setTitle("Select Address:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(booking_application_page.this, android.R.layout.select_dialog_singlechoice);

                DatabaseReference myAddressDatabase = FirebaseDatabase.getInstance().getReference("Address");

                myAddressDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                            MyAddress myAddress = dataSnapshot.getValue(MyAddress.class);
                            String addrses = myAddress.getAddressValue();
                            latString = myAddress.getLatString();
                            longString = myAddress.getLongString();
                            arrayAdapter.add(addrses);
                        }

                        arrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                builderSingle.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String addressFromDialog = arrayAdapter.getItem(which);
                        tv_address.setText(addressFromDialog);
                        dialog.dismiss();
                    }
                });

                builderSingle.show();
            }
        });

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(booking_application_page.this, booking_page.class);
                intent.putExtra("Project ID", projectIdFromIntent);
                startActivity(intent);
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // placePicker();


                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ADDRESS,
                        com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.NAME);

                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(booking_application_page.this);

                //Start Activity result
                startActivityForResult(intent, 100);



            }
        });

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        booking_application_page.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        month = month+1;
                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEE");
                        Date dateOfWeek = new Date(year, month, day-1);
                        String dayOfWeek = simpledateformat.format(dateOfWeek);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MMMM/dd/yyyy");
                        calendar.set(year, month, day);
                        String dateString = sdf.format(calendar.getTime());

                        et_date.setText(dateString  + "/" + dayOfWeek);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + 1*24*60*60*1000);
                datePickerDialog.show();

            }
        });

        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;

                        boolean isPM = (hour >= 12);
                        et_time.setText(String.format("%02d:%02d %s", (hour == 12 || hour == 0) ? 12 : hour % 12, minute, isPM ? "PM" : "AM"));


                    }
                };

                int style = TimePickerDialog.THEME_HOLO_DARK;

                TimePickerDialog timePickerDialog = new TimePickerDialog(booking_application_page.this, style, onTimeSetListener, hour, minute, false);
                timePickerDialog.updateTime(8,00);

                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }
        });

        auto_complete_txt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 propertyType = adapterView.getItemAtPosition(i).toString();
            }
        });

        auto_brand.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 acBrand = adapterView.getItemAtPosition(i).toString();
            }
        });

        auto_aircon_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 acType = adapterView.getItemAtPosition(i).toString();
            }
        });

        auto_unit_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 acUnitType = adapterView.getItemAtPosition(i).toString();
            }
        });

        btn_next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(tv_address.getText().toString())){
                    Toast.makeText(booking_application_page.this, "Address is required", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(auto_complete_txt.getText().toString()))
                {
                    Toast.makeText(booking_application_page.this, "Property type is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    cardView12.setVisibility(View.GONE);
                    cardView13.setVisibility(View.VISIBLE);
                    cardView14.setVisibility(View.GONE);
                    cardView15.setVisibility(View.GONE);
                }
            }
        });

        btn_next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(auto_brand.getText().toString())){
                    Toast.makeText(booking_application_page.this, "Unit Brand is required", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(auto_aircon_type.getText().toString()))
                {
                    Toast.makeText(booking_application_page.this, "Aircon Type is required", Toast.LENGTH_SHORT);
                }
                else if(TextUtils.isEmpty(auto_unit_type.getText().toString()))
                {
                    Toast.makeText(booking_application_page.this, "Unit Type is required", Toast.LENGTH_SHORT);
                }
                else
                {
                    cardView12.setVisibility(View.GONE);
                    cardView13.setVisibility(View.GONE);
                    cardView14.setVisibility(View.VISIBLE);
                    cardView15.setVisibility(View.GONE);
                }

            }
        });

        btn_next3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_date.getText().toString()))
                {
                    Toast.makeText(booking_application_page.this, "Date is required", Toast.LENGTH_SHORT);
                }
                else if(TextUtils.isEmpty(et_time.getText().toString()))
                {
                    Toast.makeText(booking_application_page.this, "Time is required", Toast.LENGTH_SHORT);
                }
                else
                {
                    cardView12.setVisibility(View.GONE);
                    cardView13.setVisibility(View.GONE);
                    cardView14.setVisibility(View.GONE);
                    cardView15.setVisibility(View.VISIBLE);
                }

            }
        });

        btn_next4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_phoneNum.getText().toString()))
                {
                    Toast.makeText(booking_application_page.this, "Contact Number is required", Toast.LENGTH_SHORT);
                }
                else if(et_phoneNum.length() < 10)
                {
                    Toast.makeText(booking_application_page.this, "Contact Number should be 11 digit", Toast.LENGTH_SHORT);
                }
                else
                {
                    cardView12.setVisibility(View.GONE);
                    cardView13.setVisibility(View.GONE);
                    cardView14.setVisibility(View.GONE);
                    cardView15.setVisibility(View.GONE);
                    cardView16.setVisibility(View.VISIBLE);
                }

            }
        });

        btn_back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView12.setVisibility(View.VISIBLE);
                cardView13.setVisibility(View.GONE);
                cardView14.setVisibility(View.GONE);
                cardView15.setVisibility(View.GONE);
            }
        });

        btn_back3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView12.setVisibility(View.GONE);
                cardView13.setVisibility(View.VISIBLE);
                cardView14.setVisibility(View.GONE);
                cardView15.setVisibility(View.GONE);
            }
        });

        btn_back4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView12.setVisibility(View.GONE);
                cardView13.setVisibility(View.GONE);
                cardView14.setVisibility(View.VISIBLE);
                cardView15.setVisibility(View.GONE);
            }
        });

        btn_back5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardView12.setVisibility(View.GONE);
                cardView13.setVisibility(View.GONE);
                cardView14.setVisibility(View.GONE);
                cardView15.setVisibility(View.VISIBLE);
                cardView16.setVisibility(View.GONE);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               Intent intent = new Intent(booking_application_page.this, booking_summary_page.class);
                Bundle extras = new Bundle();
                extras.putString("latitude", latString);
                extras.putString("longitude", longString);
                extras.putString("projectIdFromIntent", projectIdFromIntent);
                extras.putString("address", tv_address.getText().toString());
                extras.putString("property type", propertyType);
                extras.putString("aircon brand", acBrand);
                extras.putString("aircon type", acType);
                extras.putString("unit type",acUnitType);
                extras.putString("booking date", et_date.getText().toString());
                extras.putString("booking time", et_time.getText().toString());
                extras.putString("contact number", "0" + et_phoneNum.getText().toString());
                extras.putString("add info", et_addInfo.getText().toString());
                intent.putExtras(extras);
                startActivity(intent);

            }
        });


    }

    private void setRef() {
        iv_pickAddress = findViewById(R.id.iv_pickAddress);

        tv_address = findViewById(R.id.et_address);
        tv_back = findViewById(R.id.tv_back);

        iv_messageBtn = findViewById(R.id.iv_messageBtn);
        iv_notificationBtn = findViewById(R.id.iv_notificationBtn);
        iv_homeBtn = findViewById(R.id.iv_homeBtn);
        iv_accountBtn = findViewById(R.id.iv_accountBtn);
        iv_moreBtn = findViewById(R.id.iv_moreBtn);

        et_phoneNum = findViewById(R.id.et_phoneNum);
        et_addInfo = findViewById(R.id.et_addInfo);
        et_date = findViewById(R.id.et_date);
        et_time = findViewById(R.id.et_time);

        auto_complete_txt = findViewById(R.id.auto_complete_txt);
        auto_brand = findViewById(R.id.auto_brand);
        auto_aircon_type = findViewById(R.id.auto_aircon_type);
        auto_unit_type = findViewById(R.id.auto_unit_type);

        cardView12 = findViewById(R.id.cardView12);
        cardView13 = findViewById(R.id.cardView13);
        cardView14 = findViewById(R.id.cardView14);
        cardView15 = findViewById(R.id.cardView15);
        cardView16 = findViewById(R.id.cardView16);

        btn_next1 = findViewById(R.id.btn_next1);
        btn_next2 = findViewById(R.id.btn_next2);
        btn_next3 = findViewById(R.id.btn_next3);
        btn_next4 = findViewById(R.id.btn_next4);
        btn_back2 = findViewById(R.id.btn_back2);
        btn_back3 = findViewById(R.id.btn_back3);
        btn_back4 = findViewById(R.id.btn_back4);
        btn_back5 = findViewById(R.id.btn_back5);
        btn_submit = findViewById(R.id.btn_submit);

    }

    private void bottomNavTaskbar() {

        iv_messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMessageBtn = new Intent(booking_application_page.this, message_page.class);
                startActivity(intentMessageBtn);
            }
        }); // end of message button

        iv_notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNotification = new Intent(booking_application_page.this, notification_page.class);
                startActivity(intentNotification);
            }
        }); // end of notification button

        iv_homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHomeBtn = new Intent(booking_application_page.this, homepage.class);
                startActivity(intentHomeBtn);
            }
        }); // end of home button

        iv_accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAccount = new Intent(booking_application_page.this, switch_account_page.class);
                startActivity(intentAccount);
            }
        }); // end of account button

        iv_moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMoreBtn = new Intent(booking_application_page.this, more_page.class);
                startActivity(intentMoreBtn);
            }
        }); // end of more button
    }

    private void dropDownMenuTextView() {
        adapterPropertyItems = new ArrayAdapter<CharSequence>(this, R.layout.list_property, propertyItems);
        auto_complete_txt.setAdapter(adapterPropertyItems);

        adapterBrand = new ArrayAdapter<CharSequence>(this, R.layout.list_property, brand);
        auto_brand.setAdapter(adapterBrand);

        adapterAirconType = new ArrayAdapter<CharSequence>(this, R.layout.list_property, airconType);
        auto_aircon_type.setAdapter(adapterAirconType);

        adapterUnitType = new ArrayAdapter<CharSequence>(this, R.layout.list_property, unitType);
        auto_unit_type.setAdapter(adapterUnitType);

    }
}