package com.example.A4_1155150604;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LoginActivityFragment extends Fragment {
    private EditText et_user_name;
    private EditText et_user_password;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_user_name = view.findViewById(R.id.et_user_name);
        et_user_password = view.findViewById(R.id.et_user_password);

        view.findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = et_user_name.getText().toString();
                String userPassword = et_user_password.getText().toString();

                et_user_name.setText("");
                et_user_password.setText("");

                if (userName.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userPassword.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_password_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                new LoginTask(userName, userPassword, getContext()).execute("http://3.17.158.90/api/a3/login");
            }
        });

        view.findViewById(R.id.bt_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = et_user_name.getText().toString();
                String userPassword = et_user_password.getText().toString();

                et_user_name.setText("");
                et_user_password.setText("");
                
                if (userName.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userName.length() > 20) {
                    Toast.makeText(getActivity(), R.string.user_name_too_long, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userPassword.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_password_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userPassword.length() > 20) {
                    Toast.makeText(getActivity(), R.string.user_password_too_long, Toast.LENGTH_SHORT).show();
                    return;
                }

                new SignUpTask(userName, userPassword, getContext()).execute("http://3.17.158.90/api/a3/sign_up");
            }
        });

    }


}