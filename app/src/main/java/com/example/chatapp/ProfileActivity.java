package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.example.chatapp.chat.ChatActivity;
import com.example.chatapp.chat.ChatRegionalActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import static com.example.chatapp.R.string.permission_required;

public class ProfileActivity extends AppCompatActivity {

    private Tabs tbs;
    private TabLayout tabs;
    private EditText emailXML, nameXML;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ImageView ivProfile;
    private FirebaseStorage storage;
    private UploadTask uploadTask;
    private String picID;
    private String usrID;
    private DatabaseReference refName;
    private boolean doesHavePic = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Remove the title bar
        if(this.getSupportActionBar() != null)
        {
            this.getSupportActionBar().hide();
        }

        tbs = new Tabs ( findViewById(R.id.tabBarProfile), this);
        tabs = tbs.addTabs(4);

        //TODO:Set up remove it when done
     tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                int tabPos = tabs.getSelectedTabPosition();
                switch (tabPos)
                {
                    case 0: {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, MapsActivity.class));
                        break;
                    }
                    case 1:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, ChatActivity.class));
                        break;
                    }
                    case 2:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, ChatRegionalActivity.class));
                        break;
                    }
                    case 3:{
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, FindFriendsActivity.class));
                        break;
                    }
                    case 4: {
                        tabs.clearOnTabSelectedListeners();
                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        nameXML = findViewById(R.id.nameXML);  //shows name
        emailXML = findViewById(R.id.userEmail); //shows email
        ivProfile = findViewById(R.id.ivProfile); //shows picture or default pic

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //gets userId then sets that as the pic name
        usrID = firebaseUser.getUid();
        picID = usrID + ".jpg";

        //display users info
        refName = FirebaseDatabase.getInstance().getReference("UserInfo");

        final String[] name = new String[1];
        refName.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                name[0] = snapshot.child(usrID).child("name").getValue().toString();
                nameXML.setText(name[0]);
                emailXML.setText(firebaseUser.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //ref to the storage bucket
        storage = FirebaseStorage.getInstance();

            StorageReference picsChild = storage.getReference().child(picID);

            picsChild.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>()
            {
                @Override
                public void onSuccess(byte[] bytes)
                {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivProfile.setImageBitmap(bitmap);
                doesHavePic = true;
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    ivProfile.setImageResource(R.drawable.default_picture);
                    doesHavePic = false;
                }
            });
   }//on create end

    //set to onclick in XML
    public void btnChangePassword(View view)
    {
        startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
    }

    //so user can logout
    //set to onclick in XML
    public void btnLogoutclick(View view)
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        //finish---- cant click back arrow to go back to profile page
        finish();
    }

    //set to onclick in XML
    public void btnSaveClick(View view)
    {
        if(nameXML.getText().toString().trim().equals(""))
        {
            nameXML.setError(getString(R.string.enter_name));
        }
        if(emailXML.getText().toString().trim().equals(""))
        {
            emailXML.setError("Enter Email");
        }
        else
        {
            updateProfile(nameXML.getText().toString().trim(), emailXML.getText().toString().trim());
        }

    }

    //called onClick from the frame for user profile
    public void changeImage(View view)
    {
        if (doesHavePic)
        {
            PopupMenu popupMenu = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_picture, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem)
                {
                    int id = menuItem.getItemId();

                    if(id==R.id.mnuChangePic)
                    {
                        //allows users to pick a pic from their gallery
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        if (intent != null)
                            startActivityForResult(intent, 101);
                    }
                    else if (id==R.id.mnuRemovePicture)
                    {
                        //needs set to delete from storage
                        ivProfile.setImageResource(R.drawable.default_picture);
                        StorageReference uidPlace = storage.getReference().child(picID);
                        uidPlace.delete();
                        doesHavePic = false;
                    }
                    return false;
                }
            });
            popupMenu.show();


        }else
        { //no pic so dont pop up menu
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                //allows users to pick a pic from their gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if (intent != null)
                    startActivityForResult(intent, 101);

            } else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        , 102);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (data != null)
        {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101)
        {
            Uri localFileUri = data.getData();

            ivProfile.setImageURI(localFileUri);

            StorageReference uidPlace = storage.getReference().child(picID);
            uploadTask = uidPlace.putFile(localFileUri);
            doesHavePic = true;
        }
    }}

    //pop up requesting permission from the user
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 102)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else
            {
                Toast.makeText(this, permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void updateProfile(String name, String email)
    {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            firebaseUser.updateEmail (email).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ProfileActivity.this,
                                R.string.profile_updated_secussfully,
                                Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong, task.getException()), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        //set users new name
        refName.child(usrID).child("name").setValue(name);
    }
}


