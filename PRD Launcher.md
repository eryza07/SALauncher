# Product Requirement Document (PRD)
## ZalithLauncher2 Optimized Edition (Zalith-OE)

| Metadata | Detail |
| :--- | :--- |
| **Versi Produk** | 1.0.0 (MVP) |
| **Repositori Basis** | `ZalithLauncher/ZalithLauncher2` |
| **Target Platform** | Android 8.0 (API 26) s.d. Android 14+ (API 34) |
| **Kategori** | Gaming Runtime, Utility & Launcher Environment |
| **Status Dokumen** | Siap untuk Pengerjaan Teknis (Ready for Engineering) |

---

## 1. Ringkasan Eksekutif
ZalithLauncher2 Optimized Edition (Zalith-OE) adalah launcher Minecraft Java Edition berbasis Android yang dimodifikasi untuk memprioritaskan efisiensi memori (RAM), modularitas renderer grafis, dan otomatisasi diagnosa kegagalan (crash analyzer). 

Aplikasi ini menargetkan pengguna smartphone entry-level hingga mid-range yang kerap mengalami *force close* akibat *Out-of-Memory (OOM)* Android, salah konfigurasi renderer grafis, atau bentrok dependensi mod. Dengan mengeliminasi jejak memori launcher saat game berjalan serta mengintegrasikan sistem pelaporan crash ke platform web eksternal (mclo.gs), Zalith-OE menghadirkan pengalaman bermain dan pemecahan masalah yang jauh lebih stabil dan ramah pengguna.

---

## 2. Latar Belakang & Problem Statement

### 2.1 Latar Belakang
Menjalankan Minecraft Java Edition di atas sistem operasi Android menuntut abstraksi berlapis:
1. Lapisan JVM (OpenJDK) untuk eksekusi bytecode game.
2. Lapisan Translation Native (NDK/C++) seperti gl4es, Zink, ANGLE, atau MobileGL untuk menerjemahkan instruksi desktop OpenGL ke OpenGL ES/Vulkan mobile.
3. Lapisan Antarmuka Pengguna (Android SDK/UI Activity).

### 2.2 Problem Statement
* **Bottleneck Konsumsi RAM:** Pada perangkat dengan kapasitas RAM 3–4 GB, launcher Android default tetap berjalan di background bersamaan dengan JVM game, memicu Android Low Memory Killer (LMK) untuk menghentikan proses game secara paksa (*Signal 9 / Out of Memory*).
* **Fragmentasi Driver Grafis:** Banyak pengguna mencoba plugin pihak ketiga (MobileGL, gl4es mod) namun sering mengalami *black screen* atau crash instan tanpa mengetahui renderer mana yang kompatibel dengan GPU mereka (Adreno vs. Mali).
* **Kompleksitas Log Error:** Log crash Java dan native dump sangat panjang dan membingungkan pengguna awam. Pengguna kesulitan membagikan log tersebut ke forum/komunitas bantuan teknis.

---

## 3. Goals, Non-Goals, dan Metrik Sukses

### 3.1 Goals
* Memangkas konsumsi RAM background launcher hingga di bawah 35 MB saat proses game aktif.
* Menyediakan arsitektur renderer modular yang mendukung renderer bawaan sekaligus plugin eksternal (MobileGL/gl4es).
* Menghadirkan sistem diagnosa crash lokal berbasis regex yang otomatis menjelaskan penyebab error dan terintegrasi dengan viewer web mclo.gs API.
* Menerapkan pemisahan instance (profil) terisolasi untuk folder `mods/` dan `config/` dengan tetap berbagi cache aset vanilla secara global.

### 3.2 Non-Goals (v1.0)
* Tidak membangun rendering engine OpenGL/Vulkan kustom dari nol (tetap mengandalkan pipeline translation layer yang sudah ada).
* Tidak menyediakan sistem cloud save / sinkronisasi data antarperangkat.
* Tidak menyediakan integrasi server proxy multiplayer P2P bawaan.

