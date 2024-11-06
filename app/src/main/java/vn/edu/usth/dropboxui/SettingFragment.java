package vn.edu.usth.dropboxui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle dark mode toggle
        });

        return view;
    }
}
