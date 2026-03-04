package com.ma25.fixmaster.ui

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ma25.fixmaster.model.IssueReport
import com.ma25.fixmaster.model.Priority
import com.ma25.fixmaster.repository.ObjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ReportViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = ObjectRepository()
    private val storage = FirebaseStorage.getInstance()

    private val _state = MutableStateFlow<ReportState>(ReportState.Idle)
    val state: StateFlow<ReportState> = _state

    fun submitReport(
        objectId: String,
        objectName: String,
        faultType: String,
        createdBy: String,
        priority: Priority,
        imageUri: Uri?,
        comment: String?
    ) {
        viewModelScope.launch {
            try {
                val online = isOnline()

                // ✅ إذا Offline: أعطِ رسالة واضحة للمستخدم ولا تعمل Loading طويل
                if (!online) {
                    // ممنوع رفع صورة Offline
                    if (imageUri != null) {
                        _state.value = ReportState.Error(
                            "Du är offline. Bild kan inte laddas upp utan internet. Skicka utan bild."
                        )
                        return@launch
                    }

                    // رسالة Offline (ليست خطأ فعليًا، لكنها تُستخدم لعرض Toast بسهولة)
                    _state.value = ReportState.Error(
                        "Du är offline. Ärendet sparas lokalt och synkas när du är online."
                    )

                    // أنشئ report بدون صورة (Firestore queue)
                    val newReport = IssueReport(
                        objectId = objectId,
                        objectName = objectName,
                        qrCode = objectId,
                        description = faultType,
                        status = "Ny",
                        imageUrl = null,
                        createdBy = createdBy,
                        priority = priority.name,
                        comment = comment
                    )

                    // مهم: addReport يجب أن لا يستخدم await في Firestore أثناء Offline
                    repository.addReport(newReport)

                    // بعد عرض الرسالة، روح للنجاح
                    _state.value = ReportState.Success
                    return@launch
                }

                // ✅ Online: هنا نستخدم Loading ونرفع الصورة لو موجودة
                _state.value = ReportState.Loading
                delay(200) // اختياري

                var downloadUrl: String? = null
                if (imageUri != null) {
                    val fileName = "reports/${UUID.randomUUID()}.jpg"
                    val ref = storage.reference.child(fileName)
                    ref.putFile(imageUri).await()
                    downloadUrl = ref.downloadUrl.await().toString()
                }

                val newReport = IssueReport(
                    objectId = objectId,
                    objectName = objectName,
                    qrCode = objectId,
                    description = faultType,
                    status = "Ny",
                    imageUrl = downloadUrl,
                    createdBy = createdBy,
                    priority = priority.name,
                    comment = comment
                )

                repository.addReport(newReport)

                _state.value = ReportState.Success
            } catch (e: Exception) {
                _state.value = ReportState.Error("Fel: ${e.message}")
            }
        }
    }

    private fun isOnline(): Boolean {
        val cm = getApplication<Application>()
            .getSystemService(ConnectivityManager::class.java)

        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}