### 3.3 Metrik Sukses (KPI)
* **Crash Reduction:** Penurunan frekuensi crash bertipe OOM / Signal 9 minimal 40% pada perangkat RAM 3–4 GB.
* **Launcher Footprint:** Penggunaan RAM background di bawah 35 MB saat JVM aktif.
* **Diagnostic Success Rate:** Minimal 90% crash yang umum (OOM, missing mod, incompatible driver) berhasil dipetakan ke pesan diagnosa bahasa manusia.
* **Upload Efficiency:** Pengunggahan log error ke mclo.gs tuntas dalam waktu < 5 detik pada koneksi internet standar (≥ 5 Mbps).

---

## 4. Target Pengguna & Persona

### 4.1 Persona 1: Budget Mobile Gamer (Rian, 17 Tahun)
* **Karakteristik:** Menggunakan ponsel Android RAM 4 GB (chipset Helio G85/Snapdragon 680).
* **Kebutuhan:** Ingin memainkan Minecraft versi 1.20.x dengan mod Fabric performa (Sodium/Lithium) tanpa mengalami *force close*.
* **Pain Point:** Sering mengalami game tertutup sendiri secara mendadak dan tidak mengerti cara membaca isi file `latest.log`.

### 4.2 Persona 2: Advanced Modder (Budi, 22 Tahun)
* **Karakteristik:** Sering bereksperimen dengan plugin grafis pihak ketiga (seperti MobileGL custom builds) dan memainkan banyak variasi modpack.
* **Kebutuhan:** Memisahkan modpack antar-versi tanpa membuat memori internal penuh dan ingin kemudahan mengganti library render per profil.

---

## 5. Scope & Prioritas Fitur (MoSCoW)

### 5.1 Must-Have (Prioritas Utama - MVP)
* **MH-01:** *Aggressive Activity Unload* (penghancuran UI Activity saat game JVM aktif untuk membebaskan RAM).
* **MH-02:** *Smart Crash Analyzer* (parser teks log lokal untuk mendeteksi OOM, missing mod, dan driver failure).
* **MH-03:** Integrasi pengiriman crash log ke REST API `mclo.gs` dalam satu kali klik dengan fitur otomatis salin URL.
* **MH-04:** Sistem Multi-Akun (Microsoft OAuth 2.0 terenkripsi + Akun Offline/Lokal).
* **MH-05:** Pemasang otomatis (One-Click Installer) untuk Fabric dan Forge.
* **MH-06:** Dukungan renderer modular (renderer internal bawaan + slot plugin MobileGL / `.so` loader).
* **MH-07:** Isolasi direktori profil (Shared Global Cache untuk assets/libraries + Isolated Instance untuk mods/configs).

### 5.2 Should-Have (Prioritas Menengah)
* **SH-01:** *Safe Fallback Mode*: Notifikasi dan opsi pemulihan otomatis ke renderer default jika plugin grafis mengalami crash NDK.
* **SH-02:** *Adaptive Heap Limiter*: Penentuan batas alokasi `-Xmx` yang disarankan secara otomatis berdasarkan sisa RAM aktual perangkat.

### 5.3 Could-Have (Prioritas Sekunder / Pasca-MVP)
* **CH-01:** Deteksi periferal otomatis (auto-hide tombol virtual saat keyboard/mouse OTG terhubung).
* **CH-02:** Visual layout editor untuk tombol virtual dengan slider transparansi dinamis.

### 5.4 Won't-Have (Fase Mendatang)
* **WH-01:** Editor shaderpack in-app.
* **WH-02:** Emulator controller untuk mapping gamepad pihak ketiga via bluetooth proprietary.

---

## 6. User Journey & Alur Utama

