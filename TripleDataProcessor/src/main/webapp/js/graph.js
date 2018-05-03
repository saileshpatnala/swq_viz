var svg = d3.select("svg"),
		width = +svg.attr("width"),
		height = +svg.attr("height"),
		node,
		link;

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

// var graphJSON;
// jQuery.ajax({
//     type: "GET",
//     url: "http://localhost:8080/TripleDataProcessor/webapi/myresource",
//     success: function(resp) {
//         graphJSON = resp;
//     },
//     async: false // setting async value allows the global variable to be set
// });

var baseNodes = [];
var baseLinks = [];

d3.json("http://localhost:8080/TripleDataProcessor/webapi/myresource", function (error, json) {
    if (error) throw error;

    var links = [];
    var nodes = [];
    var graph = {};

    createGraphJSON(json, nodes, links);

    nodes = removeDuplicates(nodes, "id");
    baseNodes = nodes;
    baseLinks = links;

    console.log("nodes");
    console.log(nodes);
    
    graph["nodes"] = baseNodes;
    graph["links"] = baseLinks;

    update(graph.links, graph.nodes);

    setTimeout(function() {console.log("waiting")}, 5000);

    requery();
    requery();
});

function requery() {
    while(URIs.length > 0) {
        console.log(URIs[0]);
        var nodes = [];
        var links = [];
        var graph = {};
        jQuery.ajax({
            type: "POST",
            url: "http://localhost:8080/TripleDataProcessor/webapi/query",
            data: URIs[0],
            contentType: "application/json",
            success:
                function (json) {
                    console.log(json);
                    createGraphJSON(json, nodes, links);

                    baseNodes.concat(nodes);
                    baseLinks.concat(links);

                    console.log("nodes1");
                    console.log(baseLinks);
                    // baseNodes = removeDuplicates(baseNodes, "id");
                    console.log("nodes2");
                    console.log(baseLinks);
                        
                    graph["nodes"] = baseNodes;
                    graph["links"] = baseLinks;

                    update(graph.links, graph.nodes);

                    setTimeout(function () {
                        console.log("waiting");
                    }, 5000);
                }
        });
        URIs.pop();
    }
}

function parsePredicateValue(predicateURI) {
    var lenURI = predicateURI.length;
    var pos = predicateURI.search("#");
    if (pos === -1) {
        pos = predicateURI.lastIndexOf("/");
    }
    var predicate = predicateURI.substr(pos+1, lenURI);
    return predicate
}

var URIs = [];

function doesExist(stringArray, string) {
    stringArray.forEach(function(item) {
        if (item == string) {
            return 1;
        }
    });
    return 0;
}

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
            if (!(triple["predicate"] === "label" || triple["predicate"] === "sameAs")) {
                baseLinks.push(triple);

                var node = {};
                node["id"] = key.subject.value;
                if (key.subject.type === "uri") {
                    if (!doesExist(URIs, key.subject.value)) {
                        URIs.push(key.subject.value);
                    }
                }
                node["group"] = 1;
                baseNodes.push(node);
                node = {};
                node["id"] = key.object.value;
                if (key.object.type === "uri") {
                    if (!doesExist(URIs, key.object.value)) {
                        URIs.push(key.object.value);
                    }
                }
                node["group"] = 1;
                baseNodes.push(node);
            }
        });
    });
}

function removeDuplicates(originalArray, prop) {
    var newArray = [];
    var lookupObject  = {};
 

    for(var i in originalArray) {
        lookupObject[originalArray[i][prop]] = originalArray[i];
    }
if(originalArray[0]){
    console.log("orgAr0");
    console.log(originalArray[0].id);
}

    for(i in lookupObject) {
        newArray.push(lookupObject[i]);
    }
    return newArray;
}

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
                //.on("end", dragended)
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