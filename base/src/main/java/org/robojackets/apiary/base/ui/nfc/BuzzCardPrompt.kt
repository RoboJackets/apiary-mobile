package org.robojackets.apiary.base.ui.nfc

import android.nfc.NfcAdapter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.nxp.nfclib.NxpNfcLib
import com.nxp.nfclib.desfire.DESFireFactory
import java.nio.charset.StandardCharsets

@Composable
fun BuzzCardPrompt(
    nfcLib: NxpNfcLib,
    onGtidAvailable: (gtid: String) -> Unit,
) {
    nfcLib.enableReaderMode(
        100, {
            Log.d("AttendanceScreen", nfcLib.getCardType(it).toString())

            val desFireEV1 = DESFireFactory.getInstance().getDESFire(nfcLib.customModules)
            val buzz_application = 0xBBBBCD
            val buzz_file = 0x01
            var buzzData = ByteArray(48)
            val buzzString: String?

            desFireEV1.selectApplication(buzz_application)
            buzzData = desFireEV1.readData(buzz_file, 0, buzzData.size)
            buzzString = String(buzzData, StandardCharsets.UTF_8)
            val buzzStringParts = buzzString.split("=").toTypedArray()
            val gtid = buzzStringParts[0]

            Log.d("BuzzCardPrompt", "gtid: $gtid")
            onGtidAvailable(gtid)

        },
        NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_V or NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_NFC_F
    )

    // Make sure we stop responded to card taps when this Composable is disposed (no longer on
    // screen)
    DisposableEffect(Unit) {
        onDispose {
            nfcLib.disableReaderMode()
        }
    }

}