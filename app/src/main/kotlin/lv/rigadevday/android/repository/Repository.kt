package lv.rigadevday.android.repository

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function5
import io.reactivex.subjects.PublishSubject
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.Root
import lv.rigadevday.android.repository.model.lottery.*
import lv.rigadevday.android.repository.model.other.Venue
import lv.rigadevday.android.repository.model.partners.Partners
import lv.rigadevday.android.repository.model.schedule.Rating
import lv.rigadevday.android.repository.model.schedule.Schedule
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.repository.model.schedule.Timeslot
import lv.rigadevday.android.repository.model.speakers.Speaker
import lv.rigadevday.android.utils.auth.AuthStorage
import lv.rigadevday.android.utils.bindSchedulers
import lv.rigadevday.android.utils.showMessage
import java.util.concurrent.TimeUnit

/**
 * All of the observables provided by repository are non-closable so it is mandatory
 * to unsubscribe any subscription when closing screen to prevent memory leak.
 */
class Repository(
    private val context: Context,
    private val authStorage: AuthStorage,
    private val dataCache: DataCache
) {

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance("https://devfest-2018.firebaseio.com/").reference.apply { keepSynced(true) }
    }

    val cacheUpdated: PublishSubject<Boolean> by lazy {
        PublishSubject.create<Boolean>().also { sub ->
            Flowable.combineLatest(
                RxFirebaseDatabase.observeValueEvent(database.child("partners"), DataSnapshotMapper.listOf(Partners::class.java)),
                RxFirebaseDatabase.observeValueEvent(database.child("venues"), DataSnapshotMapper.listOf(Venue::class.java)),
                RxFirebaseDatabase.observeValueEvent(database.child("speakers"), DataSnapshotMapper.listOf(Speaker::class.java)),
                RxFirebaseDatabase.observeValueEvent(database.child("schedule"), DataSnapshotMapper.listOf(Schedule::class.java)),
                RxFirebaseDatabase.observeValueEvent(database.child("sessions"), DataSnapshotMapper.mapOf(Session::class.java)),
                Function5 { _: List<Partners>, _: List<Venue>, _: List<Speaker>, _: List<Schedule>, _: Map<String, Session> -> }
            )
                .debounce(1, TimeUnit.SECONDS)
                .skip(1)
                .flatMapSingle { updateCache() }
                .subscribe {
                    sub.onNext(true)
                    context.showMessage(R.string.data_updated)
                }
        }
    }

    private fun getCache(predicate: () -> Boolean): Single<DataCache> =
        if (predicate()) Single.just(dataCache)
        else updateCache()

    // Basic requests
    fun updateCache(): Single<DataCache> = RxFirebaseDatabase
        .observeSingleValueEvent(database, Root::class.java)
        .map { dataCache.update(it) }
        .toSingle()

    fun speakers(): Flowable<Speaker> = getCache { dataCache.speakers.isNotEmpty() }
        .flattenAsFlowable { it.speakers.values }
        .bindSchedulers()

    fun speaker(id: Int): Single<Speaker> = getCache { dataCache.speakers.isNotEmpty() }
        .map { it.speakers.getValue(id) }
        .bindSchedulers()

    fun schedule(): Flowable<Schedule> = getCache { dataCache.schedule.isNotEmpty() }
        .flattenAsFlowable { it.schedule.values }
        .bindSchedulers()

    fun partners(): Flowable<Partners> = getCache { dataCache.partners.isNotEmpty() }
        .flattenAsFlowable { it.partners }
        .bindSchedulers()

    fun venues(): Flowable<Venue> = getCache { dataCache.venues.isNotEmpty() }
        .flattenAsFlowable { it.venues }
        .bindSchedulers()

    fun venue(id: Int): Single<Venue> = getCache { dataCache.venues.isNotEmpty() }
        .map { it.venues[id] }
        .bindSchedulers()

    fun sessions(): Flowable<Session> = getCache { dataCache.sessions.isNotEmpty() }
        .flattenAsFlowable { it.sessions.values }
        .bindSchedulers()

    fun session(id: Int): Single<Session> = getCache { dataCache.sessions.isNotEmpty() }
        .map { it.sessions.getValue(id) }
        .bindSchedulers()

    fun scheduleDayTimeslots(dateCode: String): Flowable<Timeslot> = getCache { dataCache.schedule.isNotEmpty() }
        .flattenAsFlowable { it.schedule.getValue(dateCode).timeslots }
        .bindSchedulers()

    // Read-Write stuff

    // Ratings
    private fun sessionRating() = database.child("userFeedbacks").child(authStorage.uId)

    fun rating(sessionId: Int): Single<Rating> = if (authStorage.hasLogin) {
        RxFirebaseDatabase.observeSingleValueEvent(
            sessionRating().child(sessionId.toString()),
            Rating::class.java
        ).toSingle(Rating()).onErrorResumeNext(Single.just(Rating()))
    } else Single.just(Rating())

    fun saveRating(sessionId: Int, rating: Rating) {
        if (authStorage.hasLogin) {
            sessionRating().child(sessionId.toString()).setValue(rating)
        }
    }

    // Bookmarks
    private fun bookmarkedSessions() = database.child("userSessions").child(authStorage.uId)

    fun bookmarkedIds(): Single<List<String>> = if (authStorage.hasLogin) {
        RxFirebaseDatabase.observeSingleValueEvent(
            bookmarkedSessions(),
            DataSnapshotMapper.mapOf(Boolean::class.java)
        ).map { it.keys.toList() }.toSingle(emptyList())
    } else {
        Single.just(emptyList())
    }

    fun isSessionBookmarked(sessionId: Int): Single<Boolean> = bookmarkedIds()
        .map { it.contains(sessionId.toString()) }

    fun bookmarkSession(sessionId: Int) {
        if (authStorage.hasLogin) {
            bookmarkedSessions().child(sessionId.toString()).setValue(true)
        }
    }

    fun removeBookmark(sessionId: Int) {
        if (authStorage.hasLogin) {
            bookmarkedSessions().child(sessionId.toString()).removeValue()
        }
    }

    // Lottery
    private fun lotteryPartners() = database.child("lotteryPartners")

    private fun lotteryRef() = database.child("lottery")

    fun getLotteryState(): Single<out LotteryState> =
        if (!authStorage.hasLogin) Single.just(LotteryState.NotLoggedIn)
        else {
            RxFirebaseDatabase.observeSingleValueEvent(
                lotteryPartners(),
                DataSnapshotMapper.listOf(LotteryPartner::class.java)
            ).map { partners ->
                if (partners.map { it.id }.contains(authStorage.uId)) LotteryState.Partner
                else LotteryState.Participant
            }.toSingle(LotteryState.NotLoggedIn)
        }.bindSchedulers()

    fun lotteryParticipantEmails(): Flowable<List<ParticipantEmail>> = if (authStorage.hasLogin) {
        RxFirebaseDatabase.observeValueEvent(
            lotteryRef().child(authStorage.uId),
            DataSnapshotMapper.mapOf(String::class.java)
        ).map { it.map { (id, email) -> ParticipantEmail(id, email) } }
    } else {
        Flowable.just(emptyList())
    }.bindSchedulers()

    fun deleteParticipantEmail(item: ParticipantEmail) {
        if (authStorage.hasLogin) {
            lotteryRef().child(authStorage.uId).child(item.id).removeValue()
        }
    }

    fun saveParticipantEmail(item: ParticipantEmail) {
        if (authStorage.hasLogin) {
            lotteryRef().child(authStorage.uId).child(item.id).setValue(item.email)
        }
    }

    fun lotteryPartnerStatuses(): Flowable<List<PartnerStatus>> = if (authStorage.hasLogin) {
        val userUid = authStorage.uId
        RxFirebaseDatabase.observeValueEvent(
            lotteryPartners(),
            DataSnapshotMapper.listOf(LotteryPartner::class.java)
        ).flatMap { partners ->
            Flowable.combineLatest<PartnerStatus, List<PartnerStatus>>(
                partners.map { partner ->
                    RxFirebaseDatabase.observeValueEvent(lotteryRef().child(partner.id).child(userUid)).map {
                        PartnerStatus(partner, (it.getValue(String::class.java) != null))
                    }
                }
            ) { status: Array<in PartnerStatus> ->
                status.map { it as PartnerStatus }
            }
        }
    } else {
        Flowable.empty<List<PartnerStatus>>()
    }.bindSchedulers()
}
