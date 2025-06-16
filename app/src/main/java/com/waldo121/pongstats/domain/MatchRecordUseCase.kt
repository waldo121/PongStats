import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.repository.MatchRecordRepository


class SingleMatchRecordsUseCase(
    private val matchRecordRepository: MatchRecordRepository
) {
    suspend fun invoke(matchRecord: SingleMatchRecord ) {
        matchRecordRepository.createSingleMatchRecord(record = matchRecord)
    }
}

class DoubleMatchRecordsUseCase(
    private val matchRecordRepository: MatchRecordRepository
) {
    suspend fun invoke(matchRecord: DoubleMatchRecord) {
        matchRecordRepository.createDoubleMatchRecord(record = matchRecord)
    }
}