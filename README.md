# ARcho.go

ARcho.go je modulárny Android projekt v Kotlin postavený na Fragmentoch a Navigation Component.

## Moduly
- **MapFragment**: Google mapa, GPS poloha hráča, markery archeologických lokalít, prechod na detail.
- **DetailFragment**: názov + popis lokality, tlačidlo na spustenie AR skenera.
- **ARScannerFragment**: ARCore dostupnosť check + simulované získanie artefaktu (kompatibilné bez Sceneform konfliktov).
- **InventoryFragment**: zoznam získaných artefaktov cez RecyclerView.
- **QuizFragment**: otázky z artefaktov hráča, 4 odpovede, vyhodnotenie úspešnosti a bodov.
- **RewardsFragment**: aktuálne body, simulovaná výmena bodov za odmeny.
- **Lokálne notifikácie**: upozornenie pri blízkosti archeologickej lokality.

## Architektúra
- **Clean-ish layers**: `domain` (modely + repository kontrakt), `data` (in-memory implementácia), `ui` (fragmenty + adaptery + view model).
- Zdieľaný stav bodov/inventára je v `GameRepository` (aktuálne in-memory).

## Kompatibilita Android Studio
- Android Studio: Iguana / Jellyfish / novšie
- JDK: **17**
- Compile SDK: **34**
- Min SDK: **24**

## Spustenie v Android Studio
1. Otvor root priečinok projektu (`ARcho.go`).
2. Počkaj na Gradle Sync.
3. Doplň Google Maps API key do `AndroidManifest.xml` (`com.google.android.geo.API_KEY`).
4. Spusti `app` na emulátore alebo zariadení.

## Spustenie cez CLI
```bash
./gradlew assembleDebug
```

## Poznámky
- ARCore funkcie vyžadujú kompatibilné zariadenie; fragment obsahuje aj fallback simulovaný režim.
- Pre Android 13+ povoľ notifikácie, inak sa proximity upozornenia nezobrazia.
