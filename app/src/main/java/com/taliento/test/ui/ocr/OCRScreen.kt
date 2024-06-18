package com.taliento.test.ui.ocr

import android.R.attr.text
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.abs


private var imageUri = mutableStateOf<Uri?>(null)
private var textChanged = mutableStateOf("Scanned text will appear here..")

private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

data class BillInfo(var date: String?, var currency: String?, var total: String?)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun OCRScreen() {
    var billInfo by remember {
        mutableStateOf(BillInfo(null, null, null))
    }
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var tabStateIndex by remember { mutableIntStateOf(0) }
    val selectImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {
                imageUri.value = uri

                val image = InputImage.fromFilePath(context, imageUri.value!!)
                recognizer.process(image)
                    .addOnSuccessListener {
                        processTextBlock(it)
                        billInfo = processBillInfo(it)
                        textChanged.value = processTextBlock(it)
                        showBottomSheet = true
                    }
                    .addOnFailureListener {
                        Log.e("TEXT_REC", it.message.toString())
                    }
            }

        }

    Scaffold(
        topBar = {
            TopAppBar({ Text("OCR test") }, actions = {
                IconButton(
                    onClick = {
                        selectImage.launch("image/*")
                    }) {
                    Icon(
                        Icons.Filled.Add,
                        "add"
                    )
                }
                IconButton(
                    onClick = {
                        shareText(textChanged.value, context)
                    }) {
                    Icon(
                        Icons.Filled.Share,
                        "share"
                    )
                }
            })

        },
        floatingActionButton = {
            if (tabStateIndex == 0) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (billInfo.currency != null || billInfo.date != null || billInfo.total != null) {
                        FloatingActionButton(onClick = { showBottomSheet = true }) {
                            Icon(
                                Icons.Filled.Info,
                                "add"
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            selectImage.launch("image/*")
                        }) {
                        Icon(
                            Icons.Filled.Add,
                            "add"
                        )
                    }
                }
            }

        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                PrimaryTabRow(tabStateIndex) {
                    Tab(selected = tabStateIndex == 0, onClick = { tabStateIndex = 0 }, text = { Text(text = "Image")})
                    Tab(selected = tabStateIndex == 1, onClick = { tabStateIndex = 1 }, text = { Text(text = "Text")})
                }

                when(tabStateIndex) {
                    0 -> { Box(modifier = Modifier.fillMaxSize()) {
                        if (imageUri.value != null) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = rememberImagePainter(
                                    data = imageUri.value
                                ),
                                contentDescription = "image"
                            )
                        } else { Text(text = "Add an image...", Modifier.align(Alignment.Center))}
                    } }

                    1 -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            item {
                                Text(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    text = textChanged.value,
                                    style = MaterialTheme.typography.bodySmall

                                )
                            }
                        }
                    }
                }


                if (showBottomSheet) {
                    ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                        Column(
                            Modifier
                                .height(150.dp)
                                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Data", Modifier.weight(1f))
                                Text(text = billInfo.date ?: "-", Modifier.weight(1f))
                            }
                            HorizontalDivider()
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Totale", Modifier.weight(1f))
                                Text(text = billInfo.total ?: "-", Modifier.weight(1f))
                            }
                            HorizontalDivider()
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Valuta", Modifier.weight(1f))
                                Text(text = billInfo.currency ?: "-", Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    )
}

/**
 * TODO
 *
 * @param result
 * @return
 */
fun processBillInfo(result: Text): BillInfo {
    //val nf: NumberFormat = DecimalFormat("990.0")
    //val d: Number? = nf.parse("text")
    val billInfo = BillInfo(null, null, null)
    val values = mutableListOf<Float>()
    val nf: NumberFormat = DecimalFormat("999999.00")

    for (block in result.textBlocks) {
        /*val blockText = block.text
        val blockCornerPoints = block.cornerPoints
        val blockFrame = block.boundingBox*/
        for (line in block.lines) {
            val lineText = line.text
            /*val lineCornerPoints = line.cornerPoints
            val lineFrame = line.boundingBox*/
            if (lineText.contains("data", true)) {
                billInfo.date = lineText.replace("data", "", true)
            }
            if (lineText.contains("eur", true)) {
                billInfo.currency = "EUR"
            }

            if (lineText.contains(".") || lineText.contains(",")) {
                try {
                    val d: Number? = nf.parse(lineText)
                    if (d != null) {
                        values.add(d.toFloat())
                    }
                } catch (e: Exception) {
                    Log.e("NUMBER_FORMAT", "Not a number")
                }
            }



            /*for (element in line.elements) {
                val elementText = element.text
                val elementCornerPoints = element.cornerPoints
                val elementFrame = element.boundingBox

            }*/
        }
    }
    if (values.isNotEmpty()) billInfo.total = values.maxOf { it }.toString()

    return billInfo
}

private fun processTextBlock(text: Text): String {
    val tolerance = 20

    val rows: MutableMap<Int, MutableList<String>> = mutableMapOf()

    val blocks = text.textBlocks.sortedBy { it.boundingBox?.top }

    for (block in blocks) {
        for (line in block.lines) {
            val topValue = line.boundingBox?.top ?: continue

            var foundRow: MutableList<String>? = null
            for ((rowTop, rowTexts) in rows) {
                if (abs(rowTop - topValue) <= tolerance) {
                    foundRow = rowTexts
                    break
                }
            }

            if (foundRow == null) {
                foundRow = mutableListOf()
                rows[topValue] = foundRow
            }

            foundRow.add(line.text)
        }
    }
    var result = ""
    rows.forEach { row ->
        result += "\n"
        row.value.forEach { element ->
            result += "$element "
        }

    }

    return result
}

private fun shareText(sharedText: String, context: Context) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, sharedText)
    sendIntent.type = "text/plain"
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}