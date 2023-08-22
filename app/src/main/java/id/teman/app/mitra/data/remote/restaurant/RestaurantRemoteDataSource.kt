package id.teman.app.mitra.data.remote.restaurant

import com.google.android.gms.common.util.Strings
import id.teman.app.mitra.data.dto.BaseResponse
import id.teman.app.mitra.data.dto.restaurant.AddProductCategoryRequestDto
import id.teman.app.mitra.data.dto.restaurant.ProductResponseDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantCategoriesDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantHoursDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantMenuCategoryDto
import id.teman.app.mitra.data.dto.restaurant.RestaurantSummaryDto
import id.teman.app.mitra.data.dto.restaurant.UpdateProductCategoryDto
import id.teman.app.mitra.data.dto.restaurant.UpdateProductDto
import id.teman.app.mitra.data.dto.restaurant.UpdateRestaurantOrderStatusDto
import id.teman.app.mitra.data.dto.restaurant.UpdateRestaurantStatusDto
import id.teman.app.mitra.data.dto.transport.TransportDataResponseDto
import id.teman.app.mitra.data.dto.transport.TransportResponseDto
import id.teman.app.mitra.data.dto.user.response.MitraRestaurantBasicInfoDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface RestaurantRemoteDataSource {
    suspend fun getRestaurantDetail(): Flow<MitraRestaurantBasicInfoDto>
    suspend fun updateRestaurantStatus(status: String): Flow<MitraRestaurantBasicInfoDto>
    suspend fun getRestaurantMenuCategories(status: String): Flow<RestaurantCategoriesDto>
    suspend fun getRestaurantOrderRequest(
        requestStatus: String?,
        searchQuery: String?
    ): Flow<TransportDataResponseDto>

    suspend fun addRestaurantMenuCategory(
        name: String,
        description: String
    ): Flow<RestaurantMenuCategoryDto>

    suspend fun addRestaurantMenu(
        partMap: MutableMap<String, RequestBody>,
        image: MultipartBody.Part
    ): Flow<ProductResponseDto>

    suspend fun updateRestaurantProduct(
        productId: String,
        body: UpdateProductDto
    ): Flow<ProductResponseDto>

    suspend fun updateRestaurantOrderStatus(
        requestId: String,
        orderStatus: String
    ): Flow<TransportResponseDto>

    suspend fun getRestaurantSummary(dateFilter: String): Flow<RestaurantSummaryDto>
    suspend fun getRestaurantOrderDetail(requestId: String): Flow<TransportResponseDto>
    suspend fun updateRestaurantMenuCategory(
        categoryId: String,
        name: String
    ): Flow<RestaurantMenuCategoryDto>

    suspend fun getRestaurantHours(): Flow<RestaurantHoursDto>
    suspend fun updateRestaurantHours(update: RestaurantHoursDto): Flow<RestaurantHoursDto>
    suspend fun updateRestaurantMenu(
        partMap: MutableMap<String, RequestBody>,
        image: MultipartBody.Part?,
        menuId: String
    ): Flow<ProductResponseDto>

    suspend fun deleteRestaurantMenu(menuId: String): Flow<BaseResponse>
}

class DefaultRestaurantRemoteDataSource(private val httpClient: ApiServiceInterface) :
    RestaurantRemoteDataSource {

    override suspend fun getRestaurantDetail(): Flow<MitraRestaurantBasicInfoDto> =
        handleRequestOnFlow {
            httpClient.getRestaurantDetail()
        }

    override suspend fun updateRestaurantStatus(status: String): Flow<MitraRestaurantBasicInfoDto> =
        handleRequestOnFlow {
            httpClient.updateRestaurantStatus(UpdateRestaurantStatusDto(status))
        }

    override suspend fun getRestaurantMenuCategories(status: String): Flow<RestaurantCategoriesDto> =
        handleRequestOnFlow {
            httpClient.getRestaurantMenuCategories(Strings.emptyToNull(status))
        }

    override suspend fun getRestaurantOrderRequest(
        requestStatus: String?,
        searchQuery: String?
    ): Flow<TransportDataResponseDto> =
        handleRequestOnFlow {
            httpClient.getRestaurantOrderRequest(requestStatus, searchQuery)
        }

    override suspend fun addRestaurantMenuCategory(
        name: String,
        description: String
    ): Flow<RestaurantMenuCategoryDto> =
        handleRequestOnFlow {
            httpClient.addRestaurantCategories(AddProductCategoryRequestDto(name, description))
        }

    override suspend fun addRestaurantMenu(
        partMap: MutableMap<String, RequestBody>,
        image: MultipartBody.Part
    ): Flow<ProductResponseDto> = handleRequestOnFlow {
        httpClient.addRestaurantProduct(partMap, image)
    }

    override suspend fun updateRestaurantMenu(
        partMap: MutableMap<String, RequestBody>,
        image: MultipartBody.Part?,
        menuId: String
    ): Flow<ProductResponseDto> = handleRequestOnFlow {
        httpClient.updateRestaurantProduct(
            partMap = partMap,
            menuId = menuId,
            productPhotoFile = image
        )
    }

    override suspend fun deleteRestaurantMenu(menuId: String): Flow<BaseResponse> =
        handleRequestOnFlow { httpClient.deleteRestaurantProduct(menuId) }

    override suspend fun updateRestaurantProduct(
        productId: String,
        body: UpdateProductDto
    ): Flow<ProductResponseDto> = handleRequestOnFlow {
        httpClient.updateProduct(
            productId = productId,
            body = body
        )
    }

    override suspend fun updateRestaurantOrderStatus(
        requestId: String,
        orderStatus: String
    ): Flow<TransportResponseDto> =
        handleRequestOnFlow {
            httpClient.updateRestaurantOrderStatus(
                requestId,
                UpdateRestaurantOrderStatusDto(orderStatus)
            )
        }

    override suspend fun getRestaurantSummary(dateFilter: String): Flow<RestaurantSummaryDto> =
        handleRequestOnFlow {
            httpClient.getRestaurantSummary(dateFilter.ifEmpty { null })
        }

    override suspend fun getRestaurantOrderDetail(requestId: String): Flow<TransportResponseDto> =
        handleRequestOnFlow {
            httpClient.getRestaurantOrderDetail(requestId)
        }

    override suspend fun updateRestaurantMenuCategory(
        categoryId: String,
        name: String
    ): Flow<RestaurantMenuCategoryDto> = handleRequestOnFlow {
        httpClient.updateRestaurantMenuCategory(categoryId, UpdateProductCategoryDto(name))
    }

    override suspend fun getRestaurantHours(): Flow<RestaurantHoursDto> = handleRequestOnFlow {
        httpClient.getRestaurantHours()
    }

    override suspend fun updateRestaurantHours(update: RestaurantHoursDto): Flow<RestaurantHoursDto> =
        handleRequestOnFlow {
            httpClient.updateRestaurantHours(update)
        }
}