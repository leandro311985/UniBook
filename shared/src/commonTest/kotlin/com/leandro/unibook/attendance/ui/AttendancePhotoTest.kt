package com.leandro.unibook.attendance.ui

import com.leandro.unibook.attendance.presentation.ui.decodePhotoBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull

@OptIn(ExperimentalEncodingApi::class)
class AttendancePhotoTest {
    @Test
    fun decodesPlainBase64() {
        val expected = byteArrayOf(0x01, 0x02, 0x03)

        assertContentEquals(expected, decodePhotoBytes(Base64.encode(expected)))
    }

    @Test
    fun decodesBase64DataUriAndIgnoresLineBreaks() {
        val expected = byteArrayOf(0x01, 0x02, 0x03)
        val encoded = Base64.Default.encode(expected)

        assertContentEquals(
            expected,
            decodePhotoBytes("data:image/jpeg;base64,${encoded.take(4)}\n${encoded.drop(4)}")
        )
    }

    @Test
    fun rejectsDataUriWithoutBase64Encoding() {
        assertNull(decodePhotoBytes("data:image/jpeg,/9j/"))
    }
}
