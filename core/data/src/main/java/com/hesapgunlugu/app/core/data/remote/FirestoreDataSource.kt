package com.hesapgunlugu.app.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.hesapgunlugu.app.core.data.local.TransactionEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Firestore veri kaynağı
 *
 * Kullanıcı bazlı veri senkronizasyonu sağlar.
 * Tüm veriler kullanıcının UID'si altında saklanır.
 *
 * Yapı:
 * users/{userId}/transactions/{transactionId}
 * users/{userId}/settings/{settingKey}
 * users/{userId}/categories/{categoryId}
 */
@Singleton
class FirestoreDataSource
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        private val auth: FirebaseAuth,
    ) {
        companion object {
            private const val USERS_COLLECTION = "users"
            private const val TRANSACTIONS_COLLECTION = "transactions"
            private const val SETTINGS_COLLECTION = "settings"
        }

        /**
         * Mevcut kullanıcının UID'sini döndürür
         * @return Kullanıcı UID veya null
         */
        private fun getCurrentUserId(): String? = auth.currentUser?.uid

        /**
         * Kullanıcının transaction koleksiyonuna referans
         */
        private fun transactionsRef(userId: String) =
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(TRANSACTIONS_COLLECTION)

        /**
         * Kullanıcının settings koleksiyonuna referans
         */
        private fun settingsRef(userId: String) =
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .collection(SETTINGS_COLLECTION)

        // ==================== TRANSACTION İŞLEMLERİ ====================

        /**
         * Transaction'ı Firestore'a kaydeder
         */
        suspend fun saveTransaction(transaction: TransactionEntity): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    IllegalStateException("Kullanıcı giriş yapmamış"),
                )

            return try {
                val docRef = transactionsRef(userId).document(transaction.id.toString())
                val data = transactionToMap(transaction)
                docRef.set(data, SetOptions.merge()).await()
                Timber.d("Transaction Firestore'a kaydedildi: ${transaction.id}")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Firestore'a kaydetme hatası")
                Result.failure(e)
            }
        }

        /**
         * Transaction'ı Firestore'dan siler
         */
        suspend fun deleteTransaction(transactionId: Int): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    IllegalStateException("Kullanıcı giriş yapmamış"),
                )

            return try {
                transactionsRef(userId).document(transactionId.toString()).delete().await()
                Timber.d("Transaction Firestore'dan silindi: $transactionId")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Firestore'dan silme hatası")
                Result.failure(e)
            }
        }

        /**
         * Kullanıcının tüm transaction'larını Firestore'dan çeker
         */
        suspend fun getAllTransactions(): Result<List<TransactionEntity>> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    IllegalStateException("Kullanıcı giriş yapmamış"),
                )

            return try {
                val snapshot: QuerySnapshot = transactionsRef(userId).get().await()
                val transactions =
                    snapshot.documents.mapNotNull { doc: DocumentSnapshot ->
                        mapToTransaction(doc.data)
                    }
                Timber.d("Firestore'dan ${transactions.size} transaction çekildi")
                Result.success(transactions)
            } catch (e: Exception) {
                Timber.e(e, "Firestore'dan veri çekme hatası")
                Result.failure(e)
            }
        }

        /**
         * Gerçek zamanlı transaction güncellemelerini dinler
         */
        fun observeTransactions(): Flow<List<TransactionEntity>> =
            callbackFlow {
                val userId = getCurrentUserId()
                if (userId == null) {
                    trySend(emptyList())
                    close()
                    return@callbackFlow
                }

                val listener: ListenerRegistration =
                    transactionsRef(userId)
                        .addSnapshotListener {
                                snapshot: QuerySnapshot?,
                                error: com.google.firebase.firestore.FirebaseFirestoreException?,
                            ->
                            if (error != null) {
                                Timber.e(error, "Firestore dinleme hatası")
                                return@addSnapshotListener
                            }

                            val transactions =
                                snapshot?.documents?.mapNotNull { doc: DocumentSnapshot ->
                                    mapToTransaction(doc.data)
                                } ?: emptyList()

                            trySend(transactions)
                        }

                awaitClose { listener.remove() }
            }

        // ==================== SETTINGS İŞLEMLERİ ====================

        /**
         * Kullanıcı ayarlarını Firestore'a kaydeder
         */
        suspend fun saveSettings(settings: Map<String, Any>): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    IllegalStateException("Kullanıcı giriş yapmamış"),
                )

            return try {
                val docRef = settingsRef(userId).document("preferences")
                docRef.set(settings, SetOptions.merge()).await()
                Timber.d("Ayarlar Firestore'a kaydedildi")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Ayarları kaydetme hatası")
                Result.failure(e)
            }
        }

        /**
         * Kullanıcı ayarlarını Firestore'dan çeker
         */
        suspend fun getSettings(): Result<Map<String, Any>> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    IllegalStateException("Kullanıcı giriş yapmamış"),
                )

            return try {
                val snapshot: DocumentSnapshot = settingsRef(userId).document("preferences").get().await()
                val settings: Map<String, Any> = snapshot.data ?: emptyMap()
                Timber.d("Ayarlar Firestore'dan çekildi")
                Result.success(settings)
            } catch (e: Exception) {
                Timber.e(e, "Ayarları çekme hatası")
                Result.failure(e)
            }
        }

        // ==================== SENKRONİZASYON ====================

        /**
         * Yerel veritabanını Firestore ile senkronize eder
         * Çakışma durumunda Firestore verisi önceliklidir (son yazan kazanır)
         */
        suspend fun syncFromFirestore(): Result<List<TransactionEntity>> {
            return getAllTransactions()
        }

        /**
         * Yerel verileri toplu olarak Firestore'a yükler
         */
        suspend fun syncToFirestore(transactions: List<TransactionEntity>): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    IllegalStateException("Kullanıcı giriş yapmamış"),
                )

            return try {
                val batch = firestore.batch()
                transactions.forEach { transaction ->
                    val docRef = transactionsRef(userId).document(transaction.id.toString())
                    batch.set(docRef, transactionToMap(transaction), SetOptions.merge())
                }
                batch.commit().await()
                Timber.d("${transactions.size} transaction Firestore'a senkronize edildi")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Toplu senkronizasyon hatası")
                Result.failure(e)
            }
        }

        /**
         * Kullanıcının tüm verilerini siler (GDPR uyumlu)
         */
        suspend fun deleteAllUserData(): Result<Unit> {
            val userId =
                getCurrentUserId() ?: return Result.failure(
                    IllegalStateException("Kullanıcı giriş yapmamış"),
                )

            return try {
                // Transaction'ları sil
                val transactionDocs: QuerySnapshot = transactionsRef(userId).get().await()
                val batch = firestore.batch()
                transactionDocs.documents.forEach { doc: DocumentSnapshot ->
                    batch.delete(doc.reference)
                }

                // Settings'i sil
                val settingsDocs: QuerySnapshot = settingsRef(userId).get().await()
                settingsDocs.documents.forEach { doc: DocumentSnapshot ->
                    batch.delete(doc.reference)
                }

                batch.commit().await()
                Timber.d("Kullanıcının tüm verileri Firestore'dan silindi")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Veri silme hatası")
                Result.failure(e)
            }
        }

        // ==================== YARDIMCI METOTLAR ====================

        private fun transactionToMap(entity: TransactionEntity): Map<String, Any?> {
            return mapOf(
                "id" to entity.id,
                "title" to entity.title,
                "amount" to entity.amount,
                "type" to entity.type,
                "category" to entity.category,
                "emoji" to entity.emoji,
                "date" to entity.date,
                "scheduledPaymentId" to entity.scheduledPaymentId,
                "updatedAt" to System.currentTimeMillis(),
            )
        }

        private fun mapToTransaction(data: Map<String, Any?>?): TransactionEntity? {
            if (data == null) return null

            return try {
                TransactionEntity(
                    id = (data["id"] as? Number)?.toInt() ?: 0,
                    title = data["title"] as? String ?: "",
                    amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
                    type = data["type"] as? String ?: "EXPENSE",
                    category = data["category"] as? String ?: "",
                    emoji = data["emoji"] as? String ?: "",
                    date = (data["date"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                    scheduledPaymentId = (data["scheduledPaymentId"] as? Number)?.toLong(),
                )
            } catch (e: Exception) {
                Timber.e(e, "Transaction dönüşüm hatası")
                null
            }
        }
    }