```
[Mulai Aplikasi] 
       │
       ▼
[Manajemen Akun] ──► (Pilih Akun Microsoft / Offline)
       │
       ▼
[Pilih / Konfigurasi Profil] ──► (Pilih Versi, Mod Loader, & Renderer)
       │
       ▼
[Klik "Mainkan"] 
       │
       ├─► Activity UI Dihancurkan (finish())
       ├─► GameWatcherService Aktif (< 30 MB RAM)
       └─► JVM Dieksekusi
       │
   ┌───┴────────────────────────┐
   │                            │
   ▼                            ▼
[Sesi Normal Selesai]     [Game Mengalami Crash]
   │                            │
   │                            ▼
   │                      [CrashAnalyzerActivity Dibuka]
   │                            │
   │                      ├─► Tampilkan Penyebab Utama (Bahasa Manusia)
   │                      └─► Klik "Upload & Bagikan Log"
   │                            │
   │                            ▼
   │                      (Kirim ke mclo.gs ──► URL Tersalin)
   │                            │
   └────────────┬───────────────┘
                │
                ▼
        [Kembali ke Launcher]
```

---

## 7. User Stories & Acceptance Criteria

### US-01: Lifecycle Aggressive Memory Unload
* **Sebagai** pengguna ponsel berspesifikasi RAM 3–4 GB,
* **Saya ingin** launcher melepaskan seluruh memori antarmuka ketika game dimulai,
* **Agar** kapasitas RAM tersedia penuh untuk JVM game dan terhindar dari kill oleh Android LMK.
* **Acceptance Criteria:**
  * **Given:** Pengguna menekan tombol "Mainkan" pada profil yang valid.
  * **When:** Proses runtime JVM telah mencapai status inisialisasi surface rendering.
  * **Then:** `MainActivity` harus memanggil siklus `finish()`, membebaskan seluruh bitmap dan layout cache dari memory heap Android.
  * **Given:** Proses JVM game berhenti (baik secara normal maupun crash).
  * **When:** Status exit-code diterima oleh `GameWatcherService`.
  * **Then:** Launcher harus otomatis meluncurkan kembali `MainActivity` atau `CrashAnalyzerActivity` tanpa mengalami *state loss*.

### US-02: Diagnosa Crash Otomatis & Integrasi mclo.gs
* **Sebagai** pemain yang mengalami kendala saat game crash,
* **Saya ingin** mendapatkan ringkasan masalah dan tautan log web secara instan,
* **Agar** saya dapat memahami error tersebut atau membagikannya ke forum komunitas tanpa repot menyalin ribuan baris teks mentah.
* **Acceptance Criteria:**
  * **Given:** Proses game berhenti dengan exit-code tidak normal (`exitCode != 0`).
  * **When:** `CrashAnalyzerActivity` aktif.
  * **Then:** Sistem membaca file `latest.log`, mencocokkan baris error dengan pola kamus regex, dan menampilkan ringkasan diagnosa (contoh: *"Kehabisan Memori (OOM)"* atau *"Mod [X] membutuhkan Mod [Y] versi Z"*).
  * **Given:** Diagnosa berhasil dimuat di antarmuka.
  * **When:** Pengguna menekan tombol "Unggah & Salin Link Log".
  * **Then:** Aplikasi mengirimkan payload teks log ke endpoint `https://api.mclo.gs/1/log` via HTTP POST, menerima respon JSON berisi URL, dan menyalin tautan tersebut ke clipboard perangkat disertai konfirmasi toast.

### US-03: Dukungan Renderer Plugin Modular
* **Sebagai** pemain yang ingin mengoptimalkan frame rate,
* **Saya ingin** memilih antara renderer bawaan atau plugin pihak ketiga (seperti MobileGL),
* **Agar** performa rendering grafis dapat disesuaikan dengan arsitektur GPU saya.
* **Acceptance Criteria:**
  * **Given:** Terdapat file library renderer pihak ketiga di dalam direktori `renderers/`.
  * **When:** Pengguna membuka menu konfigurasi profil dan memilih plugin tersebut.
  * **Then:** Launcher harus mengatur path symlink library native (`.so`) yang ditargetkan sebelum memanggil entrypoint native JNI saat proses peluncuran.

