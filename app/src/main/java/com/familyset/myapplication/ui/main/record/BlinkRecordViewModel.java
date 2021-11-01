package com.familyset.myapplication.ui.main.record;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.familyset.myapplication.data.repo.PersonalInfoRepository;
import com.familyset.myapplication.data.repo.UsersRepository;
import com.familyset.myapplication.model.blink.PersonalInfo;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class BlinkRecordViewModel extends ViewModel {

    private UsersRepository usersRepository;

    private PersonalInfoRepository personalInfoRepository;

    private MutableLiveData<List<PersonalInfo>> _items = new MutableLiveData<>();
    public LiveData<List<PersonalInfo>> items = _items;

    private MutableLiveData<Boolean> _dataLoading = new MutableLiveData<>();
    public LiveData<Boolean> dataLoading = _dataLoading;

    @Inject
    public BlinkRecordViewModel(UsersRepository usersRepository, PersonalInfoRepository personalInfoRepository) {
        this.usersRepository = usersRepository;
        this.personalInfoRepository = personalInfoRepository;
    }

    public void loadPersonalInfos() {
        personalInfoRepository.getPersonalInfos(usersRepository.getUser().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        //on success
                        personalInfos -> {
                            //Log.d("CHCH", String.valueOf(personalInfos.size()));

                            _items.setValue(personalInfos);
                        },
                        //on error
                        error -> {
                            Log.d("BlinkRecordVM", "Subscribe err");
                        }
                );
    }
}
