function defaultHorizontalBarChart(containerId, data) {
	nv.addGraph(function() {
		 var chart = nv.models.multiBarHorizontalChart()
		 	.x(function(d) { return d.label })
		 	.y(function(d) { return d.value })
		 	.showValues(true)
		 	.tooltips(true)
		 	.showControls(false)
		 	.showLegend(true);
		 	
		 	chart.yAxis
		 	.tickFormat(d3.format(',.2f'));

		 	d3.select('#'+ containerId + ' svg')
		 	.datum(data)
		 	.transition().duration(500)
		 	.call(chart);

		 	nv.utils.windowResize(chart.update);
		 	return chart;
		});
}

function defaultBarChart(containerId, data) {
	nv.addGraph(function() {
		 var chart = nv.models.discreteBarChart()
		 .x(function(d) { return d.label })
		 .y(function(d) { return d.value })
		 .tooltips(true)
		 .valueFormat(d3.format('.0%'))
		 .showValues(true)
		 
		 chart.yAxis
		 .tickFormat(d3.format(".0%"));
		  
		 d3.select('#' + containerId + ' svg')
		 .datum(data)
		 .transition().duration(500)
		 .call(chart);
		 
		 nv.utils.windowResize(chart.update);
		 
		return chart;
	});
}

function defaultPieChart(containerId, data, labelType) {
  nv.addGraph(function() {
      var width = 500,
          height = 500;

      var chart = nv.models.pieChart()
          .x(function(d) { return d.key })
          .y(function(d) { return d.y })
          .color(d3.scale.category10().range())
          .valueFormat(d3.format('.0%'))
          .labelType(labelType);
      
      chart.margin({right: 80});

        d3.select("#" + containerId + " svg")
            .datum(data)
          .transition().duration(1200)
            .call(chart);

      nv.utils.windowResize(chart.update);
      return chart;
  });
}

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
        .tickFormat(d3.format('d'));

    d3.select('#' + containerid + ' svg')
        .datum(data)
      .transition().duration(500)
        .call(chart);

    nv.utils.windowResize(chart.update);
    return chart;
  });
}