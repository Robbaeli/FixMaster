class ReportViewModel : ViewModel() {

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

            _state.value = ReportState.Loading

            try {

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
}