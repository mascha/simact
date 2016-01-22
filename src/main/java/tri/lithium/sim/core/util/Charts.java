package tri.lithium.sim.core.util;

import com.xeiam.xchart.*;
import tri.lithium.meta.qss.core.DoubleSink;
import tri.lithium.meta.pdevs.core.Composite;
import tri.lithium.meta.pdevs.library.sinks.Sink;
import tri.lithium.meta.pdevs.util.visitor.ClassFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Chart helper class.
 */
public class Charts {

    public static void plotAll(String name, List<DoubleSink> sinks, boolean drawTicks, String yAxisLabel, String timeAxisLabel) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(sinks);
        List<Chart> charts = createChart(name, sinks, drawTicks, yAxisLabel, timeAxisLabel);
        new SwingWrapper(charts).displayChart();
    }

    public static List<Chart> createChart(String name, List<DoubleSink> sinks, boolean drawTicks, String yAxisLabel, String timeAxisLabel) {
        Chart chart = new ChartBuilder().width(1680).height(800)
                .theme(StyleManager.ChartTheme.GGPlot2)
                .title(name)
                .chartType(StyleManager.ChartType.Line)
                .xAxisTitle(timeAxisLabel)
                .yAxisTitle(yAxisLabel).build();

        chart.getStyleManager().setLegendPosition(StyleManager.LegendPosition.OutsideE);

        for (int i = 0; i < sinks.size(); i++) {
            if (sinks.get(i).getDataStreams().size() > 0)
                if (sinks.get(i).getDataCount() > 0) {

                    Series series = chart.addSeries(sinks.get(i).getName(),
                            Arrays.copyOf(sinks.get(i).getTimeStreams().get(0), sinks.get(i).getDataCount()),
                            Arrays.copyOf(sinks.get(i).getDataStreams().get(0), sinks.get(i).getDataCount())
                    );

                    if (!drawTicks) series.setMarker(SeriesMarker.NONE);
                }
        }

        return Collections.singletonList(chart);
    }


    public static void asPNG(Composite root, boolean drawTicks, String yAxisLabel, String timeAxisLabel) {
        ClassFilter<DoubleSink> visitor = ClassFilter.create(DoubleSink.class);
        visitor.visitComposite(root);
        visitor.setClazz(Sink.class);
        visitor.visitComposite(root);
        List<Chart> chartList = createChart(root.getName(), visitor.getFoundInstances(), true, yAxisLabel, timeAxisLabel);
        for (Chart chart : chartList)
            try {
                BitmapEncoder.saveBitmap(chart, String.valueOf(chart.hashCode()), BitmapEncoder.BitmapFormat.PNG);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void plotAll(Composite composite, boolean drawTicks, String yAxisLabel, String timeAxisLabel) {
        ClassFilter<DoubleSink> visitor = ClassFilter.create(DoubleSink.class);
        visitor.visitComposite(composite);
        visitor.setClazz(Sink.class);
        visitor.visitComposite(composite);
        plotAll(composite.getName(), visitor.getFoundInstances(), drawTicks, yAxisLabel, timeAxisLabel);
    }

    public static void plotAll(Composite root) {
        plotAll(root, true, "Values", "Time");
    }
}
