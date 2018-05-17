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

var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().distance(200))
    .force("charge", d3.forceManyBody())
    .force("center", d3.forceCenter(width / 2, height / 2))
    .force("x", d3.forceX())
    .force("y", d3.forceY())
    .alphaTarget(0.3);
    // .on("tick", ticked);

// initializing all global variables
// we are no longer popping off of this array, instead we are
// indexing one by one until we have reached the end and only new URIs are
// going to be pushed
// realized that popping off URIs would be we could query same URI again
// in a later iteration
var URIs = [];
var GraphNodes = {};
// returns new list of nodes that does not contain duplicates
function removeDuplicateNodes(originalArray) {
    var newArray = [];
    var lookupObject = {};

    for (var i in originalArray) {
        lookupObject[originalArray[i]["id"]] = originalArray[i];
    }

    for (i in lookupObject) {
        newArray.push(lookupObject[i]);
    }
    return newArray;
}

// returns new list of links that does not contain duplicates
function removeDuplicateLinks(origLinkArray) {

    var map = {};
    origLinkArray.forEach(function(value, index) {
        var obj = JSON.stringify({
            source: value.source.index,
            target: value.target.index,
            predicate: value.predicate
        });
        // console.log("dupLink", obj);
        if (map[obj]) {
            origLinkArray.splice(index, 1);
        } else {
            map[obj] = true; 
        }
    });

    return origLinkArray;
}

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
var UniversalN = [];
function createGraph(json) {
    var nodes = [];
    var links = [];
    var graph = {};
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
                console.log("***SUBJECT***", key.subject.value);
                node["group"] = 1;
                if(addNode(node)){
                    if (key.subject.type === "uri") {
                        if (URIs.indexOf(key.subject.value) === -1) {
                            URIs.push(key.subject.value);
                        }
                    }
                    nodes.push(node);
                    UniversalN.push(node);
                }

                node = {};
                node["id"] = key.object.value;
                console.log("***OBJECT***", key.object.value);
                node["group"] = 1;
                if(addNode(node)){
                    if (key.object.type === "uri") {
                        if (URIs.indexOf(key.object.value) === -1) {
                            URIs.push(key.object.value);
                        }
                    }
                    nodes.push(node);
                    UniversalN.push(node);
                }

                // if((nodes.findIndex(x => x.id == key.subject.value) || nodes.findIndex(x => x.id == key.object.value)) == 13 )
                // console.log("hi\n",UniversalN.findIndex(x => x.id == key.subject.value), key.subject.value);
                // console.log(UniversalN.findIndex(x => x.id == key.object.value), key.object.value)
                // console.log(node.id);
                triple["source"] = UniversalN.findIndex(function(x){ return x.id === key.subject.value});
                triple["target"] = UniversalN.findIndex(function(x){ return x.id === key.object.value});
                links.push(triple);
            }
        });
    });

    // console.log(nodes, links);

    // nodes = removeDuplicateNodes(nodes);
    // links = removeDuplicateLinks(links);

    // GraphNodes = GraphNodes.add(nodes);

    
    // for(var elem of (new Set(nodes))){
    //     GraphNodes.add(elem);
    // }
    

    // console.log("GraphNodes", UniversalN);

    graph["nodes"] = nodes;
    graph["links"] = links;
    console.log("~~~~~NODES~~~~~~", nodes);
    console.log("~~~~~~LINKS~~~~~", links);

    return graph;
}


function addNode(node){
    // console.log(GraphNodes[node.id] == null);
    // console.log(GraphNodes[node.id]);
    // let temp = GraphNodes[node.id];
    if(GraphNodes[node.id] == null){
     GraphNodes[node.id] = node;
    // if(GraphNodes[node.id] != temp){
        return true;
    }
    
    return false;
}

