package com.hesapgunlugu.app.core.data.repository

import com.hesapgunlugu.app.core.data.local.TransactionDao
import com.hesapgunlugu.app.core.data.remote.FirestoreDataSource
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firestore ile yerel veritabanı senkronizasyonunu yöneten repository
 *
 * Kullanıcı giriş yaptığında Firestore'dan verileri çeker ve yerel veritabanına yazar.
 * Kullanıcı çıkış yaptığında yerel veritabanını temizler (isteğe bağlı).
 */
@Singleton
class SyncRepository
    @Inject
    constructor(
        private val transactionDao: TransactionDao,
        private val firestoreDataSource: FirestoreDataSource,
    ) {
        /**
         * Firestore'dan verileri çekip yerel veritabanına yazar
         * Kullanıcı giriş yaptığında çağrılmalı
         *
         * @param clearLocalFirst Önce yerel verileri temizle
         * @return Result<Int> Senkronize edilen transaction sayısı
         */
        suspend fun syncFromFirestore(clearLocalFirst: Boolean = true): Result<Int> {
            return try {
                // Firestore'dan verileri çek
                val firestoreResult = firestoreDataSource.getAllTransactions()

                if (firestoreResult.isFailure) {
                    return Result.failure(
                        firestoreResult.exceptionOrNull() ?: Exception("Firestore senkronizasyon hatası"),
                    )
                }

                val transactions = firestoreResult.getOrNull() ?: emptyList()

                // Yerel veritabanını temizle (isteğe bağlı)
                if (clearLocalFirst) {
                    transactionDao.deleteAllTransactions()
                    Timber.d("Yerel veritabanı temizlendi")
                }

                // Firestore verilerini yerel veritabanına yaz
                transactions.forEach { entity ->
                    transactionDao.insertTransaction(entity)
                }

                Timber.d("${transactions.size} transaction Firestore'dan senkronize edildi")
                Result.success(transactions.size)
            } catch (e: Exception) {
                Timber.e(e, "Firestore'dan senkronizasyon hatası")
                Result.failure(e)
            }
        }

        /**
         * Yerel veritabanındaki tüm verileri Firestore'a yükler
         * İlk kayıt veya veri transferi için kullanılır
         *
         * @return Result<Int> Yüklenen transaction sayısı
         */
        suspend fun syncToFirestore(): Result<Int> {
            return try {
                val transactions = transactionDao.getAllTransactionsForExport()

                val result = firestoreDataSource.syncToFirestore(transactions)

                if (result.isFailure) {
                    return Result.failure(
                        result.exceptionOrNull() ?: Exception("Firestore'a yükleme hatası"),
                    )
                }

                Timber.d("${transactions.size} transaction Firestore'a yüklendi")
                Result.success(transactions.size)
            } catch (e: Exception) {
                Timber.e(e, "Firestore'a senkronizasyon hatası")
                Result.failure(e)
            }
        }

        /**
         * Kullanıcı çıkış yaptığında yerel verileri temizler
         */
        suspend fun clearLocalData(): Result<Unit> {
            return try {
                transactionDao.deleteAllTransactions()
                Timber.d("Yerel veriler temizlendi (kullanıcı çıkışı)")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Yerel verileri temizleme hatası")
                Result.failure(e)
            }
        }

        /**
         * Kullanıcının Firestore'daki tüm verilerini siler (GDPR)
         */
        suspend fun deleteAllCloudData(): Result<Unit> {
            return firestoreDataSource.deleteAllUserData()
        }
    }
