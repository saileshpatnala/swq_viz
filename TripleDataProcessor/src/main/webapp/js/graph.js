var svg = d3.select("svg"),
    width = +svg.attr("width"),
    height = +svg.attr("height");

var colors = d3.scaleOrdinal(d3.schemeCategory10);

svg.append('defs').append('marker')
    .attrs({
        'id': 'arrowhead',
        'viewBox': '-0 -5 10 10',
        'refX': 13,
        'refY': 0,
        'orient': 'auto',
        'markerWidth': 13,
        'markerHeight': 13,
        'xoverflow': 'visible'
    })
    .append('svg:path')
    .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
    .attr('fill', '#000')
    .style('stroke', 'none');

// set up simulation to gravitate to center of svg component
var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().distance(200))
	.force("collide",d3.forceCollide(20).iterations(16))
    .force("charge", d3.forceManyBody())
    .force("center", d3.forceCenter(width / 2, height / 2))
	.force("y", d3.forceY(0))
	.force("x", d3.forceX(0));

// initializing all global variables
// we are no longer popping off of this array, instead we are
// indexing one by one until we have reached the end and only new URIs are
// going to be pushed
// realized that popping off URIs would be we could query same URI again
// in a later iteration
var URIs = []; //Store all incoming URIs to be queried
var UniversalN = []; //Hold all unique graph nodes
var UniversalL = []; // Hold all unique graph links
var GraphNodes = {}; //A map to hold all the nodes currently in graph
var RQreps = 0; // current number of requery NEED TO MAKE BOOLEAN
var MAXRQ = 5; // max number of reps

// converting the predicate URI into string
// parsing the URI from last occurrence of # or /
function parsePredicateValue(predicateURI) {
    var lenURI = predicateURI.length;
    var pos = predicateURI.search("#");
    if (pos === -1) {
        pos = predicateURI.lastIndexOf("/");
    }
    var predicate = predicateURI.substr(pos + 1, lenURI);
    return predicate
}

// convert the JSON returned from backend into new JSON for D3
// involves removing duplicate links, nodes and appending to global
// graph JSON properly
// basically has the logic for the aggregation

function createGraph(json) {
    Object.keys(json).forEach(function(key) {
        var triples = json[key];
        triples.forEach(function(key) {
            var triple = {};
            // call to parse and extract predicate value
            triple["predicate"] = parsePredicateValue(key.predicate.value);
            triple["value"] = 1;
            if (!(triple["predicate"] === "sameAs")) {
                var node = {};
                node["id"] = key.subject.value;

                node["group"] = 1;
                if (addNode(node)) {
                    if(key.subject.type === "uri" && URIs.indexOf(key.subject.value) === -1 && URI_filter(key.subject.value)) {
                            URIs.push(key.subject.value);
                        }
                    UniversalN.push(node);
                }

                node = {};
                node["id"] = key.object.value;

                node["group"] = 1;
                if(addNode(node)) {
                    if (key.object.type === "uri" && URIs.indexOf(key.object.value) === -1 && URI_filter(key.object.value)) {
                            URIs.push(key.object.value);
                    }
                    UniversalN.push(node);
                }

                triple["source"] = UniversalN.findIndex(function(x) { return x.id === key.subject.value });
                triple["target"] = UniversalN.findIndex(function(x) { return x.id === key.object.value });
                UniversalL.push(triple);
            }
        });
    });

}

function URI_filter(uri){
	if(uri.search("/oclc/") !== -1 || uri.search("/names/") !== -1  || uri.search("/bibs/") !== -1)
		return true;
	return false;
}

// This function takes a new nodes that is to be inserted and checks to
// see if it is in the GraphNodes map. If it isn't it is saved.
function addNode(node) {

// If node exists in map then return false, else save new node
    if (GraphNodes[node.id] == null) {
        GraphNodes[node.id] = node;

        return true;
    }
    return false;
}

// requerying the unresolved URIs using, appending to the origGraph and updating D3
function requery() {
	var i = 0;
    while (1) {
        if (i === URIs.length) {
            break;
        }
        console.log(URIs[i]);
        jQuery.ajax({
            type: "POST",
            url: "http://localhost:8080/TripleDataProcessor/webapi/library",
            data: URIs[i],
            contentType: "application/json",
            success: function(json) {
                console.log("POST successful");
				createGraph(json);
				update();
            }
        });
        i++;
    }
RQreps++;
}

