# Security & Privacy Checklist

Proyek Tsembilan Keyboard **HARUS** selalu mematuhi checklist ini pada setiap rilis untuk memastikan tidak ditandai sebagai *malware*, *spyware*, atau aplikasi yang melanggar kebijakan privasi oleh Google Play Protect maupun OS Android.

- [x] Tidak ada `INTERNET` permission
- [x] Tidak ada `READ_SMS`
- [x] Tidak ada `READ_CONTACTS`
- [x] Tidak ada `READ_NOTIFICATIONS`
- [x] Tidak ada Accessibility Service
- [x] Tidak ada Notification Listener
- [x] Tidak ada overlay permission (`SYSTEM_ALERT_WINDOW`)
- [x] Tidak ada `QUERY_ALL_PACKAGES`
- [x] Tidak ada analytics SDK (misal: Firebase Analytics, Mixpanel, Crashlytics)
- [x] Tidak ada ads SDK (misal: AdMob, Facebook Audience Network)
- [x] Tidak ada network library (misal: Retrofit, OkHttp, Volley)
- [x] Tidak ada logging input user (karakter, angka, tombol yang ditekan) di `LogCat`
- [x] Tidak ada penyimpanan input user ke `SharedPreferences`, `Room`, SQLite, maupun file lokal
- [x] Tidak ada clipboard scraping / pembacaan teks dari clipboard
- [x] APK release harus *signed* dengan keystore yang konsisten
- [x] `targetSdk` tetap modern (saat ini 35)
