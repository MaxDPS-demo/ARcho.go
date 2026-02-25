# ARcho.go

ARcho.go je modulárny Android projekt v Kotlin postavený na Fragmentoch a Navigation Component.

## Moduly
- **MapFragment**: Google mapa, GPS poloha hráča, markery archeologických lokalít, prechod na detail.
- **DetailFragment**: názov + popis lokality, tlačidlo na spustenie AR skenera.
- **ARScannerFragment**: ARCore + Sceneform, detekcia horizontálnej plochy, umiestnenie 3D objektu, získanie artefaktu kliknutím.
- **InventoryFragment**: zoznam získaných artefaktov cez RecyclerView.
- **QuizFragment**: otázky z artefaktov hráča, 4 odpovede, vyhodnotenie úspešnosti a bodov.
- **RewardsFragment**: aktuálne body, simulovaná výmena bodov za odmeny.
- **Lokálne notifikácie**: upozornenie pri blízkosti archeologickej lokality.

## Architektúra
- **Clean-ish layers**: `domain` (modely + repository kontrakt), `data` (in-memory implementácia), `ui` (fragmenty + adaptery + view model).
- Zdieľaný stav bodov/inventára je v `GameRepository` (aktuálne in-memory).

## Poznámky
- Do `AndroidManifest.xml` doplň vlastný Google Maps API kľúč (`com.google.android.geo.API_KEY`).
- AR funkcie vyžadujú ARCore kompatibilné zariadenie.
