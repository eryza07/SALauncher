# UI/UX Product Requirement Document (PRD)
## ZalithLauncher2: Neobrutalism Design System & Interface Specifications

| Metadata | Detail |
| :--- | :--- |
| **Gaya Desain** | **Neobrutalism** (Bold Outlines, Hard Shadows, Retro-Pop Colors) |
| **Aplikasi Target** | ZalithLauncher2 Optimized Edition (Zalith-OE) |
| **Framework UI Target** | Android XML Layout / Jetpack Compose |
| **Orientasi Layar** | Fleksibel (Portrait untuk Launcher, Landscape untuk In-Game Virtual Controls) |
| **Dokumen Pendamping** | `PRD.md` (Core Architecture & Engineering PRD) |

---

## 1. Filosofi & Arah Visual

Kebanyakan launcher Android Minecraft menggunakan tema gelap (*dark mode*) yang monoton dan meniru interface PC secara kaku. Desain **Neobrutalism** pada Zalith-OE dirancang untuk memberikan identitas visual yang segar, enerjik, dan sangat mudah digunakan (accessible) bagi pemain mobile.

### 5 Pilar Visual Neobrutalism Zalith-OE:
1. **Border Hitam Tegas (*Heavy Strokes*):** Semua elemen interaktif dan container wajib memiliki border solid `2dp` s.d. `3dp` berwarna `#000000`.
2. **Bayangan Keras (*Hard Drop Shadows*):** Dilarang menggunakan *soft gaussian blur shadow*. Semua bayangan bersifat offset padat (misal: offset `X: 4dp, Y: 4dp` warna `#000000`).
3. **Palet Warna Pop & Kontras (*Vibrant Candy Palette*):** Kuning terang, ungu electric, hijau mint, dan pink cerah di atas kanvas krem/putih bersih.
4. **Window Box Styling:** Dialog, popup diagnosa, dan kartu profil mengadopsi gaya bingkai retro-desktop OS dengan title bar solid.
5. **Tipografi Punchy & Geometris:** Menggunakan font bold sans-serif berkarakter kuat (seperti *Plus Jakarta Sans*, *Syne*, atau *Outfit*).

---

## 2. Design System & Design Tokens

### 2.1 Color Palette Tokens

| Token Name | Hex Code | Role & Peruntukan |
| :--- | :--- | :--- |
| `surface-canvas` | `#F4F0EA` / `#FFFDF9` | Warna background utama aplikasi (warm retro cream) |
| `surface-card` | `#FFFFFF` | Background kartu default |
| `border-hard` | `#000000` | Border wajib untuk seluruh kartu, tombol, dan input |
| `shadow-hard` | `#000000` | Warna bayangan solid (tanpa alpha/blur) |
| `accent-primary` | `#FFE600` *(Electric Yellow)* | Tombol aksi utama ("MAINKAN", "DOWNLOAD") |
| `accent-secondary`| `#6B4EFF` *(Electric Violet)* | Status bar profil, chip mod loader, aksen aktif |
| `accent-mint` | `#2EE59D` *(Mint Neo)* | Status online, akun valid, indikator RAM aman |
| `accent-pink` | `#FF66C4` *(Bubblegum Pink)* | Indikator crash analyzer, peringatan, badge alert |
| `accent-orange`| `#FF8C32` *(Vivid Orange)* | Tombol pengaturan renderer, selector instance |
| `text-primary` | `#000000` | Teks utama dengan kontras maksimal |
| `text-muted` | `#4B4B4B` | Keterangan versi, deskripsi sekunder |

### 2.2 Elevation & Shadow Rules
* **Elevation Level 1 (Card & Button Static):**
  * Border: `2.5dp solid #000000`
  * Shadow Offset: `X: 4dp, Y: 4dp, Blur: 0dp, Color: #000000`
* **Elevation Level 2 (Floating Action Button & Modal):**
  * Border: `3dp solid #000000`
  * Shadow Offset: `X: 6dp, Y: 6dp, Blur: 0dp, Color: #000000`
* **State Pressed (Touch Down):**
  * Tombol bergerak diagonal ke kanan bawah sejauh `+4dp, +4dp`
  * Shadow offset mengecil menjadi `X: 0dp, Y: 0dp` (memberikan efek mekanikal klik nyata).

### 2.3 Corner Radius (Karakter Sudut)
* Mengadopsi **Softened Brutalism**: Sudut tidak dibuat 100% lancip agar tetap nyaman di layar sentuh Android.
* Standard Cards: `12dp`
* Action Buttons & Input Fields: `10dp`
* Tag / Badges / Pills: `999dp` (Full capsule pill)

---

## 3. Spesifikasi Layar Utama (Screen Layouts)

### 3.1 Layar Utama: Launcher Dashboard (Home Screen)

