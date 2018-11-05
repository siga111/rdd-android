package lv.rigadevday.android.ui.schedule.sessions

import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import lv.rigadevday.android.R
import lv.rigadevday.android.repository.model.schedule.Session
import lv.rigadevday.android.ui.base.BaseActivity
import lv.rigadevday.android.ui.openSessionDetailsActivity
import lv.rigadevday.android.ui.openSpeakerActivity
import lv.rigadevday.android.utils.BaseApp
import lv.rigadevday.android.utils.bindSchedulers
import lv.rigadevday.android.utils.showMessage

class SessionsActivity : BaseActivity() {

    override val layoutId = R.layout.fragment_list

    private val listAdapter: SessionsAdapter = SessionsAdapter(object : SessionContract {
        override fun openSession(id: Int) {
            openSessionDetailsActivity(id)
        }

        override fun openSpeaker(id: Int) {
            openSpeakerActivity(id)
        }
    })

    private var cachedSessions: List<Session>? = null

    override fun inject() {
        BaseApp.graph.inject(this)
    }

    override fun viewReady() {
        setupActionBar(getString(R.string.schedule_filter_bookmarked))
        homeAsUp()

        list_fragment_recycler.run {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        dataFetchSubscription = repo.sessions()
            .filter { it.speakers.isNotEmpty() }
            .toList()
            .subscribe(
                { sessions ->
                    cachedSessions = sessions
                    filterBookmarked()
                },
                { list_fragment_recycler.showMessage(R.string.error_message) }
            )
    }

    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }

    private fun filterBookmarked() {
        dataFetchSubscription = repo.bookmarkedIds()
            .bindSchedulers()
            .subscribe(
                { bookmarkedIds ->
                    cachedSessions?.let { sessions ->
                        listAdapter.data = sessions.filter { bookmarkedIds.contains(it.id.toString()) }
                    }
                },
                { cachedSessions?.let { listAdapter.data = emptyList() } }
            )
    }
}

interface SessionContract {
    fun openSession(id: Int)
    fun openSpeaker(id: Int)
}
