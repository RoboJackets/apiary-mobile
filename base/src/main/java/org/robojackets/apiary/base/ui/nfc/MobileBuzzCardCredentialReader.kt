package org.robojackets.apiary.base.ui.nfc

import android.nfc.tech.IsoDep
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Reads GTID from a mobile wallet BuzzCard credential over ISO-DEP per [README_MOBILE.md]:
 * SELECT HCE applet → DESFire select `BB BB BB` → AES mutual auth → read encrypted file `0x02`.
 */
object MobileBuzzCardCredentialReader {
    private class MobileApduException(
        val sw1: Int,
        val sw2: Int,
        swIsoOk: Boolean
    ) : IllegalStateException(
        "Unexpected SW: ${sw1.toString(16)} ${sw2.toString(16)} (swIsoOk=$swIsoOk)"
    )

    sealed interface ReadResult {
        data class Success(val gtid: String) : ReadResult
        data object AuthFailure : ReadResult
        data object Failure : ReadResult
    }

    private val secureRandom = SecureRandom()

    private val hceAid = byteArrayOf(
        0xA0.toByte(), 0x00, 0x00, 0x06, 0x76, 0x60, 0x09, 0x1E, 0x02, 0x00, 0x78
    )
    private val desfireAppBuzz = byteArrayOf(0xBB.toByte(), 0xBB.toByte(), 0xBB.toByte())
    private const val FILE_ID_SIS: Byte = 0x02
    private const val READ_LENGTH = 28

    fun readGtid(isoDep: IsoDep, aesKey128: ByteArray): ReadResult {
        require(aesKey128.size == 16) { "AES key must be 16 bytes" }
        try {
            isoDep.timeout = 5000
            if (!isoDep.isConnected) isoDep.connect()

            transceiveChecked(isoDep, selectHceCommand(), swIsoOk = true)
            transceiveChecked(isoDep, selectDesfireApplicationCommand(), swIsoOk = false)

            val encRndB = requestAuthChallenge(isoDep)
            val rndB = aesDecryptCbc(aesKey128, ByteArray(16), encRndB)
            val rndBPrime = rotateLeft(rndB)
            val rndA = ByteArray(16).also { secureRandom.nextBytes(it) }

            val plainAf = rndA + rndBPrime
            val encPayload = aesEncryptCbc(aesKey128, encRndB, plainAf)

            val authContResp = transceiveChecked(
                isoDep,
                continueAuthCommand(encPayload),
                swIsoOk = false
            )
            val encRndAPrime = authContResp.copyOfRange(0, 16)
            val ivSecondBlock = encPayload.copyOfRange(16, 32)
            val rndAPrimeReceived = aesDecryptCbc(aesKey128, ivSecondBlock, encRndAPrime)
            val rndAExpectedPrime = rotateLeft(rndA)
            if (!rndAPrimeReceived.contentEquals(rndAExpectedPrime)) {
                Timber.w("Mobile credential: RndA' verification failed")
                return ReadResult.AuthFailure
            }

            val sessionKey = deriveSessionKey(rndA, rndB)

            val readResp = transceiveChecked(isoDep, readFileCommand(FILE_ID_SIS, 0, READ_LENGTH), swIsoOk = false)
            val encFile = readResp.copyOfRange(0, readResp.size - 2)
            val plainFile = aesDecryptCbc(sessionKey, ByteArray(16), encFile)

            val gtid = parseGtidFromFile02Plaintext(plainFile) ?: return ReadResult.Failure
            return ReadResult.Success(gtid)
        } catch (e: Exception) {
            when (classifyFailure(e)) {
                FailureKind.AuthFailure -> {
                    Timber.d("Mobile credential authentication failed with SW 91 AE")
                    return ReadResult.AuthFailure
                }
                FailureKind.ExpectedFallback -> {
                    Timber.v("Mobile credential path not applicable for this tap; using plastic fallback")
                    return ReadResult.Failure
                }
                FailureKind.Unexpected -> {
                    Timber.w(e, "Mobile credential read failed")
                    return ReadResult.Failure
                }
            }
        } finally {
            try {
                isoDep.close()
            } catch (_: Exception) {
            }
        }
    }

    private enum class FailureKind {
        AuthFailure,
        ExpectedFallback,
        Unexpected,
    }

    private fun classifyFailure(e: Exception): FailureKind {
        val apduError = e as? MobileApduException ?: return FailureKind.Unexpected
        return when {
            apduError.sw1 == 0x91 && apduError.sw2 == 0xAE -> FailureKind.AuthFailure
            apduError.sw1 == 0x6A && apduError.sw2 == 0x82 -> FailureKind.ExpectedFallback
            else -> FailureKind.Unexpected
        }
    }

