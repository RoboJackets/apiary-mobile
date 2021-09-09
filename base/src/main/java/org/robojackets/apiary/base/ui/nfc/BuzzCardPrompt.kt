package org.robojackets.apiary.base.ui.nfc

import android.nfc.NfcAdapter
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.CardType
import com.nxp.nfclib.NxpNfcLib
import com.nxp.nfclib.desfire.DESFireFactory
import com.nxp.nfclib.exceptions.NxpNfcLibException
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.ContactlessIcon
import org.robojackets.apiary.base.ui.icons.CreditCardIcon
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptError.*
import org.robojackets.apiary.base.ui.nfc.BuzzCardTapSource.*
import org.robojackets.apiary.base.ui.theme.danger
import java.nio.charset.StandardCharsets

/**
 * Show a prompt for BuzzCards and call a callback function each time a valid GTID is obtained.
 *
 * When a continuous stream of GTIDs from many taps is desired, do *not* hide the entire BuzzCardPrompt
 * composable.  If you hide the entire composable, then nfcLib.disableReaderMode() is called and
 * the composable has to call nfcLib.enableReaderMode(...) when it is displayed again.
 * enableReaderMode takes a noticeable amount of time from being called to when the device will
 * be able to read BuzzCards again.
 *
 * As an alternative, you can use the hidePrompt parameter to hide the displayed UI elements from
 * this composable, making it hidden whilst also not allowing it to be disposed.
 */
val gtidRegex = Regex("90[0-9]{7}")

@Suppress("MagicNumber", "LongMethod", "ComplexMethod")
@Composable
fun BuzzCardPrompt(
    hidePrompt: Boolean,
    nfcLib: NxpNfcLib,
    onBuzzCardTap: (buzzCardTap: BuzzCardTap) -> Unit,
) {
    val tag = "BuzzCardPrompt"
    var error by remember { mutableStateOf<BuzzCardPromptError?>(null) }
    val nfcPresenceDelayCheckMs = 50 // the minimum number of ms allowed between successive NFC
    // tag reads. Lower is better, but too low seems to cause an increase in NFC read errors when
    // tapping many BuzzCards as quickly as possible
    nfcLib.enableReaderMode(
        nfcPresenceDelayCheckMs,
        {
            val cardType: CardType?
            try {
                cardType = nfcLib.getCardType(it)

                if (cardType == CardType.DESFireEV1) {
                    val desFireEV1 = DESFireFactory.getInstance().getDESFire(nfcLib.customModules)

                    // Below info figured out by :ross: mostly using the NFC TagInfo app
                    val buzzApplication = 0xBBBBCD
                    val buzzFile = 0x01
                    var buzzData = ByteArray(48)
                    val buzzString: String?

                    desFireEV1.selectApplication(buzzApplication)
                    buzzData = desFireEV1.readData(buzzFile, 0, buzzData.size)

                    // a string containing "gtid=proxID", such as "901234567=123456"
                    buzzString = String(buzzData, StandardCharsets.UTF_8)

                    // In some cases (e.g., GTRI badges) the proxID is fewer than 6 characters
                    // Since we don't care about the proxID, the regex just checks
                    // for the GTID and =
                    val buzzStringRegex = Regex("90[0-9]{7}=.*")

                    if (!buzzStringRegex.matches(buzzString)) {
                        error = InvalidBuzzCardData
                        Log.e(tag, "Unexpected BuzzCard buzzString format: $buzzString")
                        return@enableReaderMode
                    }

                    val buzzStringParts = buzzString.split("=").toTypedArray()
                    val gtid = buzzStringParts[0]

                    if (!gtidRegex.matches(gtid)) {
                        error = InvalidBuzzCardData
                        Log.e(tag, "Unexpected BuzzCard GTID format: $gtid")
                    }

                    error = null
                    onBuzzCardTap(BuzzCardTap(gtid.toInt()))
                } else {
                    Log.i(tag, "Unknown card type ($cardType) presented")
                    error = NotABuzzCard
                }
            } catch (e: NxpNfcLibException) {
                Log.w(tag, "NxpNfcLib exception occurred while processing this NFC tag", e)
                error = when (e.localizedMessage) {
                    "Wrong CLA" -> NotABuzzCard
                    "Tag was lost." -> TagLost
                    "Incomplete response" -> TagLost
                    else -> UnknownNfcError
                }
            } catch (e: NumberFormatException) {
                Log.e(
                    tag,
                    "GTID string from (probably a) BuzzCard could not be parsed as an Int",
                    e
                )
                error = InvalidBuzzCardData
            }
        },
        NfcAdapter.FLAG_READER_NFC_A // NFC adapter flags, BuzzCards are Type A according to TagInfo
    )

    // Make sure we stop responding to card taps when this Composable is disposed (no longer on
    // screen)
    DisposableEffect(Unit) {
        onDispose {
            nfcLib.disableReaderMode()
        }
    }

    var showGtidPrompt by remember { mutableStateOf(false) }
    Column {
        if (!hidePrompt) {
            when (error) {
                null -> BuzzCardReadyForTap()
                TagLost -> NfcTagLostError()
                NotABuzzCard -> NfcNotABuzzCardError()
                InvalidBuzzCardData -> NfcInvalidBuzzCardDataError()
                UnknownNfcError -> NfcReadUnknownError()
            }
            Button(
                onClick = { showGtidPrompt = true },
                Modifier.align(CenterHorizontally)
            ) {
                Text("Enter GTID manually")
            }
        }
    }

    if (showGtidPrompt) {
        ManualGtidEntryPrompt(
            onGtidEntered = {
                onBuzzCardTap(it)
                error = null
            },
            onHide = { showGtidPrompt = false },
        )

    }
}

