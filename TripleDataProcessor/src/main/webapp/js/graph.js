/* GLOBALS */
/* NOTE:
    - we are no longer popping off of this array, instead we are indexing one by one until we have reached the end and only new URIs are going to be pushed realized that popping off URIs would be we could query same URI again in a later iteration
 */
var URIs = []; // Store all incoming URIs to be queried
var UniversalN = []; // Hold all unique graph nodes
var UniversalL = []; // Hold all unique graph links
var GraphNodes = {}; // A map to hold all the nodes currently in graph
var RQreps = 0; // Current number of requery "NOTE::NEED TO MAKE BOOLEAN"
var graphRender = true; // Max number of reps
var ajaxCalls = [];
var nodeRadius = 10; // Node radius of the d3 nodes displayed
var itr = 0; // Iterator to track next URI to query
var endptColor = 1; // to set color for each endpoint queried KEY:[ 1: LIBRARY, 2:]

var svg = d3.select("body")
    .append("svg")
    .style("width", window.innerWidth + "px")
    .style("height", window.innerHeight + "px")
    .call(d3.zoom()
        .scaleExtent([0.3, 10])
        .on("zoom", zoomed));

width = window.innerWidth;
height = window.innerHeight;

window.addEventListener("resize", redraw);

function redraw() {
    width = window.innerWidth;
    height = window.innerHeight;

    svg
        .style("width", width)
        .style("height", height);
}

var colors = d3.scaleOrdinal(d3.schemeCategory10)
    .domain([1, 2, 3, 4, 5, 6]);


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


var g = svg.append("g")
    .attr("class", "everything");

function zoomed() {
    g.attr("transform", d3.event.transform);
}


/* NOTE:
    - set up simulation to gravitate to center of svg component
 */
var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().distance(100).strength(1))
    .force("collide", d3.forceCollide(20).iterations(16))
    .force("charge", d3.forceManyBody().strength(-1500))
    .force("center", d3.forceCenter(width / 2, height / 2))
    .force("y", d3.forceY(0))
    .force("x", d3.forceX(0));

window.onload = function(){document.getElementById("status_text").textContent = "Querying Library...";};

/* NOTE:
    - Query for first set of triples in library for the search term
*/
d3.json("http://localhost:8080/TripleDataProcessor/webapi/myresource", function(error, json) {
    if (error) throw error;
    var count = JSON.stringify(json).length;
    if (count > 20) {
        
        // document.getElementById("back").style.display="none";
        // document.getElementById("main").style.display="none";
        createGraph(json);
        
        update();
        setTimeout(function() { requery(); }, 3000);
    } else {
        window.location.href = "./noresults";
        // document.getElementById("column").style.display="none";
    }
});

function search_page() {
    window.location.href = "./search";
}

function pause_page() {
    console.log("stop");
    graphRender = false;
}

function resume_page() {
    console.log("start");
    itr = URIs.indexOf(ajaxCalls[0]);
    RQreps = itr;
    console.log(URIs.indexOf(ajaxCalls[0]));
    ajaxCalls.length = 0;
    graphRender = true;
    update();
    requery();
}


/* NOTE:
    - converting the predicate URI into string parsing the URI from last occurrence of # or /
 */
function parsePredicateValue(predicateURI) {
    var lenURI = predicateURI.length;
    var pos = predicateURI.search("#");
    if (pos === -1) {
        pos = predicateURI.lastIndexOf("/");
    }
    var predicate = predicateURI.substr(pos + 1, lenURI);
    return predicate.replace('>', '');
}


/* NOTE:
    - convert the JSON returned from backend into new JSON for D3 involves removing duplicate links, nodes and appending to global graph JSON properly
    - Basically has the logic for the aggregation && URI_filter(key.object.value)
 */
