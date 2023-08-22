package id.teman.app.mitra.repository.wallet

import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.reward.RewardRedeemRequestDto
import id.teman.app.mitra.data.dto.wallet.WalletRequestDto
import id.teman.app.mitra.data.dto.wallet.WithdrawRequestDto
import id.teman.app.mitra.data.remote.wallet.WalletRemoteDataSource
import id.teman.app.mitra.domain.model.wallet.ItemBankSpec
import id.teman.app.mitra.domain.model.wallet.ItemReward
import id.teman.app.mitra.domain.model.wallet.ItemRewardRedeemed
import id.teman.app.mitra.domain.model.wallet.ItemRewardTransaction
import id.teman.app.mitra.domain.model.wallet.WalletBankInformationSpec
import id.teman.app.mitra.domain.model.wallet.WalletDataTransferSpec
import id.teman.app.mitra.domain.model.wallet.WalletHistoryItemSpec
import id.teman.app.mitra.domain.model.wallet.WalletItemDetailSpec
import id.teman.app.mitra.domain.model.wallet.convertToListBank
import id.teman.app.mitra.domain.model.wallet.toHistoryPoint
import id.teman.app.mitra.domain.model.wallet.toListRewardRedeemed
import id.teman.app.mitra.domain.model.wallet.toListRewards
import id.teman.app.mitra.domain.model.wallet.toWalletBankInformationSpec
import id.teman.app.mitra.domain.model.wallet.toWalletDetailItem
import id.teman.app.mitra.domain.model.wallet.toWalletHistoryItemSpec
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.RequestBody

class WalletRepository @Inject constructor(
    private val walletRemoteDataSource: WalletRemoteDataSource
) {
    suspend fun requestWalletOtpPin(): Flow<String> = walletRemoteDataSource
        .requestWalletOtpPin().map { it.message.orEmpty() }

    suspend fun verifyWalletOtpPIN(otp: String): Flow<String> = walletRemoteDataSource
        .verifyWalletOtpPIN(otp).map { it.token.orEmpty() }

    suspend fun updateWalletPIN(code: String, token: String): Flow<String> = walletRemoteDataSource
        .updateWalletPIN(code, token).map { it.message.orEmpty() }

    suspend fun getWalletBalance(): Flow<Double> = walletRemoteDataSource
        .getWalletBalance().map { it.balance.orZero() }

    suspend fun getWalletTransactions(): Flow<List<WalletHistoryItemSpec>> = walletRemoteDataSource
        .getWalletTransactions().map { it.data.orEmpty().map { spec -> spec.toWalletHistoryItemSpec() } }

    suspend fun getWalletBankInformation(): Flow<WalletBankInformationSpec> = walletRemoteDataSource
        .getWalletBankInformation().map { it.toWalletBankInformationSpec() }

    suspend fun updateWalletBankInformation(partMap: Map<String, RequestBody>): Flow<WalletBankInformationSpec> = walletRemoteDataSource
        .updateWalletBankInformation(partMap).map { it.toWalletBankInformationSpec() }

    suspend fun withdrawMoney(pin: String, spec: WalletDataTransferSpec): Flow<String> = walletRemoteDataSource
        .withdrawMoney(WithdrawRequestDto(
            pin = pin,
            bankName = spec.bankName,
            accountNumber = spec.accountNumber,
            accountName = spec.accountName,
            amount = spec.withdrawalAmount.toString()
        )).map { it.message.orEmpty() }

    suspend fun topUpBalanceWallet(amount: Int): Flow<WalletItemDetailSpec> =
        walletRemoteDataSource.topUpWallet(WalletRequestDto(amount)).map { it.toWalletDetailItem() }

    suspend fun getRewardRedeemed(): Flow<List<ItemRewardRedeemed>> =
        walletRemoteDataSource.getListRewardRedeemed().map { it.data.toListRewardRedeemed() }

    suspend fun getRewards(): Flow<List<ItemReward>> =
        walletRemoteDataSource.getListReward().map { it.data.toListRewards() }

    suspend fun redeemReward(id: String): Flow<String> =
        walletRemoteDataSource.redeemReward(requestDto = RewardRedeemRequestDto(id)).map { it.message.orEmpty() }

    suspend fun getListBank(): Flow<List<ItemBankSpec>> =
        walletRemoteDataSource.getListBank().map { it.convertToListBank() }.flowOn(Dispatchers.IO)

    suspend fun getHistoryPoint(): Flow<List<ItemRewardTransaction>> =
        walletRemoteDataSource.getListRewardTransaction().map { it.data.toHistoryPoint() }
}