import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.repository.MatchRecordRepository


class SingleMatchRecordsUseCase(
    val repository: MatchRecordRepository
) {
    suspend fun invoke(matchRecord: SingleMatchRecord ) {
        repository.createSingleMatchRecord(record = matchRecord)
    }
}

class DoubleMatchRecordsUseCase(
    val repository: MatchRecordRepository
) {
    suspend fun invoke(matchRecord: DoubleMatchRecord) {
        repository.createDoubleMatchRecord(record = matchRecord)
    }
}