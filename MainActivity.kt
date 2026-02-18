package com.archo.go

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val artefactDatabase by lazy { ArtefactDatabase(this) }

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) enablePlayerLocation()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        seedDatabaseIfEmpty()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
        }
        supportActionBar?.title = "ARcho.go"
    }

    private fun seedDatabaseIfEmpty() {
        if (artefactDatabase.countArtefacts() > 0) return

        artefactDatabase.insertArtefacts(
            listOf(
                ArtefactPoint(
                    id = "m1",
                    name = "Slovenské národné múzeum",
                    type = ArtefactType.MUSEUM,
                    position = LatLng(48.1416, 17.1009),
                    detail = "Expozícia archeologických artefaktov od praveku po stredovek."
                ),
                ArtefactPoint(
                    id = "m2",
                    name = "Múzeum mesta Bratislavy",
                    type = ArtefactType.MUSEUM,
                    position = LatLng(48.1425, 17.1081),
                    detail = "Historické zbierky a mestské nálezy z hradného vrchu."
                ),
                ArtefactPoint(
                    id = "s1",
                    name = "Nálezisko Devín",
                    type = ArtefactType.SITE,
                    position = LatLng(48.1738, 16.9784),
                    detail = "Keltské a rímske nálezy v lokalite sútoku Dunaja a Moravy."
                ),
                ArtefactPoint(
                    id = "s2",
                    name = "Nálezisko Gerulata",
                    type = ArtefactType.SITE,
                    position = LatLng(48.0346, 17.1560),
                    detail = "Rímsky vojenský tábor a artefakty z Limes Romanus."
                )
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setupMap()
    }

    private fun setupMap() {
        if (hasLocationPermission()) {
            enablePlayerLocation()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        addMuseumAndSiteMarkersFromDatabase()
        setupMarkerClickListener()
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun enablePlayerLocation() {
        if (!hasLocationPermission()) return

        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val defaultCenter = LatLng(48.1486, 17.1077)
            val playerPosition = location?.let { LatLng(it.latitude, it.longitude) } ?: defaultCenter
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(playerPosition, 12f))
        }
    }

    private fun addMuseumAndSiteMarkersFromDatabase() {
        artefactDatabase.getAllArtefacts().forEach { point ->
            val color = when (point.type) {
                ArtefactType.MUSEUM -> BitmapDescriptorFactory.HUE_AZURE
                ArtefactType.SITE -> BitmapDescriptorFactory.HUE_ORANGE
            }

            map.addMarker(
                MarkerOptions()
                    .position(point.position)
                    .title(point.name)
                    .snippet(point.type.label)
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
            )?.tag = point
        }
    }

    private fun setupMarkerClickListener() {
        map.setOnMarkerClickListener { marker ->
            val point = marker.tag as? ArtefactPoint ?: return@setOnMarkerClickListener false
            showArtefactDetail(point)
            true
        }
    }

    private fun showArtefactDetail(point: ArtefactPoint) {
        AlertDialog.Builder(this)
            .setTitle(point.name)
            .setMessage("Typ: ${point.type.label}\n\n${point.detail}")
            .setPositiveButton("Zavrieť", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, MENU_INFO_ID, 0, "Menu")
            .setIcon(android.R.drawable.ic_menu_more)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == MENU_INFO_ID) {
            AlertDialog.Builder(this)
                .setTitle("ARcho.go menu")
                .setMessage("Mapa zobrazuje GPS polohu hráča, múzeá a archeologické náleziská.")
                .setPositiveButton("OK", null)
                .show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        artefactDatabase.close()
        super.onDestroy()
    }

    companion object {
        private const val MENU_INFO_ID = 1001
    }
}

private class ArtefactDatabase(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_ARTEFACTS (
                $COL_ID TEXT PRIMARY KEY,
                $COL_NAME TEXT NOT NULL,
                $COL_TYPE TEXT NOT NULL,
                $COL_LAT REAL NOT NULL,
                $COL_LNG REAL NOT NULL,
                $COL_DETAIL TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ARTEFACTS")
        onCreate(db)
    }

    fun countArtefacts(): Int {
        readableDatabase.rawQuery("SELECT COUNT(*) FROM $TABLE_ARTEFACTS", null).use { cursor ->
            return if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }

    fun insertArtefacts(points: List<ArtefactPoint>) {
        writableDatabase.beginTransaction()
        try {
            points.forEach { point ->
                val values = ContentValues().apply {
                    put(COL_ID, point.id)
                    put(COL_NAME, point.name)
                    put(COL_TYPE, point.type.name)
                    put(COL_LAT, point.position.latitude)
                    put(COL_LNG, point.position.longitude)
                    put(COL_DETAIL, point.detail)
                }
                writableDatabase.insertWithOnConflict(
                    TABLE_ARTEFACTS,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }

    fun getAllArtefacts(): List<ArtefactPoint> {
        val points = mutableListOf<ArtefactPoint>()
        readableDatabase.query(
            TABLE_ARTEFACTS,
            arrayOf(COL_ID, COL_NAME, COL_TYPE, COL_LAT, COL_LNG, COL_DETAIL),
            null,
            null,
            null,
            null,
            COL_NAME
        ).use { cursor ->
            while (cursor.moveToNext()) {
                points += ArtefactPoint(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                    type = ArtefactType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE))),
                    position = LatLng(
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LAT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LNG))
                    ),
                    detail = cursor.getString(cursor.getColumnIndexOrThrow(COL_DETAIL))
                )
            }
        }
        return points
    }

    companion object {
        private const val DB_NAME = "archo_go.db"
        private const val DB_VERSION = 1

        private const val TABLE_ARTEFACTS = "artefacts"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_TYPE = "type"
        private const val COL_LAT = "latitude"
        private const val COL_LNG = "longitude"
        private const val COL_DETAIL = "detail"
    }
}

enum class ArtefactType(val label: String) {
    MUSEUM("Múzeum"),
    SITE("Nálezisko")
}

data class ArtefactPoint(
    val id: String,
    val name: String,
    val type: ArtefactType,
    val position: LatLng,
    val detail: String
)
