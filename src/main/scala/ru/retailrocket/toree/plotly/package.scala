package ru.retailrocket.toree


package object plotly {
  trait Pointable[T] {
    def x: T
    def y: Double
    def xAsString: String = x.toString
  }

  case class Point(x: Double, y: Double) extends Pointable[Double]

  case class LabeledPoint(x: String, y: Double) extends Pointable[String] {
    override def xAsString: String = "'%s'".format(x)
  }

  def plotMulty(
    src: Seq[Seq[Pointable[_]]],
    labels: Seq[String] = Seq.empty,
    plotType: String = "scatter",
    mode: String = "lines+markers",
    xlab: String = "",
    ylab: String = "",
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = {
    val id = "plot_%d".format(System.currentTimeMillis)

    val labelsMap = labels.zipWithIndex.map(_.swap).toMap

    val data = src.zipWithIndex
      .map { case (ps, i) =>
        val x = ps.map(_.xAsString).mkString(",")
        val y = ps.map(_.y).mkString(",")
        "{ x: [%s], y: [%s], mode: '%s', type: '%s', name: '%s' }"
          .format(x, y, mode, plotType, labelsMap.getOrElse(i, "trace %d".format(i))) }
      .mkString(",")

    """
      <div id="%s" style="width:%dpx;height:%dpx;"></div>

      <script>
      requirejs.config({
          paths: {
              'plotly': ['/static/components/plotly/plotly-latest'],
          },
      });

      require(['plotly'], function(Plotly) {
        plot = document.getElementById('%s');
        Plotly.plot(plot, [%s], { xaxis: { title: '%s' }, yaxis: { title: '%s' }, title: '%s' });
      });
      </script>
    """.format(id, width, height, id, data, xlab, ylab, title)
  }

  def plot(
    src: Seq[Pointable[_]],
    mode: String = "lines+markers",
    xlab: String = "",
    ylab: String = "",
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = plotMulty(Seq(src), mode = mode, xlab = xlab, ylab = ylab, title = title, width = width, height = height)

  def barplotMulty(
    src: Seq[Seq[Pointable[_]]],
    labels: Seq[String] = Seq.empty,
    xlab: String = "",
    ylab: String = "",
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = plotMulty(src, plotType = "bar", xlab = xlab, ylab = ylab, title = title, width = width, height = height)

  def barplot(
    src: Seq[Pointable[_]],
    xlab: String = "",
    ylab: String = "",
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = barplotMulty(Seq(src), xlab = xlab, ylab = ylab, title = title, width = width, height = height)

  def histMulty(
    src: Seq[Seq[Double]],
    labels: Seq[String] = Seq.empty,
    plotType: String = "histogram",
    mode: String = "overlay",
    opacity: Double = 0.5,
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = {
    val id = "plot_%d".format(System.currentTimeMillis)

    val labelsMap = labels.zipWithIndex.map(_.swap).toMap

    val data = src.zipWithIndex
      .map { case (os, i) =>
        val x = os.mkString(",")
        "{ x: [%s], type: '%s', name: '%s', opacity: %f }"
          .format(x, plotType, labelsMap.getOrElse(i, "trace %d".format(i)), opacity) }
      .mkString(",")

    """
      <div id="%s" style="width:%dpx;height:%dpx;"></div>

      <script>
      requirejs.config({
          paths: {
              'plotly': ['/static/components/plotly/plotly-latest'],
          },
      });

      require(['plotly'], function(Plotly) {
        plot = document.getElementById('%s');
        Plotly.plot(plot, [%s], { barmode: '%s', title: '%s' });
      });
      </script>
    """.format(id, width, height, id, data, mode, title)
  }

  def hist(
    src: Seq[Double],
    mode: String = "overlay",
    opacity: Double = 0.5,
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = histMulty(Seq(src), plotType = "histogram", mode = mode, opacity = opacity, title = title, width = width, height = height)

  def boxplotMulty(
    src: Seq[Seq[Double]],
    labels: Seq[String] = Seq.empty,
    opacity: Double = 0.5,
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = histMulty(src, plotType = "box", labels = labels, opacity = opacity, title = title, width = width, height = height)

  def boxplot(
    src: Seq[Double],
    opacity: Double = 0.5,
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = histMulty(Seq(src), plotType = "box", opacity = opacity, title = title, width = width, height = height)

  def pieplot(
    src: Seq[Pointable[_]],
    title: String = "",
    width: Int = 900,
    height: Int = 500): String = {
    val id = "plot_%d".format(System.currentTimeMillis)

    val x = src.map(_.xAsString).mkString(",")
    val y = src.map(_.y).mkString(",")

    val data = "{ labels: [%s], values: [%s], type: 'pie' }".format(x, y)

    """
      <div id="%s" style="width:%dpx;height:%dpx;"></div>

      <script>
      requirejs.config({
          paths: {
              'plotly': ['/static/components/plotly/plotly-latest'],
          },
      });

      require(['plotly'], function(Plotly) {
        plot = document.getElementById('%s');
        Plotly.plot(plot, [%s], { title: '%s' });
      });
      </script>
    """.format(id, width, height, id, data, title)
  }

  def mkPoints(xs: Seq[Double], ys: Seq[Double]): Seq[Point] = xs.zip(ys).map { case (x, y) => Point(x, y) }

  def mkLabeledPoints(xs: Seq[String], ys: Seq[Double]): Seq[LabeledPoint] = xs.zip(ys).map { case (x, y) => LabeledPoint(x, y) }
}
