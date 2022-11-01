package org.danilopianini.plagiarismdetector.output

import org.danilopianini.plagiarismdetector.core.Report
import org.danilopianini.plagiarismdetector.core.detector.Match
import java.io.PrintWriter
import java.nio.file.Path
import java.time.LocalDateTime

/**
 * A very simple [ReportsExporter], which exports results on a plain file, inside [outputDirectory].
 */
class PlainFileExporter<in M : Match>(outputDirectory: Path) : FileExporter<M>(outputDirectory) {

    override fun export(reports: Set<Report<M>>, output: PrintWriter) {
        printHeader(output)
        reports.forEach { printBody(it, output) }
    }

    private fun printHeader(out: PrintWriter) {
        out.println("*".repeat(LINE_LENGTH))
        out.println("$FILE_PREAMBLE ${LocalDateTime.now()}")
        out.println("*".repeat(LINE_LENGTH))
    }

    private fun printBody(report: Report<M>, out: PrintWriter) {
        out.println("> Matches between ${report.submittedProject.name} and ${report.comparedProject.name}")
        report.comparisonResult
            .filter { it.matches.any() }
            .groupBy { it.similarity }
            .toSortedMap(Comparator.reverseOrder())
            .forEach {
                out.println(">> With similarity: ${it.key}")
                it.value.flatMap { it.matches }.forEach { formatDuplicatedSections(it, out) }
            }
    }

    private fun formatDuplicatedSections(match: M, out: PrintWriter) {
        val (ptn, txt) = match.formattedMatch
        out.println(">".repeat(LINE_LENGTH))
        out.println(ptn)
        out.println("<".repeat(LINE_LENGTH))
        out.println(txt)
        out.println(">".repeat(LINE_LENGTH))
        out.println()
    }

    companion object {
        private const val LINE_LENGTH = 120
        private const val FILE_PREAMBLE = "This is the generated report from plagiarism detector tool at "
    }
}