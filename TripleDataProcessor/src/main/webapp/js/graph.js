var svg = d3.select("svg"),
		width = +svg.attr("width"),
		height = +svg.attr("height");

var colors = d3.scaleOrdinal(d3.schemeCategory10);

svg.append('defs').append('marker')
    .attrs({'id':'arrowhead',
        'viewBox':'-0 -5 10 10',
        'refX':13,
        'refY':0,
        'orient':'auto',
        'markerWidth':13,
        'markerHeight':13,
        'xoverflow':'visible'})
    .append('svg:path')
    .attr('d', 'M 0,-5 L 10 ,0 L 0,5')
    .attr('fill', '#000')
    .style('stroke','none');

var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id(function (d) {return d.id;}).distance(200))
    .force("charge", d3.forceManyBody())
    .force("center", d3.forceCenter(width / 2, height / 2));

// initializing all global variables
// we are no longer popping off of this array, instead we are
// indexing one by one until we have reached the end and only new URIs are
// going to be pushed
// realized that popping off URIs would be we could query same URI again
// in a later iteration
var URIs = [];

// returns new list of nodes that does not contain duplicates
function removeDuplicateNodes(originalArray) {
    var newArray = [];
    var lookupObject  = {};

    for(var i in originalArray) {
        lookupObject[originalArray[i]["id"]] = originalArray[i];
    }

    for(i in lookupObject) {
        newArray.push(lookupObject[i]);
    }
    return newArray;
}

