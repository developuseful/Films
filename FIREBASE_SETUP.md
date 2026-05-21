# Firebase интеграция для FilmsApp

Это руководство по интеграции Firebase в приложение.

## ✅ Что уже сделано

Я уже обновил необходимые файлы проекта:

1. ✅ Добавлен Google Services plugin в `build.gradle.kts` (root)
2. ✅ Применен Google Services plugin в `app/build.gradle.kts`
3. ✅ Добавлены Firebase зависимости в `gradle/libs.versions.toml`
4. ✅ Создан `FirebaseHelper.kt` - вспомогательный класс для работы с Firebase
5. ✅ Создан `FirebaseModule.kt` - Hilt модуль для внедрения зависимостей

## 📋 Что нужно сделать

### 1. Создание Firebase проекта

1. Перейдите на [Firebase Console](https://console.firebase.google.com/)
2. Нажмите **"Create a project"** или **"Add project"**
3. Введите название проекта (например, `FilmsApp`)
4. Выберите страну и согласитесь с условиями
5. Нажмите **"Create project"** и дождитесь завершения

### 2. Добавление Android приложения

1. В Firebase Console нажмите **"+ Add app"**
2. Выберите **Android**
3. Заполните данные:
   - **Android Package Name**: `com.develop.films`
   - **App Nickname**: `Films` (опционально)
   - **SHA-1 Certificate Fingerprint** (опционально, можно пропустить)
4. Нажмите **"Register App"**

### 3. Скачивание google-services.json

1. На следующем этапе нажмите **"Download google-services.json"**
2. Файл скачается на ваш компьютер

### 4. Добавление файла в проект

1. Скопируйте скачанный `google-services.json`
2. Поместите его в папку **`app/`** вашего проекта (рядом с build.gradle.kts)
3. Структура должна выглядеть так:
   ```
   app/
   ├── build.gradle.kts
   ├── google-services.json  ← Файл должен быть здесь
   ├── src/
   └── ...
   ```

### 5. Синхронизация проекта

1. Откройте проект в Android Studio
2. Нажмите **File → Sync Now** (или Ctrl+Alt+Y)
3. Дождитесь завершения синхронизации

### 6. Включение Firebase сервисов

1. Вернитесь в Firebase Console
2. Для каждого сервиса, который вам нужен, включите его:

#### Authentication (Аутентификация)
1. В боковом меню выберите **"Authentication"**
2. Нажмите **"Get started"**
3. Выберите методы входа:
   - **Email/Password** - для входа с email и пароля
   - **Google** - для входа через Google (опционально)

#### Cloud Firestore (База данных)
1. В боковом меню выберите **"Firestore Database"**
2. Нажмите **"Create Database"**
3. Выберите режим: **"Start in test mode"** (для разработки)
4. Выберите расположение сервера
5. Нажмите **"Create"**

#### Storage (Хранилище файлов)
1. В боковом меню выберите **"Storage"**
2. Нажмите **"Get started"**
3. Выберите режим: **"Start in test mode"**
4. Выберите расположение сервера
5. Нажмите **"Done"**

#### Realtime Database (База данных в реальном времени)
1. В боковом меню выберите **"Realtime Database"**
2. Нажмите **"Create Database"**
3. Выберите расположение
4. Выберите режим: **"Start in test mode"**
5. Нажмите **"Enable"**

#### Analytics (Аналитика)
1. В боковом меню выберите **"Analytics"**
2. Нажмите **"Get started"**
3. Аналитика будет включена автоматически

## 🚀 Как использовать Firebase в коде

### Вариант 1: Использование FirebaseHelper (простой способ)

```kotlin
import com.develop.films.firebase.FirebaseHelper

// Регистрация
FirebaseHelper.signUp(email, password).onSuccess { userId ->
    Log.d("AUTH", "User registered: $userId")
}.onFailure { error ->
    Log.e("AUTH", "Registration failed", error)
}

// Вход
FirebaseHelper.signIn(email, password).onSuccess { userId ->
    Log.d("AUTH", "User logged in: $userId")
}

// Сохранение в Firestore
val filmData = mapOf(
    "title" to "Film Name",
    "description" to "Some description",
    "rating" to 8.5
)
FirebaseHelper.saveToFirestore("films", "film1", filmData)

// Получение из Firestore
FirebaseHelper.getFromFirestore("films", "film1").onSuccess { data ->
    Log.d("FIRESTORE", "Film: $data")
}
```

### Вариант 2: Использование Hilt для внедрения зависимостей

```kotlin
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilmsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {
    
    fun loadFilms() {
        firestore.collection("films")
            .get()
            .addOnSuccessListener { snapshot ->
                val films = snapshot.documents.mapNotNull { it.data }
                // Обработка данных
            }
    }
}
```

### Вариант 3: Использование Coroutines (рекомендуется)

```kotlin
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

viewModelScope.launch {
    // Асинхронная работа с Firebase
    val result = FirebaseHelper.getFromFirestore("films", "film1")
    result.onSuccess { film ->
        // Обновить UI
    }.onFailure { error ->
        // Показать ошибку
    }
}
```

## 🔒 Безопасность в Production

Перед выпуском приложения обновите правила безопасности:

### Firestore Rules
1. Перейдите в **Firestore Database → Rules**
2. Замените содержимое на:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### Storage Rules
1. Перейдите в **Storage → Rules**
2. Замените содержимое на:

```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 📚 Полезные ссылки

- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Console](https://console.firebase.google.com/)
- [Android Firebase Guide](https://firebase.google.com/docs/android/setup)
- [Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Firebase Authentication](https://firebase.google.com/docs/auth)

## ❓ Решение проблем

### Ошибка: "google-services.json not found"
- Убедитесь, что файл находится в папке `app/`

### Ошибка при синхронизации
- Очистите Gradle cache: **File → Invalidate Caches / Restart → Invalidate and Restart**

### Проблемы с аутентификацией
- Убедитесь, что в Firebase Console включена "Email/Password" аутентификация

### Невозможно сохранить данные в Firestore
- Проверьте правила безопасности в Firestore (должны быть в "test mode" для разработки)

---

**Все готово! Следуйте шагам выше и Firebase будет полностью интегрирован в ваше приложение.**
