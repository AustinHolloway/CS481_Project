package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/* MAIN PAGE FOR WHEN APP OPENS, THE LOGIN PAGE
 *
 * TODO: JAVA DOC DIS
 */
public class MainActivity extends AppCompatActivity
{
    //Class fields connect with XML
    private EditText emailXML, passwordXML;
    private Button signInButtonXML;
    private TextView textToSignUpXML;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //basically a constructor.
    private void instantiateFields ()
    {
        //create authentication for firebase
        mFirebaseAuth = FirebaseAuth.getInstance();

        //finds the id's in the XML files and sets them to the fields
        emailXML = findViewById(R.id.emailSignIn);
        passwordXML = findViewById(R.id.signInPassword);
        signInButtonXML = findViewById(R.id.signInButton);
        textToSignUpXML = findViewById(R.id.textViewToSignUp);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Remove the title bar
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_main);

        instantiateFields ();//constructor

        mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if(mFirebaseUser != null )
                {
                    Toast.makeText(MainActivity.this, "You have been logged in",Toast.LENGTH_SHORT).show();
                    Intent toActivityHome = new Intent(MainActivity.this,ChatActivity.class);
                    startActivity(toActivityHome);
                }else
                {
                    Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT);
                }
            }
        };

        signInButtonXML.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //get input text
                String email = emailXML.getText().toString();
                String password = passwordXML.getText().toString();

                //Check for missing input
                if (email.isEmpty() && password.isEmpty())
                {
                    emailXML.setError("Please enter your email");
                    passwordXML.setError("Please enter your password");
                } else if (password.isEmpty())
                {
                    passwordXML.setError("Please enter your password");
                    passwordXML.requestFocus();
                } else if (email.isEmpty())
                {
                    emailXML.setError("Please enter your email");
                    emailXML.requestFocus();
                } else
                { //try to create authentication
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this
                                    , new OnCompleteListener<AuthResult>()
                    {
                        //logged in or nah?
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (!task.isSuccessful())
                            {
                                Toast.makeText(MainActivity.this
                                        , "Unsuccessful Log In, try again", Toast.LENGTH_SHORT);
                            } else
                            {
                                startActivity(new Intent(MainActivity.this,
                                        ChatActivity.class));
                            }
                        }
                    });
                }
            }
        });
        textToSignUpXML.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity( new Intent(MainActivity.this, SignUpActivity.class));
            }
        });
    }
}