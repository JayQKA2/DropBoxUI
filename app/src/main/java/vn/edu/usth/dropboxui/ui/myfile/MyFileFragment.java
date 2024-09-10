// MyFileFragment.java
package vn.edu.usth.dropboxui.ui.myfile;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.usth.dropboxui.R;

public class MyFileFragment extends Fragment {

    private MyFileViewModel myFileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_file, container, false);
        myFileViewModel = new ViewModelProvider(this).get(MyFileViewModel.class);
        myFileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                // Handle the observed data here if needed
            }
        });
        return root;
    }
}