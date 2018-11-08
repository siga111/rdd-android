package lv.rigadevday.android.repository.model.lottery

data class ParticipantData(
    val email: String,
    val id: String,
    val partnerLogos: List<PartnerStatus>
)