package com.example.docswap.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Xml
import androidx.core.graphics.createBitmap
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Phrase
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfAnnotation
import com.itextpdf.text.pdf.PdfCopy
import com.itextpdf.text.pdf.PdfGState
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfStamper
import com.itextpdf.text.pdf.PdfStream
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object FileConverter {
    fun getOutputDirectory(): File {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val docSwapDir = File(downloadsDir, "DocSwap")
        if (!docSwapDir.exists()) {
            docSwapDir.mkdirs()
        }
        return docSwapDir
    }

    fun generateOutputPath(fileName: String, targetFormat: String): String {
        val nameWithoutExtension = fileName.substringBeforeLast(".")
        return File(getOutputDirectory(), "$nameWithoutExtension.$targetFormat").absolutePath
    }

    /**
     * Converts a PDF file to a basic DOCX file by extracting text and packaging it into a ZIP structure.
     */
    fun convertPdfToDocx(inputPath: String): String {
        val outputPath = generateOutputPath(File(inputPath).name, "docx")
        try {
            val reader = PdfReader(inputPath)
            val textBuilder = StringBuilder()
            for (i in 1..reader.numberOfPages) {
                textBuilder.append(PdfTextExtractor.getTextFromPage(reader, i))
                textBuilder.append("\n")
            }
            reader.close()

            ZipOutputStream(FileOutputStream(outputPath)).use { out ->
                // 1. [Content_Types].xml
                out.putNextEntry(ZipEntry("[Content_Types].xml"))
                val contentTypes = """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                      <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                      <Default Extension="xml" ContentType="application/xml"/>
                      <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                    </Types>
                """.trimIndent()
                out.write(contentTypes.toByteArray())
                out.closeEntry()

                // 2. _rels/.rels
                out.putNextEntry(ZipEntry("_rels/.rels"))
                val rels = """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
                    </Relationships>
                """.trimIndent()
                out.write(rels.toByteArray())
                out.closeEntry()

                // 3. word/_rels/document.xml.rels
                out.putNextEntry(ZipEntry("word/_rels/document.xml.rels"))
                val docRels = """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"/>
                """.trimIndent()
                out.write(docRels.toByteArray())
                out.closeEntry()

                // 4. word/document.xml
                out.putNextEntry(ZipEntry("word/document.xml"))
                val bodyBuilder = StringBuilder()
                textBuilder.toString().split("\n").forEach { line ->
                    if (line.isNotBlank()) {
                        bodyBuilder.append("<w:p><w:r><w:t>${escapeXml(line)}</w:t></w:r></w:p>")
                    }
                }
                val documentXml = """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                      <w:body>
                        $bodyBuilder
                      </w:body>
                    </w:document>
                """.trimIndent()
                out.write(documentXml.toByteArray())
                out.closeEntry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPath
    }

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    /**
     * Converts a DOCX file to a PDF by parsing word/document.xml and rendering text using PdfDocument.
     */
    fun convertDocxToPdf(inputPath: String): String {
        val outputPath = generateOutputPath(File(inputPath).name, "pdf")
        val pdfDocument = PdfDocument()
        val paint = TextPaint().apply {
            textSize = 12f
        }
        val margin = 40f
        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points

        try {
            val zipFile = ZipFile(inputPath)
            val entry = zipFile.getEntry("word/document.xml") ?: return ""
            val inputStream = zipFile.getInputStream(entry)

            val parser = Xml.newPullParser()
            parser.setInput(inputStream, "UTF-8")

            val textBuilder = StringBuilder()
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "t") {
                    textBuilder.append(parser.nextText())
                } else if (eventType == XmlPullParser.END_TAG && parser.name == "p") {
                    textBuilder.append("\n")
                }
                eventType = parser.next()
            }
            zipFile.close()

            val lines = textBuilder.toString().split("\n")
            var lineIndex = 0
            var currentPage = 0

            while (lineIndex < lines.size) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage++).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                var y = margin

                var linesInThisPage = 0
                while (lineIndex < lines.size && linesInThisPage < 40) {
                    val line = lines[lineIndex++]
                    if (line.isBlank()) {
                        y += paint.fontSpacing
                        linesInThisPage++
                        continue
                    }

                    // Use StaticLayout to handle text wrapping within page margins
                    val staticLayout = StaticLayout.Builder.obtain(line, 0, line.length, paint, pageWidth - 2 * margin.toInt())
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0f, 1f)
                        .setIncludePad(false)
                        .build()

                    canvas.save()
                    canvas.translate(margin, y)
                    staticLayout.draw(canvas)
                    canvas.restore()

                    y += staticLayout.height
                    linesInThisPage++

                    // Basic check to avoid drawing beyond page bottom
                    if (y > pageHeight - margin) break
                }
                pdfDocument.finishPage(page)
            }

            FileOutputStream(outputPath).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outputPath
    }

    fun convertImageToPdf(inputPath: String): String {
        return convertImagesToPdf(listOf(inputPath), File(inputPath).nameWithoutExtension)
    }

    fun convertImagesToPdf(imagePaths: List<String>, outputFileName: String): String {
        val outputPath = generateOutputPath(outputFileName, "pdf")
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points

        try {
            imagePaths.forEachIndexed { index, path ->
                val bitmap = BitmapFactory.decodeFile(path) ?: return@forEachIndexed
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Maintain aspect ratio and fit to A4
                val scale = Math.min(
                    pageWidth.toFloat() / bitmap.width,
                    pageHeight.toFloat() / bitmap.height
                )
                val xOffset = (pageWidth - bitmap.width * scale) / 2
                val yOffset = (pageHeight - bitmap.height * scale) / 2

                val matrix = Matrix().apply {
                    postScale(scale, scale)
                    postTranslate(xOffset, yOffset)
                }

                canvas.drawBitmap(bitmap, matrix, Paint(Paint.FILTER_BITMAP_FLAG))
                pdfDocument.finishPage(page)
                bitmap.recycle()
            }

            FileOutputStream(outputPath).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
        return outputPath
    }

    fun convertPdfToImages(inputPath: String): List<String> {
        val outputPaths = mutableListOf<String>()
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val imagesDir = File(downloadsDir, "DocSwap_Images")
        if (!imagesDir.exists()) imagesDir.mkdirs()

        try {
            val file = File(inputPath)
            val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fileDescriptor)

            for (i in 0 until renderer.pageCount) {
                val page = renderer.openPage(i)
                val bitmap = createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                val imageFile = File(imagesDir, "${file.nameWithoutExtension}_page_${i + 1}.jpg")
                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                outputPaths.add(imageFile.absolutePath)

                page.close()
                bitmap.recycle()
            }
            renderer.close()
            fileDescriptor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPaths
    }

    fun performOcr(inputPath: String): String {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        var resultText = ""
        try {
            val bitmap = if (inputPath.endsWith(".pdf", ignoreCase = true)) {
                // Render first page of PDF for OCR
                val file = File(inputPath)
                val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fileDescriptor)
                val page = renderer.openPage(0)
                val b = createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                page.render(b, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                renderer.close()
                fileDescriptor.close()
                b
            } else {
                BitmapFactory.decodeFile(inputPath)
            }

            if (bitmap != null) {
                val image = InputImage.fromBitmap(bitmap, 0)
                val result = Tasks.await(recognizer.process(image))
                resultText = result.text
                bitmap.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultText
    }

    fun protectPdf(inputPath: String, password: String): String {
        val outputPath = generateOutputPath(File(inputPath).nameWithoutExtension + "_protected", "pdf")
        try {
            val reader = PdfReader(inputPath)
            val stamper = PdfStamper(reader, FileOutputStream(outputPath))
            stamper.setEncryption(
                password.toByteArray(),
                null,
                PdfWriter.ALLOW_PRINTING,
                PdfWriter.ENCRYPTION_AES_128
            )
            stamper.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPath
    }

    fun unlockPdf(inputPath: String, password: String): String {
        val outputPath = generateOutputPath(File(inputPath).nameWithoutExtension + "_unlocked", "pdf")
        try {
            val reader = PdfReader(inputPath, password.toByteArray())
            val stamper = PdfStamper(reader, FileOutputStream(outputPath))
            stamper.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPath
    }

    fun mergePdfs(inputPaths: List<String>, outputFileName: String): String {
        val outputPath = generateOutputPath(outputFileName, "pdf")
        val document = com.itextpdf.text.Document()
        try {
            val copy = PdfCopy(document, FileOutputStream(outputPath))
            document.open()
            inputPaths.forEach { path ->
                val reader = PdfReader(path)
                for (i in 1..reader.numberOfPages) {
                    copy.addPage(copy.getImportedPage(reader, i))
                }
                reader.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (document.isOpen) document.close()
        }
        return outputPath
    }

    fun splitPdf(inputPath: String, pageRanges: List<IntRange>): List<String> {
        val outputPaths = mutableListOf<String>()
        val fileName = File(inputPath).nameWithoutExtension + "_split"
        val outputPath = generateOutputPath(fileName, "pdf")
        val document = com.itextpdf.text.Document()
        try {
            val reader = PdfReader(inputPath)
            val copy = PdfCopy(document, FileOutputStream(outputPath))
            document.open()
            pageRanges.forEach { range ->
                for (i in range) {
                    if (i in 1..reader.numberOfPages) {
                        copy.addPage(copy.getImportedPage(reader, i))
                    }
                }
            }
            reader.close()
            outputPaths.add(outputPath)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (document.isOpen) document.close()
        }
        return outputPaths
    }

    fun compressPdf(inputPath: String, quality: Float): String {
        val outputPath = generateOutputPath(File(inputPath).nameWithoutExtension + "_compressed", "pdf")
        try {
            val reader = PdfReader(inputPath)
            val stamper = PdfStamper(reader, FileOutputStream(outputPath))
            stamper.setFullCompression()
            stamper.writer.compressionLevel = PdfStream.BEST_COMPRESSION
            stamper.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPath
    }

    fun addSignatureToPdf(inputPath: String, signatureBitmap: Bitmap, page: Int, x: Float, y: Float, width: Float, height: Float): String {
        val outputPath = generateOutputPath(File(inputPath).nameWithoutExtension + "_signed", "pdf")
        try {
            val reader = PdfReader(inputPath)
            val stamper = PdfStamper(reader, FileOutputStream(outputPath))
            val content = stamper.getOverContent(page)

            val stream = ByteArrayOutputStream()
            signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = Image.getInstance(stream.toByteArray())
            image.setAbsolutePosition(x, y)
            image.scaleToFit(width, height)
            content.addImage(image)

            stamper.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPath
    }

    fun addWatermark(inputPath: String, text: String, opacity: Float, rotation: Float): String {
        val outputPath = generateOutputPath(File(inputPath).nameWithoutExtension + "_watermarked", "pdf")
        try {
            val reader = PdfReader(inputPath)
            val stamper = PdfStamper(reader, FileOutputStream(outputPath))
            val font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED)
            val gs = PdfGState()
            gs.setFillOpacity(opacity)
            gs.setStrokeOpacity(opacity)

            for (i in 1..reader.numberOfPages) {
                val content = stamper.getOverContent(i)
                content.setGState(gs)
                val pageSize = reader.getPageSize(i)
                ColumnText.showTextAligned(
                    content,
                    Element.ALIGN_CENTER,
                    Phrase(text, Font(font, 60f, Font.NORMAL, BaseColor.GRAY)),
                    pageSize.width / 2,
                    pageSize.height / 2,
                    rotation
                )
            }
            stamper.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPath
    }

    fun addAnnotation(inputPath: String, page: Int, text: String, x: Float, y: Float): String {
        val outputPath = generateOutputPath(File(inputPath).nameWithoutExtension + "_annotated", "pdf")
        try {
            val reader = PdfReader(inputPath)
            val stamper = PdfStamper(reader, FileOutputStream(outputPath))
            val annotation = PdfAnnotation.createText(
                stamper.writer,
                Rectangle(x, y, x + 20f, y + 20f),
                "Annotation",
                text,
                true,
                "Comment"
            )
            stamper.addAnnotation(annotation, page)
            stamper.close()
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputPath
    }
}
