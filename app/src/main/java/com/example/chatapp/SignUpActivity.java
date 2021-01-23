package com.example.chatapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity
{

    //Class fields
    private EditText emailXML, passwordXML, confirmPasswordXML,
            nameXML, usernameXML, birthdayXML;
    private Button signUpButtonXML;
    private TextView textToSignInXML;
    private FirebaseAuth mFirebaseAuth;

    //basically a constructor. but a constructor was not okay here.
    private void instantiateFields ()
    {
        //create authentication for firebase
        mFirebaseAuth = FirebaseAuth.getInstance();

        //finds the id's in the XML files and sets them to the fields
        emailXML = findViewById(R.id.emailSignUp);
        passwordXML = findViewById(R.id.passwordSignUp);
        signUpButtonXML = findViewById(R.id.buttonSignUp);
        textToSignInXML = findViewById(R.id.smallTxtToSignIn);
        confirmPasswordXML = findViewById(R.id.confirmPasswordSignUp);
        nameXML = findViewById(R.id.legalNameSignUp);
        usernameXML = findViewById(R.id.userNameSignUp);
        birthdayXML = findViewById(R.id.birthdaySignUp);
    }

    //On Create happens when opened
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); //Created by android studio

        //Remove the title bar
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_sign_up);

        instantiateFields(); //create fields

        //TODO: FORMAT DATE AND KICK OUT KIDDOS

        //TODO: CHECK FOR USERNAMES IN FIRE BASE DB

        //Listener and auth for the sign up button
        signUpButtonXML.setOnClickListener(new View.OnClickListener()
        {
            //TODO: ADD MORE DATA SANITATION
            @Override
            public void onClick(View v)
            {
                //get input text
                String email = emailXML.getText().toString().trim();
                String password = passwordXML.getText().toString().trim();
                String confPassword = confirmPasswordXML.getText().toString().trim();


                //Check for missing input
                if (email.isEmpty() && password.isEmpty())
                {
                    emailXML.setError("Please enter your email");
                    passwordXML.setError("Please enter your password");
                }else if (password.isEmpty())
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

    //currently doesnt do anything.
    @Override
    public void onStart()
    {
        super.onStart();
        if(mFirebaseAuth.getCurrentUser() != null){}
    }
}