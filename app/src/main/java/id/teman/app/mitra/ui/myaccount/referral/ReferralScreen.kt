package id.teman.app.mitra.ui.myaccount.referral

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage
import id.teman.app.mitra.R
import id.teman.app.mitra.common.TopBarReferral
import id.teman.app.mitra.ui.destinations.ListHistoryReferralScreenDestination
import id.teman.app.mitra.ui.theme.Theme
import id.teman.coreui.typography.UiColor
import id.teman.coreui.typography.UiFont

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Destination
@Composable
fun ReferralScreen(
    navigator: DestinationsNavigator,
    viewModel: ReferralViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val codeReferral = viewModel.getUserProfile()?.referralCode.orEmpty()
    LaunchedEffect(key1 = uiState.successReferralCode, block = {
        uiState.successReferralCode?.consumeOnce {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    it
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    })
    Scaffold(
        topBar = {
            TopBarReferral(
                title = "Referral", onHistoryClick = {
                    navigator.navigate(ListHistoryReferralScreenDestination)
                }, onBackClick = {
                    navigator.popBackStack()
                })
        }, content = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GlideImage(
                    imageModel = R.drawable.referral,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(237.dp),
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                )
                Text(
                    modifier = Modifier.padding(
                        top = Theme.dimension.size_40dp,
                        start = Theme.dimension.size_36dp,
                        end = Theme.dimension.size_36dp
                    ),
                    text = "Ajak teman dan raih hadiah menarik! \uD83C\uDF81 ",
                    style = UiFont.poppinsH3Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.padding(
                        horizontal = Theme.dimension.size_36dp,
                        vertical = Theme.dimension.size_16dp
                    ),
                    text = "Semakin banyak yang bergabung dengan kode referalmu, semakin besar hadiah yang bisa kamu dapatkan. Bagikan kode referalmu sekarang!",
                    style = UiFont.poppinsP3Medium,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = codeReferral,
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(
                                    topStart = Theme.dimension.size_12dp,
                                    bottomStart = Theme.dimension.size_12dp
                                ), color = UiColor.neutralGray0
                            )
                            .padding(
                                top = Theme.dimension.size_12dp,
                                start = Theme.dimension.size_24dp,
                                end = Theme.dimension.size_28dp,
                                bottom = Theme.dimension.size_12dp
                            ),
                        style = UiFont.poppinsP2SemiBold
                    )
                    Text(
                        text = "Share Kode",
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(
                                    topEnd = Theme.dimension.size_12dp,
                                    bottomEnd = Theme.dimension.size_12dp
                                ), color = UiColor.primaryRed500
                            )
                            .padding(
                                top = Theme.dimension.size_12dp,
                                bottom = Theme.dimension.size_12dp,
                                end = Theme.dimension.size_8dp,
                                start = Theme.dimension.size_8dp
                            )
                            .clickable {
                                viewModel.generateDynamicLink()
                            },
                        style = UiFont.poppinsP2SemiBold,
                        textAlign = TextAlign.End,
                        color = UiColor.white
                    )
                }
            }
        }
    )
}