// returns new list of links that does not contain duplicates
function removeDuplicateLinks(origLinkArray) {

    var map = {};
    origLinkArray.forEach(function(value, index){
        var obj = JSON.stringify({
            source: value.source.id,
            target: value.target.id,
            predicate: value.predicate
        });
        if (!map[obj]) {
            map[obj] = true;
        }
        else {
            origLinkArray.splice(index, 1);
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
    var predicate = predicateURI.substr(pos+1, lenURI);
    return predicate
}

    var nodes = [];
    var links = [];
    var graph = {};

// convert the JSON returned from backend into new JSON for D3
// involves removing duplicate links, nodes and appending to global
// graph JSON properly
// basically has the logic for the aggregation
function createGraph(json) {
    // console.log(json);
    Object.keys(json).forEach(function(key){
        var triples = json[key];
        var exists = false;
        var triple = {};
        triples.forEach(function(keyf){
            // console.log(keyf);
            triple = {};
            triple["source"] = keyf.subject.value;
            triple["target"] = keyf.object.value;
            // call to parse and extract predicate value
            triple["predicate"] = parsePredicateValue(keyf.predicate.value);
            triple["value"] = 1;
            for (var i = 0; i <= links.length - 1; i++) {
                if(links[i].source == triple.source && links[i].target == triple.target && links[i].predicate == triple.predicate){
                    exists = true;
                    break;
                }
            }
            if(!exists && !(triple["predicate"] === "sameAs")){
                links.push(triple);
            }
            exists = false;
            
            if (!(triple["predicate"] === "sameAs")){
                // links.push(triple);

                var node = {};
                node["id"] = keyf.subject.value;
                if (keyf.subject.type === "uri") {
                    if (URIs.indexOf(keyf.subject.value) === -1) {
                        URIs.push(keyf.subject.value);
                    }
                }
                node["group"] = 1;
                // console.log("node1", node);
                for (var i = 0; i < nodes.length; i++) {
                    if(nodes[i].id == node.id){
                        exists = true;
                        break;
                    }
                }
                if(!exists){nodes.push(node);}
                exists = false;
                node = {};
                node["id"] = keyf.object.value;
                // if (key.object.type === "uri") {
                //     if (!doesExist(URIs, key.object.value)) {
                //         URIs.push(key.object.value);
                //     }
                // }
                node["group"] = 1;
                // console.log("node2", node);
                for (var i = 0; i < nodes.length; i++) {
                    if(nodes[i].id == node.id){
                        exists = true;
                        break;
                    }
                }
                if(!exists){nodes.push(node);}
                exists = false;
            }
        });
    });

    // nodes = removeDuplicateNodes(nodes);
    // links = removeDuplicateLinks(links);

    // console.log("nodes",nodes);
    // console.log("links",links);

    graph["nodes"] = nodes;
    graph["links"] = links;
    return graph;
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
    newLinks.forEach(function(item){
        concatLinks.push(item);
    });

    // appending nodes from newGraph to origGraph
    newNodes.forEach(function(item){
        concatNodes.push(item);
    });

    // removing any duplicate nodes and links we might have pushed
    // concatNodes = removeDuplicateNodes(concatNodes);
    // concatLinks = removeDuplicateLinks(concatLinks);

    // creating the appended graph and returning for next iteration
    concatGraph["nodes"] = concatNodes;
    concatGraph["links"] = concatLinks;
    return concatGraph;
}

// requerying the unresolved URIs using, appending to the origGraph and updating D3
function requery(origGraph) {
    var i = 0;
    while(1) {
        if (i === URIs.length) {
            break;
        }
        console.log(URIs[i]);
        jQuery.ajax({
            type: "POST",
            url: "http://localhost:8080/TripleDataProcessor/webapi/library",
            data: URIs[i],
            contentType: "application/json",
            success:
                function (json) {
                    console.log("POST successful");
                    // console.log(json);
                    var newGraph = createGraph(json);
                    console.log("NewGraph");
                    // console.log(newGraph);
                    var nextGraph = concatGraph(origGraph, newGraph);
                    console.log("FinalGraph");
                    // console.log(nextGraph);
                    update(nextGraph.links, nextGraph.nodes);

                }
        });
        i++;
    }
}

d3.json("http://localhost:8080/TripleDataProcessor/webapi/myresource", function (error, json) {
    if (error) throw error;

    var graph = createGraph(json);

    console.log(graph);
    // console.log(URIs);

    update(graph.links, graph.nodes);

    setTimeout(function() {}, 10000);

    requery(graph);

    console.log("Last",graph);
});

function update(llinks, lnodes) {

    link = svg.selectAll(".link")
        .data(llinks)
        .enter()
        .append("line")
        .attr("class", "link")
        .attr('marker-end','url(#arrowhead)');

    edgepaths = svg.selectAll(".edgepath")
        .data(llinks)
        .enter()
        .append('path')
        .attrs({
            'class': 'edgepath',
            'fill-opacity': 0,
            'stroke-opacity': 0,
            'id': function (d, i) {return 'edgepath' + i}
        })
        .style("pointer-events", "none");

    edgelabels = svg.selectAll(".edgelabel")
        .data(llinks)
        .enter()
        .append('text')
        .style("pointer-events", "none")
        .style("font-family", "sans-serif")
        .style("font-size", "0.7em")
        .attrs({
            'class': 'edgelabel',
            'id': function (d, i) {return 'edgelabel' + i},
            'font-size': 12,
            'fill': '#aaa'
        });

    node = svg.selectAll(".node")
        .data(lnodes)
        .enter()
        .append("g")
        .attr("class", "node")
        .call(d3.drag()
                .on("start", dragstarted)
                .on("drag", dragged)
                // .on("end", dragended)
        );

    link.append("title")
        .text(function (d) {return d.predicate;});

    edgelabels.append('textPath')
        .attr('xlink:href', function (d, i) {return '#edgepath' + i})
        .style("text-anchor", "middle")
        .style("pointer-events", "none")
        .attr("startOffset", "50%")
        .text(function (d) {return d.predicate});

    node.append("circle")
        .attr("r", 15)
        .style("fill", function (d) {return colors(d.group);})
        //.style("fill", function (d, i) {return colors(i);})

    node.append("title")
        .text(function (d) {return d.id;});

    node.append("text")
        .attr("dy", -3)
        .style("font-family", "sans-serif")
        .style("font-size", "0.7em")
        .text(function (d) {return d.id;});

    simulation
        .nodes(lnodes)
        .on("tick", ticked);

    simulation.force("link")
        .links(llinks);

    simulation.restart();
}

function ticked() {

    link
        .attr("x1", function (d) {return d.source.x;})
        .attr("y1", function (d) {return d.source.y;})
        .attr("x2", function (d) {return d.target.x;})
        .attr("y2", function (d) {return d.target.y;});
    node
        .attr("transform", function (d) {return "translate(" + d.x + ", " + d.y + ")";});
    edgepaths.attr('d', function (d) {
        return 'M ' + d.source.x + ' ' + d.source.y + ' L ' + d.target.x + ' ' + d.target.y;
    });
    edgelabels.attr('transform', function (d) {
        if (d.target.x < d.source.x) {
            var bbox = this.getBBox();
            rx = bbox.x + bbox.width / 2;
            ry = bbox.y + bbox.height / 2;
            return 'rotate(180 ' + rx + ' ' + ry + ')';
        }
        else {
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