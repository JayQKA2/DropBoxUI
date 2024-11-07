package vn.edu.usth.dropboxui.ui.user;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import vn.edu.usth.dropboxui.R;


public class SettingFragment extends Fragment {

    private SwitchCompat switchNotifications;
    private SwitchCompat switchDarkMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Initialize the switches
        switchNotifications = view.findViewById(R.id.switch_notifications);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode); // Initialize dark mode switch

        // Set listeners for switches
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle notification toggle
            Toast.makeText(getContext(), "Notification enabled", Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle dark mode toggle
            Toast.makeText(getContext(), "Dark Mode enabled", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