```
┌────────────────────────────────────────────────────────┐
│  [ZALITH-OE] ★                         [Akun: Steve ▼] │  <-- Top Bar (Border 2dp, Hard Shadow)
├────────────────────────────────────────────────────────┤
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ ■ PROFIL AKTIF                [Edit Profil ✎]   │  │  <-- Instance Window Card (Yellow / Cream)
│  │                                                  │  │
│  │   Minecraft 1.20.1 Fabric                        │  │
│  │   Renderer: MobileGL v2.4 (Plugin)               │  │
│  │   RAM: 2400 MB / 4096 MB                         │  │
│  │                                                  │  │
│  │   [ 42 Mods Terpasang ]   [ Memory Safe: OK ]    │  │  <-- Badges (Capsule Pill, White bg, 2dp border)
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  ┌─────────────────────────┐ ┌──────────────────────┐  │
│  │ ⚡ MODS & LOADER         │ │ ⚙ RENDERER SETTINGS  │  │  <-- Secondary Quick Cards (Mint & Violet)
│  │ Unduh Fabric / Forge    │ │ Atur Plugin .SO      │  │
│  └─────────────────────────┘ └──────────────────────┘  │
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │                                                  │  │
│  │              ▶   M A I N K A N                   │  │  <-- Massive Primary CTA (Electric Yellow,
│  │                                                  │  │      Border 3dp, Shadow 6dp 6dp #000)
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  [  ⌂ Home  ]   [  ▤ Profil  ]   [  ⬇ Mods  ]   [  ⚙ Set  ]  │  <-- Bottom Dock (Neobrutal Pill Style)
└────────────────────────────────────────────────────────┘
```

#### Komponen Utama:
1. **Window-Header Card:** Kartu profil aktif yang memiliki bar atas retro (seperti jendela desktop dengan ikon kotak kecil di pojok kanan).
2. **Massive Hero Button ("MAINKAN"):** Tombol besar berukuran tinggi minimal `64dp`, latar warna `accent-primary` (`#FFE600`), teks kapital bold berjarak tegas (*letter-spacing*), dan bayangan mekanikal 6dp.
3. **Bottom Navigation Dock:** Mengambang (`floating dock`) dengan sudut rounded `16dp`, border hitam tebal, dan ikon bergaya *duotone-line* tebal.

---

### 3.2 Layar Diagnosa: Smart Crash Analyzer (Dialog Modal)

Saat terjadi force close/crash JVM, antarmuka tidak menampilkan layar abu-abu atau dump teks hitam, melainkan popup ala jendela retro bergaya brutalist.

```
┌────────────────────────────────────────────────────────┐
│  ┌───────────────────────────────────────────────[X]┐  │
│  │ ⚠ SYSTEM ALERT: CRASH DETECTED                   │  │  <-- Title Bar Pink (#FF66C4) + Border 3dp
│  ├──────────────────────────────────────────────────┤  │
│  │                                                  │  │
│  │  DIAGNOSA:                                       │  │
│  │  "Pemberhentian Paksa: Kehabisan Memori (OOM)"   │  │  <-- Bold Highlight Card (Yellow bg)
│  │                                                  │  │
│  │  PENYEBAB:                                       │  │
│  │  JVM meminta alokasi 2800 MB, tetapi OS Android  │  │
│  │  hanya memiliki sisa 320 MB RAM bebas.           │  │
│  │                                                  │  │
│  │  REKOMENDASI:                                    │  │
│  │  • Turunkan alokasi RAM profil ke 2048 MB.       │  │
│  │  • Matikan shaderpack berat pada Sodium/Iris.    │  │
│  │                                                  │  │
│  │  ┌────────────────────────────────────────────┐  │  │
│  │  │ ⬆ UPLOAD KE MCLO.GS & SALIN LINK           │  │  │  <-- Action Button (Mint #2EE59D)
│  │  └────────────────────────────────────────────┘  │  │
│  │  ┌────────────────────────────────────────────┐  │  │
│  │  │ ⎘ Salin Log Mentah  │ ⚙ Buka Pengaturan RAM│  │  │  <-- Dual Options (White Card bg)
│  │  └────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

#### Komponen Kritis Crash:
* Header modal berwarna `accent-pink` (`#FF66C4`) dengan icon silang retro di kanan atas.
* Kotak rekomendasi dengan bullet point kontras tinggi yang langsung memberi instruksi tanpa bahasa teknis yang rumit.
* Tombol CTA utama berwarna mint: "Upload ke mclo.gs & Salin Link" yang langsung memicu request API dan meng-copy URL web.

---

### 3.3 Layar Manajemen Renderer & Plugin (`.so` Selector)

