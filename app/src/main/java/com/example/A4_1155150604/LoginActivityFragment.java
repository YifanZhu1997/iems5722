package com.example.A4_1155150604;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class LoginActivityFragment extends Fragment {
    private EditText et_user_id;
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

        et_user_id = view.findViewById(R.id.et_user_id);
        et_user_password = view.findViewById(R.id.et_user_password);

        view.findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = et_user_id.getText().toString();
                String userPassword = et_user_password.getText().toString();

                et_user_id.setText("");
                et_user_password.setText("");

                if (userId.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_id_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                int intUserId = 0;
                try {
                    intUserId = Integer.valueOf(userId).intValue();
                    if (intUserId < 0) {
                        Toast.makeText(getActivity(), "User ID format error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "User ID format error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userPassword.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_password_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                new LoginTask(intUserId, userPassword, getContext()).execute("http://3.17.158.90/api/a3/login");
            }
        });

        view.findViewById(R.id.bt_sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent);
            }
        });

    }


}