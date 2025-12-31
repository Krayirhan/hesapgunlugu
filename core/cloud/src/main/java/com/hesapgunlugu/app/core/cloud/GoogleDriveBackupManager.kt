package com.hesapgunlugu.app.core.cloud

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Drive backup manager
 * Handles cloud backup and restore operations
 */
@Singleton
class GoogleDriveBackupManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        companion object {
            private const val BACKUP_FOLDER_NAME = "FinanceAppBackups"
        }

        private val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()

        private val googleSignInClient: GoogleSignInClient by lazy {
            GoogleSignIn.getClient(context, googleSignInOptions)
        }

        private fun createDriveService(account: GoogleSignInAccount): Drive {
            val credential =
                GoogleAccountCredential.usingOAuth2(
                    context,
                    listOf(DriveScopes.DRIVE_FILE),
                ).apply {
                    selectedAccount = account.account
                }

            return Drive.Builder(
                NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential,
            )
                .setApplicationName(context.packageName)
                .build()
        }

        private suspend fun getOrCreateBackupFolderId(drive: Drive): String =
            withContext(Dispatchers.IO) {
                val query =
                    "mimeType='application/vnd.google-apps.folder' and " +
                        "name='${BACKUP_FOLDER_NAME.replace("'", "\\'")}' and trashed=false"

                val existing =
                    drive.files().list()
                        .setQ(query)
                        .setFields("files(id,name)")
                        .execute()
                        .files
                        ?.firstOrNull()

                if (existing != null) {
                    return@withContext existing.id
                }

                val metadata =
                    File().apply {
                        name = BACKUP_FOLDER_NAME
                        mimeType = "application/vnd.google-apps.folder"
                    }

                drive.files().create(metadata)
                    .setFields("id")
                    .execute()
                    .id
            }

        private fun findExistingBackupId(
            drive: Drive,
            folderId: String,
            fileName: String,
        ): String? {
            val safeName = fileName.replace("'", "\\'")
            val query = "name='$safeName' and '$folderId' in parents and trashed=false"
            return drive.files().list()
                .setQ(query)
                .setFields("files(id,name)")
                .execute()
                .files
                ?.firstOrNull()
                ?.id
        }

        /**
         * Get sign-in intent
         */
        fun getSignInIntent(): Intent {
            return googleSignInClient.signInIntent
        }

        /**
         * Get last signed in account
         */
        fun getLastSignedInAccount(): GoogleSignInAccount? {
            return GoogleSignIn.getLastSignedInAccount(context)
        }

        /**
         * Handle sign-in result intent
         */
        fun handleSignInResult(data: Intent?): Result<GoogleSignInAccount> {
            return try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                Result.success(account)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * Sign out
         */
        suspend fun signOut() {
            googleSignInClient.signOut()
        }

        /**
         * Check if user is signed in
         */
        fun isSignedIn(): Boolean {
            return getLastSignedInAccount() != null
        }

        /**
         * Upload backup to Google Drive
         * @param fileName File name for backup
         * @param content Backup content (JSON string)
         * @return Success status
         */
        suspend fun uploadBackup(
            fileName: String,
            content: String,
        ): Result<String> {
            return try {
                val account =
                    getLastSignedInAccount()
                        ?: return Result.failure(Exception("Not signed in to Google"))

                withContext(Dispatchers.IO) {
                    val drive = createDriveService(account)
                    val folderId = getOrCreateBackupFolderId(drive)
                    val contentBytes = content.toByteArray(Charsets.UTF_8)
                    val mediaContent = ByteArrayContent("application/json", contentBytes)

                    val existingId = findExistingBackupId(drive, folderId, fileName)
                    val uploaded =
                        if (existingId != null) {
                            drive.files().update(existingId, null, mediaContent)
                                .setFields("id,name")
                                .execute()
                        } else {
                            val metadata =
                                File().apply {
                                    name = fileName
                                    parents = listOf(folderId)
                                    mimeType = "application/json"
                                }
                            drive.files().create(metadata, mediaContent)
                                .setFields("id,name")
                                .execute()
                        }

                    Result.success(uploaded.id)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * Download backup from Google Drive
         * @param fileId Google Drive file ID
         * @return Backup content as JSON string
         */
        suspend fun downloadBackup(fileId: String): Result<String> {
            return try {
                val account =
                    getLastSignedInAccount()
                        ?: return Result.failure(Exception("Not signed in to Google"))

                withContext(Dispatchers.IO) {
                    val drive = createDriveService(account)
                    val output = ByteArrayOutputStream()
                    drive.files().get(fileId).executeMediaAndDownloadTo(output)
                    Result.success(output.toString(Charsets.UTF_8.name()))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * List all backups from Google Drive
         * @return List of backup file IDs and names
         */
        suspend fun listBackups(): Result<List<BackupFile>> {
            return try {
                val account =
                    getLastSignedInAccount()
                        ?: return Result.failure(Exception("Not signed in to Google"))

                withContext(Dispatchers.IO) {
                    val drive = createDriveService(account)
                    val folderId = getOrCreateBackupFolderId(drive)
                    val query = "'$folderId' in parents and trashed=false and mimeType='application/json'"

                    val files =
                        drive.files().list()
                            .setQ(query)
                            .setFields("files(id,name,size,createdTime)")
                            .execute()
                            .files
                            ?.map { file ->
                                BackupFile(
                                    id = file.id,
                                    name = file.name,
                                    size = file.getSize()?.toLong() ?: 0L,
                                    createdTime = file.createdTime?.value ?: 0L,
                                )
                            }
                            ?: emptyList()

                    Result.success(files)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        /**
         * Delete backup from Google Drive
         */
        suspend fun deleteBackup(fileId: String): Result<Unit> {
            return try {
                val account =
                    getLastSignedInAccount()
                        ?: return Result.failure(Exception("Not signed in to Google"))

                withContext(Dispatchers.IO) {
                    val drive = createDriveService(account)
                    drive.files().delete(fileId).execute()
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

/**
 * Backup file info
 */
data class BackupFile(
    val id: String,
    val name: String,
    val size: Long,
    val createdTime: Long,
)