    private fun parseGtidFromFile02Plaintext(plain: ByteArray): String? {
        val asString = { b: ByteArray ->
            String(b, StandardCharsets.UTF_8).trim { it <= ' ' || it == '\u0000' }
        }
        val fromContent: (String) -> String? = content@ { buzzString ->
            val buzzStringRegex = Regex("90[0-9]{7}[=0].*")
            if (!buzzStringRegex.matches(buzzString)) return@content null
            val gtid = buzzString.take(GTID_LENGTH)
            if (GTID_REGEX.matches(gtid)) gtid else null
        }
        fromContent(asString(plain))?.let { return it }
        if (plain.size >= 4) {
            fromContent(asString(plain.copyOfRange(0, plain.size - 4)))?.let { return it }
        }
        Timber.e("Unexpected mobile credential file 0x02 content after decrypt")
        return null
    }

    private fun selectHceCommand(): ByteArray =
        byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, 0x0B.toByte()) + hceAid + byteArrayOf(0x00)

    private fun selectDesfireApplicationCommand(): ByteArray =
        byteArrayOf(0x90.toByte(), 0x5A, 0x00, 0x00, 0x03) + desfireAppBuzz + byteArrayOf(0x00)

    private fun requestAuthChallenge(isoDep: IsoDep): ByteArray {
        val cmd = byteArrayOf(0x90.toByte(), 0xAA.toByte(), 0x00, 0x00, 0x01, 0x00, 0x00)
        val resp = isoDep.transceive(cmd)
        checkDesfireSw(resp, 0x91.toByte(), 0xAF.toByte())
        return resp.copyOfRange(0, 16)
    }

    private fun continueAuthCommand(encPayload: ByteArray): ByteArray =
        byteArrayOf(0x90.toByte(), 0xAF.toByte(), 0x00, 0x00, 0x20) + encPayload + byteArrayOf(0x00)

    private fun readFileCommand(fileId: Byte, offset: Int, length: Int): ByteArray {
        val data = ByteArray(7)
        data[0] = fileId
        data[1] = (offset and 0xFF).toByte()
        data[2] = ((offset shr 8) and 0xFF).toByte()
        data[3] = ((offset shr 16) and 0xFF).toByte()
        data[4] = (length and 0xFF).toByte()
        data[5] = ((length shr 8) and 0xFF).toByte()
        data[6] = ((length shr 16) and 0xFF).toByte()
        val lc = data.size.toByte()
        return byteArrayOf(0x90.toByte(), 0xBD.toByte(), 0x00, 0x00, lc) + data + byteArrayOf(0x00)
    }

    private fun transceiveChecked(isoDep: IsoDep, cmd: ByteArray, swIsoOk: Boolean): ByteArray {
        val resp = isoDep.transceive(cmd)
        val sw1 = resp[resp.size - 2].toInt() and 0xFF
        val sw2 = resp[resp.size - 1].toInt() and 0xFF
        val ok = if (swIsoOk) {
            sw1 == 0x90 && sw2 == 0x00
        } else {
            sw1 == 0x91 && sw2 == 0x00
        }
        if (!ok) {
            throw MobileApduException(sw1, sw2, swIsoOk)
        }
        return resp
    }

    private fun checkDesfireSw(resp: ByteArray, expectedSw1: Byte, expectedSw2: Byte) {
        val sw1 = resp[resp.size - 2]
        val sw2 = resp[resp.size - 1]
        if (sw1 != expectedSw1 || sw2 != expectedSw2) {
            error("Unexpected DESFire SW: ${sw1.toInt() and 0xff} ${sw2.toInt() and 0xff}")
        }
    }

    private fun aesDecryptCbc(key: ByteArray, iv: ByteArray, data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }

    private fun aesEncryptCbc(key: ByteArray, iv: ByteArray, data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(iv))
        return cipher.doFinal(data)
    }

    private fun rotateLeft(block: ByteArray): ByteArray {
        require(block.size == 16)
        val out = block.copyOf()
        val first = out[0]
        for (i in 0 until 15) out[i] = out[i + 1]
        out[15] = first
        return out
    }

    private fun deriveSessionKey(rndA: ByteArray, rndB: ByteArray): ByteArray {
        require(rndA.size == 16 && rndB.size == 16)
        return rndA.copyOfRange(0, 4) + rndB.copyOfRange(0, 4) +
            rndA.copyOfRange(12, 16) + rndB.copyOfRange(12, 16)
    }
}
