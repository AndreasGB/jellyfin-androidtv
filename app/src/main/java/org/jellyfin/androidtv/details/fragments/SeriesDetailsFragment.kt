package org.jellyfin.androidtv.details.fragments

import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jellyfin.androidtv.details.DetailsOverviewRow
import org.jellyfin.androidtv.details.actions.DeleteAction
import org.jellyfin.androidtv.details.actions.SecondariesPopupAction
import org.jellyfin.androidtv.details.actions.ToggleFavoriteAction
import org.jellyfin.androidtv.model.itemtypes.Series


class SeriesDetailsFragment(private val series: Series) : BaseDetailsFragment<Series>(series) {
	// Action definitions
	private val actions by lazy {
		val item = MutableLiveData(series)

		listOf(
			ToggleFavoriteAction(context!!, item),

			// "More" button
			SecondariesPopupAction(context!!, listOfNotNull(
				DeleteAction(context!!, item) { activity?.finish() }
			))
		)
	}

	// Row definitions
	private val detailRow by lazy {
		val primary = series.images.primary
		val backdrops = series.images.backdrops
		DetailsOverviewRow(series, actions, primary, backdrops)
	}

	override suspend fun onCreateAdapters(rowSelector: ClassPresenterSelector, rowAdapter: ArrayObjectAdapter) {
		super.onCreateAdapters(rowSelector, rowAdapter)

		loadAdditionalInformation()

		// Add rows
		rowAdapter.apply {
			add(detailRow)
		}
	}

	private suspend fun loadAdditionalInformation() = withContext(Dispatchers.IO) {
		// Get additional information asynchronously
		awaitAll(
			async {

			}
		)
	}
}
