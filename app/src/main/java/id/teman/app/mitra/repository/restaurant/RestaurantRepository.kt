package id.teman.app.mitra.repository.restaurant

import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.BaseResponse
import id.teman.app.mitra.data.dto.restaurant.UpdateProductDto
import id.teman.app.mitra.data.dto.restaurant.toRestaurantHoursDto
import id.teman.app.mitra.data.remote.restaurant.RestaurantRemoteDataSource
import id.teman.app.mitra.domain.model.restaurant.RestaurantCategorySpec
import id.teman.app.mitra.domain.model.restaurant.RestaurantMenuSpec
import id.teman.app.mitra.domain.model.restaurant.RestaurantOpenHourSpec
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderSpec
import id.teman.app.mitra.domain.model.restaurant.RestaurantOrderStatus
import id.teman.app.mitra.domain.model.restaurant.RestaurantSummaryFilter
import id.teman.app.mitra.domain.model.restaurant.RestaurantSummarySpec
import id.teman.app.mitra.domain.model.restaurant.toRestaurantCategorySpec
import id.teman.app.mitra.domain.model.restaurant.toRestaurantMenuSpec
import id.teman.app.mitra.domain.model.restaurant.toRestaurantOpenHourListSpec
import id.teman.app.mitra.domain.model.restaurant.toRestaurantOrderListSpec
import id.teman.app.mitra.domain.model.restaurant.toRestaurantOrderSpec
import id.teman.app.mitra.domain.model.user.MitraRestaurantInfo
import id.teman.app.mitra.domain.model.user.toMitraRestaurantInfo
import id.teman.app.mitra.ui.food.menu.domain.MenuSpec
import id.teman.app.mitra.ui.food.menu.domain.toMenuCategorySpec
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RestaurantRepository @Inject constructor(
    private val restaurantRemoteDataSource: RestaurantRemoteDataSource
) {

    suspend fun getRestaurantDetail(): Flow<MitraRestaurantInfo> =
        restaurantRemoteDataSource.getRestaurantDetail().flowOn(Dispatchers.IO).map { it.toMitraRestaurantInfo() }

    suspend fun updateRestaurantStatus(value: String): Flow<MitraRestaurantInfo> =
        restaurantRemoteDataSource.updateRestaurantStatus(value).flowOn(Dispatchers.IO).map { it.toMitraRestaurantInfo() }

    suspend fun getRestaurantMenuCategory(isActive: Boolean?): Flow<List<MenuSpec>> = flow {
        // null isActive means we wanted to get all categories.
        val query = if (isActive == null) "" else if (isActive) {
            """{"products.is_active":true}"""
        } else {
            """{"products.is_active":false}"""
        }
        restaurantRemoteDataSource.getRestaurantMenuCategories(query)
            .catch { exception -> throw exception }
            .flowOn(Dispatchers.IO)
            .collect { categories ->
                val categoryMenu = categories.data?.map { it.toMenuCategorySpec() }.orEmpty()
                emit(categoryMenu)
            }
    }

    suspend fun getRestaurantOrderRequest(status: RestaurantOrderStatus? = null, searchQuery: String? = null): Flow<List<RestaurantOrderSpec>> {
        val queryBuilder =
            if (status != null) """{"order_status":"${status.value}"}""".trim() else null
        return restaurantRemoteDataSource.getRestaurantOrderRequest(queryBuilder, searchQuery)
            .flowOn(Dispatchers.IO)
            .map { it.data?.toRestaurantOrderListSpec().orEmpty() }
    }

    suspend fun addRestaurantMenuCategory(
        name: String,
        description: String
    ): Flow<RestaurantCategorySpec> =
        restaurantRemoteDataSource.addRestaurantMenuCategory(name, description)
            .flowOn(Dispatchers.IO)
            .map { it.toRestaurantCategorySpec() }

    suspend fun addRestaurantMenu(
        partMap: MutableMap<String, RequestBody>,
        image: MultipartBody.Part
    ): Flow<RestaurantMenuSpec> =
        restaurantRemoteDataSource.addRestaurantMenu(partMap, image)
            .flowOn(Dispatchers.IO)
            .map { it.toRestaurantMenuSpec() }

    suspend fun updateProduct(
        item: RestaurantMenuSpec,
        newStatus: Boolean
    ): Flow<RestaurantMenuSpec> =
        restaurantRemoteDataSource.updateRestaurantProduct(
            item.id, UpdateProductDto(
                categoryId = item.categoryId,
                name = item.name,
                isActive = newStatus
            )
        ).flowOn(Dispatchers.IO).map { it.toRestaurantMenuSpec() }

    suspend fun getRestaurantSummary(filter: RestaurantSummaryFilter?): Flow<RestaurantSummarySpec> =
        restaurantRemoteDataSource.getRestaurantSummary(filter?.value.orEmpty())
            .flowOn(Dispatchers.IO)
            .map { RestaurantSummarySpec(it.transaction.orZero(), it.income.orZero()) }

    suspend fun getRestaurantOrderDetail(requestId: String): Flow<RestaurantOrderSpec> =
        restaurantRemoteDataSource.getRestaurantOrderDetail(requestId).flowOn(Dispatchers.IO).map { it.toRestaurantOrderSpec()}

    suspend fun updateRestaurantOrderStatus(requestId: String, status: String): Flow<RestaurantOrderSpec> =
        restaurantRemoteDataSource.updateRestaurantOrderStatus(requestId, status).flowOn(Dispatchers.IO).map {
            it.toRestaurantOrderSpec()
        }

    suspend fun updateRestaurantMenuCategory(categoryId: String, name: String): Flow<RestaurantCategorySpec> =
        restaurantRemoteDataSource.updateRestaurantMenuCategory(categoryId, name).flowOn(Dispatchers.IO).map { it.toRestaurantCategorySpec() }

    suspend fun getRestaurantHours(): Flow<List<RestaurantOpenHourSpec>> =
        restaurantRemoteDataSource.getRestaurantHours().flowOn(Dispatchers.IO).map { it.toRestaurantOpenHourListSpec() }

    suspend fun updateRestaurantHours(items: List<RestaurantOpenHourSpec>): Flow<List<RestaurantOpenHourSpec>> =
        restaurantRemoteDataSource.updateRestaurantHours(items.toRestaurantHoursDto()).flowOn(Dispatchers.IO).map { it.toRestaurantOpenHourListSpec() }

    suspend fun updateRestaurantMenu(
        partMap: MutableMap<String, RequestBody>,
        image: MultipartBody.Part? = null,
        menuId: String
    ): Flow<RestaurantMenuSpec> =
        restaurantRemoteDataSource.updateRestaurantMenu(
            partMap = partMap,
            menuId = menuId,
            image = image
        ).flowOn(Dispatchers.IO).map { it.toRestaurantMenuSpec() }

    suspend fun deleteRestaurantMenu(menuId: String): Flow<BaseResponse> =
        restaurantRemoteDataSource.deleteRestaurantMenu(menuId)
}