var svg = d3.select("svg"),
		width = +svg.attr("width"),
		height = +svg.attr("height"),
		node,
		link;

var width="80vw", height="90vh";

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

d3.json("http://localhost:8080/TripleDataProcessor/webapi/myresource", function (error, obj) {
    if (error) throw error;

    var links = [];
    var nodes = [];
    var graph = {};

    Object.keys(obj).forEach(function(key){
        var triples = obj[key];
        triples.forEach(function(key){
        var triple = {};
        triple["source"] = key.subject.value;
        triple["target"] = key.object.value;
        triple["predicate"] = key.predicate.value;
        triple["value"] = 1;
        links.push(triple);

        var node = {};
        node["id"] = key.subject.value;
        node["group"] = 1;
        nodes.push(node);
        node = {};
        node["id"] = key.object.value;
        node["group"] = 1;
        nodes.push(node);
        });
    });

    function removeDuplicates(originalArray, prop) {
        var newArray = [];
        var lookupObject  = {};

        for(var i in originalArray) {
        lookupObject[originalArray[i][prop]] = originalArray[i];
        }

        for(i in lookupObject) {
            newArray.push(lookupObject[i]);
        }
        return newArray;
    }

    var nodes = removeDuplicates(nodes, "id");
    var baseNodes = [...nodes]
    var baseLinks = [...links]

    graph["nodes"] = nodes;
    graph["links"] = links;

    update(graph.links, graph.nodes);

    var triple = {};
        triple["source"] = "a";
        triple["target"] = nodes[1].id;
        triple["predicate"] = "letter";
        triple["value"] = 1;
        baseLinks.push(triple);

        var node = {};
        node["id"] = "a";
        node["group"] = 1;
        baseNodes.push(node);

    graph["nodes"] = baseNodes;
    graph["links"] = baseLinks;

    setTimeout(function() {
    update(graph.links, graph.nodes);
}, 12000);

var triple = {};
        triple["source"] = "b";
        triple["target"] = nodes[0].id;
        triple["predicate"] = "letter";
        triple["value"] = 1;
        baseLinks.push(triple);

        var node = {};
        node["id"] = "b";
        node["group"] = 1;
        baseNodes.push(node);

    graph["nodes"] = baseNodes;
    graph["links"] = baseLinks;

    setTimeout(function() {
    update(graph.links, graph.nodes);
}, 20000);
    
})

function update(links, nodes) {
    
    link = svg.selectAll(".link")
        .data(links)
        .enter()
        .append("line")
        .attr("class", "link")
        .attr('marker-end','url(#arrowhead)')

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