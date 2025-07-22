package com.example.jetsetgo.worker
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.jetsetgo.repository.StepRepository
import java.time.LocalDate

class MidnightResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val stepRepository = StepRepository(context)
        val today = LocalDate.now().toString()

        val currentTotalSteps = stepRepository.getLastSensorTotalSteps()

        stepRepository.saveBaseStepForDate(today, currentTotalSteps)
        stepRepository.saveStepsForDate(today, 0)

        return Result.success()
    }
}
