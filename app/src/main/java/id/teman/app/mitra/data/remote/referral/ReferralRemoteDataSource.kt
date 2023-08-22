package id.teman.app.mitra.data.remote.referral

import id.teman.app.mitra.data.dto.referral.ReferralResponseDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface ReferralRemoteDataSource {
    suspend fun getHistoryReferral(): Flow<ReferralResponseDto>
}

class DefaultReferralDataSource(private val httpClient: ApiServiceInterface): ReferralRemoteDataSource {
    override suspend fun getHistoryReferral(): Flow<ReferralResponseDto> =
        handleRequestOnFlow {
            httpClient.getHistoryReferral()
        }
}