Neobrutalism menuntut elemen daftar dibuat seperti folder file retro bertumpuk:
* **Item List Card:** Tiap item renderer (gl4es bawaan, Zink, MobileGL custom) dibungkus kotak bertingkat dengan border `2.5dp`.
* **Badge "Active":** Badge kapsul hitam dengan teks putih `#FFFFFF` menyala di pojok kanan kartu.
* **Slot Tambah Plugin:** Kartu dengan garis putus-putus hitam tebal (*dashed border 2.5dp*), tombol `+ Pasang Plugin Library (.so)`.

---

## 4. Virtual Controller & In-Game Touch HUD (Landscape)

Bila mode kontrol sentuh diaktifkan saat in-game:
* **Tombol D-Pad & Tombol Aksi (Jump, Sneak, Attack):**
  * Border luar hitam solid `2dp` dengan transparansi adaptif (slider `30%` – `100%`).
  * Desain tombol berbentuk persegi tumpul (*squircle*) khas neobrutalist dengan ikon/label teks berbobot tebal (`JUMP`, `ATK`, `USE`).
* **Visual Grid Editor (Mode Edit Kontrol):**
  * Grid latar belakang berupa titik-titik hitam (*polka dot canvas grid*) khas brutalist untuk mempermudah pemain melakukan snap tombol secara rapi.
  * Selector ukuran tombol menggunakan slider tebal berwarna kuning dan ber-border hitam.

---

## 5. Micro-Interactions & Feedback Haptik

1. **Efek Tekan Tombol (Mechanical Click):**
   * Setiap tombol ditekan harus merespons dengan translasi posisi `+3dp` ke bawah dan kanan, menghapus bayangan sementara waktu, disertai **haptic feedback singkat (tick vibe)** dari motor getar perangkat.
2. **Loading States (Progress Bar):**
   * Progress bar instalasi asset dan mod loader tidak menggunakan lingkaran berputar modern tipis.
   * Gunakan progress bar kotak panjang dengan border hitam `2.5dp`, latar belakang putih, dan bar terisi berupa pola garis diagonal (*striped diagonal bars*) berwarna kuning/ungu cerah.
3. **Empty States:**
   * Jika daftar mod kosong: Tampilkan ilustrasi folder terbuka dengan border tebal kartun dan teks lugas: *"Belum ada Mod. Pasang sekarang atau main vanilla santai!"*.

---

## 6. Functional UI Requirements (Kebutuhan Frontend)

| ID | Komponen | Kebutuhan Implementasi |
| :--- | :--- | :--- |
| **UFR-01** | Global Theme Wrapper | Menerapkan custom view styling atau Compose Theme dengan atribut seragam: `strokeWidth = 2.5.dp`, `strokeColor = Black`, `shadowColor = Black`. |
| **UFR-02** | Crash Dialog Trigger | Mampu merender dialog modal brutalist secara instan saat menerima broadcast intent dari background service crash watcher. |
| **UFR-03** | Toast Notification | Mengganti Toast native Android standar dengan Neobrutalist Snackbar (banner kotak mengambang dari atas layar dengan border hitam dan warna mint jika sukses atau pink jika error). |
| **UFR-04** | Responsive Scale | Seluruh ukuran border dan offset bayangan menggunakan satuan `dp` (Density-independent Pixels) agar ketebalan garis tidak pecah atau terlalu tebal di resolusi layar HP yang berbeda-beda. |
| **UFR-05** | Color Fallback | Kontras teks dan latar belakang wajib mematuhi standar WCAG AAA (minimal rasio kontras 7:1 untuk teks utama). |

---

## 7. Acceptance Criteria untuk Desain & UI Implementation

* **AC-UI-01 (Konsistensi Border & Shadow):**
  * *Given* layar aplikasi apa pun (Dashboard, Settings, Crash Modal, atau Akun),
  * *When* elemen kartu atau tombol dimuat,
  * *Then* tidak boleh ada bayangan bergaya gaussian/blur, dan semua elemen wajib memiliki stroke hitam minimal `2dp` dengan offset bayangan padat.
* **AC-UI-02 (Feedback Interaksi Tombol):**
  * *Given* pengguna menekan tombol "MAINKAN" atau "PILIH RENDERER",
  * *When* jari menyentuh layar (*ACTION_DOWN*),
  * *Then* tombol harus bergeser diagonal menutup offset bayangan, dan kembali ke posisi semula saat jari dilepas (*ACTION_UP*).
* **AC-UI-03 (Tampilan Diagnosa Crash):**
  * *Given* sesi permainan mengalami error OOM atau mod bentrok,
  * *When* Crash Activity diluncurkan,
  * *Then* pengguna langsung disajikan ringkasan diagnosa berlatar warna kuning/pink dengan tombol salin link mclo.gs tanpa harus melakukan *scroll* panjang di layar HP standar 6 inci.