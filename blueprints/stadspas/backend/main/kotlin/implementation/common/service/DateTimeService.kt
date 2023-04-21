package implementation.common.service

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DateTimeService {

    fun getDifferenceInMilliseconds(from: String, to: String): Long =
        ZonedDateTime.parse(to).toInstant().toEpochMilli() - ZonedDateTime.parse(from).toInstant().toEpochMilli()

    fun getCurrentTimeStamp(): String {
        val dateTimeFormatter = DateTimeFormatter.ISO_INSTANT
        return ZonedDateTime.now().format(dateTimeFormatter)
    }
}
