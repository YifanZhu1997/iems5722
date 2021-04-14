package com.example.A4_1155150604;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class SignUpActivityFragment extends Fragment {
    private EditText et_user_id;
    private EditText et_user_password;
    private EditText et_user_name;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_user_id = view.findViewById(R.id.et_user_id);
        et_user_password = view.findViewById(R.id.et_user_password);
        et_user_name = view.findViewById(R.id.et_user_name);


        view.findViewById(R.id.bt_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = et_user_id.getText().toString();
                String userPassword = et_user_password.getText().toString();
                String userName = et_user_name.getText().toString();

                et_user_id.setText("");
                et_user_password.setText("");
                et_user_name.setText("");

                if (userId.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_id_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                int userIdSize = userId.length();
                if (userIdSize < 4 || userIdSize > 9) {
                    Toast.makeText(getActivity(), R.string.user_id_length_hint, Toast.LENGTH_SHORT).show();
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

                int userPasswordSize = userPassword.length();
                if (userPasswordSize < 4 || userPasswordSize > 20) {
                    Toast.makeText(getActivity(), R.string.user_password_length_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (userName.equals("")) {
                    Toast.makeText(getActivity(), R.string.user_name_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                int userNameSize = userName.length();
                if (userNameSize < 4 || userNameSize > 20) {
                    Toast.makeText(getActivity(), R.string.user_name_length_hint, Toast.LENGTH_SHORT).show();
                    return;
                }


                new SignUpTask(intUserId, userPassword, userName, getContext()).execute("http://3.17.158.90/api/a3/sign_up");
            }
        });

    }


}