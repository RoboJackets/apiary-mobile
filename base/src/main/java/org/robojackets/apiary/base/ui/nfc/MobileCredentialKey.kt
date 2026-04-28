package org.robojackets.apiary.base.ui.nfc

/**
 * Parsing and validation for the 128-bit AES key used with mobile (HCE) BuzzCard credentials.
 * Stored as 32 hexadecimal characters (POC — not secure storage).
 */
object MobileCredentialKey {
    private val hexCharRegex = Regex("^[0-9a-fA-F]{32}$")

    fun normalizeHex(input: String): String =
        input.filter { it.isDigit() || it in 'a'..'f' || it in 'A'..'F' }.lowercase()

    /** @return parsed 16-byte key, or null if invalid */
    fun parse128BitKey(hex: String): ByteArray? {
        val n = normalizeHex(hex)
        if (!hexCharRegex.matches(n)) return null
        return ByteArray(16) { i ->
            n.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    }

}
