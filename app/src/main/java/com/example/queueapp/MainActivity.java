package com.example.queueapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queueapp.api.ApiClient;
import com.example.queueapp.api.ApiService;
import com.example.queueapp.api.LoginRequest;
import com.example.queueapp.api.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Clear the login status every time the app is launched (restart)
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false); // Set login status to false
        editor.apply();

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Add eye icon toggle functionality
        setupPasswordToggle();

        // Handle login button click
        buttonLogin.setOnClickListener(v -> authenticateUser());
    }

    private void setupPasswordToggle() {
        // Load the drawables
        Drawable eyeOpen = getResources().getDrawable(R.drawable.ic_eye_open, null);
        Drawable eyeClosed = getResources().getDrawable(R.drawable.ic_eye_closed, null);

        // Set a fixed size for the icon in pixels (e.g., 60px by 60px)
        int iconWidth = 35;  // Set your desired width here in pixels
        int iconHeight = 35; // Set your desired height here in pixels

        // Set the bounds of the icons to your desired size
        eyeOpen.setBounds(0, 0, iconWidth, iconHeight);
        eyeClosed.setBounds(0, 0, iconWidth, iconHeight);

        // Set the eye icon initially (closed eye)
        editTextPassword.setCompoundDrawables(null, null, eyeClosed, null);

        // Add a touch listener to handle the eye icon click
        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Get the right drawable (eye icon)
                int drawableRight = 2; // Right drawable index

                // Check if the touch event is on the eye icon
                if (event.getRawX() >= (editTextPassword.getRight() -
                        editTextPassword.getCompoundDrawables()[drawableRight].getBounds().width())) {

                    // Toggle password visibility
                    isPasswordVisible = !isPasswordVisible;
                    editTextPassword.setInputType(isPasswordVisible ?
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    // Change icon based on visibility
                    editTextPassword.setCompoundDrawables(null, null,
                            isPasswordVisible ? eyeOpen : eyeClosed, null);

                    // Keep cursor at the end
                    editTextPassword.setSelection(editTextPassword.getText().length());

                    // Prevent focus change and movement
                    return true;
                }
            }
            return false;
        });
    }



    private void authenticateUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(MainActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (loginResponse.isSuccess()) {
                        String targetPage = loginResponse.getPage();

                        // Navigate to the specified page based on the response
                        Intent intent;
                        switch (targetPage) {
                            case "WindowPage1":
                                intent = new Intent(MainActivity.this, WindowPage1.class);
                                break;
                            case "WindowPage2":
                                intent = new Intent(MainActivity.this, WindowPage2.class);
                                break;
                            case "WindowPage3":
                                intent = new Intent(MainActivity.this, WindowPage3.class);
                                break;
                            case "WindowPage4":
                                intent = new Intent(MainActivity.this, WindowPage4.class);
                                break;
                            default:
                                intent = new Intent(MainActivity.this, WindowPage5.class); // Or handle any unexpected response here
                                break;
                        }

                        startActivity(intent);
                        finish(); // Optional: Prevent user from navigating back to login
                    }
                } else {
                    try {
                        // Parse error message from the server
                        String errorMessage = response.errorBody().string();
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