d3.json("http://localhost:8080/TripleDataProcessor/webapi/myresource", function(error, json) {
    if (error) throw error;

	createGraph(json);

	update();
});

function update() {
	var links = UniversalL;
	var nodes = UniversalN;

	console.log("lnks", UniversalL, "nds", UniversalN);

    link = svg.selectAll(".link")
        .data(links, function(d) { return d.source.id + "-" + d.target.id; });

    link.exit().remove();

    link = link.enter()
        .append("line")
        .attr("class", "link")
        .attr('marker-end', 'url(#arrowhead)')
        .merge(link);

    edgepaths = svg.selectAll(".edgepath")
        .data(links);

    edgepaths.exit().remove();
    edgepaths = edgepaths.enter()
        .append('path')
        .attrs({
            'class': 'edgepath',
            'fill-opacity': 0,
            'stroke-opacity': 0,
            'id': function(d, i) { return 'edgepath' + i }
        })
        .style("pointer-events", "none")
        .merge(edgepaths);

    edgelabels = svg.selectAll(".edgelabel")
        .data(links);

    edgelabels.exit().remove();

    edgelabels = edgelabels.enter()
        .append('text')
        .style("pointer-events", "none")
        .style("font-family", "sans-serif")
        .style("font-size", "0.7em")
        .attrs({
            'class': 'edgelabel',
            'id': function(d, i) { return 'edgelabel' + i },
            'font-size': 12,
            'fill': '#aaa'
        })
        .merge(edgelabels);

    var g = svg.append("g")
        .attr("class", "everything");

    var nodesd = g.append("g")
        .attr("class", "nodes");

    var node = nodesd.selectAll("g")
        .data(nodes)
        .enter()
        .append("g")
        .attr('class', 'node');

    var circle = node.append("circle")
        .attr("r", 15)
        .attr("fill", function(d) { return colors(d.group); })
        .attr("cx", 0)
        .attr("cy", 0);

    var text = node.append("text")
        .style("text-anchor", "middle");

    // Add drag capabilities
    var drag_handler = d3.drag()
        .on("start", dragstarted)
        .on("drag", dragged);
    // .on("end", drag_end);

    drag_handler(node);

    link.append("title")
        .text(function(d) { return d.predicate; });

    edgelabels.append('textPath')
        .attr('xlink:href', function(d, i) { return '#edgepath' + i })
        .style("text-anchor", "middle")
        .style("pointer-events", "none")
        .attr("startOffset", "50%")
        .text(function(d) { return d.predicate });


    node.append("title")
        .text(function(d) { return d.id; });

    node.append("text")
        .attr("dy", -3)
        .style("font-family", "sans-serif")
        .style("font-size", "0.7em")
        .text(function(d) { return d.id; });

    simulation.nodes(nodes).on("tick", ticked);
    simulation.force("link").links(links);
    simulation.alpha(0.3).restart()

	if(RQreps < MAXRQ){
		requery();
	}
}

function ticked() {

	let radius = 15;

   link = svg.selectAll(".link")
		.attr("x1", function (d) {return Math.max(radius, Math.min(width-radius, d.source.x));})
        .attr("y1", function (d) {return Math.max(radius, Math.min(width-radius, d.source.y));})
        .attr("x2", function (d) {return Math.max(radius, Math.min(height-radius, d.target.x));})
        .attr("y2", function (d) {return Math.max(radius, Math.min(height-radius, d.target.y));});

   node = svg.selectAll(".node")
        .attr("transform", function(d) { return "translate(" + d.x + ", " + d.y + ")"; })
		.attr("cx", function(d) { return d.x = Math.max(radius, Math.min(width - radius, d.x)); })
        .attr("cy", function(d) { return d.y = Math.max(radius, Math.min(height - radius, d.y)); });

    edgepaths.attr('d', function(d) {
        return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y;
    });
    edgelabels.attr('transform', function(d) {
        if (d.target.x < d.source.x) {
            var bbox = this.getBBox();
            rx = bbox.x + bbox.width / 2;
            ry = bbox.y + bbox.height / 2;
            return 'rotate(180 ' + rx + ' ' + ry + ')';
        } else {
            return 'rotate(0)';
        }
    });

    svg.select()
}

function dragstarted(d) {
    if (!d3.event.active) simulation.alphaTarget(0.3).restart()
    d.fx = d.x;
    d.fy = d.y;
}

function dragged(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
}
