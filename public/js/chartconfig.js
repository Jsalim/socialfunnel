function defaultChartConfig(containerid, data, guideline, useDates, auxOptions, xLabel, yLabel) {
  if (auxOptions === undefined) auxOptions = {};
  if (guideline === undefined) guideline = true;
  nv.addGraph(function() {
    var chart;
    chart = nv.models.lineChart().useInteractiveGuideline(guideline);

    chart
        .x(function(d,i) { 
          return d.x;
        });

    if (auxOptions.width) 
      chart.width(auxOptions.width);

    if (auxOptions.height)
      chart.height(auxOptions.height);

    if (auxOptions.forceY) 
      chart.forceY([0]);

    var formatter;
    if (useDates !== undefined) {
        formatter = function(d,i) {
                var now = (new Date()).getTime() - 86400 * 1000 * 365;
                now = new Date(now + d * 86400 * 1000);
                return d3.time.format('%d/%m/%y')(now );
        }
    }
    else {
        formatter = d3.format(",.1f");
    }
    
    
    chart.margin({right: 40});
    chart.xAxis // chart sub-models (ie. xAxis, yAxis, etc) when accessed directly, return themselves, not the parent chart, so need to chain separately
    .axisLabel(xLabel)
        .tickFormat(
            formatter
          );

    chart.yAxis
        .axisLabel(yLabel)
        .tickFormat(d3.format(',.2f'));

    d3.select('#' + containerid + ' svg')
        .datum(data)
      .transition().duration(500)
        .call(chart);

    nv.utils.windowResize(chart.update);
    if($){
    	$("g.nv-y g.nv-wrap text.nv-axislabel").attr("y", -40);
    }
    return chart;
  });
}