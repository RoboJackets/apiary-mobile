package org.robojackets.apiary.base.ui.nfc

import android.nfc.NfcAdapter
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nxp.nfclib.CardType
import com.nxp.nfclib.NxpNfcLib
import com.nxp.nfclib.desfire.DESFireFactory
import com.nxp.nfclib.exceptions.NxpNfcLibException
import kotlinx.coroutines.android.awaitFrame
import org.robojackets.apiary.base.ui.ActionPrompt
import org.robojackets.apiary.base.ui.IconWithText
import org.robojackets.apiary.base.ui.icons.ContactlessIcon
import org.robojackets.apiary.base.ui.icons.CreditCardIcon
import org.robojackets.apiary.base.ui.icons.ErrorIcon
import org.robojackets.apiary.base.ui.icons.WarningIcon
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptError.InvalidBuzzCardData
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptError.NotABuzzCard
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptError.TagLost
import org.robojackets.apiary.base.ui.nfc.BuzzCardPromptError.UnknownNfcError
import org.robojackets.apiary.base.ui.nfc.BuzzCardTapSource.Keyboard
import org.robojackets.apiary.base.ui.theme.danger
import timber.log.Timber
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
val GTID_REGEX = Regex("90[0-9]{7}")
const val GTID_LENGTH = 9

@Suppress("MagicNumber", "LongMethod", "ComplexMethod")
@Composable
fun BuzzCardPrompt(
    hidePrompt: Boolean,
    nfcLib: NxpNfcLib,
    onBuzzCardTap: (buzzCardTap: BuzzCardTap) -> Unit,
    externalError: BuzzCardPromptExternalError?,
) {
    var error by remember { mutableStateOf<BuzzCardPromptError?>(null) }
    var lastTap by remember { mutableStateOf<BuzzCardTap?>(null) }
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
                    // for the GTID and (= or 0).
                    // - Some newer BuzzCards (after Nov 2022) use a 0 to separate the GTID and prox
                    // ID
                    val buzzStringRegex = Regex("90[0-9]{7}[=0].*")

                    if (!buzzStringRegex.matches(buzzString)) {
                        error = InvalidBuzzCardData
                        Timber.e("Unexpected BuzzCard buzzString format: $buzzString")
                        return@enableReaderMode
                    }

                    // Since the separator between GTID and prox ID is increasingly varied and
                    // problematic, just take the first 9 digits of the string which is (so far)
                    // always the GTID
                    val gtid = buzzString.take(GTID_LENGTH)

                    if (!GTID_REGEX.matches(gtid)) {
                        error = InvalidBuzzCardData
                        Timber.e("Unexpected BuzzCard GTID format: $gtid")
                    }

                    error = null
                    val buzzCardTap = BuzzCardTap(gtid.toInt())
                    lastTap = buzzCardTap
                    onBuzzCardTap(buzzCardTap)
                } else {
                    Timber.i("Unknown card type ($cardType) presented")
                    error = NotABuzzCard
                }
            } catch (e: NxpNfcLibException) {
                Timber.w(e, "NxpNfcLib exception occurred while processing this NFC tag")
                error = when (e.localizedMessage) {
                    "Wrong CLA" -> NotABuzzCard
                    "Tag was lost." -> TagLost
                    "Incomplete response" -> TagLost
                    else -> UnknownNfcError
                }
            } catch (e: NumberFormatException) {
                Timber.e(e, "GTID string from (probably a) BuzzCard could not be parsed as an Int")
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
    if (!hidePrompt) {
        Column {
            if (externalError != null) {
                ExternalError(externalError)
            } else {
                when (error) {
                    null -> BuzzCardReadyForTap()
                    TagLost -> NfcTagLostError()
                    NotABuzzCard -> NfcNotABuzzCardError()
                    InvalidBuzzCardData -> NfcInvalidBuzzCardDataError()
                    UnknownNfcError -> NfcReadUnknownError()
                }
            }
            Button(
                onClick = { showGtidPrompt = true },
                Modifier.align(CenterHorizontally)
            ) {
                Text("Enter GTID manually")
            }
// TODO(before merge): Figure out how to make this compile in CI when uncommented
//            if (BuildConfig.DEBUG) {
//                when (lastTap) {
//                    null -> {
//                        Button(
//                            onClick = {
//                                onBuzzCardTap(
//                                    BuzzCardTap(
//                                        BuildConfig.localGTID.toInt(),
//                                        source = Debug
//                                    )
//                                )
//                            },
//                            Modifier.align(CenterHorizontally)
//                        ) {
//                            Text("Tap ${BuildConfig.localGTID.toInt()} again")
//                        }
//                    }
//
//                    else -> {
//                        Button(
//                            onClick = {
//                                onBuzzCardTap(BuzzCardTap(lastTap!!.gtid, source = Debug))
//                            },
//                            Modifier.align(CenterHorizontally)
//                        ) {
//                            Text("Tap ${lastTap?.gtid ?: "unknown GTID"} again")
//                        }
//                    }
//                }
//            }
        }
    }

    if (showGtidPrompt) {
        ManualGtidEntryPrompt(
            onGtidEntered = {
                lastTap = it
                onBuzzCardTap(it)
                error = null
            },
            onHide = { showGtidPrompt = false },
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun ManualGtidEntryPrompt(
    onHide: () -> Unit,
    onGtidEntered: (entry: BuzzCardTap) -> Unit,
) {
    var gtid by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(focusRequester) {
        awaitFrame()
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = {
            gtid = ""
            onHide()
        },
        confirmButton = {
            TextButton(onClick = {
                if (GTID_REGEX.matches(gtid)) {
                    onGtidEntered(BuzzCardTap(gtid.toInt(), source = Keyboard))
                    onHide()
                    gtid = ""
                }
            }) {
                Text("Submit")
            }
        },
        title = {
            Text(
                text = "Manual GTID entry",
                style = MaterialTheme.typography.headlineSmall
            )
        },
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
                    isError = gtid.isNotEmpty() && !GTID_REGEX.matches(gtid),
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .focusRequester(focusRequester), // Focuses this field and shows the keyboard
                    // when this text field is visible on screen. See https://stackoverflow.com/a/76321706
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

@Composable
fun ExternalError(externalError: BuzzCardPromptExternalError) {
    ActionPrompt(
        icon = { ContactlessIcon(Modifier.size(114.dp), tint = danger) },
        title = externalError.title,
        subtitle = externalError.message,
    )
}
