package org.robojackets.apiary.attendance

import android.nfc.NfcAdapter
import android.util.Log
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.core.text.trimmedLength
import com.nxp.nfclib.NxpNfcLib
import com.nxp.nfclib.desfire.DESFireFactory
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.robojackets.apiary.attendance.model.AttendanceState
import org.robojackets.apiary.attendance.model.AttendanceViewModel
import org.robojackets.apiary.base.CardScanEvent
import java.nio.charset.StandardCharsets

@Composable
private fun Attendance(
    @Suppress("UnusedPrivateMember") viewState: AttendanceState,
    latestCardScan: CardScanEvent?,
) {
    if (latestCardScan?.gtid?.trimmedLength() == 9) {
        Text("Attendance: 9xxxxx${latestCardScan.gtid.substring(6)} at ${latestCardScan.timestamp}")
    } else {
        Text("No card scanned yet")
    }

}

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    cardScanFlow: StateFlow<CardScanEvent?>,
    nfcLib: NxpNfcLib?
) {
    val state by viewModel.state.collectAsState()

    val cardScanState by cardScanFlow.collectAsState()

    LaunchedEffect(cardScanState) {
        snapshotFlow { cardScanState }
            .distinctUntilChanged()
            .collect {
                Log.d("AttendanceScreen", it?.gtid ?: "null event")
                Log.d("AttendanceScreen", "-----")
            }
    }


    nfcLib?.enableReaderMode(100, {
        Log.d("AttendanceScreen", nfcLib?.getCardType(it).toString())

        val desFireEV1 = DESFireFactory.getInstance().getDESFire(nfcLib?.customModules)
        val buzz_application = 0xBBBBCD
        val buzz_file = 0x01
        var buzzData = ByteArray(48)
        var buzzString: String? = null

        desFireEV1.selectApplication(buzz_application)
        buzzData = desFireEV1.readData(buzz_file, 0, buzzData.size)
        buzzString = String(buzzData, StandardCharsets.UTF_8)
        val buzzStringParts = buzzString.split("=").toTypedArray()
        val gtid = buzzStringParts[0]

        Log.d("AttendanceScreen", "gtid: $gtid")

    },
        NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_V or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_NFC_F)

//    NfcAdapter.getDefaultAdapter(LocalContext.current)
//        ?.enableReaderMode(LocalContext.current as Activity, {
//            Log.d("AttendanceScreen", nfcLib?.getCardType(it).toString())
//        }, NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null)
    Attendance(state, cardScanState)
}