@Composable
fun ManualGtidEntryPrompt(
    onHide: () -> Unit,
    onGtidEntered: (entry: BuzzCardTap) -> Unit,
) {
    var gtid by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {
            gtid = ""
            onHide()
        },
        confirmButton = {
            TextButton(onClick = {
                if (gtidRegex.matches(gtid)) {
                    onGtidEntered(BuzzCardTap(gtid.toInt(), source = Keyboard))
                    onHide()
                    gtid = ""
                }
            }) {
                Text("Submit")
            }
        },
        title = { Text(text = "Manual GTID entry", style = MaterialTheme.typography.h5) },
        text = {
            Column {
                Text("Type the entire 9-digit GTID, starting with 90")
                OutlinedTextField(
                    value = gtid,
                    onValueChange = { enteredGtid ->
                        var newGtid = enteredGtid
                        if (enteredGtid.length > 9) {
                            newGtid = enteredGtid.take(9)
                        }

                        gtid = newGtid.filter {
                            it.isDigit()
                        }
                    },
                    label = { Text("GTID") },
                    singleLine = true,
                    isError = gtid.isNotEmpty() && !gtidRegex.matches(gtid),
                    modifier = Modifier.padding(top = 14.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { CreditCardIcon() }
                )
            }
        }
    )
}

@Composable
fun BuzzCardReadyForTap() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp)) },
        title = "Tap a BuzzCard",
    )
}

@Composable
fun NfcNotABuzzCardError() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
        title = "Card read error",
        subtitle = "If you are tapping a BuzzCard, please hold it against your phone longer, or " +
                "reach out in #it-helpdesk for assistance",
    ) {
        IconWithText(icon = { WarningIcon(tint = danger) }, text = "We only support BuzzCards 😉")
    }
}

@Composable
fun NfcTagLostError() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
        title = "Card read error",
        subtitle = "Hold the BuzzCard up to your phone for a few seconds",
    ) {
        IconWithText(icon = { WarningIcon(tint = danger) }, text = "BuzzCard removed too quickly")
    }
}

@Composable
fun NfcInvalidBuzzCardDataError() {
    ActionPrompt(
        icon = { ErrorIcon(Modifier.size(114.dp), tint = danger) },
        title = "Card read error",
        subtitle = "Try holding the BuzzCard against your phone longer, or reach out in #it-helpdesk for assistance",
    ) {
        IconWithText(
            icon = { WarningIcon(tint = danger) },
            text = "Unexpected BuzzCard data format"
        )
    }
}

@Composable
fun NfcInvalidTypedGtidError() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
        title = "Invalid typed GTID",
    )
}

@Composable
fun NfcReadUnknownError() {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
        title = "Card read error",
        subtitle = "Something went wrong while reading this card. If the problem continues, " +
                "reach out in #it-helpdesk for assistance",
    ) {
        IconWithText(icon = { WarningIcon(tint = danger) }, text = "Unknown NFC read error")
    }
}

// Unused for now but useful once NFC disabled support is added back
// @Composable
// fun NfcUnsupportedError() {
//    ActionPrompt(
//        icon = { ErrorIcon(Modifier.size(114.dp), tint = danger) },
//        title = "NFC is unavailable",
//        subtitle = "Your device cannot read BuzzCards because it does not support NFC",
//    ) {
//        IconWithText(icon = { WarningIcon(tint = danger) }, text = "NFC adapter was null")
//    }
// }
//
// @Composable
// fun NfcDisabledError() {
//    ActionPrompt(
//        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
//        title = "NFC is disabled",
//        subtitle = "",
//    ) {
//        Button(onClick = {
//
//        }) {
//            Text(text = "Enable NFC")
//        }
//    }
// }
