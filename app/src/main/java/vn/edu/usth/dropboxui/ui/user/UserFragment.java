package vn.edu.usth.dropboxui.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import vn.edu.usth.dropboxui.LoginActivity;
import vn.edu.usth.dropboxui.databinding.FragmentUserBinding;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private TextView name, email;
    private GoogleSignInClient gsc;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment using view binding
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the TextViews using binding
        name = binding.userName;
        email = binding.userEmail;

        // Initialize GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        // Retrieve the signed-in Google account
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText(personName);
            email.setText(personEmail);
        }

        // Set up sign-out button click listener (if you have one in your layout)
        binding.signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return root;
    }

    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Close the activity and redirect to LoginActivity
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // To avoid memory leaks
    }
}
