package lv.rigadevday.android.repository.model.lottery

sealed class LotteryState {

    object NotLoggedIn: LotteryState()

    data class Partner(
        val emails: List<ParticipantEmail>
    ): LotteryState()

    data class Participant(
        val email: String,
        val id: String,
        val partnerLogos: List<PartnerStatus>
    ): LotteryState()

}