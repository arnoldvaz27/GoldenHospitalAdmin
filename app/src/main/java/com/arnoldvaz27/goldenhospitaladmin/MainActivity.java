package com.arnoldvaz27.goldenhospitaladmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private LinearLayout designationLayout,userRegisterLayout,roleLayout;
    private EditText Username, Password;
    private TextView PasswordVisible, role;
    private Button Login, Reset;
    private ProgressBar progressBar,progress;
    private ImageView hidden, visible;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserId;
    private Button Save, ResetRole;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.golden));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        designationLayout = findViewById(R.id.designationLayout);
        userRegisterLayout = findViewById(R.id.userRegisterLayout);
        roleLayout = findViewById(R.id.roleLayout);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        role = findViewById(R.id.gender);
        PasswordVisible = findViewById(R.id.Visible);
        Login = findViewById(R.id.log_in_button);
        Reset = findViewById(R.id.reset_button);
        progressBar = findViewById(R.id.progress_circular);
        hidden = findViewById(R.id.hidden);
        visible = findViewById(R.id.visible);
        Save = findViewById(R.id.save);
        ResetRole = findViewById(R.id.reset);
        progress = findViewById(R.id.progress);
        Username.setSelection(Username.getText().length());
        Username.requestFocus();
        Username.setShowSoftInputOnFocus(true);

        hidden.setOnClickListener(v -> {
            PasswordVisible.setVisibility(View.VISIBLE);
            hidden.setVisibility(View.GONE);
            visible.setVisibility(View.VISIBLE);
            Password.setSelection(Password.getText().length());
            Password.requestFocus();
        });

        visible.setOnClickListener(v -> {
            PasswordVisible.setVisibility(View.GONE);
            hidden.setVisibility(View.VISIBLE);
            visible.setVisibility(View.GONE);
            Password.setSelection(Password.getText().length());
            Password.requestFocus();
        });

        Password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                PasswordVisible.setText(Password.getText().toString());
            }
        });

        findViewById(R.id.genderCategory).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getApplicationContext(), findViewById(R.id.genderCategory));
            popup.getMenuInflater().inflate(R.menu.role, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.doctor) {
                    role.setText("Doctor");
                }
                if (item.getItemId() == R.id.nurse) {
                    role.setText("Nurse");
                }
                if (item.getItemId() == R.id.management) {
                    role.setText("Management");
                }
                return true;
            });

            popup.show();
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role.setText("");
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                Save.setVisibility(View.GONE);
                if(role.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please select the role", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.GONE);
                    Save.setVisibility(View.VISIBLE);
                }else{
                    HashMap<String, Object> profileMap = new HashMap<>();
                    profileMap.put("uid", currentUserId);
                    profileMap.put("designation", role.getText().toString());
                    RootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Role added successfully", Toast.LENGTH_SHORT).show();
                                userRegisterLayout.setVisibility(View.VISIBLE);
                                designationLayout.setVisibility(View.GONE);
                                roleLayout.setVisibility(View.GONE);
                                Username.setText("");
                                Password.setText("");
                                PasswordVisible.setText("");
                            }else{
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                userRegisterLayout.setVisibility(View.GONE);
                                designationLayout.setVisibility(View.VISIBLE);
                                roleLayout.setVisibility(View.VISIBLE);
                            }
                            progress.setVisibility(View.GONE);
                            Save.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Username.setText("");
                Password.setText("");
                PasswordVisible.setText("");
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                String email = Username.getText().toString();
                String password = Password.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Please enter email....", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please enter password....", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                        RootRef.child("Users").child(currentUserId).setValue("");
                                        Toast.makeText(MainActivity.this,
                                                "Account Created Successfully !!",
                                                Toast.LENGTH_SHORT).show();
                                        userRegisterLayout.setVisibility(View.GONE);
                                        designationLayout.setVisibility(View.VISIBLE);
                                        roleLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        String message;
                                        message = Objects.requireNonNull(task.getException()).toString();
                                        Toast.makeText(MainActivity.this,
                                                "Error : " + message,
                                                Toast.LENGTH_SHORT).show();
                                        userRegisterLayout.setVisibility(View.VISIBLE);
                                        designationLayout.setVisibility(View.GONE);
                                        roleLayout.setVisibility(View.GONE);
                                    }
                                    Login.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
    }
}