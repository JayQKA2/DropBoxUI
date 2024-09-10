package vn.edu.usth.dropboxui.ui.myfile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyFileViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MyFileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is my file fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