function createGraph(json) {
    if (graphRender) {
        var tripleCt = 0;
        Object.keys(json).forEach(function(key) {
            var triples = json[key];
            triples.forEach(function(key) {
                if (tripleCt >= 100) {
                    return;
                }
                tripleCt++;
                var triple = {};
                key.subject.value = key.subject.value.replace('>', '').replace('<', '');
                key.object.value = key.object.value.replace('>', '').replace('<', '');

                /* call to parse and extract predicate value */
                triple["predicate"] = parsePredicateValue(key.predicate.value);
                triple["value"] = 1;
                if (!(triple["predicate"] === "sameAs")) {
                    var node = {};
                    node["id"] = idShortener(key.subject.value);
                    node["uri"] = key.subject.value;
                    node["group"] = endptColor;
                    if (key.subject.value.includes("wikidata") || key.object.value.includes("wikidata"))
                        console.log("wiki", key.object.value, key.subject.value);
                    if (addNode(node)) {
                        if ((key.subject.type === "uri" || key.subject.value.includes("http:/")) && (URIs.indexOf(key.subject.value) === -1) && URI_filter(key.subject.value)) {
                            if (key.subject.value.includes("wikidata") || key.object.value.includes("wikidata"))
                                console.log("wikiPUsh");
                            URIs.push(key.subject.value);
                        }
                        UniversalN.push(node);
                    }
                    if (key.subject.value.includes("wikidata") || key.object.value.includes("wikidata"))
                        console.log("wikiFins");
                    node = {};
                    node["id"] = idShortener(key.object.value);
                    node["uri"] = key.object.value;
                    node["group"] = endptColor;
                    if (addNode(node)) {
                        if ((key.object.type === "uri" || key.object.value.includes("http:/")) && (URIs.indexOf(key.object.value) === -1) && URI_filter(key.object.value)) {
                            if (key.subject.value.includes("wikidata") || key.object.value.includes("wikidata"))
                                console.log("wikiPUsh");
                            URIs.push(key.object.value);
                        }
                        UniversalN.push(node);
                    }

                    let sourceIndex = UniversalN.findIndex(function(x) { return x.uri === key.subject.value });

                    if (triple["predicate"] === "title" || triple["predicate"] === "label") {
                        UniversalN[sourceIndex].id = key.object.value;
                    }
                    triple["source"] = sourceIndex;

                    triple["target"] = UniversalN.findIndex(function(x) { return x.uri === key.object.value });
                    var exists = UniversalL.filter( link => (link.source == triple["source"] && link.target == triple["target"]));
                    if(exists.length > 0)
                        console.log("exists", exists);
                    if(exists.length > 0){
                        if(!(exists[0]["predicate"].includes(triple["predicate"]))){
                          exists[0]["predicate"] = exists[0]["predicate"] + "/" + triple["predicate"];  
                        }
                    } else {
                        UniversalL.push(triple);
                    }
                    
                }
            });
        });
    }
}

