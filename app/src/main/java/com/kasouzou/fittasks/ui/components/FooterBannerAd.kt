package com.kasouzou.fittasks.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.kasouzou.fittasks.BuildConfig

@Composable
fun FooterBannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = BuildConfig.ADMOB_BANNER_AD_UNIT_ID
) {
    if (adUnitId.isBlank()) {
        return
    }

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val adSize = remember(configuration) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context,
            configuration.screenWidthDp
        )
    }
    val adView = remember {
        AdView(context).apply {
            this.adUnitId = adUnitId
        }
    }

    LaunchedEffect(adSize) {
        adView.setAdSize(adSize)
        adView.loadAd(AdRequest.Builder().build())
    }

    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }

    AndroidView(
        factory = { adView },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}
