package com.develop.films.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.tasks.await

/**
 * Helper class для работы с Firebase сервисами.
 * Упрощает использование Authentication, Firestore, Storage, Realtime Database и Analytics.
 */
object FirebaseHelper {
    private const val TAG = "FirebaseHelper"

    // Firebase instances
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // ==================== Authentication ====================

    /**
     * Регистрация нового пользователя с email и паролем
     */
    suspend fun signUp(email: String, password: String): Result<String> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        Log.d(TAG, "User created successfully: ${result.user?.uid}")
        Result.success(result.user?.uid ?: "")
    } catch (e: Exception) {
        Log.e(TAG, "Sign up failed", e)
        Result.failure(e)
    }

    /**
     * Вход пользователя с email и паролем
     */
    suspend fun signIn(email: String, password: String): Result<String> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        Log.d(TAG, "User signed in: ${result.user?.uid}")
        Result.success(result.user?.uid ?: "")
    } catch (e: Exception) {
        Log.e(TAG, "Sign in failed", e)
        Result.failure(e)
    }

    /**
     * Выход текущего пользователя
     */
    fun signOut() {
        auth.signOut()
        Log.d(TAG, "User signed out")
    }

    /**
     * Получение ID текущего пользователя
     */
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    /**
     * Проверка, авторизован ли пользователь
     */
    fun isUserAuthenticated(): Boolean = auth.currentUser != null

    // ==================== Firestore ====================

    /**
     * Сохранение документа в Firestore
     * @param collection - название коллекции
     * @param document - ID документа
     * @param data - данные для сохранения
     */
    suspend fun saveToFirestore(
        collection: String,
        document: String,
        data: Map<String, Any>
    ): Result<Unit> = try {
        firestore.collection(collection).document(document).set(data).await()
        Log.d(TAG, "Document saved: $collection/$document")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Save to Firestore failed", e)
        Result.failure(e)
    }

    /**
     * Получение документа из Firestore
     * @param collection - название коллекции
     * @param document - ID документа
     */
    suspend fun getFromFirestore(collection: String, document: String): Result<Map<String, Any>?> =
        try {
            val snapshot = firestore.collection(collection).document(document).get().await()
            Result.success(snapshot.data)
        } catch (e: Exception) {
            Log.e(TAG, "Get from Firestore failed", e)
            Result.failure(e)
        }

    /**
     * Получение всех документов из коллекции
     */
    suspend fun getCollectionFromFirestore(collection: String): Result<List<Map<String, Any>>> =
        try {
            val snapshot = firestore.collection(collection).get().await()
            val data = snapshot.documents.mapNotNull { it.data }
            Result.success(data)
        } catch (e: Exception) {
            Log.e(TAG, "Get collection from Firestore failed", e)
            Result.failure(e)
        }

    /**
     * Удаление документа из Firestore
     */
    suspend fun deleteFromFirestore(collection: String, document: String): Result<Unit> = try {
        firestore.collection(collection).document(document).delete().await()
        Log.d(TAG, "Document deleted: $collection/$document")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Delete from Firestore failed", e)
        Result.failure(e)
    }

    // ==================== Storage ====================

    /**
     * Загрузка файла в Firebase Storage
     * @param storagePath - путь в Storage (например: "images/photo.jpg")
     * @param fileBytes - байты файла
     */
    suspend fun uploadToStorage(storagePath: String, fileBytes: ByteArray): Result<String> = try {
        val ref = storage.reference.child(storagePath)
        val uploadTask = ref.putBytes(fileBytes).await()
        val downloadUrl = ref.downloadUrl.await().toString()
        Log.d(TAG, "File uploaded: $storagePath")
        Result.success(downloadUrl)
    } catch (e: Exception) {
        Log.e(TAG, "Upload to Storage failed", e)
        Result.failure(e)
    }

    /**
     * Загрузка файла в Firebase Storage по URI
     */
    suspend fun uploadToStorageByUri(storagePath: String, fileUri: android.net.Uri): Result<String> =
        try {
            val ref = storage.reference.child(storagePath)
            ref.putFile(fileUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d(TAG, "File uploaded: $storagePath")
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Upload to Storage failed", e)
            Result.failure(e)
        }

    // ==================== Realtime Database ====================

    /**
     * Сохранение данных в Realtime Database
     * @param path - путь в базе (например: "users/user1")
     * @param data - данные для сохранения
     */
    suspend fun saveToRealtimeDB(path: String, data: Any): Result<Unit> = try {
        database.reference.child(path).setValue(data).await()
        Log.d(TAG, "Data saved to Realtime DB: $path")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Save to Realtime DB failed", e)
        Result.failure(e)
    }

    /**
     * Получение данных из Realtime Database
     */
    suspend fun getFromRealtimeDB(path: String): Result<Any?> = try {
        val snapshot = database.reference.child(path).get().await()
        Result.success(snapshot.value)
    } catch (e: Exception) {
        Log.e(TAG, "Get from Realtime DB failed", e)
        Result.failure(e)
    }

    // ==================== Analytics ====================

    /**
     * Логирование кастомного события в Firebase Analytics
     */
    fun logEvent(eventName: String, bundle: android.os.Bundle? = null) {
        try {
            FirebaseAnalytics.getInstance(android.app.Application()).logEvent(eventName, bundle)
            Log.d(TAG, "Event logged: $eventName")
        } catch (e: Exception) {
            Log.e(TAG, "Analytics logging failed", e)
        }
    }

    /**
     * Установка пользовательских свойств для Analytics
     */
    fun setUserProperty(name: String, value: String) {
        try {
            FirebaseAnalytics.getInstance(android.app.Application()).setUserProperty(name, value)
            Log.d(TAG, "User property set: $name = $value")
        } catch (e: Exception) {
            Log.e(TAG, "Set user property failed", e)
        }
    }
}