---

## 8. Kebutuhan Fungsional (Functional Requirements)

| ID | Modul | Deskripsi Kebutuhan |
| :--- | :--- | :--- |
| **FR-001** | Autentikasi | Mendukung login Microsoft OAuth 2.0 dengan auto-refresh token serta pembuatan akun lokal/offline tanpa koneksi internet. |
| **FR-002** | Security | Seluruh refresh token dan kredensial sensitif wajib dienkripsi menggunakan Android Keystore / `EncryptedSharedPreferences`. |
| **FR-003** | Mod Installer | Integrasi API unduhan otomatis versi rilis resmi untuk Fabric Loader dan Minecraft Forge. |
| **FR-004** | Regex Parser | Parser teks lokal yang mengidentifikasi kondisi kegagalan: <br>1. *OOM:* `java.lang.OutOfMemoryError` <br>2. *Missing Mod:* `net.fabricmc.loader.discovery.ModResolutionException` / `MissingMandatoryModException` <br>3. *Native Crash:* `Fatal signal 11 (SIGSEGV)` / kegagalan simbol driver `.so`. |
| **FR-005** | API Dispatcher | Modul client HTTP untuk melakukan POST payload log mentah ke endpoint `https://api.mclo.gs/1/log` dengan batasan payload maksimal 10 MB. |
| **FR-006** | Struktur Instance | Pemisahan direktori profil: folder `mods/`, `config/`, dan `options.txt` berada di folder instance spesifik, sementara direktori `assets/` dan `libraries/` dipusatkan di root folder global. |
| **FR-007** | Plugin Loader | Antarmuka pemilihan driver yang memuat konfigurasi dynamic library (`libgl4es.so`, `libMobileGL.so`, dll.) berdasarkan pilihan profil pengguna. |

---

## 9. Kebutuhan Non-Fungsional (Non-Functional Requirements)

### 9.1 Performa & Efisiensi Sumber Daya
* **NFR-01 (Memory Footprint):** Service watcher launcher tidak boleh menggunakan lebih dari 35 MB RAM ketika JVM aktif.
* **NFR-02 (Cold Start Time):** Launcher harus siap menerima input pengguna dalam waktu kurang dari 2,5 detik sejak ikon aplikasi ditekan pada perangkat uji standar.

### 9.2 Stabilitas & Keandalan
* **NFR-03 (Network Timeout):** Permintaan HTTP ke API eksternal (mclo.gs / Mojang / Fabric meta) dibatasi dengan batas waktu (timeout) 10 detik. Jika jaringan gagal, antarmuka wajib menyediakan tombol fallback "Simpan Log ke Penyimpanan Lokal".
* **NFR-04 (Safe Native Catching):** Setiap kegagalan native exit (Signal 11/Signal 9) wajib ditangkap oleh daemon watcher dan tidak boleh memicu Android *App Not Responding* (ANR).

### 9.3 Kompatibilitas Sistem Operasi
* **NFR-05 (Android Target):** Minimum SDK: Android 8.0 (API Level 26); Target SDK: Android 14 (API Level 34).
* **NFR-06 (Storage Compliance):** Penyimpanan file game ditempatkan pada direktori aplikasi privat/eksternal standar (`Android/data/<package_name>/files/`) guna mematuhi regulasi Scoped Storage Android modern tanpa mewajibkan izin akses file luas (`MANAGE_EXTERNAL_STORAGE`).

---

## 10. Arsitektur Data & Struktur File

