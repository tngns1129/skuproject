package com.familyset.myapplication.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.familyset.myapplication.model.login.LoggedInUser;
import com.familyset.myapplication.databinding.ActivityLoginBinding;
import com.familyset.myapplication.ui.main.MainActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private LoginViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initView();

        observeViewModel();
    }

    private void initView() {
        binding.login.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            viewModel.login(
                    binding.username.getText().toString(),
                    binding.password.getText().toString()
            );
        });

        binding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.loginDataChanged(
                        binding.username.getText().toString(),
                        binding.password.getText().toString()
                );
            }
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.loginDataChanged(
                        binding.username.getText().toString(),
                        binding.password.getText().toString()
                );
            }
        });
    }

    private void observeViewModel() {
        viewModel.loginResponse.observe(this, loginResponse -> {
            binding.loading.setVisibility(View.GONE);
            if (loginResponse.getLoggedInUser() != null) {
                updateUIWithUser(loginResponse.getLoggedInUser());
            }
            if (loginResponse.getError() != null) {
                showLoginFailed(getString(loginResponse.getError()));
            }
        });

        viewModel.loginFormState.observe(this, loginFormState -> {
            if (loginFormState != null) {
                binding.login.setEnabled(loginFormState.getValid());
            }
        });
    }

    private void updateUIWithUser(LoggedInUser user) {
        Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoginFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}