// this function is supposed to add the nodes and links from the newGraph
// to the origGraph making sure not to add duplicates
// note: objects are passed by reference in javascript
function concatGraph(origGraph, newGraph) {
    var concatGraph = {};
    var baseLinks = origGraph.links;
    var baseNodes = origGraph.nodes;
    var newLinks = newGraph.links;
    var newNodes = newGraph.nodes;
    var concatLinks = baseLinks;
    var concatNodes = baseNodes;

    // appending links from newGraph to origGraph
    newLinks.forEach(function(item) {
        concatLinks.push(item);
    });

    // appending nodes from newGraph to origGraph
    newNodes.forEach(function(item) {
        concatNodes.push(item);
    });

    // removing any duplicate nodes and links we might have pushed
    concatNodes = removeDuplicateNodes(concatNodes);
    concatLinks = removeDuplicateLinks(concatLinks);

    // creating the appended graph and returning for next iteration
    concatGraph["nodes"] = concatNodes;
    concatGraph["links"] = concatLinks;
    return concatGraph;
}

// requerying the unresolved URIs using, appending to the origGraph and updating D3
var i = 0;
function requery(origGraph) {
    while (1) {
        if (i === URIs.length) {
            break;
        }
        console.log(URIs);
        jQuery.ajax({
            type: "POST",
            url: "http://localhost:8080/TripleDataProcessor/webapi/library",
            data: URIs[i],
            contentType: "application/json",
            success: function(json) {
                console.log("POST successful");
                // console.log(json);
                var newGraph = createGraph(json);
                console.log("NewGraph");
                // console.log(newGraph);
                var nextGraph = concatGraph(origGraph, newGraph);
                console.log("FinalGraph");
                // console.log(nextGraph);
                update(nextGraph.links, UniversalN);

            }
        });
        i++;
    }
}

d3.json("http://localhost:8080/TripleDataProcessor/webapi/myresource", function(error, json) {
    if (error) throw error;

    var graph = createGraph(json);

    console.log("***** GRAPH ******", graph);
    console.log("***** URIs *****", URIs);
    console.log("***** UniversaLN *****", UniversalN);

    update(graph.links, UniversalN);

    setTimeout(function() {}, 10000);
    var temp = 0;
    while (temp != 3) {
        requery(graph);
        temp++;

    }
});

function update(links, nodes) {

    console.log("lnk", links, "nds", UniversalN);

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

    // node = svg.selectAll("g")
    //     .data(nodes)
    //     .text(function(d) {return d.id;});

    // node.exit().remove();

    // node = node.enter()
    //     .append("circle")
    //     .attr("r", 15)
    //     .style("fill", function(d) { return colors(d.group); })
    //     .merge(node);

    // node = node.enter()
    //     .append("g")
    //     .attr("r", 15)
    //     .style("fill", function(d) { return colors(d.group); })
    //     .merge(node);


    // node = node.attr("class", "node")
    //     .call(d3.drag()
    //         .on("start", dragstarted)
    //         .on("drag", dragged)
    //         // .on("end", dragended)
    //     );



    // node.
        

    link.append("title")
        .text(function(d) { return d.predicate; });

    edgelabels.append('textPath')
        .attr('xlink:href', function(d, i) { return '#edgepath' + i })
        .style("text-anchor", "middle")
        .style("pointer-events", "none")
        .attr("startOffset", "50%")
        .text(function(d) { return d.predicate });

    //.style("fill", function (d, i) {return colors(i);})

    node.append("title")
        .text(function(d) { return d.id; });

    node.append("text")
        .attr("dy", -3)
        .style("font-family", "sans-serif")
        .style("font-size", "0.7em")
        .text(function(d) { return d.id; });

    simulation.nodes(nodes).on("tick", ticked);
    simulation.force("link").links(links);
    // simulation.restart();

    simulation.alpha(0.3).restart()
}

function ticked() {

    link = svg.selectAll(".link")
        .attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });
    node = svg.selectAll(".node")
        .attr("transform", function(d) { return "translate(" + d.x + ", " + d.y + ")"; });
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

// function dragended(d) {
//     if (!d3.event.active) simulation.alphaTarget(0);
//     d.fx = undefined;
//     d.fy = undefined;
// }