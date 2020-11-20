package com.wile.app.ui.social

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.wile.app.base.LiveCoroutinesViewModel
import com.wile.database.model.Training
import com.wile.training.TrainingRepository
import org.hashids.Hashids
import java.time.Instant

class JoinViewModel @ViewModelInject constructor(
        private val trainingRepository: TrainingRepository,
        @Assisted private val savedStateHandle: SavedStateHandle
) : LiveCoroutinesViewModel() {

    val trainingListLiveData: MutableLiveData<List<Training>> = MutableLiveData()
    val trainingDurationLiveData: MutableLiveData<Int> = MutableLiveData(0)
    val roomName: MutableLiveData<String> = MutableLiveData("")
    val userName: MutableLiveData<String> = MutableLiveData("")

    init {
        userName.value = getRotatedUserName()
        val hashids = Hashids(userName.value)
        roomName.value = hashids.encode(Instant.now().toEpochMilli()/1000)
    }

    private fun getRotatedUserName(): String {
        val fakeUserNames = listOf<String>(
                "RapidPanda",
                "RunningFrog",
                "BuffyPuppy",
                "EnergeticLama",
                "JumpyFox",
                "StretchyCat"
        )
        return fakeUserNames.random()
    }
}