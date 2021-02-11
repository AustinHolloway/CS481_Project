package com.example.chatapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import org.apache.commons.validator.routines.EmailValidator;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class SignUpActivity extends AppCompatActivity
{

    //Class fields
    private EditText emailXML, passwordXML, confirmPasswordXML,
            nameXML, usernameXML;
    private Button signUpButtonXML, birthdayXML;
    private TextView textToSignInXML;
    private FirebaseAuth mFirebaseAuth;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;


    //basically a constructor. but a constructor was not okay here.
    private void instantiateFields ()
    {
        //create authentication for firebase
        mFirebaseAuth = FirebaseAuth.getInstance();

        //finds the id's in the XML files and sets them to the field
        emailXML = findViewById(R.id.emailSignUp);
        passwordXML = findViewById(R.id.passwordSignUp);
        signUpButtonXML = findViewById(R.id.buttonSignUp);
        textToSignInXML = findViewById(R.id.smallTxtToSignIn);
        confirmPasswordXML = findViewById(R.id.confirmPasswordSignUp);
        nameXML = findViewById(R.id.legalNameSignUp);
        usernameXML = findViewById(R.id.userNameSignUp);
        birthdayXML = findViewById(R.id.datePickerButton);

    }

    //On Create happens when opened
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); //Created by android studio
        setContentView(R.layout.activity_sign_up);
       // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }


        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText("Birthday");

        instantiateFields(); //create fields
        //TODO: FORMAT DATE(DONE) AND KICK OUT KIDDOS

        //TODO: CHECK FOR USERNAMES IN FIRE BASE DB

        //Listener and auth for the sign up button
        signUpButtonXML.setOnClickListener(new View.OnClickListener()
        {
            //TODO: ADD MORE DATA SANITATION
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v)
            {
                //get input text
                String name = nameXML.getText().toString().trim();
                String email = emailXML.getText().toString().trim();
                String password = passwordXML.getText().toString().trim();
                String confPassword = confirmPasswordXML.getText().toString().trim();
                String birthday = birthdayXML.getText().toString().trim();

                //Check for missing input
                if (email.isEmpty() && password.isEmpty() && name.isEmpty())
                {
                    emailXML.setError("Please enter your email");
                    passwordXML.setError("Please enter your password");
                    nameXML.setError("Please enter your name");
                }
                else if(password.length() < 6){
                    passwordXML.setError("Password must be 6 characters long.");
                    passwordXML.requestFocus();
                }
                else if(tooYoung(birthday)){
                    birthdayXML.setError("Must be 18 to use this app.");
                }
                else if(name.isEmpty())
                {
                    nameXML.setError("Please enter your name");
                    nameXML.requestFocus();
                }
                else if (password.isEmpty())
                {
                    passwordXML.setError("Please enter your password");
                    passwordXML.requestFocus();
                }else if (email.isEmpty())
                {
                    emailXML.setError("Please enter your email");
                    emailXML.requestFocus();
                }else if (EmailValidator.getInstance().isValid(email) == false)
                {
                    emailXML.setError("Please enter a valid email");
                    emailXML.requestFocus();
                }else if (password.equals(confPassword) == false )
                {
                    passwordXML.setError("Passwords must match");
                    passwordXML.requestFocus();
                    passwordXML.setText("");

                    confirmPasswordXML.setError("Passwords must match");
                    confirmPasswordXML.setText("");
                }else
                { //try to create authentication

                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                            SignUpActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        //this is nested within the above else.
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            if(!task.isSuccessful())
                            {
                                Toast.makeText(SignUpActivity.this,
                                        "Log In was unsuccessful. Try again. ",
                                        Toast.LENGTH_SHORT).show();
                            }else
                            {
                                String name = nameXML.getText().toString().trim();
                                String birthday = birthdayXML.getText().toString().trim();
                                String username = usernameXML.getText().toString().trim();

                                //create user for database
                                UserInfo user = new UserInfo(name, birthday,username);

                                //send to the database the user
                                FirebaseDatabase.getInstance().getReference("UserInfo")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(SignUpActivity.this,
                                                    "Registration was Successful. ",
                                                    Toast.LENGTH_SHORT).show();
                                        }else
                                        {
                                            Toast.makeText(SignUpActivity.this,
                                                    "Registration was not Successful. ",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                startActivity(new Intent(SignUpActivity.this,
                                        MapsActivity.class));
                            }
                        }
                    });
                }
            }


        });


        textToSignInXML.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent (SignUpActivity.this, MainActivity.class));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean tooYoung(String birthday) {

        String[] temp = birthday.split(" ");
        int[] birthArray = new int[temp.length];
        for (int i = 0; i < temp.length; i++){
            birthArray[i] = Integer.parseInt(temp[i]);
        }
        int birthYear = birthArray[2];
        int birthMonth = birthArray[0];
        int birthDayOfMonth = birthArray[1];
        LocalDate birthDate = LocalDate.of(birthYear, birthMonth, birthDayOfMonth);
        LocalDate currentDate = LocalDate.now();
        long age = ChronoUnit.YEARS.between(birthDate, currentDate);
        return age < 18;

    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;
        return makeDateString(day, month, year);

    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);
                dateButton.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    private String makeDateString(int day, int month, int year)
    {
        return month+ " " + day + " " + year;
    }
    //currently doesnt do anything.
    @Override
    public void onStart()
    {
        super.onStart();
        if(mFirebaseAuth.getCurrentUser() != null){}
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
}