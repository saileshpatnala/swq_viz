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
var URIs = [];
var finalLinks = [];
var finalNodes = [];
var finalGraph = {};

// removing duplicate nodes to that they don't appear on the
function removeDuplicateNodes(origNodeArray) {
    var newNodes = [];
    var lookupObject  = {};

    for(var i in origNodeArray) {
        lookupObject[origNodeArray[i]["id"]] = origNodeArray[i];
    }

    for(i in lookupObject) {
        newNodes.push(lookupObject[i]);
    }
    return newNodes;
}

function doesLinkExist(linkArray, link) {
    linkArray.forEach(function(item) {
        if (item.source === link.source && item.target === link.target
            && item.predicate === link.predicate) {
            return 1;
        }
    });
    return 0;
}

function removeDuplicateLinks(origLinkArray) {
    var newLinks = [];

    origLinkArray.forEach(function(linkObj) {
        if (!doesLinkExist(newLinks, linkObj)) {
            newLinks.push(linkObj);
        }
    });

    return newLinks;
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

// return 1 if string exists in array
// return 0 if string doesn't exist in array
// going to be used to avoid duplicates in URIs array
function doesStringExist(stringArray, string) {
    stringArray.forEach(function(item) {
        if (item == string) {
            return 1;
        }
    });
    return 0;
}

// convert the JSON returned from backend into new JSON for D3
// involves removing duplicate links, nodes and appending to global
// graph JSON properly
// basically has the business logic for the aggregation
function createGraphJSON(json, nodes, links) {
    Object.keys(json).forEach(function(key){
        var triples = json[key];
        var searchParameter = key;
        triples.forEach(function(key){
            var triple = {};
            triple["source"] = key.subject.value;
            triple["target"] = key.object.value;
            // call to parse and extract predicate value
            triple["predicate"] = parsePredicateValue(key.predicate.value);
            triple["value"] = 1;
            if (!(triple["predicate"] === "sameAs")){
                links.push(triple);

                var node = {};
                node["id"] = key.subject.value;
                if (key.subject.type === "uri") {
                    if (!doesStringExist(URIs, key.subject.value)) {
                        URIs.push(key.subject.value);
                    }
                }
                node["group"] = 1;
                nodes.push(node);
                node = {};
                node["id"] = key.object.value;
                // if (key.object.type === "uri") {
                //     if (!doesExist(URIs, key.object.value)) {
                //         URIs.push(key.object.value);
                //     }
                // }
                node["group"] = 1;
                nodes.push(node);
            }
        });
    });
}

function restart(nodes, links) {
    // Apply the general update pattern to the nodes.
    node = node.data(nodes, function(d) { return d.id;});
    node.exit().remove();
    node = node.enter().append("circle").attr("fill", function(d) { return color(d.id); }).attr("r", 8).merge(node);

    // Apply the general update pattern to the links.
    link = link.data(links, function(d) { return d.source.id + "-" + d.target.id; });
    link.exit().remove();
    link = link.enter().append("line").merge(link);

    // Update and restart the simulation.
    simulation.nodes(nodes);
    simulation.force("link").links(links);
    simulation.alpha(1).restart();
}

function requery() {
    while(URIs.length > 0) {
        console.log(URIs[URIs.length-1]);
        var newNodes = [];
        var newLinks = [];
        jQuery.ajax({
            type: "POST",
            url: "http://localhost:8080/TripleDataProcessor/webapi/query",
            data: URIs.pop(),
            contentType: "application/json",
            success:
                function (json) {
                    console.log("POST successful");
                    createGraphJSON(json, newNodes, newLinks);

                    newNodes.forEach(function(node){
                        finalNodes.push(node);
                    });
                    newLinks.forEach(function(link){
                        finalLinks.push(link);
                    });

                    // console.log(newGraph);

                    finalNodes = removeDuplicateNodes(finalNodes);
                    finalLinks = removeDuplicateLinks(finalLinks);

                    finalGraph["nodes"] = finalNodes;
                    finalGraph["links"] = finalLinks;

                    console.log(finalGraph);

                    update(finalLinks, finalNodes)

                    setTimeout(function () {
                        console.log("waiting");
                    }, 10000);
                }
        });
    }
}

d3.json("http://localhost:8080/TripleDataProcessor/webapi/myresource", function (error, json) {
    if (error) throw error;

    createGraphJSON(json, finalNodes, finalLinks);

    finalNodes = removeDuplicateNodes(finalNodes);
    finalLinks = removeDuplicateLinks(finalLinks);

    finalGraph["nodes"] = finalNodes;
    finalGraph["links"] = finalLinks;

    console.log(finalGraph);
    console.log(URIs);

    update(finalGraph.links, finalGraph.nodes);

    setTimeout(function() {}, 10000);

    requery();
});

function update(links, nodes) {

    link = svg.selectAll(".link")
        .data(links)
        .enter()
        .append("line")
        .attr("class", "link")
        .attr('marker-end','url(#arrowhead)');

    edgepaths = svg.selectAll(".edgepath")
        .data(links)
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
        .data(links)
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
        .data(nodes)
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
        .nodes(nodes)
        .on("tick", ticked);

    simulation.force("link")
        .links(links);
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