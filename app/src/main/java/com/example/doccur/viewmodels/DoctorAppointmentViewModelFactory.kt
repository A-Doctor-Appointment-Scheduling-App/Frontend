import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.doccur.repositories.DoctorAppointmentRepository
import com.example.doccur.viewmodels.DoctorAppointmentViewModel

class DoctorAppointmentViewModelFactory(
    private val repository: DoctorAppointmentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorAppointmentViewModel::class.java)) {
            return DoctorAppointmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
