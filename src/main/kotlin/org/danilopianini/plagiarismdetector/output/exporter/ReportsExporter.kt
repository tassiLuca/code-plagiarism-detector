package org.danilopianini.plagiarismdetector.output.exporter

import org.danilopianini.plagiarismdetector.core.facade.Report
import org.danilopianini.plagiarismdetector.core.detector.Match

/**
 * An interface modeling the component responsible for
 * exporting the comparison process results.
 */
interface ReportsExporter<in M : Match> : (Set<Report<M>>) -> (Unit)
