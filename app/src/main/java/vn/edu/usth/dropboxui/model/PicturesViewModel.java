package vn.edu.usth.dropboxui.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import vn.edu.usth.dropboxui.model.PictureModel;
import vn.edu.usth.dropboxui.ApiConfig;

public class PicturesViewModel extends ViewModel {
    private MutableLiveData<ArrayList<PictureModel>> picturesLiveData;

    public PicturesViewModel() {
        picturesLiveData = new MutableLiveData<>();
    }

    public LiveData<ArrayList<PictureModel>> getPicturesLiveData() {
        return picturesLiveData;
    }

    public void fetchPictures(String accessToken) {
        // Simulate fetching pictures from an API
        ArrayList<PictureModel> pictures = new ArrayList<>();
        // Add your logic to fetch pictures from Dropbox API using accessToken
        // For example:
        // pictures.add(new PictureModel("https://example.com/image1.jpg"));
        // pictures.add(new PictureModel("https://example.com/image2.jpg"));
        picturesLiveData.postValue(pictures);
    }
}