```
Android/data/com.zalith.launcher.oe/files/
├── .minecraft/
│   ├── assets/              <-- [SHARED GLOBAL] Aset tekstur vanilla, audio, index
│   ├── libraries/           <-- [SHARED GLOBAL] Native dependencies, JVM library
│   └── runtime/             <-- [SHARED GLOBAL] OpenJDK binaries (Java 8, 17, 21)
├── instances/
│   ├── instance_default/
│   │   ├── mods/            <-- [ISOLATED] Modpack khusus profil ini
│   │   ├── config/          <-- [ISOLATED] Konfigurasi mod
│   │   └── options.txt      <-- [ISOLATED] Pengaturan in-game
│   └── instance_modded/
│       ├── mods/
│       └── config/
├── plugins/
│   └── renderers/           <-- Direktori peletakan library plugin (.so / MobileGL)
└── logs/
    └── latest.log           <-- File log aktif yang dipantau oleh crash analyzer
```

---

## 11. Format Payload API mclo.gs

### Request (HTTP POST)
* **Endpoint:** `https://api.mclo.gs/1/log`
* **Header:** `Content-Type: application/x-www-form-urlencoded`
* **Body Form Data:**
  ```text
  content=<ISI_FILE_LATEST_LOG_DIPOTONG_MAKS_10MB>
  ```

### Response (JSON)
```json
{
  "success": true,
  "id": "a1b2c3d",
  "url": "https://mclo.gs/a1b2c3d",
  "raw": "https://api.mclo.gs/1/raw/a1b2c3d"
}
```

---

## 12. Matriks Manajemen Risiko & Rencana Mitigasi

| Risiko Teknis | Dampak | Probabilitas | Rencana Mitigasi |
| :--- | :--- | :--- | :--- |
| **Android LMK tetap mematikan JVM saat game berjalan** | Tinggi | Sedang | Terapkan alokasi `-Xmx` adaptif yang menyisakan minimal 1,2 GB RAM bebas untuk kebutuhan internal OS Android, serta jalankan `GameWatcherService` dalam status *Foreground Service* dengan notifikasi persisten prioritas rendah. |
| **Plugin MobileGL crash instan di GPU Mali** | Sedang | Tinggi | Sediakan deteksi otomatis chipset via OpenGL renderer string. Jika GPU Mali terdeteksi, berikan peringatan kompatibilitas sebelum profil dijalankan dan sediakan opsi *Safe Fallback* ke gl4es bawaan. |
| **Gagal mengunggah ke mclo.gs karena koneksi offline** | Rendah | Sedang | Tangani kegagalan HTTP gracefully: tampilkan dialog berisi opsi "Salin Teks Log", "Buka File Log", atau "Simpan ke Folder Unduhan". |
| **File log membengkak > 10 MB akibat mod looping error** | Rendah | Rendah | Parser hanya mengambil 500 baris pertama dan 2.500 baris terakhir dari file log sebelum mengirimkannya ke API payload. |

---

## 13. Rencana Pengujian (QA & Acceptance Testing)

1. **Pengujian Alokasi Memori (Low RAM Emulation):**
   * Uji coba peluncuran Minecraft 1.20.1 Fabric pada perangkat dengan RAM 3 GB atau emulator yang dibatasi RAM-nya.
   * Pastikan konsumsi memori background sebelum dan sesudah `finish()` pada Activity UI terpangkas sesuai target target NFR-01 (< 35 MB).
2. **Pengujian Skenario Crash:**
   * **Simulasi OOM:** Jalankan instance dengan parameter alokasi `-Xmx256M` hingga crash; verifikasi bahwa pesan "Kehabisan Memori" muncul di dialog.
   * **Simulasi Mod Bentrok:** Masukkan dua mod yang saling konflik secara sengaja; verifikasi bahwa nama mod bentrok tersebut berhasil diekstraksi oleh parser.
3. **Pengujian Ekspor Web:**
   * Lakukan pengujian tombol "Unggah & Salin Link Log" dan pastikan URL yang disalin valid dan dapat dibuka langsung melalui web browser.