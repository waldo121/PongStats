package com.waldo121.pongstats.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import com.waldo121.pongstats.data.local.dao.DoubleMatchRecordDao
import com.waldo121.pongstats.data.local.dao.SingleMatchRecordDao
import com.waldo121.pongstats.data.local.MatchRecordsDatabase

// Fonction d'export complète
suspend fun ExportRoomDatabase(context: Context, db: MatchRecordsDatabase) = withContext(Dispatchers.IO) {
    val gson = GsonBuilder().setPrettyPrinting().create()

    // 🔹 Récupère les données de chaque DAO
    val singleMatches = db.singleMatchRecordDao().getAll()
    val doubleMatches = db.doubleMatchRecordDao().getAll()

    // 🔹 Mets tout dans un seul objet
    val exportData: Map<String, Any> = mapOf(
        "single_match_records" to singleMatches,
        "double_match_records" to doubleMatches,
    )

    // 🔹 Sérialise en JSON
    val json = gson.toJson(exportData)

    // 🔹 Sauvegarde dans un fichier
    val exportFile = File(
        context.getExternalFilesDir(null),
        "room_export_${System.currentTimeMillis()}.json"
    )
    exportFile.writeText(json)

    // 🔹 Optionnel : crée un intent pour partager
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", exportFile)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Lance le sélecteur de partage
    withContext(Dispatchers.Main) {
        context.startActivity(Intent.createChooser(shareIntent, "Partager la base exportée"))
    }

    exportFile.absolutePath // Retourne le chemin du fichier
}