function idShortener(value) {
    if (value.includes("XMLSchema")) {
        return value.replace(/\^\^(.*)/g, '').replace(/\"/g, '');
    } else if (value.includes("worldcat") && value.includes("#")) {
        return value.replace(/([\w]*)\//g, '').replace(/([\w.:]*)#/g, '').replace(/[_]/g, ' ');
    } else {
        return value.replace(/^(.*[\\\/])/, '').replace(/\"/g, '').replace('/', '');
    }
    // return value.replace(/\"/g, '');
}


/* NOTE:
    - This function filters only the triples we want into the URI array
*/
function URI_filter(uri) {
    if (uri.includes("worldcat.org/oclc/") || uri.includes("/names/") || uri.includes("/bibs/") || uri.includes("/subjects/") || uri.includes("wiki\/Special:EntityData"))
        return true;
    return false;
}


/* NOTE:
    - This function takes a new nodes that is to be inserted and checks to see if it is in the GraphNodes map. If it isn't it is saved.
 */
function addNode(node) {
    /* If node exists in map then return false, else save new node */
    if (GraphNodes[node.uri] == null) {
        GraphNodes[node.uri] = node;
        return true;
    }
    return false;
}

function remove(array, data) {
    // var search = "";
    // for (var elem in array) {
    //     if (elem.includes(data.toString())) {
    //         search = elem;
    //         break;
    //     }
    // }
    const index = array.indexOf(data);
    console.log("removing", index, array, data);
    if (index !== -1) {
        array.splice(index, 1);
    }
}

function ajaxSuccess(json, type, data) {
    var endpoints = ["", "lib", "oclc", "loc", "wiki"];
    if (graphRender) {
        remove(ajaxCalls, data);
        console.log("POST successful");
        document.getElementById("status_text").textContent = "Updating Graph...";
        if (type === "reconciler") {
            if (json.length < 1) {
                console.log("Reconciler: Found Nothing");
                // requery();
            } else {
                console.log("ReconcilerLID", json.Reconciler[1].locID);
                locQuery(json.Reconciler[1].locID);

                console.log("ReconcilerWID", json.Reconciler[2].wikiID);
                setTimeout(function() { wikiQuery(json.Reconciler[2].wikiID); }, 3000);
            }
        } else {
            endptColor = endpoints.indexOf(type);
            // if (type === "loc") {
            //     createGraph(JSON.parse(json));
            // } else {
            createGraph(json);
            // }
            update();
        }
    }
}

/* NOTE:
    - requerying the unresolved URIs using, appending to the origGraph and updating D3
 */
function requery() {
    if (itr < URIs.length) {
        RQreps++;
        console.log("URIs", URIs[itr]);
        console.log("RQreps", URIs);
        if (typeof(URIs[itr]) !== 'undefined') {
            ajaxCalls.push(URIs[itr]);
            if (URIs[itr].includes("www.worldcat.org/oclc/")) {
                var input = URIs[itr];
                document.getElementById("status_text").textContent = "Querying oclc...";
                console.log("Querying oclc...", URIs[itr]);
                jQuery.ajax({
                    type: "POST",
                    url: "http://localhost:8080/TripleDataProcessor/webapi/oclc",
                    data: URIs[itr].replace('http://www.worldcat.org/oclc/', ''),
                    contentType: "text/plain",
                    success: function(json) {
                        ajaxSuccess(json, "oclc", input);
                    }
                });
            } else if (URIs[itr].includes("id.loc.gov/authorities/names/")) {
                var input = URIs[itr];
                // ajaxCalls.push(URIs[itr]);
                // document.getElementById("status_text").textContent = "Querying reconciler...";
                jQuery.ajax({
                    // async: false,
                    type: "POST",
                    url: "http://localhost:8080/TripleDataProcessor/webapi/reconcile",
                    data: URIs[itr].replace('http://id.loc.gov/authorities/names/', ''),
                    contentType: "text/plain",
                    success: function(json) {
                        ajaxSuccess(json, "reconciler", input);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        remove(ajaxSuccess, input);
                        console.log("XML", XMLHttpRequest);
                        console.log("txtStatus", textStatus);
                        console.log("err", errorThrown);
                    }
                });
            } else if (URIs[itr].includes("wikidata.org/entity")) {
                wikiQuery(URIs[itr]);

            } else {
                document.getElementById("status_text").textContent = "Querying Library...";
                libraryQuery(URIs[itr]);
            }

        }
        if (graphRender) {
            itr++;
            setTimeout(function() { requery(); }, 5000);
            // if (typeof(URIs[itr])!=='undefined') {
            //     requery();
            // }},5000);
            // console.log("RQreps = " + RQreps + " | MAXRQ = " + MAXRQ);
        }
    } else {
                document.getElementById("status_text").textContent = "Done";
    }
}

function locQuery(data) {
    ajaxCalls.push(data);
    document.getElementById("status_text").textContent = "Querying LOC...";
    console.log("Querying loc...", data);
    jQuery.ajax({
        // async: false,
        type: "POST",
        url: "http://localhost:8080/TripleDataProcessor/webapi/libraryofcongress",
        data: data,
        contentType: "text/plain",
        success: function(json) {
            ajaxSuccess(JSON.parse(json), "loc", data);
        }
    });
}

function wikiQuery(data) {
    ajaxCalls.push(data);
    document.getElementById("status_text").textContent = "Querying WIKI...";
    console.log("Querying wiki...", data);
    jQuery.ajax({
        // async: false,
        type: "POST",
        url: "http://localhost:8080/TripleDataProcessor/webapi/wiki",
        data: data.replace('/^(.*[\\\/])/', ''),
        contentType: "text/plain",
        success: function(json) {
            ajaxSuccess(json, "wiki", data);
        }
    });
}

// function dbpediaQuery(data) {
//     console.log("Querying dbpedia...", data);
//     jQuery.ajax({
//         async: false,
//         type: "POST",
//         url: "http://localhost:8080/TripleDataProcessor/webapi/dbpedia",
//         data: data,
//         contentType: "text/plain",
//         success: function (json) {
//             document.getElementById("status_text").textContent="Done.";
//             console.log("POST successful", json);
//             endptColor = 6;
//             createGraph(json);
//             update();
//         }
//     });
// }

function libraryQuery(data) {
    // ajaxCalls.push(data);
    console.log("Querying library...", data);
    if (data.includes("/subjects/")) { /*subject nodes*/
        console.log("subject");
        jQuery.ajax({
            type: "POST",
            url: "http://localhost:8080/TripleDataProcessor/webapi/librarysubject",
            data: data,
            contentType: "application/json",
            success: function(json) {
                ajaxSuccess(json, "lib", data);
            }
        });
    }
    /* all other nodes */
    else {
        console.log("regular");
        jQuery.ajax({
            type: "POST",
            url: "http://localhost:8080/TripleDataProcessor/webapi/library",
            data: data,
            contentType: "application/json",
            success: function(json) {
                ajaxSuccess(json, "lib", data);
            }
        });

    }
}

function update() {
    // if (graphRender) {
    var links = UniversalL;
    var nodes = UniversalN;

    g = svg.select("g");

    console.log("lnks", UniversalL, "nds", UniversalN);


    link = svg.select("g").selectAll(".link")
        .data(links, function(d) { return d.source.id + "-" + d.target.id; });

    link.exit().remove();

    link = link.enter()
        .append("line")
        .attr("class", "link")
        .attr('marker-end', 'url(#arrowhead)')
        .merge(link);

    edgepaths = svg.select("g").selectAll(".edgepath")
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

    edgelabels = svg.select("g").selectAll(".edgelabel")
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

    var node = svg.select("g").selectAll("g")
        .data(nodes);

    node.exit().remove();

    node = node.enter()
        .append("g")
        .attr('class', 'node');

    var circle = node.append("circle")
        .attr("r", nodeRadius)
        .attr("fill", function(d) { return colors(d.group); })
        .attr("cx", 0)
        .attr("cy", 0);

    /* Add drag capabilities */
    var drag_handler = d3.drag()
        .on("start", dragstarted)
        .on("drag", dragged);

    drag_handler(node);

    link.append("title")
        .text(function(d) { return d.predicate; });

    edgelabels.append('textPath')
        .attr('xlink:href', function(d, i) { return '#edgepath' + i })
        .style("text-anchor", "middle")
        .style("pointer-events", "none")
        .attr("startOffset", "50%")
        .text(function(d) { return d.predicate });

    node.append("text")
        .attr("class", "ndtext")
        .attr("dy", -1)
        .style("font-family", "sans-serif")
        .style("font-size", "0.7em")
        .text(function(d) { return d.id; });

     d3.selectAll(".ndtext")
        .text(function(d) { return d.id; })
        .call(wrap, 200);

    simulation.nodes(nodes).on("tick", ticked);
    simulation.force("link").links(links);
    simulation.alpha(0.3).restart()

    // if (RQreps < MAXRQ){
    // setTimeout(function(){requery();},5000);
    //            // if (typeof(URIs[itr])!=='undefined') {
    //            //     requery();
    //            // }},5000);
    //        console.log("RQreps = "+RQreps+" | MAXRQ = "+MAXRQ);
    // }
    // }
}

function ticked() {

    link = svg.selectAll(".link")
        .attr("x1", function(d) { return d.source.x })
        .attr("y1", function(d) { return d.source.y })
        .attr("x2", function(d) { return d.target.x })
        .attr("y2", function(d) { return d.target.y });

    node = svg.selectAll(".node")
        .attr("transform", function(d) { return "translate(" + d.x + ", " + d.y + ")"; })
        .attr("cx", function(d) { return d.x })
        .attr("cy", function(d) { return d.y });

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

function wrap(text, width) {
    text.each(function() {
        var text = d3.select(this),
            words = text.text().split(/\s+/).reverse(),
            word,
            line = [],
            lineNumber = 0,
            lineHeight = 1, // ems
            y = text.attr("y"),
            dy = parseFloat(text.attr("dy")),
            tspan = text.text(null).append("tspan").attr("x", 0).attr("y", 0).attr("dy", dy + "em")
        while (word = words.pop()) {
            line.push(word)
            tspan.text(line.join(" "))
            if (tspan.node().getComputedTextLength() > width) {
                line.pop()
                tspan.text(line.join(" "))
                line = [word]
                tspan = text.append("tspan").attr("x", 0).attr("y", 0).attr("dy", `${++lineNumber * lineHeight + dy}em`).text(word)
            }
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
