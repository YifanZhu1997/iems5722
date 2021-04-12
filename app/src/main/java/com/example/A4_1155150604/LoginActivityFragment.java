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
    private EditText et_user_name;
    private EditText et_user_id;

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
        et_user_id = view.findViewById(R.id.et_user_id);

        view.findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = et_user_name.getText().toString();
                if (userName.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = et_user_id.getText().toString();
                if (userId.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_id_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                int intUserId = 0;
                try {
                    intUserId = Integer.valueOf(userId).intValue();
                    if (intUserId < 0) {
                        Toast.makeText(getActivity(), "UserID format error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "UserID format error", Toast.LENGTH_SHORT).show();
                    return;
                }

                MainActivityFragment.setLoginInformation(true, userName, intUserId);
                Toast.makeText(getActivity(), "You have successfully logged in as " + userName, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });


    }


}