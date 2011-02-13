package ru.jfchart

import java.awt.Dimension
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.swing.JFrame
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.axis.DateTickUnit
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.function.Function2D
import org.jfree.data.general.DatasetUtilities
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import static org.jfree.ui.RefineryUtilities.centerFrameOnScreen

/**
 * User: dima
 * Date: 13/2/11
 */
class JFC_Playground {
  public static void main(String[] args) {
    XYDataset xyDataset = DatasetUtilities.sampleFunction2D(new Function2D() {
      @Override
      public double getValue(double x) {
        return Math.pow(x, 2);
      }
    }, -100, 100, 100, 1);

    XYSeries series = new XYSeries(1);
    series.add(1, 1);
    series.add(1, 10);
    XYSeriesCollection seriesCollection = new XYSeriesCollection(series);

/*
            JFreeChart chart = ChartFactory.createXYLineChart("ooo", "Xxx", "Yyy", xyDataset, PlotOrientation.VERTICAL, true, true, true);
*/
    JFreeChart chart = ChartFactory.createScatterPlot("aaaa", "x", "y", seriesCollection, PlotOrientation.VERTICAL, false, false, false);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setMouseWheelEnabled(true);
    chartPanel.setPreferredSize(new Dimension(500, 500));

    JFrame jFrame = new JFrame();
    jFrame.add(chartPanel);
    jFrame.pack();
    centerFrameOnScreen(jFrame);
    jFrame.setVisible(true);
  }

  def static showBeans() {
    XYSeries series1 = new XYSeries(1);
    XYSeries series2 = new XYSeries(1);
//    beans.each{
//      series.add(it.date.time, it.open);
//    }
    XYSeriesCollection seriesCollection = new XYSeriesCollection();
    seriesCollection.addSeries(series1)
    seriesCollection.addSeries(series2)

    DateAxis dateAxis = new DateAxis("date");
    dateAxis.setTickUnit(new DateTickUnit(DateTickUnit.MONTH, 1));
    DateFormat chartFormatter = new SimpleDateFormat("yyyy/MM");
    dateAxis.setDateFormatOverride(chartFormatter);

    def priceAxis = new NumberAxis("price")

    XYPlot xyPlot = new XYPlot(seriesCollection, dateAxis, priceAxis, new XYLineAndShapeRenderer(true, false))
    JFreeChart chart = new JFreeChart("OHLC", JFreeChart.DEFAULT_TITLE_FONT, xyPlot, false);
//    JFreeChart chart = ChartFactory.createXYLineChart("OHCL", "date", "price", seriesCollection, PlotOrientation.VERTICAL, true, true, true);
//    JFreeChart chart = ChartFactory.createScatterPlot("aaaa", "x", "y", seriesCollection, PlotOrientation.VERTICAL, false, false, false);

    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setMouseWheelEnabled(true);
    chartPanel.setPreferredSize(new Dimension(500, 500));

    JFrame jFrame = new JFrame();
    jFrame.add(chartPanel);
    jFrame.pack();
    centerFrameOnScreen(jFrame);
    jFrame.setVisible(true);

    [series1, series2]
  }
}
