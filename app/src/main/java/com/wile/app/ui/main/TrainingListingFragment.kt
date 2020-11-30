package com.wile.app.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.wile.app.R
import com.wile.app.base.DataBindingFragment
import com.wile.app.databinding.FragmentTrainingListBinding
import com.wile.core.extensions.exhaustive
import com.wile.app.ui.adapter.TrainingAdapter
import com.wile.app.ui.add.AddActivity
import com.wile.app.ui.add.TabataAddActivity
import com.wile.database.model.Training
import com.wile.database.model.TrainingTypes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingListingFragment : DataBindingFragment() {

    private val viewModel: TrainingListingViewModel by viewModels()
    private val binding: FragmentTrainingListBinding by binding(R.layout.fragment_training_list)
    private val adapter by lazy { TrainingAdapter(
        onDeleteTraining = ::onDeleteTraining,
        onMoveTraining =  ::onMoveTraining,
        onTouchTraining = ::onTouchTraining
    )}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.apply {
            lifecycleOwner = this@TrainingListingFragment
            adapter = this@TrainingListingFragment.adapter
            viewModel = this@TrainingListingFragment.viewModel
        }
        return binding.root
    }

    private fun onDeleteTraining(training: Training) {
        viewModel.deleteTraining(training.id)
    }

    private fun onMoveTraining(trainings: List<Training>) {
        viewModel.saveTrainings(trainings)
    }

    private fun onTouchTraining(training: Training) {
        when (training.trainingType) {
            TrainingTypes.Tabata -> startActivity(TabataAddActivity.editTabata(requireContext(), training.id))
            else -> startActivity(AddActivity.editTraining(requireContext(), training.id))
        }.exhaustive
    }
    
    companion object {
        const val WORKOUT_ID = "workout_id"

        fun newFragment(workoutId: Int) = TrainingListingFragment().apply {
            arguments = Bundle().apply {
                putInt(WORKOUT_ID, workoutId)
            }
        }
    }
}
