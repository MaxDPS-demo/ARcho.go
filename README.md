# ARcho.go

ARcho.go je modulárny Android projekt v Kotlin postavený na Fragmentoch a Navigation Component.

## Moduly
- **MapFragment**: Google mapa, GPS poloha hráča, markery archeologických lokalít, prechod na detail.
- **DetailFragment**: názov + popis lokality, tlačidlo na spustenie AR skenera.
- **ARScannerFragment**: ARCore + Sceneform UX, detekcia horizontálnej plochy, umiestnenie 3D objektu, získanie artefaktu kliknutím.
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
- AR funkcie vyžadujú ARCore kompatibilné zariadenie.
- Pre Android 13+ povoľ notifikácie, inak sa proximity upozornenia nezobrazia.


## Riešenie chyby Sceneform dependency
Ak vidíš chybu typu:
`Could not find com.gorisse.thomas.sceneform:sceneform-ux:1.23.0`,
postup je:
1. Skontroluj, že v `app/build.gradle.kts` je závislosť
   `com.google.ar.sceneform.ux:sceneform-ux:1.17.1`.
2. V Android Studio daj **File > Invalidate Caches / Restart**.
3. Vymaž lokálny build cache projektu:
   - zmaž `.gradle/` v root projekte
   - spusti `./gradlew --refresh-dependencies clean`
4. Urob nový Gradle Sync.
