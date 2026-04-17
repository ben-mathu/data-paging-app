# data-paging-app

Libraries
// Paging
implementation("androidx.paging:paging-runtime-ktx:$pagingVersion")
implementation("androidx.paging:paging-rxjava3:$pagingVersion")
implementation("androidx.paging:paging-compose:$pagingVersion")

@Module
InstallIn(SingleToComponent
Class Module

@OptIn(ExperimentalPagingApi::class)
@Provides
@Singleton
fun provideTransactionsPager(database: AppDatabase,
                         transactionApiService: TransactionApiService
): Pager<Int, Transaction> {
return Pager(
    config = PagingConfig(10),
    remoteMediator = TransactionsRemoteMediator(TRANSACTION_LABEL, database, transactionApiService),
    pagingSourceFactory = { database.transactionsDao().getPaidOutTransactions() }
)
}

@Dao
Class Dao
abstract fun getPaidOutTransactions(): PagingSource<Int, Transaction>

Remote Key

data class RemoteKey(@PrimaryKey val label: String, val nextKey: Int?, val prevKey: Int?)

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE label = :query")
    suspend fun remoteKeyByQuery(query: String): RemoteKey?

    @Query("DELETE FROM remote_keys WHERE label = :query")
    suspend fun deleteByQuery(query: String)
}

Class Mediator
Class params
private val query: String,
private val database: AppDatabase,
private val transactionApiService: TransactionApiService

override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Transaction>
    ): MediatorResult {
        Log.d(TAG, "load -> updating the data sources...")
        return try {
            val loadKey: Int = when(loadType) {
                // invalidates the data to request new data from remote data source
                LoadType.REFRESH -> {
                    Log.d(TAG, "load -> Trying to understand refresh does")
                    remoteKeyDao.remoteKeyByQuery(query)?.nextKey?:1
                }

                LoadType.PREPEND -> {
                    Log.d(TAG, "load -> Trying to understand prepend does")
                    val remoteKey = remoteKeyDao.remoteKeyByQuery(query = query)

                    if (remoteKey?.prevKey == null)
                        return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKey.prevKey
                }
                LoadType.APPEND -> {
                    Log.d(TAG, "load -> Trying to understand append does")
                    val remoteKey = remoteKeyDao.remoteKeyByQuery(query = query)

                    if (remoteKey?.nextKey == null)
                        return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKey.nextKey
                }
            }

            val response = transactionApiService.getAllTransactions(page = loadKey)
            val prevPage = if (loadKey == 1) null else loadKey - 1
            val nextPage = if (response.transactions.size < state.config.pageSize) null else loadKey + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d(TAG, "load -> refresh triggered updating database")
                    remoteKeyDao.deleteByQuery(query)
                    transactionDao.updateTransactionRemoveQuery(query)
                }

                remoteKeyDao.insertOrReplace(RemoteKey(query, nextPage, prevPage))

                for (transaction in response.transactions)
                    transaction.label = TRANSACTION_LABEL
                transactionDao.insertAll(response.transactions)
            }

            MediatorResult.Success(endOfPaginationReached = response.transactions.size < state.config.pageSize)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        }
    }
    
    Repository
    
    @Inject
    private val transactionPager: Pager<Int, Transaction>
 fun getPaidOutTransactions(): Flow<PagingData<Transaction>> {
        return transactionPager.flow
    }
    
    // viewmodel
val paidOutTransactionsFlow: Flow<PagingData<Transaction>> = transactionRepository.getPaidOutTransactions()
        .cachedIn(viewModelScope)
        
View Class
viewModel.paidOutTransactionsFlow.collectAsLazyPagingItems()

// First thing show circular loader
if (paidOutTransactionsPages.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
LazyColumn{
if (paidOutTransactionsPages.itemCount > 0) {
    items(
        count = paidOutTransactionsPages.itemCount,
        key = paidOutTransactionsPages.itemKey { it.transactionId!! }
    ) { index ->
        val transaction = paidOutTransactionsPages[index]
        ...
        }

// refreshing at bottom of page
item {
            if (paidOutTransactionsPages.loadState.append is LoadState.Loading) {
                CircularProgressIndicator()
            }
        }
}


