package com.example.doccur.repositories



import android.util.Log
import com.example.doccur.api.ApiService
import com.example.doccur.entities.AppointmentResponse

class PatientAppointmentRepository(private val apiService: ApiService) {

   /* suspend fun getAppointmentsForPatient(patientId: Int): List<AppointmentResponse> {
        Log.d("Repository", "Calling API for patientId = $patientId")
        return apiService.getAppointmentsByPatient(patientId)
    }*/
   suspend fun getFullAppointmentsForPatient(patientId: Int): List<AppointmentResponse> {
       Log.d("Repository", "Calling API for patientId = $patientId")

       val response = apiService.getAppointmentsByPatient(patientId)

       // Log each appointment in a readable format
       response.forEachIndexed { index, appointment ->
           Log.d("Repository", "Appointment #$index: $appointment")
       }

       return response
   }

}
