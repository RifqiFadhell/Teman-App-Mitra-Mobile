package id.teman.app.mitra.data.remote.wallet

import id.teman.app.mitra.data.dto.BaseResponse
import id.teman.app.mitra.data.dto.reward.RewardRedeemRequestDto
import id.teman.app.mitra.data.dto.reward.RewardRedeemedResponse
import id.teman.app.mitra.data.dto.reward.RewardResponseDto
import id.teman.app.mitra.data.dto.reward.RewardTransactionResponseDto
import id.teman.app.mitra.data.dto.user.response.OtpResponseDto
import id.teman.app.mitra.data.dto.wallet.ItemBankDto
import id.teman.app.mitra.data.dto.wallet.OtpPinVerificationDto
import id.teman.app.mitra.data.dto.wallet.UpdatePinRequestDto
import id.teman.app.mitra.data.dto.wallet.VerifyOtpRequestDto
import id.teman.app.mitra.data.dto.wallet.WalletBalanceDto
import id.teman.app.mitra.data.dto.wallet.WalletBankAccountDto
import id.teman.app.mitra.data.dto.wallet.WalletRequestDto
import id.teman.app.mitra.data.dto.wallet.WalletTransactionResponseDto
import id.teman.app.mitra.data.dto.wallet.WithdrawRequestDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import id.teman.app.mitra.domain.model.wallet.WalletHistoryTransactionDetail
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface WalletRemoteDataSource {
    suspend fun requestWalletOtpPin(): Flow<OtpResponseDto>
    suspend fun verifyWalletOtpPIN(code: String): Flow<OtpPinVerificationDto>
    suspend fun updateWalletPIN(code: String, token: String): Flow<BaseResponse>
    suspend fun getWalletBalance(): Flow<WalletBalanceDto>
    suspend fun getWalletTransactions(): Flow<WalletTransactionResponseDto>
    suspend fun getWalletBankInformation(): Flow<WalletBankAccountDto>
    suspend fun getListBank(): Flow<List<ItemBankDto>>
    suspend fun updateWalletBankInformation(partMap: Map<String, RequestBody>): Flow<WalletBankAccountDto>
    suspend fun withdrawMoney(request: WithdrawRequestDto): Flow<BaseResponse>
    suspend fun topUpWallet(request: WalletRequestDto): Flow<WalletHistoryTransactionDetail>
    suspend fun getListReward(): Flow<RewardResponseDto>
    suspend fun getListRewardTransaction(): Flow<RewardTransactionResponseDto>
    suspend fun getListRewardRedeemed(): Flow<RewardRedeemedResponse>
    suspend fun redeemReward(requestDto: RewardRedeemRequestDto): Flow<BaseResponse>
}

class DefaultWalletRemoteDataSource @Inject constructor(
    private val httpClient: ApiServiceInterface
) : WalletRemoteDataSource {

    override suspend fun requestWalletOtpPin(): Flow<OtpResponseDto> =
        handleRequestOnFlow { httpClient.requestWalletOtpPin() }

    override suspend fun verifyWalletOtpPIN(code: String): Flow<OtpPinVerificationDto> =
        handleRequestOnFlow { httpClient.verifyWalletOtpPIN(VerifyOtpRequestDto(code)) }

    override suspend fun updateWalletPIN(code: String, token: String): Flow<BaseResponse> =
        handleRequestOnFlow { httpClient.updateWalletPIN(UpdatePinRequestDto(code, token)) }

    override suspend fun getWalletBalance(): Flow<WalletBalanceDto> =
        handleRequestOnFlow { httpClient.getWalletBalance() }

    override suspend fun getWalletTransactions(): Flow<WalletTransactionResponseDto> =
        handleRequestOnFlow { httpClient.getWalletTransactions() }

    override suspend fun getWalletBankInformation(): Flow<WalletBankAccountDto> =
        handleRequestOnFlow { httpClient.getBankInformation() }

    override suspend fun updateWalletBankInformation(partMap: Map<String, RequestBody>): Flow<WalletBankAccountDto> =
        handleRequestOnFlow { httpClient.updateWalletBankInformation(partMap) }

    override suspend fun withdrawMoney(request: WithdrawRequestDto): Flow<BaseResponse> =
        handleRequestOnFlow { httpClient.withdrawMoney(request) }

    override suspend fun topUpWallet(request: WalletRequestDto): Flow<WalletHistoryTransactionDetail> =
        handleRequestOnFlow {
            httpClient.topUpWalletAmount(request)
        }

    override suspend fun getListReward(): Flow<RewardResponseDto> =
        handleRequestOnFlow {
            httpClient.getListRewards()
        }

    override suspend fun getListRewardTransaction(): Flow<RewardTransactionResponseDto> =
        handleRequestOnFlow {
            httpClient.getListRewardTransaction()
        }

    override suspend fun getListRewardRedeemed(): Flow<RewardRedeemedResponse> =
        handleRequestOnFlow {
            httpClient.getListRewardRedeemed()
        }

    override suspend fun redeemReward(requestDto: RewardRedeemRequestDto): Flow<BaseResponse> =
        handleRequestOnFlow {
            httpClient.redeemReward(requestDto)
        }

    override suspend fun getListBank(): Flow<List<ItemBankDto>> =
        handleRequestOnFlow {
            httpClient.getListBank().orEmpty()
        }
}